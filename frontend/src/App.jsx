import { useEffect, useMemo, useState } from 'react'
import './App.css'
import {
  createLobby,
  getActiveLobbies,
  joinLobby,
  leaveLobby,
  loginUser,
  registerUser,
} from './sportlinkApi.js'

function App() {
  const [user, setUser] = useState(loadPersistedUser)
  const [activeLobbies, setActiveLobbies] = useState([])
  const [isLoadingLobbies, setIsLoadingLobbies] = useState(false)
  const [globalError, setGlobalError] = useState('')
  const [successMessage, setSuccessMessage] = useState('')

  const [registerForm, setRegisterForm] = useState({
    name: '',
    username: '',
    password: '',
  })
  const [loginForm, setLoginForm] = useState({
    username: '',
    password: '',
  })
  const [createLobbyForm, setCreateLobbyForm] = useState({
    title: '',
    sport: '',
    location: '',
    dateTime: '',
    maxPlayers: 10,
  })

  useEffect(() => {
    refreshLobbies()
  }, [])

  const canCreateLobby = useMemo(() => Boolean(user), [user])

  async function refreshLobbies() {
    setIsLoadingLobbies(true)
    setGlobalError('')
    try {
      const response = await getActiveLobbies()
      setActiveLobbies(response)
    } catch (error) {
      setGlobalError(error.message)
    } finally {
      setIsLoadingLobbies(false)
    }
  }

  function persistUser(nextUser) {
    setUser(nextUser)
    localStorage.setItem('sportlink_user', JSON.stringify(nextUser))
  }

  function clearUser() {
    setUser(null)
    localStorage.removeItem('sportlink_user')
  }

  function updateForm(setter, field, value) {
    setter((current) => ({ ...current, [field]: value }))
  }

  async function handleRegister(event) {
    event.preventDefault()
    setGlobalError('')
    setSuccessMessage('')
    try {
      const response = await registerUser(registerForm)
      persistUser(response)
      setSuccessMessage(`Cont creat cu succes pentru ${response.username}.`)
      setRegisterForm({ name: '', username: '', password: '' })
      await refreshLobbies()
    } catch (error) {
      setGlobalError(error.message)
    }
  }

  async function handleLogin(event) {
    event.preventDefault()
    setGlobalError('')
    setSuccessMessage('')
    try {
      const response = await loginUser(loginForm)
      persistUser(response)
      setSuccessMessage(`Autentificat ca ${response.username}.`)
      setLoginForm({ username: '', password: '' })
      await refreshLobbies()
    } catch (error) {
      setGlobalError(error.message)
    }
  }

  async function handleCreateLobby(event) {
    event.preventDefault()
    if (!user) {
      setGlobalError('Trebuie sa fii autentificat ca sa creezi un lobby.')
      return
    }

    setGlobalError('')
    setSuccessMessage('')
    try {
      await createLobby({
        creatorId: user.userId,
        title: createLobbyForm.title,
        sport: createLobbyForm.sport,
        location: createLobbyForm.location,
        dateTime: formatForBackendDateTime(createLobbyForm.dateTime),
        maxPlayers: Number(createLobbyForm.maxPlayers),
      })
      setSuccessMessage('Lobby creat cu succes.')
      setCreateLobbyForm({
        title: '',
        sport: '',
        location: '',
        dateTime: '',
        maxPlayers: 10,
      })
      await refreshLobbies()
    } catch (error) {
      setGlobalError(error.message)
    }
  }

  async function handleJoin(lobbyId) {
    if (!user) {
      setGlobalError('Trebuie sa fii autentificat ca sa intri intr-un lobby.')
      return
    }

    setGlobalError('')
    setSuccessMessage('')
    try {
      await joinLobby(lobbyId, user.userId)
      setSuccessMessage('Ai intrat in lobby.')
      await refreshLobbies()
    } catch (error) {
      setGlobalError(error.message)
    }
  }

  async function handleLeave(lobbyId) {
    if (!user) {
      setGlobalError('Trebuie sa fii autentificat ca sa iesi dintr-un lobby.')
      return
    }

    setGlobalError('')
    setSuccessMessage('')
    try {
      await leaveLobby(lobbyId, user.userId)
      setSuccessMessage('Ai iesit din lobby.')
      await refreshLobbies()
    } catch (error) {
      setGlobalError(error.message)
    }
  }

  return (
    <main className="app">
      <header className="app-header">
        <h1>SportLink MVP</h1>
        <p>Register, login, creeaza lobby-uri si inscrie-te la meciuri.</p>
      </header>

      {globalError && <p className="message error">{globalError}</p>}
      {successMessage && <p className="message success">{successMessage}</p>}

      <section className="grid">
        <div className="card">
          <h2>Register</h2>
          <form onSubmit={handleRegister} className="form">
            <input
              type="text"
              placeholder="Nume"
              value={registerForm.name}
              onChange={(event) => updateForm(setRegisterForm, 'name', event.target.value)}
              required
            />
            <input
              type="text"
              placeholder="Username"
              value={registerForm.username}
              onChange={(event) => updateForm(setRegisterForm, 'username', event.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Parola"
              value={registerForm.password}
              onChange={(event) => updateForm(setRegisterForm, 'password', event.target.value)}
              required
            />
            <button type="submit">Create account</button>
          </form>
        </div>

        <div className="card">
          <h2>Login</h2>
          <form onSubmit={handleLogin} className="form">
            <input
              type="text"
              placeholder="Username"
              value={loginForm.username}
              onChange={(event) => updateForm(setLoginForm, 'username', event.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Parola"
              value={loginForm.password}
              onChange={(event) => updateForm(setLoginForm, 'password', event.target.value)}
              required
            />
            <button type="submit">Login</button>
          </form>
        </div>

        <div className="card">
          <h2>User curent</h2>
          {user ? (
            <div>
              <p>
                <strong>{user.name}</strong> ({user.username})
              </p>
              <p>ID: {user.userId}</p>
              <button type="button" onClick={clearUser}>
                Logout local
              </button>
            </div>
          ) : (
            <p>Niciun user autentificat.</p>
          )}
        </div>
      </section>

      <section className="card">
        <h2>Create lobby</h2>
        <form onSubmit={handleCreateLobby} className="form form-inline">
          <input
            type="text"
            placeholder="Titlu"
            value={createLobbyForm.title}
            onChange={(event) => updateForm(setCreateLobbyForm, 'title', event.target.value)}
            required
            disabled={!canCreateLobby}
          />
          <input
            type="text"
            placeholder="Sport"
            value={createLobbyForm.sport}
            onChange={(event) => updateForm(setCreateLobbyForm, 'sport', event.target.value)}
            required
            disabled={!canCreateLobby}
          />
          <input
            type="text"
            placeholder="Locatie"
            value={createLobbyForm.location}
            onChange={(event) => updateForm(setCreateLobbyForm, 'location', event.target.value)}
            required
            disabled={!canCreateLobby}
          />
          <input
            type="datetime-local"
            value={createLobbyForm.dateTime}
            onChange={(event) => updateForm(setCreateLobbyForm, 'dateTime', event.target.value)}
            required
            disabled={!canCreateLobby}
          />
          <input
            type="number"
            min="1"
            value={createLobbyForm.maxPlayers}
            onChange={(event) => updateForm(setCreateLobbyForm, 'maxPlayers', event.target.value)}
            required
            disabled={!canCreateLobby}
          />
          <button type="submit" disabled={!canCreateLobby}>
            Create
          </button>
        </form>
      </section>

      <section className="card">
        <div className="section-header">
          <h2>Active lobbies</h2>
          <button type="button" onClick={refreshLobbies} disabled={isLoadingLobbies}>
            {isLoadingLobbies ? 'Refreshing...' : 'Refresh'}
          </button>
        </div>
        {activeLobbies.length === 0 && <p>Nu exista lobby-uri active momentan.</p>}
        <ul className="lobby-list">
          {activeLobbies.map((lobby) => {
            const isParticipant = user
              ? lobby.participantIds.includes(user.userId)
              : false

            return (
              <li key={lobby.id} className="lobby-item">
                <div>
                  <h3>{lobby.title}</h3>
                  <p>
                    {lobby.sport} - {lobby.location}
                  </p>
                  <p>
                    Start: {new Date(lobby.dateTime).toLocaleString('ro-RO')}
                  </p>
                  <p>
                    Participanti: {lobby.participantCount}/{lobby.maxPlayers}
                  </p>
                </div>
                <div className="lobby-actions">
                  <button
                    type="button"
                    onClick={() => handleJoin(lobby.id)}
                    disabled={!user || isParticipant || lobby.availableSpots < 1}
                  >
                    Join
                  </button>
                  <button
                    type="button"
                    onClick={() => handleLeave(lobby.id)}
                    disabled={!user || !isParticipant}
                  >
                    Leave
                  </button>
                </div>
              </li>
            )
          })}
        </ul>
      </section>
    </main>
  )
}

function formatForBackendDateTime(dateTimeValue) {
  if (!dateTimeValue) {
    return ''
  }

  return dateTimeValue.length === 16 ? `${dateTimeValue}:00` : dateTimeValue
}

function loadPersistedUser() {
  const persisted = localStorage.getItem('sportlink_user')
  if (!persisted) {
    return null
  }

  try {
    return JSON.parse(persisted)
  } catch {
    localStorage.removeItem('sportlink_user')
    return null
  }
}

export default App
