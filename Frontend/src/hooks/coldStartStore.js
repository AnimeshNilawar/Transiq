const listeners = new Set()
let state = { phase: 'idle', elapsed: 0, requestId: null }

function notify() {
  listeners.forEach((fn) => fn(state))
}

export const coldStartStore = {
  startRequest(id) {
    const startTime = Date.now()
    state = { phase: 'loading', elapsed: 0, requestId: id }
    notify()

    const timer = setInterval(() => {
      const elapsed = Date.now() - startTime
      let phase = 'loading'
      if (elapsed > 15000) phase = 'cold-start'
      else if (elapsed > 3000) phase = 'stalling'
      state = { phase, elapsed, requestId: id }
      notify()
    }, 500)

    return () => {
      clearInterval(timer)
      state = { phase: 'idle', elapsed: 0, requestId: null }
      notify()
    }
  },

  endRequest() {
    state = { phase: 'idle', elapsed: 0, requestId: null }
    notify()
  },

  subscribe(fn) {
    listeners.add(fn)
    return () => listeners.delete(fn)
  },

  getState() {
    return state
  },
}
