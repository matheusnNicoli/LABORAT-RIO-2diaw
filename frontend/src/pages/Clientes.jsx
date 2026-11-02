import { useEffect, useState } from 'react'
import { api } from '../services/api'
import { formatarCpf, formatarDinheiro } from '../services/formatos'

function Clientes() {
  const [clientes, setClientes] = useState([])
  const [erro, setErro] = useState('')

  function carregar() {
    api('/clientes').then(setClientes).catch((e) => setErro(e.message))
  }

  useEffect(carregar, [])

  async function excluir(id) {
    if (!confirm('Excluir este cliente?')) return
    try {
      await api('/clientes/' + id, { method: 'DELETE' })
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <div>
      <h1>Clientes cadastrados</h1>
      {erro && <div className="alerta erro">{erro}</div>}
      <table className="tabela">
        <thead>
          <tr>
            <th>Nome</th>
            <th>CPF</th>
            <th>RG</th>
            <th>Profissão</th>
            <th>Empregadores</th>
            <th>Renda total</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {clientes.map((c) => (
            <tr key={c.id}>
              <td>{c.nome}</td>
              <td>{formatarCpf(c.cpf)}</td>
              <td>{c.rg}</td>
              <td>{c.profissao || '-'}</td>
              <td>{c.rendimentos.map((r) => r.entidadeEmpregadora).join(', ') || '-'}</td>
              <td>{formatarDinheiro(c.rendimentos.reduce((s, r) => s + r.valor, 0))}</td>
              <td><button className="btn btn-pequeno btn-perigo" onClick={() => excluir(c.id)}>Excluir</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default Clientes
