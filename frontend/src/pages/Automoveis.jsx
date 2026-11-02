import { useEffect, useState } from 'react'
import { api } from '../services/api'
import { ehAgente } from '../services/auth'

const vazio = { matricula: '', ano: '', marca: '', modelo: '', placa: '' }

function Automoveis() {
  const agente = ehAgente()
  const [automoveis, setAutomoveis] = useState([])
  const [form, setForm] = useState(vazio)
  const [editandoId, setEditandoId] = useState(null)
  const [erro, setErro] = useState('')

  function carregar() {
    api('/automoveis').then(setAutomoveis).catch((e) => setErro(e.message))
  }

  useEffect(carregar, [])

  function alterar(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  function editar(a) {
    setEditandoId(a.id)
    setForm({ matricula: a.matricula, ano: a.ano, marca: a.marca, modelo: a.modelo, placa: a.placa })
  }

  function limpar() {
    setEditandoId(null)
    setForm(vazio)
  }

  async function salvar(e) {
    e.preventDefault()
    setErro('')
    const dados = { ...form, ano: Number(form.ano) }
    try {
      if (editandoId) {
        await api('/automoveis/' + editandoId, { method: 'PUT', body: dados })
      } else {
        await api('/automoveis', { method: 'POST', body: dados })
      }
      limpar()
      carregar()
    } catch (err) {
      setErro(err.message)
    }
  }

  async function excluir(id) {
    if (!confirm('Excluir este automóvel?')) return
    try {
      await api('/automoveis/' + id, { method: 'DELETE' })
      carregar()
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <div>
      <h1>Automóveis</h1>
      {erro && <div className="alerta erro">{erro}</div>}

      {agente && (
        <form onSubmit={salvar} className="form form-inline">
          <h3>{editandoId ? 'Editar automóvel' : 'Cadastrar automóvel'}</h3>
          <div className="linha">
            <label>Matrícula<input name="matricula" value={form.matricula} onChange={alterar} required /></label>
            <label>Ano<input name="ano" type="number" value={form.ano} onChange={alterar} required /></label>
            <label>Marca<input name="marca" value={form.marca} onChange={alterar} required /></label>
            <label>Modelo<input name="modelo" value={form.modelo} onChange={alterar} required /></label>
            <label>Placa<input name="placa" value={form.placa} onChange={alterar} required /></label>
          </div>
          <div className="acoes">
            <button className="btn">{editandoId ? 'Salvar' : 'Cadastrar'}</button>
            {editandoId && <button type="button" className="btn btn-secundario" onClick={limpar}>Cancelar</button>}
          </div>
        </form>
      )}

      <table className="tabela">
        <thead>
          <tr>
            <th>Matrícula</th>
            <th>Marca / Modelo</th>
            <th>Ano</th>
            <th>Placa</th>
            <th>Proprietário</th>
            {agente && <th>Ações</th>}
          </tr>
        </thead>
        <tbody>
          {automoveis.map((a) => (
            <tr key={a.id}>
              <td>{a.matricula}</td>
              <td>{a.marca} {a.modelo}</td>
              <td>{a.ano}</td>
              <td>{a.placa}</td>
              <td>{a.nomeProprietario || '-'} <small>{a.tipoProprietario && '(' + a.tipoProprietario + ')'}</small></td>
              {agente && (
                <td className="acoes-tabela">
                  <button className="btn btn-pequeno btn-secundario" onClick={() => editar(a)}>Editar</button>
                  <button className="btn btn-pequeno btn-perigo" onClick={() => excluir(a.id)}>Excluir</button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default Automoveis
