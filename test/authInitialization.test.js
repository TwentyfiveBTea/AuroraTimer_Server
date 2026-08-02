import test from 'node:test'
import assert from 'node:assert/strict'

import { createAuthInitializer } from '../src/stores/authInitialization.js'

test('keeps auth unready until a stale session has been validated and cleared', async () => {
  let ready = true
  let session = { token: 'stale-token', user: { userId: '25125082059' } }
  let finishValidation

  const initialize = createAuthInitializer({
    readSession: () => session,
    restoreSession: (savedSession) => {
      session = savedSession
    },
    validateSession: () => new Promise((resolve) => {
      finishValidation = resolve
    }),
    clearSession: () => {
      session = { token: null, user: null }
    },
    setReady: (value) => {
      ready = value
    }
  })

  const initialization = initialize()

  assert.equal(ready, false)
  assert.equal(session.user.userId, '25125082059')

  finishValidation(false)
  await initialization

  assert.equal(ready, true)
  assert.deepEqual(session, { token: null, user: null })
})

test('deduplicates concurrent auth initialization calls', async () => {
  let validationCount = 0

  const initialize = createAuthInitializer({
    readSession: () => ({ token: 'valid-token', user: { userId: '1' } }),
    restoreSession: () => {},
    validateSession: async () => {
      validationCount += 1
      return true
    },
    clearSession: () => {},
    setReady: () => {}
  })

  const first = initialize()
  const second = initialize()

  assert.strictEqual(first, second)
  await first
  assert.equal(validationCount, 1)
})
