import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../services/api'
import { getSessao } from '../services/auth'

function GerarContrato() {
  const { id } = useParams()
  const navigate = useNavigate()
  const sessao = getSessao()
  const [pedido, setPedido] = useState(null)
  const [comCredito, setComCredito] = useState(false)
  const [form, setForm] = useState({
    tipoPropriedade: 'CLIENTE', valor: '', valorCredito: '', numeroParcelas: '', taxaJuros: ''
  })
  const [erro, setErro] = useState('')

  useEffect(() => {
    api('/pedidos/' + id).then(setPedido).catch((e) => setErro(e.message))
  }, [id])

  function alterar(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  async function salvar(e) {
    e.preventDefault()
    setErro('')
    const dados = {
      tipoPropriedade: form.tipoPropriedade,
      valor: Number(form.valor)
    }
    if (comCredito) {
      dados.bancoId = sessao.id
      dados.valorCredito = Number(form.valorCredito)
      dados.numeroParcelas = form.numeroParcelas ? Number(form.numeroParcelas) : null
      dados.taxaJuros = form.taxaJuros ? Number(form.taxaJuros) : null
    }
    try {
      await api(`/pedidos/${id}/contrato`, { method: 'POST', body: dados })
      navigate('/contratos')
    } catch (err) {
      setErro(err.message)
    }
  }

  if (!pedido) return <p>{erro || 'Carregando...'}</p>

  return (
    <div className="pagina-estreita">
      <h1>Gerar contrato — pedido #{pedido.id}</h1>
      <p>
        Cliente: <b>{pedido.nomeCliente}</b> — Automóvel: <b>{pedido.automovel.marca} {pedido.automovel.modelo}</b>
      </p>
      {erro && <div className="alerta erro">{erro}</div>}

      <form onSubmit={salvar} className="form">
        <div className="linha">
          <label>
            Propriedade do automóvel
            <select name="tipoPropriedade" value={form.tipoPropriedade} onChange={alterar}>
              <option value="CLIENTE">Cliente</option>
              {sessao.tipoAgente === 'EMPRESA' && <option value="EMPRESA">Empresa</option>}
              {sessao.tipoAgente === 'BANCO' && <option value="BANCO">Banco</option>}
            </select>
          </label>
          <label>
            Valor do contrato (R$)
            <input type="number" step="0.01" min="0.01" name="valor" value={form.valor} onChange={alterar} required />
          </label>
        </div>
        <p className="dica">
          Se não ficar com o cliente, o automóvel será registrado em nome de {sessao.nome} ({sessao.tipoAgente}).
        </p>

        {sessao.tipoAgente === 'BANCO' && (
          <label className="checkbox">
            <input type="checkbox" checked={comCredito} onChange={(e) => setComCredito(e.target.checked)} />
            Conceder contrato de crédito como {sessao.nome}
          </label>
        )}

        {comCredito && (
          <fieldset>
            <legend>Contrato de crédito</legend>
            <div className="linha">
              <label>
                Valor do crédito (R$)
                <input type="number" step="0.01" min="0.01" name="valorCredito" value={form.valorCredito} onChange={alterar} required />
              </label>
              <label>
                Parcelas
                <input type="number" min="1" name="numeroParcelas" value={form.numeroParcelas} onChange={alterar} />
              </label>
              <label>
                Juros (% a.m.)
                <input type="number" step="0.01" min="0" name="taxaJuros" value={form.taxaJuros} onChange={alterar} />
              </label>
            </div>
          </fieldset>
        )}

        <div className="acoes">
          <button className="btn btn-sucesso">Gerar contrato</button>
          <button type="button" className="btn btn-secundario" onClick={() => navigate('/pedidos')}>Voltar</button>
        </div>
      </form>
    </div>
  )
}

export default GerarContrato
