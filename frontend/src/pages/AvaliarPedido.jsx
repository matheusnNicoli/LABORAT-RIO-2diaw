import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../services/api'
import { formatarCpf, formatarData, formatarDinheiro } from '../services/formatos'

function AvaliarPedido() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [pedido, setPedido] = useState(null)
  const [cliente, setCliente] = useState(null)
  const [parecer, setParecer] = useState('')
  const [erro, setErro] = useState('')

  useEffect(() => {
    api('/pedidos/' + id)
      .then((p) => {
        setPedido(p)
        return api('/clientes/' + p.clienteId)
      })
      .then(setCliente)
      .catch((e) => setErro(e.message))
  }, [id])

  async function avaliar(aprovado) {
    setErro('')
    if (!parecer.trim()) {
      setErro('Escreva o parecer antes de avaliar')
      return
    }
    try {
      await api(`/pedidos/${id}/avaliar`, { method: 'PATCH', body: { aprovado, parecer } })
      navigate('/pedidos')
    } catch (e) {
      setErro(e.message)
    }
  }

  if (!pedido) return <p>{erro || 'Carregando...'}</p>

  const rendaTotal = cliente ? cliente.rendimentos.reduce((soma, r) => soma + r.valor, 0) : 0

  return (
    <div className="pagina-estreita">
      <h1>Avaliar pedido #{pedido.id}</h1>
      {erro && <div className="alerta erro">{erro}</div>}

      <div className="detalhe">
        <h3>Pedido</h3>
        <p><b>Automóvel:</b> {pedido.automovel.marca} {pedido.automovel.modelo} ({pedido.automovel.placa})</p>
        <p><b>Período:</b> {formatarData(pedido.dataInicio)} a {formatarData(pedido.dataFim)}</p>
        <p><b>Observação:</b> {pedido.observacao || '-'}</p>
      </div>

      {cliente && (
        <div className="detalhe">
          <h3>Cliente</h3>
          <p><b>Nome:</b> {cliente.nome} — <b>CPF:</b> {formatarCpf(cliente.cpf)} — <b>RG:</b> {cliente.rg}</p>
          <p><b>Profissão:</b> {cliente.profissao || '-'}</p>
          <p><b>Endereço:</b> {cliente.endereco}</p>
          <p><b>Rendimentos:</b></p>
          <ul>
            {cliente.rendimentos.map((r, i) => (
              <li key={i}>{r.entidadeEmpregadora}: {formatarDinheiro(r.valor)}</li>
            ))}
          </ul>
          <p><b>Renda total:</b> {formatarDinheiro(rendaTotal)}</p>
        </div>
      )}

      <label className="form">
        Parecer financeiro
        <textarea value={parecer} onChange={(e) => setParecer(e.target.value)} rows={4} />
      </label>
      <div className="acoes">
        <button className="btn btn-sucesso" onClick={() => avaliar(true)}>Aprovar</button>
        <button className="btn btn-perigo" onClick={() => avaliar(false)}>Reprovar</button>
        <button className="btn btn-secundario" onClick={() => navigate('/pedidos')}>Voltar</button>
      </div>
    </div>
  )
}

export default AvaliarPedido
