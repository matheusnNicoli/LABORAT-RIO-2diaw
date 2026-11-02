# Evolução dos modelos (v1 → v2)

A **v1** foi feita no início do laboratório, só com base no enunciado. A **v2** foi revisada durante o
desenvolvimento, conforme o feedback em aula e os problemas que encontramos ao implementar.
A v2 está **alinhada com o código** (pacote `model` do back-end).

## Diagrama de Classes

| # | v1 | v2 | Motivo |
|---|----|----|--------|
| 1 | `Empresa` e `Banco` como classes separadas | Uma classe `Agente` com o enum `TipoAgente` (EMPRESA/BANCO) | Os atributos eram iguais (nome, cnpj); ficou mais simples no JPA e no login |
| 2 | `Usuario.tipo` era uma String | Enum `Perfil` (CLIENTE/AGENTE) e `Usuario` virou abstrata | Evitar erro de digitação e não deixar criar "usuário genérico" |
| 3 | Rendimentos como arrays dentro de `Cliente` | Nova classe `Rendimento` (entidadeEmpregadora + valor), composição 0..3 | Banco relacional não guarda array bem e precisávamos ligar empregador ↔ valor. Limite de 3 validado no service |
| 4 | `Pedido.status` String | Enum `StatusPedido` (PENDENTE, APROVADO, REPROVADO, CANCELADO, CONTRATADO) | Deixar o fluxo do pedido claro |
| 5 | `Pedido` só tinha uma data | `dataInicio`, `dataFim`, `dataCriacao`, `observacao`, `parecer` e `agenteAvaliador` | Precisávamos do período do aluguel e guardar a análise financeira |
| 6 | Crédito era só uma associação Contrato → Banco | Nova classe `ContratoCredito` (valor, parcelas, juros) ligada ao banco | O enunciado fala em "contrato de crédito"; precisava ter dados próprios |
| 7 | `Automovel` → `Cliente` (proprietário) | `Automovel` → `Usuario` + `TipoProprietario` | Pelo enunciado o carro pode ser do cliente, da empresa ou do banco |
| 8 | Classe `Pedido` | Renomeada para `PedidoAluguel` | Nome mais claro |
| 9 | Métodos de negócio nas entidades (`criarPedido()`, `avaliarPedido()`) | Regras foram para a camada `service` | Arquitetura MVC com services; entidades ficaram só com dados |

## Diagrama de Pacotes

- **v1:** só `view`, `controller` e `model`.
- **v2:** back-end com `controller`, `service`, `repository`, `model`, `dto`, `config` e `exception`;
  front-end com `pages`, `components` e `services`. Também deixamos explícitos os **dois subsistemas** do
  enunciado (gestão de pedidos e contratos / construção dinâmica das páginas) e o Supabase.
- Foram criados os `dto` para não devolver a senha do usuário no JSON e evitar loops na serialização.
- Foi criado o `AuthInterceptor` (pacote `config`) para restringir o acesso a usuários logados.

## Diagrama de Casos de Uso

- Separamos os atores **Empresa** e **Banco** como especializações de **Agente**.
- Novos casos de uso: *Gerenciar meus dados*, *Gerenciar automóveis*, *Consultar clientes*,
  *Consultar contratos* e *Conceder crédito* (`«extend»` de *Executar contrato*).
- Agente também passa a *Consultar pedidos*.

## Histórias de Usuário

- Adicionados critérios de aceitação em todas.
- Novas histórias US09 a US14 (ver `historias-de-usuario/v2.md`).

## O que ainda pode melhorar (próximas versões)

- Os tokens de login ficam em memória: se o servidor reiniciar, é preciso logar de novo (poderia usar JWT).
- Agentes são criados só pelo `DataLoader` (não tem tela de cadastro de agente).

## Melhorias implementadas após a revisão

- Conflitos de período agora são verificados contra pedidos aprovados e contratados.
- Pedidos aprovados que forem modificados voltam para pendente e exigem nova avaliação.
- O contrato de crédito só pode ser concedido pelo próprio banco autenticado.
- Foram adicionados testes automatizados das regras críticas com JUnit 5 e Mockito.
