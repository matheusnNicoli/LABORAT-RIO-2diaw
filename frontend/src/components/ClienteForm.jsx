import { useState } from 'react'

const vazio = {
  login: '', senha: '', rg: '', cpf: '', nome: '', endereco: '', profissao: '', rendimentos: []
}

function ClienteForm({ inicial, onSalvar, textoBotao, edicao }) {
  const [form, setForm] = useState(inicial ? { ...inicial, senha: '' } : vazio)

  function alterar(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  function alterarRendimento(index, campo, valor) {
    const lista = [...form.rendimentos]
    lista[index] = { ...lista[index], [campo]: valor }
    setForm({ ...form, rendimentos: lista })
  }

  function adicionarRendimento() {
    if (form.rendimentos.length >= 3) return
    setForm({ ...form, rendimentos: [...form.rendimentos, { entidadeEmpregadora: '', valor: '' }] })
  }

  function removerRendimento(index) {
    setForm({ ...form, rendimentos: form.rendimentos.filter((_, i) => i !== index) })
  }

  function enviar(e) {
    e.preventDefault()
    const dados = {
      ...form,
      rendimentos: form.rendimentos.map((r) => ({ ...r, valor: Number(r.valor) }))
    }
    onSalvar(dados)
  }

  return (
    <form onSubmit={enviar} className="form">
      <fieldset>
        <legend>Acesso</legend>
        <div className="linha">
          <label>
            Login
            <input name="login" value={form.login} onChange={alterar} required />
          </label>
          <label>
            Senha {edicao && <small>(deixe em branco para manter)</small>}
            <input name="senha" type="password" value={form.senha} onChange={alterar} required={!edicao} minLength={6} />
          </label>
        </div>
      </fieldset>

      <fieldset>
        <legend>Dados pessoais</legend>
        <label>
          Nome completo
          <input name="nome" value={form.nome} onChange={alterar} required />
        </label>
        <div className="linha">
          <label>
            RG
            <input name="rg" value={form.rg} onChange={alterar} required />
          </label>
          <label>
            CPF
            <input name="cpf" value={form.cpf} onChange={alterar} placeholder="somente números" required />
          </label>
        </div>
        <label>
          Endereço
          <input name="endereco" value={form.endereco} onChange={alterar} required />
        </label>
        <label>
          Profissão
          <input name="profissao" value={form.profissao || ''} onChange={alterar} />
        </label>
      </fieldset>

      <fieldset>
        <legend>Empregadores e rendimentos (máx. 3)</legend>
        {form.rendimentos.length === 0 && <p className="dica">Nenhum rendimento informado.</p>}
        {form.rendimentos.map((r, i) => (
          <div className="linha" key={i}>
            <label>
              Entidade empregadora
              <input value={r.entidadeEmpregadora} onChange={(e) => alterarRendimento(i, 'entidadeEmpregadora', e.target.value)} required />
            </label>
            <label>
              Rendimento (R$)
              <input type="number" step="0.01" min="0.01" value={r.valor} onChange={(e) => alterarRendimento(i, 'valor', e.target.value)} required />
            </label>
            <button type="button" className="btn btn-perigo btn-pequeno" onClick={() => removerRendimento(i)}>Remover</button>
          </div>
        ))}
        {form.rendimentos.length < 3 && (
          <button type="button" className="btn btn-secundario" onClick={adicionarRendimento}>+ Adicionar rendimento</button>
        )}
      </fieldset>

      <button type="submit" className="btn">{textoBotao}</button>
    </form>
  )
}

export default ClienteForm
