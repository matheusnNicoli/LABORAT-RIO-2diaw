import { useEffect, useState } from 'react'
import { api } from '../services/api'
import { formatarData, formatarDinheiro } from '../services/formatos'

function Contratos() {
  const [contratos, setContratos] = useState([])
  const [erro, setErro] = useState('')

  useEffect(() => {
    api('/contratos').then(setContratos).catch((e) => setErro(e.message))
  }, [])

  return (
    <div>
      <h1>Contratos</h1>
      {erro && <div className="alerta erro">{erro}</div>}
      {contratos.length === 0 ? (
        <p>Nenhum contrato ainda.</p>
      ) : (
        <table className="tabela">
          <thead>
            <tr>
              <th>#</th>
              <th>Pedido</th>
              <th>Cliente</th>
              <th>Automóvel</th>
              <th>Data</th>
              <th>Valor</th>
              <th>Propriedade</th>
              <th>Agente</th>
              <th>Crédito</th>
            </tr>
          </thead>
          <tbody>
            {contratos.map((c) => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td>#{c.pedidoId}</td>
                <td>{c.nomeCliente}</td>
                <td>{c.automovel}</td>
                <td>{formatarData(c.dataContrato)}</td>
                <td>{formatarDinheiro(c.valor)}</td>
                <td>{c.tipoPropriedade}</td>
                <td>{c.nomeAgente}</td>
                <td>
                  {c.nomeBanco
                    ? `${c.nomeBanco}: ${formatarDinheiro(c.valorCredito)}${c.numeroParcelas ? ' em ' + c.numeroParcelas + 'x' : ''}`
                    : 'Sem crédito'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

export default Contratos
