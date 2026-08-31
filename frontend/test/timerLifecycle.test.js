import test from 'node:test'
import assert from 'node:assert/strict'

test('restoring a server session enters the running state', async () => {
  const lifecycle = await import('../src/stores/timerLifecycle.js').catch(() => null)

  assert.ok(lifecycle, 'timer lifecycle implementation must exist')
  assert.deepEqual(lifecycle.getTimerFlags('RUNNING'), {
    isRunning: true,
    isPaused: false,
    isAFK: false
  })
})

test('AFK keeps the session active while pausing time updates', async () => {
  const { getTimerFlags } = await import('../src/stores/timerLifecycle.js')

  assert.deepEqual(getTimerFlags('AFK_PAUSED'), {
    isRunning: true,
    isPaused: true,
    isAFK: true
  })
})

test('a regular pause does not mark the user as AFK', async () => {
  const { getTimerFlags } = await import('../src/stores/timerLifecycle.js')

  assert.deepEqual(getTimerFlags('PAUSED'), {
    isRunning: true,
    isPaused: true,
    isAFK: false
  })
})

test('a running session saved for the current user resumes after restart', async () => {
  const { shouldResumeSavedSession } = await import('../src/stores/timerLifecycle.js')

  assert.equal(shouldResumeSavedSession({
    userId: '25125082059',
    isRunning: true,
    isPaused: false
  }, '25125082059'), true)
})

test('a stopped status refresh does not overwrite restart intent', async () => {
  const { shouldPersistTimerState } = await import('../src/stores/timerLifecycle.js')

  assert.equal(shouldPersistTimerState({ isRunning: false }), false)
  assert.equal(shouldPersistTimerState({ isRunning: true }), true)
})

test('an offline heartbeat pauses the timer instead of skipping AFK detection', async () => {
  const { shouldPauseFromHeartbeat } = await import('../src/stores/timerLifecycle.js')

  assert.equal(shouldPauseFromHeartbeat({ success: true, isOnline: false }), true)
  assert.equal(shouldPauseFromHeartbeat({ success: true, isOnline: true }), false)
})
