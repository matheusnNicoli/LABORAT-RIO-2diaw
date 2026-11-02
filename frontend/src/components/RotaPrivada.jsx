import { Navigate } from 'react-router-dom'
import { getSessao } from '../services/auth'

function RotaPrivada({ children, perfil }) {
  const sessao = getSessao()

  if (!sessao) {
    return <Navigate to="/login" />
  }
  if (perfil && sessao.perfil !== perfil) {
    return <Navigate to="/" />
  }
  return children
}

export default RotaPrivada
