import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import ClienteForm from '../components/ClienteForm'
import { api } from '../services/api'

function Cadastro() {
  const navigate = useNavigate()
  const [erro, setErro] = useState('')

  async function salvar(dados) {
    setErro('')
    try {
      await api('/clientes', { method: 'POST', body: dados })
      alert('Cadastro realizado! Agora faça o login.')
      navigate('/login')
    } catch (err) {
      setErro(err.message)
      window.scrollTo(0, 0)
    }
  }

  return (
    <div className="pagina-estreita">
      <h1>Cadastro de cliente</h1>
      {erro && <div className="alerta erro">{erro}</div>}
      <ClienteForm onSalvar={salvar} textoBotao="Cadastrar" />
      <p>Já tem conta? <Link to="/login">Entrar</Link></p>
    </div>
  )
}

export default Cadastro
