import { getSessao, limparSessao } from './auth'

const API_URL = import.meta.env.VITE_API_URL || '/api'

export async function api(caminho, opcoes = {}) {
  const sessao = getSessao()
  const headers = { 'Content-Type': 'application/json' }
  if (sessao) {
    headers.Authorization = 'Bearer ' + sessao.token
  }

  const resposta = await fetch(API_URL + caminho, {
    method: opcoes.method || 'GET',
    headers,
    body: opcoes.body ? JSON.stringify(opcoes.body) : undefined
  })

  if (resposta.status === 401 && sessao) {
    limparSessao()
    window.location.href = '/login'
    return null
  }

  if (resposta.status === 204) return null

  const dados = await resposta.json().catch(() => null)
  if (!resposta.ok) {
    throw new Error(dados?.mensagem || 'Erro ao comunicar com o servidor')
  }
  return dados
}
