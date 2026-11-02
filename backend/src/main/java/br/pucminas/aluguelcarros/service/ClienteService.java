package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.ClienteRequest;
import br.pucminas.aluguelcarros.dto.ClienteResponse;
import br.pucminas.aluguelcarros.dto.RendimentoDTO;
import br.pucminas.aluguelcarros.exception.AcessoNegadoException;
import br.pucminas.aluguelcarros.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import br.pucminas.aluguelcarros.model.Cliente;
import br.pucminas.aluguelcarros.model.Rendimento;
import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.repository.ClienteRepository;
import br.pucminas.aluguelcarros.repository.PedidoAluguelRepository;
import br.pucminas.aluguelcarros.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoAluguelRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository,
                          PedidoAluguelRepository pedidoRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ClienteResponse cadastrar(ClienteRequest dados) {
        if (dados.senha() == null || dados.senha().length() < 6) {
            throw new RegraNegocioException("A senha deve ter pelo menos 6 caracteres");
        }
        String cpf = limparCpf(dados.cpf());
        if (usuarioRepository.existsByLogin(dados.login().trim())) {
            throw new RegraNegocioException("Já existe um usuário com esse login");
        }
        if (clienteRepository.existsByCpf(cpf)) {
            throw new RegraNegocioException("Já existe um cliente com esse CPF");
        }

        Cliente cliente = new Cliente();
        cliente.setLogin(dados.login().trim());
        cliente.setSenha(passwordEncoder.encode(dados.senha()));
        preencherDados(cliente, dados, cpf);
        return ClienteResponse.de(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar(Usuario logado) {
        Permissoes.exigirAgente(logado);
        return clienteRepository.findAllByOrderByNomeAsc().stream().map(ClienteResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id, Usuario logado) {
        verificarAcesso(id, logado);
        return ClienteResponse.de(buscarEntidade(id));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest dados, Usuario logado) {
        if (!logado.getId().equals(id)) {
            throw new AcessoNegadoException("Você só pode editar os seus próprios dados");
        }
        Cliente cliente = buscarEntidade(id);
        String cpf = limparCpf(dados.cpf());

        String novoLogin = dados.login().trim();
        if (!novoLogin.equals(cliente.getLogin()) && usuarioRepository.existsByLogin(novoLogin)) {
            throw new RegraNegocioException("Já existe um usuário com esse login");
        }
        if (!cpf.equals(cliente.getCpf()) && clienteRepository.existsByCpf(cpf)) {
            throw new RegraNegocioException("Já existe um cliente com esse CPF");
        }

        cliente.setLogin(novoLogin);
        if (dados.senha() != null && !dados.senha().isBlank()) {
            if (dados.senha().length() < 6) {
                throw new RegraNegocioException("A senha deve ter pelo menos 6 caracteres");
            }
            cliente.setSenha(passwordEncoder.encode(dados.senha()));
        }
        preencherDados(cliente, dados, cpf);
        return ClienteResponse.de(clienteRepository.save(cliente));
    }

    @Transactional
    public void excluir(Long id, Usuario logado) {
        verificarAcesso(id, logado);
        Cliente cliente = buscarEntidade(id);
        if (pedidoRepository.existsByClienteId(id)) {
            throw new RegraNegocioException("Não é possível excluir: o cliente possui pedidos de aluguel");
        }
        clienteRepository.delete(cliente);
    }

    private void preencherDados(Cliente cliente, ClienteRequest dados, String cpf) {
        cliente.setRg(dados.rg().trim());
        cliente.setCpf(cpf);
        cliente.setNome(dados.nome().trim());
        cliente.setEndereco(dados.endereco().trim());
        cliente.setProfissao(dados.profissao());

        List<RendimentoDTO> rends = dados.rendimentos() == null ? List.of() : dados.rendimentos();
        if (rends.size() > 3) {
            throw new RegraNegocioException("O cliente pode ter no máximo 3 rendimentos");
        }
        cliente.getRendimentos().clear();
        for (RendimentoDTO r : rends) {
            cliente.getRendimentos().add(new Rendimento(r.entidadeEmpregadora().trim(), r.valor()));
        }
    }

    private String limparCpf(String cpf) {
        String numeros = cpf.replaceAll("\\D", "");
        if (numeros.length() != 11) {
            throw new RegraNegocioException("CPF deve ter 11 números");
        }
        return numeros;
    }

    private void verificarAcesso(Long id, Usuario logado) {
        if (!Permissoes.ehAgente(logado) && !logado.getId().equals(id)) {
            throw new AcessoNegadoException("Acesso negado");
        }
    }

    private Cliente buscarEntidade(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }
}
