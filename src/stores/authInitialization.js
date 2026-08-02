export function createAuthInitializer({
  readSession,
  restoreSession,
  validateSession,
  clearSession,
  setReady
}) {
  let initialization = null

  return function initialize() {
    if (initialization) {
      return initialization
    }

    setReady(false)
    initialization = (async () => {
      const session = readSession()

      if (!session.token) {
        clearSession()
        return false
      }

      restoreSession(session)
      const isValid = await validateSession()

      if (!isValid) {
        clearSession()
      }

      return isValid
    })().finally(() => {
      setReady(true)
    })

    return initialization
  }
}
