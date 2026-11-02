import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../services/api'

function PedidoForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [automoveis, setAutomoveis] = useState([])
  const [form, setForm] = useState({ automovelId: '', dataInicio: '', dataFim: '', observacao: '' })
  const [erro, setErro] = useState('')

  useEffect(() => {
    api('/automoveis').then(setAutomoveis).catch((e) => setErro(e.message))
    if (id) {
      api('/pedidos/' + id)
        .then((p) => setForm({
          automovelId: p.automovel.id,
          dataInicio: p.dataInicio,
          dataFim: p.dataFim,
          observacao: p.observacao || ''
        }))
        .catch((e) => setErro(e.message))
    }
  }, [id])

  function alterar(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  async function salvar(e) {
    e.preventDefault()
    setErro('')
    const dados = { ...form, automovelId: Number(form.automovelId) }
    try {
      if (id) {
        await api('/pedidos/' + id, { method: 'PUT', body: dados })
      } else {
        await api('/pedidos', { method: 'POST', body: dados })
      }
      navigate('/pedidos')
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <div className="pagina-estreita">
      <h1>{id ? 'Modificar pedido #' + id : 'Novo pedido de aluguel'}</h1>
      {erro && <div className="alerta erro">{erro}</div>}

      <form onSubmit={salvar} className="form">
        <label>
          Automóvel
          <select name="automovelId" value={form.automovelId} onChange={alterar} required>
            <option value="">Selecione...</option>
            {automoveis.map((a) => (
              <option key={a.id} value={a.id}>
                {a.marca} {a.modelo} {a.ano} - {a.placa}
              </option>
            ))}
          </select>
        </label>
        <div className="linha">
          <label>
            Data de início
            <input type="date" name="dataInicio" value={form.dataInicio} onChange={alterar} required />
          </label>
          <label>
            Data de devolução
            <input type="date" name="dataFim" value={form.dataFim} onChange={alterar} required />
          </label>
        </div>
        <label>
          Observação
          <textarea name="observacao" value={form.observacao} onChange={alterar} rows={3} />
        </label>
        <div className="acoes">
          <button className="btn">{id ? 'Salvar' : 'Enviar pedido'}</button>
          <button type="button" className="btn btn-secundario" onClick={() => navigate('/pedidos')}>Voltar</button>
        </div>
      </form>
    </div>
  )
}

export default PedidoForm
