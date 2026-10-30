#!/usr/bin/env bash
# PreToolUse guard for orchestrated feature runs (docs/orchestrator/proposal.md §10.4).
# Active only while .claude/orchestrator-active exists (it contains the OpenSpec change id).
# Exit 2 blocks the tool call and shows stderr to the agent; exit 0 allows it.
set -euo pipefail

root="${CLAUDE_PROJECT_DIR:-$(pwd)}"
marker="$root/.claude/orchestrator-active"
[ -f "$marker" ] || exit 0

change_id="$(tr -d '[:space:]' < "$marker")"
input="$(cat)"
tool="$(printf '%s' "$input" | jq -r '.tool_name // empty')"

block() {
  echo "orchestrator guard: $1 (run: $change_id). If this change is required, write openspec/changes/$change_id/.orchestrator/requests/core-change-<n>.md and stop." >&2
  exit 2
}

case "$tool" in
  Edit|Write|MultiEdit|NotebookEdit)
    path="$(printf '%s' "$input" | jq -r '.tool_input.file_path // .tool_input.notebook_path // empty')"
    [ -n "$path" ] || exit 0
    case "$path" in /*) rel="${path#"$root"/}" ;; *) rel="$path" ;; esac
    case "$rel" in
      core/*)                      block "core modules and core/dependencies are read-only" ;;
      openwiki/*)                  block "openwiki/ is generated and read-only" ;;
      openspec/specs/*)            block "openspec/specs/ is only changed by 'openspec archive'" ;;
      openspec/config.yaml)        block "openspec/config.yaml is maintained by humans" ;;
      gradle/*|build.gradle.kts|gradle.properties) block "root build configuration is read-only" ;;
      .claude/*|docs/orchestrator/*|scripts/orchestrator/*) block "orchestrator configuration is read-only during a run" ;;
      demo/*)                      block "demo modules are not part of feature runs" ;;
    esac
    ;;
  Bash)
    cmd="$(printf '%s' "$input" | jq -r '.tool_input.command // empty')"
    if printf '%s' "$cmd" | grep -Eq '(^|[;&|[:space:]])openspec[[:space:]]+archive([[:space:]]|$)'; then
      [ -f "$root/openspec/changes/$change_id/.orchestrator/archive-approved" ] \
        || block "openspec archive needs human approval (.orchestrator/archive-approved missing)"
    fi
    if printf '%s' "$cmd" | grep -Eq 'git[[:space:]]+push'; then
      block "git push is not allowed during a run"
    fi
    if printf '%s' "$cmd" | grep -Eq '(sed[[:space:]]+-i|>[[:space:]]*|tee[[:space:]]+)[^|;&]*(core/|openwiki/|openspec/specs/)'; then
      block "shell writes to read-only zones are not allowed"
    fi
    ;;
esac
exit 0
