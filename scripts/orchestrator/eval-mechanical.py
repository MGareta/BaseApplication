#!/usr/bin/env python3
"""Mechanical gate checks for orchestrated OpenSpec feature runs.

Usage:
  eval-mechanical.py --change add-orders-feature --feature orders --gate G1 [--gradle] [--base <git-ref>]

Gates: preflight, G0a, G0c, G1, G2, G3 (docs/orchestrator/proposal.md §3.3, §7.2, §7.3).
Prints a verdict JSON on stdout; exit 0 when there are no CRITICAL findings, 1 otherwise.
Standard library only (no PyYAML): reads the simple list structure of docs/orchestrator/allowlist.yaml.
"""
import argparse
import json
import os
import re
import subprocess
import sys

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
ROLE_TAGS = ["module", "data", "data-test", "viewmodel", "view", "analytics", "testing"]
findings = []


def add(rule, severity, location, evidence, owner):
    findings.append({"rule": rule, "severity": severity, "location": location, "evidence": evidence, "owner": owner})


def rel(path):
    return os.path.relpath(path, ROOT)


def run(cmd, cwd=ROOT):
    proc = subprocess.run(cmd, cwd=cwd, capture_output=True, text=True)
    return proc.returncode, proc.stdout, proc.stderr


def read(path):
    with open(path, encoding="utf-8") as handle:
        return handle.read()


# ---------- allowlist (YAML subset: top-level section -> key -> list of strings) ----------
def load_allowlist():
    data, section, key = {}, None, None
    for raw in read(os.path.join(ROOT, "docs/orchestrator/allowlist.yaml")).splitlines():
        line = raw.split(" #")[0].rstrip() if not raw.lstrip().startswith("- pattern") else raw.rstrip()
        if not line.strip() or line.lstrip().startswith("#"):
            continue
        indent = len(line) - len(line.lstrip())
        text = line.strip()
        if indent == 0 and text.endswith(":"):
            section, key = text[:-1], None
            data.setdefault(section, {})
        elif indent == 2 and section and text.endswith(":"):
            key = text[:-1]
            data[section].setdefault(key, [])
        elif section and key and text.startswith("- pattern:"):
            data[section][key].append({"pattern": text.split(":", 1)[1].strip().strip('"').replace("\\\\", "\\")})
        elif section and key and text.startswith("rule:") and data[section][key]:
            data[section][key][-1]["rule"] = text.split(":", 1)[1].strip()
        elif section and key and text.startswith("- "):
            data[section][key].append(text[2:].strip().strip('"'))
    return data


def kotlin_files(base):
    out = []
    if os.path.isdir(base):
        for dirpath, _, files in os.walk(base):
            if "/build/" in dirpath + "/":
                continue
            out += [os.path.join(dirpath, f) for f in files if f.endswith(".kt")]
    return sorted(out)


def presentation_owner(path):
    for folder, owner in (("/viewModel/", "viewmodel"), ("/model/ui/", "viewmodel"), ("/analytics/", "analytics"),
                          ("/view/", "view"), ("/navigation/", "view"), ("/model/destination/", "view"), ("/di/", "view")):
        if folder in path:
            return owner
    return "view"


def check_imports(files, allowed, forbidden, rule, owner_of, feature):
    allowed = [a.replace("<feature>", feature) for a in allowed]
    forbidden = [f.replace("<feature>", feature) for f in forbidden]
    for path in files:
        for number, line in enumerate(read(path).splitlines(), 1):
            match = re.match(r"\s*import\s+([\w.]+)", line)
            if not match:
                continue
            name = match.group(1)
            loc = f"{rel(path)}:{number}"
            if any(name.startswith(f) for f in forbidden):
                add(rule, "CRITICAL", loc, f"forbidden import {name}", owner_of(path))
            elif not any(name.startswith(a) for a in allowed):
                add(rule, "CRITICAL", loc, f"import {name} is not in the allowlist", owner_of(path))


def check_task_refs(files, change, owner_of):
    for path in files:
        if f"@see openspec {change} task" not in read(path):
            add("TR-1", "CRITICAL", rel(path), f"missing KDoc '@see openspec {change} task <n.m>'", owner_of(path))


# ---------- OpenSpec helpers ----------
def openspec_json(args):
    code, out, err = run(["openspec"] + args + ["--json"])
    try:
        return code, json.loads(out)
    except json.JSONDecodeError:
        return code, {"error": (out + err).strip()[:500]}


def scenarios_by_capability(change):
    _, data = openspec_json(["show", change, "--type", "change", "--deltas-only"])
    result = {}
    for delta in data.get("deltas", []):
        cap = delta.get("spec")
        for req in delta.get("requirements", []) or [delta.get("requirement") or {}]:
            for scn in req.get("scenarios", []):
                result.setdefault(cap, set()).add(scn.get("name"))
    return result


def check_scenario_tests(change, capability, test_files, owner):
    scenarios = scenarios_by_capability(change).get(capability, set())
    referenced = set()
    for path in test_files:
        for match in re.finditer(r"Scn:\s*([^\n*]+)", read(path)):
            for part in match.group(1).split(","):
                referenced.add(part.strip().split("/")[-1].strip())
    for scn in sorted(scenarios):
        if scn not in referenced:
            add("TR-1", "CRITICAL", f"openspec:{capability}", f"scenario '{scn}' has no test with 'Scn: {scn}'", owner)


# ---------- gates ----------
def gate_preflight(args):
    code, out, _ = run(["openspec", "--version"])
    if code != 0 or not out.strip().startswith("1.15"):
        add("PRE-0", "CRITICAL", "openspec", f"OpenSpec 1.15.x required, found '{out.strip()}'", "human")
    nav = os.path.join(ROOT, "core/presentation/src/main/java/com/core/presentation/navigation3")
    nav_src = "".join(read(p) for p in kotlin_files(nav))
    if "HasDefaultViewModelProviderFactory" not in nav_src and "hiltViewModel" not in nav_src:
        add("PRE-P1", "CRITICAL", rel(nav), "Navigation3 creates ViewModels without a Hilt factory (prerequisite P1)", "human")
    plugins = os.path.join(ROOT, "core/dependencies/src/main/java/com/core/dependencies/plugins")
    for name in ("FeatureDataPlugin.kt", "FeaturePresentationPlugin.kt"):
        src = read(os.path.join(plugins, name))
        if "configureTests()" not in src or "kspTest(" not in src:
            add("PRE-P2", "CRITICAL", f"{rel(plugins)}/{name}", "configureTests() and kspTest(hilt-compiler) missing (prerequisite P2)", "human")
    stamp_file = os.path.join(ROOT, "openwiki/.last-update.json")
    if os.path.exists(stamp_file):
        updated = json.loads(read(stamp_file)).get("updatedAt", "")[:10]
        text = read(os.path.join(ROOT, "docs/orchestrator/allowlist.yaml"))
        match = re.search(r'source_wiki_update:\s*"([^"]+)"', text)
        if match and updated and match.group(1) < updated:
            add("PRE-AL", "WARNING", "docs/orchestrator/allowlist.yaml", f"allowlist derived from wiki {match.group(1)}, wiki updated {updated}; run allowlist-derive", "coordinator")


def gate_g0a(args):
    code, data = openspec_json(["validate", args.change, "--type", "change", "--strict"])
    if code != 0:
        add("TR-3", "CRITICAL", f"openspec:{args.change}", f"openspec validate --strict failed: {json.dumps(data)[:600]}", "coordinator")
    _, status = openspec_json(["status", "--change", args.change])
    if not status.get("isPlanningComplete"):
        add("G0a", "CRITICAL", f"openspec:{args.change}", "planning artifacts incomplete (isPlanningComplete=false)", "coordinator")
    tasks_path = os.path.join(ROOT, "openspec/changes", args.change, "tasks.md")
    if not os.path.exists(tasks_path):
        return
    test_scn = {"data-test": set(), "testing": set()}
    for number, line in enumerate(read(tasks_path).splitlines(), 1):
        if not re.match(r"\s*- \[[ xX]\]", line):
            continue
        tag = re.search(r"\[(" + "|".join(ROLE_TAGS) + r")\]", line)
        loc = f"{rel(tasks_path)}:{number}"
        if not tag:
            add("TR-1", "CRITICAL", loc, "task without a role tag", "coordinator")
        if "Req:" not in line:
            add("TR-1", "CRITICAL", loc, "task without 'Req: <capability>/<requirement>'", "coordinator")
        if tag and tag.group(1) in test_scn:
            scn = re.search(r"Scn:\s*(.+)$", line)
            if not scn:
                add("TR-1", "CRITICAL", loc, "test task without 'Scn:'", "coordinator")
            else:
                test_scn[tag.group(1)].update(s.strip() for s in scn.group(1).split(","))
    caps = scenarios_by_capability(args.change)
    for cap, scenarios in caps.items():
        owner = "data-test" if cap.endswith("-data") else "testing" if cap.endswith(("-presentation", "-analytics")) else None
        for scn in sorted(scenarios):
            if owner and scn not in test_scn[owner]:
                add("TR-1", "CRITICAL", f"openspec:{cap}", f"scenario '{scn}' has no [{owner}] task", "coordinator")
    expected = {f"{args.feature}-{c}" for c in ("module", "data", "presentation", "analytics")}
    if set(caps) - expected:
        add("TR-1", "CRITICAL", f"openspec:{args.change}", f"unexpected capabilities {sorted(set(caps) - expected)}", "coordinator")


def gate_g0c(args):
    f = args.feature
    for module, plugin in (("data", "feature.data"), ("presentation", "feature.presentation")):
        build = os.path.join(ROOT, f, module, "build.gradle.kts")
        if not os.path.exists(build):
            add("M-1", "CRITICAL", rel(build), "module build file missing", "module")
            continue
        src = read(build)
        if f"alias(coreLibs.plugins.{plugin})" not in src:
            add("M-1", "CRITICAL", rel(build), f"must apply coreLibs.plugins.{plugin}", "module")
        if f'namespace = "com.{f}.{module}"' not in src:
            add("M-2", "CRITICAL", rel(build), f'namespace must be "com.{f}.{module}"', "module")
        external = [l.strip() for l in src.splitlines()
                    if re.search(r"(implementation|api|ksp|testImplementation)\(", l) and "project(" not in l]
        if external:
            add("M-5", "CRITICAL", rel(build), f"external dependencies are not allowed: {external}", "module")
        if module == "presentation" and f'project(":{f}:data")' not in src:
            add("M-3", "CRITICAL", rel(build), f'missing implementation(project(":{f}:data"))', "module")
    settings = read(os.path.join(ROOT, "settings.gradle.kts"))
    for module in ("data", "presentation"):
        if f'":{f}:{module}"' not in settings:
            add("M-4", "CRITICAL", "settings.gradle.kts", f'missing include(":{f}:{module}")', "module")
    if f'project(":{f}:presentation")' not in read(os.path.join(ROOT, "app/build.gradle.kts")):
        add("M-4", "CRITICAL", "app/build.gradle.kts", f'missing implementation(project(":{f}:presentation"))', "module")
    gradle(args, [f":{f}:data:assembleDebug", f":{f}:presentation:assembleDebug", ":app:assembleDebug"], "M-1", "module")


def gate_g1(args):
    f, allow = args.feature, load_allowlist()
    main = kotlin_files(os.path.join(ROOT, f, "data/src/main"))
    tests = kotlin_files(os.path.join(ROOT, f, "data/src/test"))
    check_imports(main, allow["data"]["imports_allowed"], allow["data"]["forbidden_imports"], "D-1", lambda p: "data", f)
    check_imports(tests, allow["data"]["imports_allowed"] + allow["tests"]["imports_allowed"],
                  allow["tests"]["forbidden_imports"], "D-T2", lambda p: "data-test", f)
    check_task_refs(main, args.change, lambda p: "data")
    check_scenario_tests(args.change, f"{f}-data", tests, "data-test")
    check_test_weakening(args, os.path.join(f, "data/src/test"), "data-test")
    gradle(args, [f":{f}:data:testDebugUnitTest"], "D-T1", "data")


def gate_g2(args):
    f, allow = args.feature, load_allowlist()
    main = kotlin_files(os.path.join(ROOT, f, "presentation/src/main"))
    pres = allow["presentation"]
    check_imports(main, pres["imports_allowed"], pres["forbidden_imports"], "VM-1", presentation_owner, f)
    check_task_refs(main, args.change, presentation_owner)
    for path in main:
        src = read(path)
        for entry in pres.get("forbidden_symbols", []):
            for match in re.finditer(entry["pattern"], src):
                line = src.count("\n", 0, match.start()) + 1
                add(entry.get("rule", "V-1"), "CRITICAL", f"{rel(path)}:{line}", f"forbidden symbol '{match.group(0)}'", presentation_owner(path))
        for comp in ("CheckboxComponent", "ToggleComponent"):
            for match in re.finditer(comp + r"\(", src):
                call = balanced_call(src, match.end() - 1)
                if "onValueChange" not in call:
                    line = src.count("\n", 0, match.start()) + 1
                    add("V-K1", "CRITICAL", f"{rel(path)}:{line}", f"{comp} without explicit onValueChange", presentation_owner(path))
        if re.search(r"override\s+fun\s+Render\(", src) and "loadInitData()" not in src:
            add("V-N3", "CRITICAL", rel(path), "Render does not call viewModel.loadInitData() in a LaunchedEffect", "view")
    gradle(args, [f":{f}:presentation:compileDebugKotlin", ":app:assembleDebug"], "VM-1", "viewmodel")


def gate_g3(args):
    f, allow = args.feature, load_allowlist()
    tests = kotlin_files(os.path.join(ROOT, f, "presentation/src/test"))
    check_imports(tests, allow["presentation"]["imports_allowed"] + allow["data"]["imports_allowed"] + allow["tests"]["imports_allowed"],
                  allow["tests"]["forbidden_imports"], "TR-2", lambda p: "testing", f)
    check_scenario_tests(args.change, f"{f}-presentation", tests, "testing")
    check_scenario_tests(args.change, f"{f}-analytics", tests, "testing")
    check_test_weakening(args, os.path.join(f, "presentation/src/test"), "testing")
    code, data = openspec_json(["validate", args.change, "--type", "change", "--strict"])
    if code != 0:
        add("TR-3", "CRITICAL", f"openspec:{args.change}", f"openspec validate --strict failed: {json.dumps(data)[:600]}", "coordinator")
    gradle(args, [f":{f}:data:testDebugUnitTest", f":{f}:presentation:testDebugUnitTest"], "TR-2", "testing")


# ---------- shared checks ----------
def balanced_call(src, open_index):
    depth = 0
    for index in range(open_index, len(src)):
        if src[index] == "(":
            depth += 1
        elif src[index] == ")":
            depth -= 1
            if depth == 0:
                return src[open_index:index + 1]
    return src[open_index:]


def check_test_weakening(args, test_dir, owner):
    if not args.base:
        return
    code, diff, _ = run(["git", "diff", args.base, "--", test_dir])
    if code != 0:
        return
    removed = len(re.findall(r"^-\s*@Test", diff, re.M))
    added_ignore = len(re.findall(r"^\+\s*@Ignore", diff, re.M))
    if removed or added_ignore:
        add("D-T4", "CRITICAL", test_dir, f"{removed} @Test removed, {added_ignore} @Ignore added since {args.base}", owner)


def gradle(args, tasks, rule, owner):
    if not args.gradle:
        return
    code, out, err = run(["./gradlew", "--console=plain", "-q"] + tasks)
    if code != 0:
        tail = "\n".join((out + err).strip().splitlines()[-40:])
        add(rule, "CRITICAL", " ".join(tasks), f"Gradle failed:\n{tail}", owner)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--change", required=True)
    parser.add_argument("--feature", required=True)
    parser.add_argument("--gate", required=True, choices=["preflight", "G0a", "G0c", "G1", "G2", "G3"])
    parser.add_argument("--gradle", action="store_true", help="also run the gate's Gradle tasks")
    parser.add_argument("--base", help="git ref of the previous gate commit for test-weakening checks")
    args = parser.parse_args()
    {"preflight": gate_preflight, "G0a": gate_g0a, "G0c": gate_g0c, "G1": gate_g1, "G2": gate_g2, "G3": gate_g3}[args.gate](args)
    critical = [x for x in findings if x["severity"] == "CRITICAL"]
    print(json.dumps({"gate": args.gate, "change": args.change, "verdict": "FAIL" if critical else "PASS",
                      "mechanical": True, "findings": findings}, indent=2))
    sys.exit(1 if critical else 0)


if __name__ == "__main__":
    main()
