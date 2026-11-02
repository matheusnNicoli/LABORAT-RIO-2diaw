export function formatarData(data) {
  if (!data) return '-'
  const [ano, mes, dia] = data.substring(0, 10).split('-')
  return `${dia}/${mes}/${ano}`
}

export function formatarDinheiro(valor) {
  if (valor == null) return '-'
  return Number(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
}

export function formatarCpf(cpf) {
  if (!cpf || cpf.length !== 11) return cpf
  return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
}

export const nomesStatus = {
  PENDENTE: 'Pendente',
  APROVADO: 'Aprovado',
  REPROVADO: 'Reprovado',
  CANCELADO: 'Cancelado',
  CONTRATADO: 'Contratado'
}
