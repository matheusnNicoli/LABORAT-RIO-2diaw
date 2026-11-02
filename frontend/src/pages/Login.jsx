import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../services/api'
import { salvarSessao } from '../services/auth'

function Login() {
  const navigate = useNavigate()
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  async function entrar(e) {
    e.preventDefault()
    setErro('')
    setCarregando(true)
    try {
      const resposta = await api('/auth/login', { method: 'POST', body: { login, senha } })
      salvarSessao(resposta)
      navigate('/')
    } catch (err) {
      setErro(err.message)
    } finally {
      setCarregando(false)
    }
  }

  return (
    <div className="caixa-login">
      <h1>Entrar</h1>
      <p className="dica">Sistema de aluguel de carros</p>
      {erro && <div className="alerta erro">{erro}</div>}
      <form onSubmit={entrar} className="form">
        <label>
          Login
          <input value={login} onChange={(e) => setLogin(e.target.value)} required />
        </label>
        <label>
          Senha
          <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} required />
        </label>
        <button className="btn" disabled={carregando}>{carregando ? 'Entrando...' : 'Entrar'}</button>
      </form>
      <p>Ainda não tem conta? <Link to="/cadastro">Cadastre-se</Link></p>
    </div>
  )
}

export default Login
