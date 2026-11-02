import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import ClienteForm from '../components/ClienteForm'
import { api } from '../services/api'
import { getSessao, limparSessao, salvarSessao } from '../services/auth'

function MeusDados() {
  const navigate = useNavigate()
  const sessao = getSessao()
  const [cliente, setCliente] = useState(null)
  const [erro, setErro] = useState('')
  const [sucesso, setSucesso] = useState('')

  useEffect(() => {
    api('/clientes/' + sessao.id).then(setCliente).catch((e) => setErro(e.message))
  }, [sessao.id])

  async function salvar(dados) {
    setErro('')
    setSucesso('')
    try {
      const atualizado = await api('/clientes/' + sessao.id, { method: 'PUT', body: dados })
      setCliente(atualizado)
      salvarSessao({ ...sessao, nome: atualizado.nome })
      setSucesso('Dados atualizados com sucesso!')
    } catch (e) {
      setErro(e.message)
    }
    window.scrollTo(0, 0)
  }

  async function excluirConta() {
    if (!confirm('Tem certeza que deseja excluir sua conta? Essa ação não pode ser desfeita.')) return
    try {
      await api('/clientes/' + sessao.id, { method: 'DELETE' })
      limparSessao()
      navigate('/login')
    } catch (e) {
      setErro(e.message)
      window.scrollTo(0, 0)
    }
  }

  if (!cliente) return <p>{erro || 'Carregando...'}</p>

  return (
    <div className="pagina-estreita">
      <h1>Meus dados</h1>
      {erro && <div className="alerta erro">{erro}</div>}
      {sucesso && <div className="alerta ok">{sucesso}</div>}
      <ClienteForm key={cliente.id + cliente.login} inicial={cliente} onSalvar={salvar} textoBotao="Salvar alterações" edicao />
      <hr />
      <button className="btn btn-perigo" onClick={excluirConta}>Excluir minha conta</button>
    </div>
  )
}

export default MeusDados
