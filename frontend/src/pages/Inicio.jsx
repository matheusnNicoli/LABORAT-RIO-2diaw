import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../services/api'
import { getSessao } from '../services/auth'

function Inicio() {
  const sessao = getSessao()
  const [pedidos, setPedidos] = useState([])

  useEffect(() => {
    api('/pedidos').then((d) => setPedidos(d || [])).catch(() => {})
  }, [])

  function contar(status) {
    return pedidos.filter((p) => p.status === status).length
  }

  return (
    <div>
      <h1>Olá, {sessao.nome}!</h1>
      {sessao.perfil === 'CLIENTE' ? (
        <p>Aqui você pode fazer pedidos de aluguel e acompanhar o status deles.</p>
      ) : (
        <p>Você está logado como agente ({sessao.tipoAgente}). Analise os pedidos pendentes e gere os contratos.</p>
      )}

      <div className="cards">
        <div className="card"><strong>{contar('PENDENTE')}</strong><span>Pendentes</span></div>
        <div className="card"><strong>{contar('APROVADO')}</strong><span>Aprovados</span></div>
        <div className="card"><strong>{contar('CONTRATADO')}</strong><span>Contratados</span></div>
        <div className="card"><strong>{contar('REPROVADO')}</strong><span>Reprovados</span></div>
      </div>

      <div className="acoes">
        {sessao.perfil === 'CLIENTE' && <Link className="btn" to="/pedidos/novo">Novo pedido de aluguel</Link>}
        <Link className="btn btn-secundario" to="/pedidos">Ver pedidos</Link>
      </div>
    </div>
  )
}

export default Inicio
