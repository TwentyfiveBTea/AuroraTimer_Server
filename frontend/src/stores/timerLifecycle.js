export function getTimerFlags(mode) {
  if (mode === 'RUNNING') {
    return {
      isRunning: true,
      isPaused: false,
      isAFK: false
    }
  }

  if (mode === 'AFK_PAUSED') {
    return {
      isRunning: true,
      isPaused: true,
      isAFK: true
    }
  }

  if (mode === 'PAUSED') {
    return {
      isRunning: true,
      isPaused: true,
      isAFK: false
    }
  }

  return {
    isRunning: false,
    isPaused: false,
    isAFK: false
  }
}

export function shouldResumeSavedSession(savedState, userId) {
  return Boolean(
    savedState &&
    userId &&
    savedState.userId === userId &&
    savedState.isRunning === true &&
    savedState.isPaused !== true
  )
}

export function shouldPersistTimerState(state) {
  return state?.isRunning === true
}

export function shouldPauseFromHeartbeat({ success, isOnline }) {
  return success === true && isOnline === false
}
