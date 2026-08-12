import test from 'node:test'
import assert from 'node:assert/strict'
import fs from 'node:fs'

test('does not mount an empty global toast on startup', () => {
  const appSource = fs.readFileSync(new URL('../src/App.vue', import.meta.url), 'utf8')

  assert.doesNotMatch(appSource, /<Toast\s*\/>/)
})
