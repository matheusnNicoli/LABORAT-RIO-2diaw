const CHAVE = 'alugacar_sessao'

export function getSessao() {
  try {
    const dados = localStorage.getItem(CHAVE)
    return dados ? JSON.parse(dados) : null
  } catch {
    return null
  }
}

export function salvarSessao(sessao) {
  localStorage.setItem(CHAVE, JSON.stringify(sessao))
}

export function limparSessao() {
  localStorage.removeItem(CHAVE)
}

export function ehAgente() {
  const s = getSessao()
  return s && s.perfil === 'AGENTE'
}
