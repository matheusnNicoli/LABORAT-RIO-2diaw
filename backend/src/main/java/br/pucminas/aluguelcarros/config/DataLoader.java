package br.pucminas.aluguelcarros.config;

import br.pucminas.aluguelcarros.model.*;
import br.pucminas.aluguelcarros.repository.AutomovelRepository;
import br.pucminas.aluguelcarros.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AutomovelRepository automovelRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, AutomovelRepository automovelRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.automovelRepository = automovelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        System.out.println(">> Banco vazio, criando dados de teste...");

        Agente empresa = new Agente();
        empresa.setLogin("empresa");
        empresa.setSenha(passwordEncoder.encode("123456"));
        empresa.setNome("Locadora BH Veículos");
        empresa.setCnpj("12345678000190");
        empresa.setTipo(TipoAgente.EMPRESA);
        usuarioRepository.save(empresa);

        Agente banco = new Agente();
        banco.setLogin("banco");
        banco.setSenha(passwordEncoder.encode("123456"));
        banco.setNome("Banco Minas Crédito");
        banco.setCnpj("98765432000110");
        banco.setTipo(TipoAgente.BANCO);
        usuarioRepository.save(banco);

        Cliente cliente = new Cliente();
        cliente.setLogin("cliente");
        cliente.setSenha(passwordEncoder.encode("123456"));
        cliente.setNome("João da Silva");
        cliente.setRg("MG1234567");
        cliente.setCpf("12345678909");
        cliente.setEndereco("Rua das Flores, 100 - Belo Horizonte/MG");
        cliente.setProfissao("Analista de Sistemas");
        cliente.getRendimentos().add(new Rendimento("Empresa XPTO Ltda", 4500.0));
        cliente.getRendimentos().add(new Rendimento("Freelancer", 1200.0));
        usuarioRepository.save(cliente);

        criarCarro("MAT-0001", 2022, "Fiat", "Argo", "ABC1D23", empresa);
        criarCarro("MAT-0002", 2023, "Chevrolet", "Onix", "DEF4G56", empresa);
        criarCarro("MAT-0003", 2021, "Volkswagen", "Gol", "HIJ7K89", empresa);
        criarCarro("MAT-0004", 2024, "Hyundai", "HB20", "LMN0P12", empresa);

        System.out.println(">> Dados de teste criados (empresa / banco / cliente - senha 123456)");
    }

    private void criarCarro(String matricula, int ano, String marca, String modelo, String placa, Agente dono) {
        Automovel a = new Automovel();
        a.setMatricula(matricula);
        a.setAno(ano);
        a.setMarca(marca);
        a.setModelo(modelo);
        a.setPlaca(placa);
        a.setProprietario(dono);
        a.setTipoProprietario(TipoProprietario.EMPRESA);
        automovelRepository.save(a);
    }
}
