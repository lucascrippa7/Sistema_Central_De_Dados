package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.out.ClienteCnaeRepositoryPort;
import com.empresa.sistema.application.port.out.ClienteCodigoServicoRepositoryPort;
import com.empresa.sistema.application.port.out.ClienteRepositoryPort;
import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.Cliente;
import com.empresa.sistema.domain.model.ClienteCnae;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Foco nos dois pontos mais faceis de quebrar sem perceber: o historico de
 * auditoria campo-a-campo (ClienteApplicationService#compararCampos, testado
 * indiretamente aqui) e a edicao nao apagar CNAEs/codigos de servico que o
 * formulario simples nao reenvia.
 */
@ExtendWith(MockitoExtension.class)
class ClienteApplicationServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private ClienteCnaeRepositoryPort clienteCnaeRepositoryPort;

    @Mock
    private ClienteCodigoServicoRepositoryPort clienteCodigoServicoRepositoryPort;

    @Mock
    private AuditoriaUseCase auditoriaUseCase;

    private ClienteApplicationService service;
    private UsuarioResumo lucas;

    @BeforeEach
    void configurar() {
        service = new ClienteApplicationService(clienteRepositoryPort, clienteCnaeRepositoryPort,
                clienteCodigoServicoRepositoryPort, auditoriaUseCase);
        lucas = new UsuarioResumo(1L, "Lucas", "lucas");
    }

    @Test
    void edicaoDeveRegistrarSoOsCamposQueMudaram() {
        Cliente existente = new Cliente();
        existente.setId(7L);
        existente.setRazaoSocial("Padaria Sao Jose Ltda");
        existente.setCidade("Guarulhos");
        existente.setEstado("SP");
        existente.setAtivo(true);
        existente.setCnaes(List.of(new ClienteCnae(1L, "4721-1/02", "Padaria", true)));

        Cliente edicao = new Cliente();
        edicao.setId(7L);
        edicao.setRazaoSocial("Padaria Sao Jose Ltda");
        edicao.setCidade("Sao Paulo"); // so este campo muda
        edicao.setEstado("SP");
        edicao.setAtivo(true);

        when(clienteRepositoryPort.findById(7L)).thenReturn(Optional.of(existente));
        when(clienteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.salvar(edicao, lucas);

        ArgumentCaptor<String> detalhes = ArgumentCaptor.forClass(String.class);
        org.mockito.Mockito.verify(auditoriaUseCase).registrar(
                eq(lucas), eq("CLIENTE"), eq(7L), eq(AcaoAuditoria.EDICAO), detalhes.capture());

        assertThat(detalhes.getValue())
                .contains("Cidade: 'Guarulhos' -> 'Sao Paulo'")
                .doesNotContain("Razao social")   // nao mudou, nao deve aparecer
                .doesNotContain("Ativo");          // nao mudou, nao deve aparecer
    }

    @Test
    void edicaoNaoDeveApagarCnaesQueOFormularioNaoReenviou() {
        Cliente existente = new Cliente();
        existente.setId(7L);
        existente.setRazaoSocial("Padaria Sao Jose Ltda");
        existente.setCnaes(List.of(new ClienteCnae(1L, "4721-1/02", "Padaria", true)));

        Cliente edicao = new Cliente();
        edicao.setId(7L);
        edicao.setRazaoSocial("Padaria Sao Jose Ltda");
        // formulario simples nao manda "cnaes" -- fica null aqui de proposito

        when(clienteRepositoryPort.findById(7L)).thenReturn(Optional.of(existente));
        when(clienteRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cliente salvo = service.salvar(edicao, lucas);

        assertThat(salvo.getCnaes()).hasSize(1);
        assertThat(salvo.getCnaes().get(0).getCodigo()).isEqualTo("4721-1/02");
    }

    @Test
    void criacaoDeveRegistrarSoNomeDoClienteSemDiff() {
        Cliente novo = new Cliente();
        novo.setRazaoSocial("Cliente Novo Ltda");
        when(clienteRepositoryPort.save(any())).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setId(50L);
            return c;
        });

        service.salvar(novo, lucas);

        ArgumentCaptor<String> detalhes = ArgumentCaptor.forClass(String.class);
        org.mockito.Mockito.verify(auditoriaUseCase).registrar(
                eq(lucas), eq("CLIENTE"), eq(50L), eq(AcaoAuditoria.CRIACAO), detalhes.capture());

        assertThat(detalhes.getValue()).isEqualTo("Cliente: Cliente Novo Ltda");
    }

    /**
     * Regressao do bug relatado: adicionar CNAE nao persistia, porque o
     * design antigo buscava o Cliente inteiro e tentava sincronizar a lista
     * inteira de volta. Agora adicionarCnae so deve chamar
     * ClienteCnaeRepositoryPort.save(...) diretamente -- nunca
     * clienteRepositoryPort.findById/save.
     */
    @Test
    void adicionarCnaeDeveSalvarDiretoSemBuscarClienteInteiro() {
        ClienteCnae novaCnae = new ClienteCnae(null, "4721-1/02", "Padaria", false);
        when(clienteCnaeRepositoryPort.save(7L, novaCnae))
                .thenReturn(new ClienteCnae(99L, "4721-1/02", "Padaria", false));

        service.adicionarCnae(7L, novaCnae, lucas);

        org.mockito.Mockito.verify(clienteCnaeRepositoryPort).save(7L, novaCnae);
        org.mockito.Mockito.verifyNoInteractions(clienteRepositoryPort);
        org.mockito.Mockito.verify(auditoriaUseCase).registrar(
                eq(lucas), eq("CLIENTE"), eq(7L), eq(AcaoAuditoria.EDICAO), eq("CNAE adicionado: 4721-1/02"));
    }
}
