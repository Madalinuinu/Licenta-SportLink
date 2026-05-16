const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {}),
    },
    ...options,
  })

  const payload = await parseJsonSafe(response)
  if (!response.ok) {
    throw new Error(extractApiError(payload, response.status))
  }

  return payload
}

function extractApiError(payload, status) {
  if (!payload) {
    return `Server error (${status}).`
  }

  if (payload.fieldErrors && typeof payload.fieldErrors === 'object') {
    return Object.entries(payload.fieldErrors)
      .map(([field, message]) => `${field}: ${message}`)
      .join(' | ')
  }

  return payload.message || payload.error || `Server error (${status}).`
}

async function parseJsonSafe(response) {
  const text = await response.text()
  if (!text) {
    return null
  }

  try {
    return JSON.parse(text)
  } catch {
    return null
  }
}

export function registerUser(payload) {
  return request('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function loginUser(payload) {
  return request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function getActiveLobbies() {
  return request('/api/lobbies/active')
}

export function createLobby(payload) {
  return request('/api/lobbies', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function joinLobby(lobbyId, userId) {
  return request(`/api/lobbies/${lobbyId}/join`, {
    method: 'POST',
    body: JSON.stringify({ userId }),
  })
}

export function leaveLobby(lobbyId, userId) {
  return request(`/api/lobbies/${lobbyId}/leave`, {
    method: 'POST',
    body: JSON.stringify({ userId }),
  })
}
