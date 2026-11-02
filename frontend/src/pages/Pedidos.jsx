import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import StatusBadge from '../components/StatusBadge'
import { api } from '../services/api'
import { getSessao } from '../services/auth'
import { formatarData } from '../services/formatos'

function Pedidos() {
  const sessao = getSessao()
  const agente = sessao.perfil === 'AGENTE'
  const [pedidos, setPedidos] = useState([])
  const [filtro, setFiltro] = useState('')
  const [erro, setErro] = useState('')

  function carregar() {
    api('/pedidos').then(setPedidos).catch((e) => setErro(e.message))
  }

  useEffect(carregar, [])

  async function cancelar(id) {
    if (!confirm('Cancelar este pedido?')) return
    try {
      await api(`/pedidos/${id}/cancelar`, { method: 'PATCH' })
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  const lista = filtro ? pedidos.filter((p) => p.status === filtro) : pedidos

  return (
    <div>
      <div className="titulo-com-botao">
        <h1>{agente ? 'Pedidos de aluguel' : 'Meus pedidos'}</h1>
        {!agente && <Link className="btn" to="/pedidos/novo">+ Novo pedido</Link>}
      </div>

      {erro && <div className="alerta erro">{erro}</div>}

      <label className="filtro">
        Filtrar por status:{' '}
        <select value={filtro} onChange={(e) => setFiltro(e.target.value)}>
          <option value="">Todos</option>
          <option value="PENDENTE">Pendente</option>
          <option value="APROVADO">Aprovado</option>
          <option value="REPROVADO">Reprovado</option>
          <option value="CANCELADO">Cancelado</option>
          <option value="CONTRATADO">Contratado</option>
        </select>
      </label>

      {lista.length === 0 ? (
        <p>Nenhum pedido encontrado.</p>
      ) : (
        <table className="tabela">
          <thead>
            <tr>
              <th>#</th>
              {agente && <th>Cliente</th>}
              <th>Automóvel</th>
              <th>Período</th>
              <th>Status</th>
              <th>Parecer</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {lista.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td>
                {agente && <td>{p.nomeCliente}</td>}
                <td>{p.automovel.marca} {p.automovel.modelo} <small>({p.automovel.placa})</small></td>
                <td>{formatarData(p.dataInicio)} a {formatarData(p.dataFim)}</td>
                <td><StatusBadge status={p.status} /></td>
                <td>{p.parecer ? <span title={'por ' + p.nomeAgente}>{p.parecer}</span> : '-'}</td>
                <td className="acoes-tabela">
                  {!agente && p.status === 'PENDENTE' && (
                    <>
                      <Link className="btn btn-pequeno btn-secundario" to={`/pedidos/${p.id}/editar`}>Editar</Link>
                      <button className="btn btn-pequeno btn-perigo" onClick={() => cancelar(p.id)}>Cancelar</button>
                    </>
                  )}
                  {agente && p.status === 'PENDENTE' && (
                    <Link className="btn btn-pequeno" to={`/pedidos/${p.id}/avaliar`}>Avaliar</Link>
                  )}
                  {agente && (p.status === 'PENDENTE' || p.status === 'APROVADO') && (
                    <Link className="btn btn-pequeno btn-secundario" to={`/pedidos/${p.id}/editar`}>Modificar</Link>
                  )}
                  {agente && p.status === 'APROVADO' && (
                    <Link className="btn btn-pequeno btn-sucesso" to={`/pedidos/${p.id}/contrato`}>Gerar contrato</Link>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default Pedidos
