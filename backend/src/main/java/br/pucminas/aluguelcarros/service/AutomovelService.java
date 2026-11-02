package br.pucminas.aluguelcarros.service;

import br.pucminas.aluguelcarros.dto.AutomovelRequest;
import br.pucminas.aluguelcarros.dto.AutomovelResponse;
import br.pucminas.aluguelcarros.exception.RecursoNaoEncontradoException;
import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import br.pucminas.aluguelcarros.model.Agente;
import br.pucminas.aluguelcarros.model.Automovel;
import br.pucminas.aluguelcarros.model.TipoAgente;
import br.pucminas.aluguelcarros.model.TipoProprietario;
import br.pucminas.aluguelcarros.model.Usuario;
import br.pucminas.aluguelcarros.repository.AutomovelRepository;
import br.pucminas.aluguelcarros.repository.PedidoAluguelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutomovelService {

    private final AutomovelRepository automovelRepository;
    private final PedidoAluguelRepository pedidoRepository;

    public AutomovelService(AutomovelRepository automovelRepository, PedidoAluguelRepository pedidoRepository) {
        this.automovelRepository = automovelRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<AutomovelResponse> listar() {
        return automovelRepository.findAllByOrderByMarcaAscModeloAsc().stream().map(AutomovelResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public AutomovelResponse buscar(Long id) {
        return AutomovelResponse.de(buscarEntidade(id));
    }

    @Transactional
    public AutomovelResponse cadastrar(AutomovelRequest dados, Usuario logado) {
        Agente agente = Permissoes.exigirAgente(logado);
        String placa = dados.placa().trim().toUpperCase();
        if (automovelRepository.existsByPlaca(placa)) {
            throw new RegraNegocioException("Já existe um automóvel com essa placa");
        }
        if (automovelRepository.existsByMatricula(dados.matricula().trim())) {
            throw new RegraNegocioException("Já existe um automóvel com essa matrícula");
        }

        Automovel automovel = new Automovel();
        preencher(automovel, dados);
        automovel.setProprietario(agente);
        automovel.setTipoProprietario(agente.getTipo() == TipoAgente.BANCO ? TipoProprietario.BANCO : TipoProprietario.EMPRESA);
        return AutomovelResponse.de(automovelRepository.save(automovel));
    }

    @Transactional
    public AutomovelResponse atualizar(Long id, AutomovelRequest dados, Usuario logado) {
        Permissoes.exigirAgente(logado);
        Automovel automovel = buscarEntidade(id);
        String placa = dados.placa().trim().toUpperCase();
        if (!placa.equals(automovel.getPlaca()) && automovelRepository.existsByPlaca(placa)) {
            throw new RegraNegocioException("Já existe um automóvel com essa placa");
        }
        if (!dados.matricula().trim().equals(automovel.getMatricula())
                && automovelRepository.existsByMatricula(dados.matricula().trim())) {
            throw new RegraNegocioException("Já existe um automóvel com essa matrícula");
        }
        preencher(automovel, dados);
        return AutomovelResponse.de(automovelRepository.save(automovel));
    }

    @Transactional
    public void excluir(Long id, Usuario logado) {
        Permissoes.exigirAgente(logado);
        Automovel automovel = buscarEntidade(id);
        if (pedidoRepository.existsByAutomovelId(id)) {
            throw new RegraNegocioException("Não é possível excluir: automóvel possui pedidos");
        }
        automovelRepository.delete(automovel);
    }

    public Automovel buscarEntidade(Long id) {
        return automovelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado"));
    }

    private void preencher(Automovel automovel, AutomovelRequest dados) {
        automovel.setMatricula(dados.matricula().trim());
        automovel.setAno(dados.ano());
        automovel.setMarca(dados.marca().trim());
        automovel.setModelo(dados.modelo().trim());
        automovel.setPlaca(dados.placa().trim().toUpperCase());
    }
}
