import { Link, NavLink, useNavigate } from 'react-router-dom'
import { getSessao, limparSessao } from '../services/auth'
import { api } from '../services/api'

function Navbar() {
  const navigate = useNavigate()
  const sessao = getSessao()

  async function sair() {
    try {
      await api('/auth/logout', { method: 'POST' })
    } catch {
    }
    limparSessao()
    navigate('/login')
  }

  return (
    <header className="navbar">
      <Link to="/" className="logo">🚗 AlugaCar</Link>

      {sessao && (
        <nav>
          <NavLink to="/" end>Início</NavLink>
          <NavLink to="/pedidos">Pedidos</NavLink>
          <NavLink to="/automoveis">Automóveis</NavLink>
          <NavLink to="/contratos">Contratos</NavLink>
          {sessao.perfil === 'AGENTE' && <NavLink to="/clientes">Clientes</NavLink>}
          {sessao.perfil === 'CLIENTE' && <NavLink to="/meus-dados">Meus dados</NavLink>}
        </nav>
      )}

      {sessao && (
        <div className="usuario">
          <span>
            {sessao.nome} <small>({sessao.perfil === 'AGENTE' ? sessao.tipoAgente : 'CLIENTE'})</small>
          </span>
          <button className="btn btn-sair" onClick={sair}>Sair</button>
        </div>
      )}
    </header>
  )
}

export default Navbar
