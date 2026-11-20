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
                          ("/view/", "view"), ("/navigation/", "view"), ("/common/navigation/", "view"), ("/di/", "view")):
        if folder in path:
            return owner
    return "view"


COMMON_DESTINATIONS = "common/navigation/src/main/java/com/common/navigation/destination"


def common_destination_dir(feature):
    """Shared-module folder that holds every destination of a feature (convention: <feature>/<Screen>ScreenNavDestination.kt)."""
    return os.path.join(ROOT, COMMON_DESTINATIONS, feature)


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
        if module == "presentation" and 'project(":common:navigation")' not in src:
            add("M-3", "CRITICAL", rel(build), 'missing implementation(project(":common:navigation")): destinations live in the shared module', "module")
    settings = read(os.path.join(ROOT, "settings.gradle.kts"))
    for module in ("data", "presentation"):
        if f'":{f}:{module}"' not in settings:
            add("M-4", "CRITICAL", "settings.gradle.kts", f'missing include(":{f}:{module}")', "module")
    if f'project(":{f}:presentation")' not in read(os.path.join(ROOT, "app/build.gradle.kts")):
        add("M-4", "CRITICAL", "app/build.gradle.kts", f'missing implementation(project(":{f}:presentation"))', "module")
    gradle(args, [f":{f}:data:assembleDebug", f":{f}:presentation:assembleDebug", ":app:assembleDebug"], "M-1", "module")


def check_data_visibility(f, main):
    manager_module = f[:1].upper() + f[1:] + "Module"
    """D-10: public API of the data module = I<Feature>Manager, model/ classes (not model/dto/) and the <Feature>Module object."""
    decl = re.compile(r"^(?P<mods>(?:\w+\s+)*?)(?P<kind>class|interface|object|typealias)\s+(?P<name>\w+)", re.M)
    for path in main:
        p = path.replace(os.sep, "/")
        src = read(path)
        is_di, is_dto = "/data/di/" in p, "/data/model/dto/" in p
        allowed = ("/data/model/" in p and not is_dto) or (re.search(r"/data/manager/I\w+Manager\.kt$", p) is not None)
        for match in decl.finditer(src):
            mods = match.group("mods").split()
            if "internal" in mods or "private" in mods or allowed or (is_di and match.group("name") == manager_module):
                continue
            line = src.count("\n", 0, match.start()) + 1
            add("D-10", "CRITICAL", f"{rel(path)}:{line}", f"{match.group('kind')} {match.group('name')} must be internal", "data")
        if is_di and re.search(rf"^(?:(?!internal|private)\w+\s+)*object\s+{manager_module}\b", src, re.M):
            for match in re.finditer(r"(@Provides|@Binds)[^\n]*\n(?:\s*@\w+[^\n]*\n)*\s*(?P<mods>(?:\w+\s+)*?)fun\s+(?P<name>\w+)", src):
                if "internal" not in match.group("mods").split() and "private" not in match.group("mods").split():
                    line = src.count("\n", 0, match.start()) + 1
                    add("D-10", "CRITICAL", f"{rel(path)}:{line}", f"@Provides function {match.group('name')} must be internal", "data")


def check_internal(files, rule, owner_of, allow_test_classes=False):
    """VIS-1 / TR-6: every top-level declaration is internal (or private).
    In test sources a class holding @Test and abstract base classes may stay public (JUnit needs it)."""
    decl = re.compile(r"^(?P<mods>(?:[a-z]+\s+)*?)(?P<kind>class|interface|object|typealias|fun|val|var)\s+(?P<name>[\w<>.]+)", re.M)
    for path in files:
        src = read(path)
        for match in decl.finditer(src):
            mods = match.group("mods").split()
            if "internal" in mods or "private" in mods:
                continue
            if allow_test_classes and match.group("kind") == "class" and ("abstract" in mods or "@Test" in src):
                continue
            line = src.count("\n", 0, match.start()) + 1
            add(rule, "CRITICAL", f"{rel(path)}:{line}", f"{match.group('kind')} {match.group('name')} must be internal", owner_of(path))


def gate_g1(args):
    f, allow = args.feature, load_allowlist()
    main = kotlin_files(os.path.join(ROOT, f, "data/src/main"))
    tests = kotlin_files(os.path.join(ROOT, f, "data/src/test"))
    check_imports(main, allow["data"]["imports_allowed"], allow["data"]["forbidden_imports"], "D-1", lambda p: "data", f)
    check_imports(tests, allow["data"]["imports_allowed"] + allow["tests"]["imports_allowed"],
                  allow["tests"]["forbidden_imports"], "D-T2", lambda p: "data-test", f)
    check_task_refs(main, args.change, lambda p: "data")
    check_data_visibility(f, main)
    check_internal(tests, "TR-6", lambda p: "data-test", allow_test_classes=True)
    check_scenario_tests(args.change, f"{f}-data", tests, "data-test")
    check_test_weakening(args, os.path.join(f, "data/src/test"), "data-test")
    gradle(args, [f":{f}:data:testDebugUnitTest"], "D-T1", "data")


def check_presentation_structure(f, main):
    """S-1: src/main layout of docs/orchestrator/proposal.md section 6.1.1 (mirrors login/presentation)."""
    base = os.path.join(ROOT, f, "presentation/src/main/java/com", f, "presentation")
    cap = f[:1].upper() + f[1:]
    owners = {"viewModel": "viewmodel", "model": "view", "navigation": "view", "view": "view", "di": "view", "analytics": "analytics"}
    exact = {  # directory -> exact file names; (suffix tuples for the rest) -> (suffixes, owner)
        "viewModel": ({f"{cap}Contract.kt", f"{cap}ViewModel.kt"}, "viewmodel"),
        "di": ({f"{cap}NavigationModule.kt"}, "view"),
        "analytics": ({f"{cap}Analytics.kt"}, "analytics"),
    }
    suffixes = {
        "navigation": (("Entry.kt", f"{cap}NavGraph.kt"), "view"),
        "view": (("Screen.kt",), "view"),
        "analytics/events": (("Event.kt",), "analytics"),
        "analytics/views": (("View.kt",), "analytics"),
    }
    for path in main:
        sub = os.path.relpath(os.path.dirname(path), base).replace(os.sep, "/")
        name = os.path.basename(path)
        top = sub.split("/")[0]
        if sub == "." or top not in owners:
            add("S-1", "CRITICAL", rel(path), "file at the package root or in a directory outside viewModel/view/navigation/model/di/analytics", "view")
        elif sub in exact and name not in exact[sub][0]:
            add("S-1", "CRITICAL", rel(path), f"{sub}/ may only contain {', '.join(sorted(exact[sub][0]))}", exact[sub][1])
        elif sub in suffixes and not name.endswith(suffixes[sub][0]):
            add("S-1", "CRITICAL", rel(path), f"files in {sub}/ must end with {' or '.join(suffixes[sub][0])}", suffixes[sub][1])
        elif top == "view" and sub != "view" and not sub.startswith("view/components"):
            add("S-1", "CRITICAL", rel(path), "view/ may only contain *Screen.kt in its root and extra composables in view/components/", "view")
        elif top == "model" and not (sub == "model/ui" or sub.startswith("model/ui/")):
            add("S-1", "CRITICAL", rel(path), "model/ may only contain ui/ (destinations live in :common:navigation)", "view")
        elif top == "analytics" and sub not in ("analytics", "analytics/events", "analytics/views"):
            add("S-1", "CRITICAL", rel(path), "analytics/ may only contain events/ and views/", "analytics")

    def names(folder, suffix):
        d = os.path.join(base, folder)
        return {n[:-len(suffix)] for n in os.listdir(d) if n.endswith(suffix)} if os.path.isdir(d) else set()

    for folder, fname in (("viewModel", f"{cap}Contract.kt"), ("viewModel", f"{cap}ViewModel.kt"), ("navigation", f"{cap}NavGraph.kt"),
                          ("di", f"{cap}NavigationModule.kt"), ("analytics", f"{cap}Analytics.kt")):
        if not os.path.isfile(os.path.join(base, folder, fname)):
            add("S-1", "CRITICAL", rel(os.path.join(base, folder)), f"missing {fname}", owners[folder])
    entries, screens = names("navigation", "Entry.kt"), names("view", "Screen.kt")
    dest_dir = common_destination_dir(f)
    destinations = {n[:-len("ScreenNavDestination.kt")] for n in os.listdir(dest_dir) if n.endswith("ScreenNavDestination.kt")} if os.path.isdir(dest_dir) else set()
    ui_models = names("model/ui", "UiModel.kt")
    if not entries:
        add("S-1", "CRITICAL", rel(os.path.join(base, "navigation")), "missing at least one <Screen>Entry.kt", "view")
    for label, found in (("view/<Screen>Screen.kt", screens), (f"{COMMON_DESTINATIONS}/{f}/<Screen>ScreenNavDestination.kt", destinations), ("model/ui/<Screen>UiModel.kt", ui_models)):
        if found != entries:
            add("S-1", "CRITICAL", rel(base), f"{label} must match the navigation entries one to one (entries={sorted(entries)}, found={sorted(found)})", "view")
    contract = os.path.join(base, "viewModel", f"{cap}Contract.kt")
    if os.path.isfile(contract):
        src = read(contract)
        for screen in sorted(ui_models):
            if f"{screen}UiModel" not in src:
                add("S-1", "CRITICAL", rel(contract), f"UiState must keep a {screen}UiModel instance", "viewmodel")


def check_common_destinations(f, allow, change):
    """CN-1: destinations of the feature live in :common:navigation as public @Serializable <Screen>ScreenNavDestination classes."""
    folder = common_destination_dir(f)
    files = kotlin_files(folder)
    if not files:
        add("CN-1", "CRITICAL", rel(folder), f"missing destinations: create {COMMON_DESTINATIONS}/{f}/<Screen>ScreenNavDestination.kt", "view")
        return
    owner = lambda p: "view"
    check_imports(files, allow["common_navigation"]["imports_allowed"], [], "CN-1", owner, f)
    check_task_refs(files, change, owner)
    decl = re.compile(r"^(?P<mods>(?:[a-z]+\s+)*?)(?P<kind>class|interface|object|typealias|fun|val|var)\s+(?P<name>\w+)", re.M)
    for path in files:
        name, src = os.path.basename(path), read(path)
        if not name.endswith("ScreenNavDestination.kt"):
            add("CN-1", "CRITICAL", rel(path), "files in destination/<feature>/ must be named <Screen>ScreenNavDestination.kt", "view")
        if not re.search(rf"^package com\.common\.navigation\.destination\.{re.escape(f)}\s*$", src, re.M):
            add("CN-1", "CRITICAL", rel(path), f"package must be com.common.navigation.destination.{f}", "view")
        tops = list(decl.finditer(src))
        if len(tops) != 1 or tops[0].group("kind") not in ("class", "object") or tops[0].group("name") != name[:-len(".kt")]:
            add("CN-1", "CRITICAL", rel(path), "exactly one top-level class/object, named like the file", "view")
        for match in tops:
            if {"internal", "private"} & set(match.group("mods").split()):
                line = src.count("\n", 0, match.start()) + 1
                add("CN-1", "CRITICAL", f"{rel(path)}:{line}", f"{match.group('name')} must be public: other modules navigate to it", "view")
        if "@Serializable" not in src or not re.search(r":\s*INavigationDestination\(\)", src):
            add("CN-1", "CRITICAL", rel(path), "must be a @Serializable class/object extending INavigationDestination()", "view")


def check_test_structure(f, tests):
    """S-2: one <Screen>FeatureTest.kt per navigation entry, <Feature>NavigationRegistrationTest.kt, di/*ModuleTest.kt only."""
    pres = os.path.join(ROOT, f, "presentation/src")
    base = os.path.join(pres, "test/java/com", f, "presentation")
    nav = os.path.join(pres, "main/java/com", f, "presentation/navigation")
    cap = f[:1].upper() + f[1:]
    screens = {n[:-len("Entry.kt")] for n in os.listdir(nav) if n.endswith("Entry.kt")} if os.path.isdir(nav) else set()
    found = []
    for path in tests:
        sub = os.path.relpath(os.path.dirname(path), base).replace(os.sep, "/")
        name = os.path.basename(path)
        if sub == "." and name.endswith("FeatureTest.kt"):
            found.append(name[:-len("FeatureTest.kt")])
        elif not ((sub == "." and name == f"{cap}NavigationRegistrationTest.kt") or (sub == "di" and name.endswith("ModuleTest.kt"))):
            add("S-2", "CRITICAL", rel(path), "presentation tests: only <Screen>FeatureTest.kt and <Feature>NavigationRegistrationTest.kt at the root and di/*ModuleTest.kt", "testing")
    if sorted(found) != sorted(screens):
        add("S-2", "CRITICAL", rel(base), f"exactly one <Screen>FeatureTest.kt per navigation entry is required (screens={sorted(screens)}, tests={sorted(found)})", "testing")
    if not any(os.path.basename(p) == f"{cap}NavigationRegistrationTest.kt" for p in tests):
        add("S-2", "CRITICAL", rel(base), f"missing {cap}NavigationRegistrationTest.kt", "testing")


def check_analytics_scaffold(f, main):
    """AN-5/AN-6: analytics/<Feature>Analytics.kt + events/ + views/, ViewModels only use <Feature>Analytics."""
    base = os.path.join(ROOT, f, "presentation/src/main/java/com", f, "presentation/analytics")
    facade = os.path.join(base, f[:1].upper() + f[1:] + "Analytics.kt")
    if not os.path.isfile(facade):
        add("AN-5", "CRITICAL", rel(base), f"missing analytics facade {rel(facade)}", "analytics")
    for path in kotlin_files(base):
        if path != facade and os.path.basename(os.path.dirname(path)) not in ("events", "views"):
            add("AN-5", "CRITICAL", rel(path), "analytics classes must live in analytics/events/ or analytics/views/", "analytics")
        elif path != facade:
            src, folder = read(path), os.path.basename(os.path.dirname(path))
            wanted = "BaseFirebaseTrackEvent" if folder == "events" else "BaseFirebaseTrackView"
            if wanted not in src:
                add("AN-5", "CRITICAL", rel(path), f"classes in analytics/{folder}/ must extend {wanted}", "analytics")
    for path in main:
        src = read(path)
        if "/analytics/" not in path:
            for match in re.finditer(r"\b(\w*ApiService|\w*NetworkModule|\w+ManagerImpl)\b", src):
                line = src.count("\n", 0, match.start()) + 1
                add("L-1", "CRITICAL", f"{rel(path)}:{line}", f"'{match.group(0)}': presentation must reach data only through I<Feature>Manager", presentation_owner(path))
            if "/viewModel/" not in path and re.search(r"\b(IAnalyticsManager|AnalyticsFramework)\b", src):
                add("L-1", "CRITICAL", rel(path), "only analytics/ classes may use IAnalyticsManager", presentation_owner(path))
        if "/viewModel/" in path:
            for symbol in ("IAnalyticsManager", "AnalyticsFramework", "BaseFirebaseTrack", "presentation.analytics.events", "presentation.analytics.views"):
                if symbol in src:
                    add("AN-6", "CRITICAL", rel(path), f"ViewModel must use only {f[:1].upper() + f[1:]}Analytics, found '{symbol}'", "analytics")


def check_form_placement(main):
    """VM-6/V-F1: field states live in model/ui/<Screen>UiModel; the view builds FormState inside remember."""
    for path in main:
        src = read(path)
        if "/viewModel/" in path:
            for match in re.finditer(r"\b(?:\w+FieldState|FormState)\s*(?:<[^(]*>)?\(", src):
                line = src.count("\n", 0, match.start()) + 1
                add("VM-6", "CRITICAL", f"{rel(path)}:{line}", f"'{match.group(0)}' belongs in a model/ui/<Screen>UiModel (fields) or the view (FormState)", "viewmodel")
        if "/view/" in path or "/navigation/" in path:
            for match in re.finditer(r"\bFormState\s*(?:<[^(]*>)?\(", src):
                if "remember" not in src[max(0, match.start() - 160):match.start()]:
                    line = src.count("\n", 0, match.start()) + 1
                    add("V-F1", "CRITICAL", f"{rel(path)}:{line}", "FormState must be created inside remember(<fields>) { ... }", "view")


def gate_g2(args):
    f, allow = args.feature, load_allowlist()
    main = kotlin_files(os.path.join(ROOT, f, "presentation/src/main"))
    pres = allow["presentation"]
    check_imports(main, pres["imports_allowed"], pres["forbidden_imports"], "VM-1", presentation_owner, f)
    check_task_refs(main, args.change, presentation_owner)
    check_internal(main, "VIS-1", presentation_owner)
    check_presentation_structure(f, main)
    check_common_destinations(f, allow, args.change)
    check_analytics_scaffold(f, main)
    check_form_placement(main)
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
    gradle(args, [":common:navigation:compileDebugKotlin", f":{f}:presentation:compileDebugKotlin", ":app:assembleDebug"], "VM-1", "viewmodel")


def gate_g3(args):
    f, allow = args.feature, load_allowlist()
    tests = kotlin_files(os.path.join(ROOT, f, "presentation/src/test"))
    check_imports(tests, allow["presentation"]["imports_allowed"] + allow["data"]["imports_allowed"] + allow["tests"]["imports_allowed"],
                  allow["tests"]["forbidden_imports"], "TR-2", lambda p: "testing", f)
    check_test_structure(f, tests)
    check_internal(tests, "TR-6", lambda p: "testing", allow_test_classes=True)
    for path in tests:
        for match in re.finditer(r"@(UninstallModules|BindValue)\b", read(path)):
            line = read(path).count("\n", 0, match.start()) + 1
            add("TR-4", "CRITICAL", f"{rel(path)}:{line}", f"'{match.group(0)}' is not allowed; replace bindings with a @TestInstallIn module in presentation/di/", "testing")
    for path in tests:
        for match in re.finditer(rf"\b(\w*ApiService|\w*NetworkModule|\w+ManagerImpl)\b|{f}\.data\.model\.dto\.", read(path)):
            line = read(path).count("\n", 0, match.start()) + 1
            add("TR-5", "CRITICAL", f"{rel(path)}:{line}", f"'{match.group(0)}': presentation tests must only mock the I<Feature>Manager surface", "testing")
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
