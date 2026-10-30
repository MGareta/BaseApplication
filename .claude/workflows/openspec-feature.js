export const meta = {
  name: 'openspec-feature',
  description: 'OpenSpec-driven feature delivery: plan, module, data, presentation, testing, archive (docs/orchestrator/proposal.md)',
  whenToUse: 'Deliver a new feature as :<feature>:data + :<feature>:presentation through OpenSpec. Run with stage "plan", approve, then "implement", then "archive".',
  phases: [
    { title: 'Preflight', detail: 'OpenSpec version, prerequisites P1/P2, allowlist freshness' },
    { title: 'Plan', detail: 'coordinator writes the OpenSpec change and passes G0a' },
    { title: 'Module', detail: 'module agent creates and wires the feature modules, module eval reviews them (G0c)' },
    { title: 'Data', detail: 'data agent, data test loop, data eval (G1)' },
    { title: 'Presentation', detail: 'ViewModel and view in parallel, analytics, presentation eval (G2)' },
    { title: 'Testing', detail: 'feature-integration tests, defect routing, final gate (G3)' },
    { title: 'Archive', detail: 'openspec archive after human approval (G4)' },
  ],
}

// ---------------------------------------------------------------------------
// args: {
//   feature: "orders",                         // required, lowercase module/package name
//   request: "natural-language feature request", // required for stage "plan"
//   stage: "plan" | "implement" | "archive",   // required
//   allowMissingPrerequisites: false,          // continue when preflight only reports PRE-P1/PRE-P2
//   baseRef: "HEAD"                            // git ref captured before implementation, for test-weakening checks
// }
// ---------------------------------------------------------------------------
const feature = args && args.feature
const stage = args && args.stage
if (!feature || !/^[a-z][a-z0-9]*$/.test(feature)) throw new Error('args.feature must be lowercase letters/digits, e.g. "orders"')
if (!['plan', 'implement', 'archive'].includes(stage)) throw new Error('args.stage must be "plan", "implement" or "archive"')
const id = `add-${feature}-feature`
const base = args.baseRef || 'HEAD'
const LIMITS = { evalRounds: 3, testFixPerPhase: 8, defectRounds: 3 }

const VERDICT = {
  type: 'object',
  properties: {
    verdict: { type: 'string', enum: ['PASS', 'FAIL'] },
    findings: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          rule: { type: 'string' }, severity: { type: 'string' }, location: { type: 'string' },
          evidence: { type: 'string' }, owner: { type: 'string' }, wiki: { type: 'string' }, fix_hint: { type: 'string' },
        },
        required: ['rule', 'severity', 'evidence', 'owner'],
      },
    },
    attributionRulings: {
      type: 'array',
      items: { type: 'object', properties: { test: { type: 'string' }, attribution: { type: 'string' }, reason: { type: 'string' } }, required: ['test', 'attribution'] },
    },
  },
  required: ['verdict', 'findings'],
}
const FAILURES = {
  type: 'object',
  properties: {
    allPassed: { type: 'boolean' },
    failures: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          test: { type: 'string' }, scenario: { type: 'string' }, failure: { type: 'string' },
          attribution: { type: 'string', enum: ['code', 'test', 'spec'] }, layer: { type: 'string' }, reason: { type: 'string' },
        },
        required: ['test', 'failure'],
      },
    },
  },
  required: ['allPassed', 'failures'],
}
const DONE = {
  type: 'object',
  properties: { ok: { type: 'boolean' }, summary: { type: 'string' }, specChangeRequested: { type: 'boolean' } },
  required: ['ok', 'summary'],
}

const ctx = `OpenSpec change: ${id}. Feature: ${feature}. Modules: :${feature}:data (com.${feature}.data), :${feature}:presentation (com.${feature}.presentation).
Design: docs/orchestrator/proposal.md. Run state: openspec/changes/${id}/.orchestrator/.`

function mechanical(gate, extra) {
  const flags = gate === 'preflight' || gate === 'G0a' ? '' : ' --gradle'
  const baseFlag = ['G1', 'G3'].includes(gate) ? ` --base ${base}` : ''
  return agent(
    `Run exactly: python3 -I scripts/orchestrator/eval-mechanical.py --change ${id} --feature ${feature} --gate ${gate}${flags}${baseFlag}
Return the JSON it prints (verdict and findings) unchanged. Don't fix anything. ${extra || ''}`,
    { label: `gate:${gate}`, schema: VERDICT, effort: 'low' })
}

function byOwner(findings, owner) {
  return findings.filter(f => f.owner === owner)
}

function fmt(findings) {
  return JSON.stringify(findings, null, 2)
}

async function stopForHuman(reason, details) {
  log(`STOP: ${reason}`)
  return { change: id, stage, status: 'escalated', reason, details }
}

// ---------------- Preflight ----------------
phase('Preflight')
const pre = await mechanical('preflight')
const blocking = (pre.findings || []).filter(f => f.severity === 'CRITICAL')
const prereqOnly = blocking.every(f => f.rule === 'PRE-P1' || f.rule === 'PRE-P2')
const missingPrereqs = blocking.filter(f => f.rule === 'PRE-P1' || f.rule === 'PRE-P2').map(f => f.rule)
if (blocking.length && !(prereqOnly && args.allowMissingPrerequisites)) {
  return await stopForHuman('preflight failed', blocking)
}
const prereqNote = missingPrereqs.length
  ? `Prerequisites missing: ${missingPrereqs.join(', ')}. Apply the fallbacks your agent definition describes for missing prerequisites.`
  : 'Prerequisites P1 and P2 are in place.'
log(prereqNote)

// ---------------- Plan ----------------
if (stage === 'plan') {
  phase('Plan')
  const plan = await agent(
    `${ctx}
Stage: plan. Feature request from the user:
"""${args.request || ''}"""
Create and fully plan the change following your agent definition (planning steps 1–5), including gate G0a. ${prereqNote}
Return ok=true only if G0a passes; summary = what the human must review (capabilities, requirements, BLOCKED platform dependencies, open questions).`,
    { label: 'coordinator:plan', agentType: 'feature-coordinator', schema: DONE })
  if (!plan || !plan.ok) return await stopForHuman('planning did not pass G0a', plan)
  const g0a = await mechanical('G0a')
  if (g0a.verdict !== 'PASS') return await stopForHuman('G0a failed', g0a.findings)
  return {
    change: id, stage, status: 'awaiting-plan-approval', summary: plan.summary,
    next: `Review with: openspec show ${id}  (or openspec view). If approved, create openspec/changes/${id}/.orchestrator/plan-approved, then run this workflow with stage "implement".`,
  }
}

// ---------------- Archive ----------------
if (stage === 'archive') {
  phase('Archive')
  const arc = await agent(
    `${ctx}
Stage: archive. If openspec/changes/${id}/.orchestrator/archive-approved does not exist, return ok=false. Otherwise run "openspec archive ${id} --yes",
then remove .claude/orchestrator-active, and return ok=true with the archive path.`,
    { label: 'coordinator:archive', agentType: 'feature-coordinator', schema: DONE })
  return { change: id, stage, status: arc && arc.ok ? 'archived' : 'not-archived', summary: arc && arc.summary }
}

// ---------------- Implement ----------------
const start = await agent(
  `${ctx}
Check that openspec/changes/${id}/.orchestrator/plan-approved exists and "openspec validate ${id} --strict" exits 0. If both hold, write the change id (${id}) into
.claude/orchestrator-active (this activates the guard hook) and return ok=true; otherwise return ok=false with the reason.`,
  { label: 'coordinator:start', agentType: 'feature-coordinator', schema: DONE, effort: 'low' })
if (!start || !start.ok) return await stopForHuman('plan not approved or invalid', start)

// Phase 0: module team (module agent builds, module eval reviews against openwiki/core-dependencies)
phase('Module')
await agent(`${ctx}\nImplement the [module] tasks. ${prereqNote}`, { label: 'module', agentType: 'feature-module' })
let moduleVerdict = null
for (let round = 1; round <= LIMITS.evalRounds; round++) {
  moduleVerdict = await agent(`${ctx}\nEvaluate gate G0c (iteration ${round}) with base ref ${base}. ${prereqNote}`,
    { label: `module-eval:G0c-${round}`, agentType: 'feature-module-eval', schema: VERDICT })
  if (moduleVerdict && moduleVerdict.verdict === 'PASS') break
  const findings = (moduleVerdict && moduleVerdict.findings) || []
  const forCoordinator = byOwner(findings, 'coordinator').filter(f => f.severity === 'CRITICAL')
  if (forCoordinator.length) return await stopForHuman('G0c findings need a spec or design change', forCoordinator)
  const forModule = byOwner(findings, 'module')
  if (forModule.length) await agent(`${ctx}\nFix these G0c findings in your owned paths:\n${fmt(forModule)}`, { label: `module:fix-eval-${round}`, agentType: 'feature-module' })
}
if (!moduleVerdict || moduleVerdict.verdict !== 'PASS') return await stopForHuman('G0c failed after max eval rounds', moduleVerdict)

// Phase 1: data
phase('Data')
await agent(`${ctx}\nImplement the [data] tasks. ${prereqNote}`, { label: 'data', agentType: 'feature-data' })
let testFixes = 0
let dataVerdict = null
for (let round = 1; round <= LIMITS.evalRounds; round++) {
  // test ↔ fix loop
  for (let iteration = 1; ; iteration++) {
    if (iteration > LIMITS.testFixPerPhase) return await stopForHuman('data test loop did not converge', null)
    const run = await agent(`${ctx}\nImplement or update the [data-test] tasks, run the data unit tests and report failures. ${prereqNote}`,
      { label: `data-test:r${round}.${iteration}`, agentType: 'feature-data-test', schema: FAILURES })
    if (!run || run.allPassed) break
    const spec = run.failures.filter(f => f.attribution === 'spec')
    if (spec.length) return await stopForHuman('data tests point to a spec problem', spec)
    const disputed = run.failures.filter(f => f.attribution === 'test')
    let codeFailures = run.failures.filter(f => f.attribution !== 'test' && f.attribution !== 'spec')
    if (disputed.length) {
      const ruling = await agent(`${ctx}\nRule on these disputed test attributions only (code, test or spec); don't run the full G1 review:\n${fmt(disputed)}`,
        { label: `data-eval:ruling-${round}.${iteration}`, agentType: 'feature-data-eval', schema: VERDICT })
      const rulings = (ruling && ruling.attributionRulings) || []
      const wrongTests = rulings.filter(r => r.attribution === 'test')
      if (rulings.some(r => r.attribution === 'spec')) return await stopForHuman('data eval ruled a spec problem', rulings)
      if (wrongTests.length) {
        await agent(`${ctx}\nThe data eval ruled these tests wrong. Fix only these tests (do not weaken their intent):\n${fmt(wrongTests)}`,
          { label: `data-test:fix-${round}.${iteration}`, agentType: 'feature-data-test' })
      }
      codeFailures = codeFailures.concat(disputed.filter(d => !wrongTests.some(t => t.test === d.test)))
    }
    if (codeFailures.length) {
      testFixes++
      if (testFixes > LIMITS.testFixPerPhase) return await stopForHuman('data test-fix limit reached', codeFailures)
      await agent(`${ctx}\nMake these failing tests pass by changing production code only:\n${fmt(codeFailures)}`,
        { label: `data:fix-tests-${testFixes}`, agentType: 'feature-data' })
    }
  }
  dataVerdict = await agent(`${ctx}\nEvaluate gate G1 (iteration ${round}) with base ref ${base}. ${prereqNote}`,
    { label: `data-eval:G1-${round}`, agentType: 'feature-data-eval', schema: VERDICT })
  if (dataVerdict && dataVerdict.verdict === 'PASS') break
  const findings = (dataVerdict && dataVerdict.findings) || []
  const forData = byOwner(findings, 'data')
  const forTests = byOwner(findings, 'data-test')
  if (forData.length) await agent(`${ctx}\nFix these G1 findings:\n${fmt(forData)}`, { label: `data:fix-eval-${round}`, agentType: 'feature-data' })
  if (forTests.length) await agent(`${ctx}\nFix these G1 findings:\n${fmt(forTests)}`, { label: `data-test:fix-eval-${round}`, agentType: 'feature-data-test' })
}
if (!dataVerdict || dataVerdict.verdict !== 'PASS') return await stopForHuman('G1 failed after max eval rounds', dataVerdict)

// Phase 2: presentation
phase('Presentation')
await parallel([
  () => agent(`${ctx}\nImplement the [viewmodel] tasks. ${prereqNote}`, { label: 'viewmodel', agentType: 'feature-viewmodel', phase: 'Presentation' }),
  () => agent(`${ctx}\nImplement the [view] tasks. ${prereqNote}`, { label: 'view', agentType: 'feature-view', phase: 'Presentation' }),
])
await agent(`${ctx}\nImplement the [analytics] tasks (ViewModel insert-only lease is now yours). ${prereqNote}`, { label: 'analytics', agentType: 'feature-analytics' })
let presVerdict = null
for (let round = 1; round <= LIMITS.evalRounds; round++) {
  presVerdict = await agent(`${ctx}\nEvaluate gate G2 (iteration ${round}) with base ref ${base}. ${prereqNote}`,
    { label: `presentation-eval:G2-${round}`, agentType: 'feature-presentation-eval', schema: VERDICT })
  if (presVerdict && presVerdict.verdict === 'PASS') break
  const findings = (presVerdict && presVerdict.findings) || []
  const routes = [['viewmodel', 'feature-viewmodel'], ['view', 'feature-view'], ['analytics', 'feature-analytics']]
  await parallel(routes
    .filter(([owner]) => byOwner(findings, owner).length)
    .map(([owner, type]) => () => agent(`${ctx}\nFix these G2 findings in your owned paths:\n${fmt(byOwner(findings, owner))}`,
      { label: `${owner}:fix-eval-${round}`, agentType: type, phase: 'Presentation' })))
  const unrouted = findings.filter(f => !routes.some(([owner]) => owner === f.owner) && f.severity === 'CRITICAL')
  if (unrouted.length) return await stopForHuman('G2 findings without a presentation owner', unrouted)
}
if (!presVerdict || presVerdict.verdict !== 'PASS') return await stopForHuman('G2 failed after max eval rounds', presVerdict)

// Phase 3: testing
phase('Testing')
let defects = null
for (let round = 1; round <= LIMITS.defectRounds; round++) {
  defects = await agent(`${ctx}\nImplement or update the [testing] tasks, run all feature tests and the G3 checks with base ref ${base}. Report defects. ${prereqNote}`,
    { label: `testing:r${round}`, agentType: 'feature-testing', schema: FAILURES })
  if (defects && defects.allPassed) break
  const list = (defects && defects.failures) || []
  if (list.some(f => f.layer === 'spec')) return await stopForHuman('testing found a spec problem', list.filter(f => f.layer === 'spec'))
  const routes = [['data', 'feature-data'], ['viewmodel', 'feature-viewmodel'], ['view', 'feature-view'], ['analytics', 'feature-analytics']]
  await parallel(routes
    .filter(([layer]) => list.some(f => f.layer === layer))
    .map(([layer, type]) => () => agent(`${ctx}\nThe feature-integration tests found these defects in your layer. Fix production code only:\n${fmt(list.filter(f => f.layer === layer))}`,
      { label: `${layer}:fix-defects-${round}`, agentType: type, phase: 'Testing' })))
}
if (!defects || !defects.allPassed) return await stopForHuman('feature tests still failing after max rounds', defects)
const g3 = await mechanical('G3')
if (g3.verdict !== 'PASS') return await stopForHuman('G3 failed', g3.findings)

return {
  change: id, stage, status: 'awaiting-archive-approval', prerequisitesMissing: missingPrereqs,
  next: `Review the feature, then create openspec/changes/${id}/.orchestrator/archive-approved and run this workflow with stage "archive".`,
}
