-- Script de referencia do banco (PostgreSQL / Supabase)
-- Obs: nao precisa rodar! O Hibernate (ddl-auto=update) cria as tabelas sozinho
-- quando o back-end sobe. Deixamos aqui para documentar o modelo fisico.

CREATE TABLE IF NOT EXISTS usuarios (
    id      BIGSERIAL PRIMARY KEY,
    login   VARCHAR(255) NOT NULL UNIQUE,
    senha   VARCHAR(255) NOT NULL,
    perfil  VARCHAR(255) NOT NULL CHECK (perfil IN ('CLIENTE', 'AGENTE'))
);

CREATE TABLE IF NOT EXISTS clientes (
    id         BIGINT PRIMARY KEY REFERENCES usuarios(id),
    rg         VARCHAR(255) NOT NULL,
    cpf        VARCHAR(11)  NOT NULL UNIQUE,
    nome       VARCHAR(255) NOT NULL,
    endereco   VARCHAR(255) NOT NULL,
    profissao  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS rendimentos (
    id                    BIGSERIAL PRIMARY KEY,
    entidade_empregadora  VARCHAR(255) NOT NULL,
    valor                 FLOAT(53) NOT NULL,
    cliente_id            BIGINT REFERENCES clientes(id)
);

CREATE TABLE IF NOT EXISTS agentes (
    id    BIGINT PRIMARY KEY REFERENCES usuarios(id),
    nome  VARCHAR(255) NOT NULL,
    cnpj  VARCHAR(255) NOT NULL UNIQUE,
    tipo  VARCHAR(255) NOT NULL CHECK (tipo IN ('EMPRESA', 'BANCO'))
);

CREATE TABLE IF NOT EXISTS automoveis (
    id                 BIGSERIAL PRIMARY KEY,
    matricula          VARCHAR(255) NOT NULL UNIQUE,
    ano                INTEGER NOT NULL,
    marca              VARCHAR(255) NOT NULL,
    modelo             VARCHAR(255) NOT NULL,
    placa              VARCHAR(255) NOT NULL UNIQUE,
    tipo_proprietario  VARCHAR(255) CHECK (tipo_proprietario IN ('CLIENTE', 'EMPRESA', 'BANCO')),
    proprietario_id    BIGINT REFERENCES usuarios(id)
);

CREATE TABLE IF NOT EXISTS pedidos_aluguel (
    id                   BIGSERIAL PRIMARY KEY,
    cliente_id           BIGINT NOT NULL REFERENCES clientes(id),
    automovel_id         BIGINT NOT NULL REFERENCES automoveis(id),
    data_inicio          DATE NOT NULL,
    data_fim             DATE NOT NULL,
    data_criacao         TIMESTAMP,
    status               VARCHAR(255) NOT NULL
                         CHECK (status IN ('PENDENTE', 'APROVADO', 'REPROVADO', 'CANCELADO', 'CONTRATADO')),
    observacao           VARCHAR(255),
    parecer              VARCHAR(255),
    agente_avaliador_id  BIGINT REFERENCES agentes(id)
);

CREATE TABLE IF NOT EXISTS contratos_credito (
    id               BIGSERIAL PRIMARY KEY,
    banco_id         BIGINT NOT NULL REFERENCES agentes(id),
    valor            FLOAT(53) NOT NULL,
    numero_parcelas  INTEGER,
    taxa_juros       FLOAT(53)
);

CREATE TABLE IF NOT EXISTS contratos (
    id                   BIGSERIAL PRIMARY KEY,
    pedido_id            BIGINT NOT NULL UNIQUE REFERENCES pedidos_aluguel(id),
    agente_id            BIGINT NOT NULL REFERENCES agentes(id),
    data_contrato        DATE,
    tipo_propriedade     VARCHAR(255) NOT NULL CHECK (tipo_propriedade IN ('CLIENTE', 'EMPRESA', 'BANCO')),
    valor                FLOAT(53) NOT NULL,
    contrato_credito_id  BIGINT UNIQUE REFERENCES contratos_credito(id)
);
