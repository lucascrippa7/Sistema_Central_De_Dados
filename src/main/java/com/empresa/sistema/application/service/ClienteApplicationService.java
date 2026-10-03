package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.ClienteUseCase;
import com.empresa.sistema.application.port.out.ClienteCnaeRepositoryPort;
import com.empresa.sistema.application.port.out.ClienteCodigoServicoRepositoryPort;
import com.empresa.sistema.application.port.out.ClienteRepositoryPort;
import com.empresa.sistema.domain.exception.ClienteNaoEncontradoException;
import com.empresa.sistema.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementacao dos casos de uso de Cliente. Note que esta classe so
 * conhece INTERFACES (ClienteRepositoryPort, AuditoriaUseCase) -- nunca
 * Spring Data, JPA ou Hibernate diretamente. E aqui que mora a regra de
 * negocio de verdade, testavel sem precisar subir um banco.
 */
@Service
public class ClienteApplicationService implements ClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ClienteCnaeRepositoryPort clienteCnaeRepositoryPort;
    private final ClienteCodigoServicoRepositoryPort clienteCodigoServicoRepositoryPort;
    private final AuditoriaUseCase auditoriaUseCase;

    public ClienteApplicationService(ClienteRepositoryPort clienteRepositoryPort,
                                      ClienteCnaeRepositoryPort clienteCnaeRepositoryPort,
                                      ClienteCodigoServicoRepositoryPort clienteCodigoServicoRepositoryPort,
                                      AuditoriaUseCase auditoriaUseCase) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.clienteCnaeRepositoryPort = clienteCnaeRepositoryPort;
        this.clienteCodigoServicoRepositoryPort = clienteCodigoServicoRepositoryPort;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @Override
    public List<Cliente> listar(FiltroCliente filtro) {
        return clienteRepositoryPort.buscar(filtro != null ? filtro : new FiltroCliente());
    }

    @Override
    public PaginaCliente listarPaginado(FiltroCliente filtro, int pagina, int tamanhoPagina) {
        return clienteRepositoryPort.buscarPaginado(filtro != null ? filtro : new FiltroCliente(), pagina, tamanhoPagina);
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepositoryPort.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));
    }

    @Transactional
    @Override
    public Cliente salvar(Cliente cliente, UsuarioResumo usuarioLogado) {
        boolean novo = (cliente.getId() == null);
        String detalheAuditoria;

        if (!novo) {
            // O formulario de edicao so manda os campos basicos -- preserva
            // quem criou o registro, quando, e os CNAEs/codigos de servico
            // ja cadastrados (senao o "salvar" do formulario simples
            // apagaria esses dados, ja que o form nao os envia de volta).
            Cliente existente = buscarPorId(cliente.getId());

            // Compara ANTES de sobrescrever os campos que nao mudam pelo
            // formulario -- assim o log de auditoria mostra exatamente o
            // que a pessoa alterou, campo a campo, nao so "editou o cliente".
            detalheAuditoria = "Cliente: " + existente.getRazaoSocial() + " -- " + compararCampos(existente, cliente);

            cliente.setCriadoPor(existente.getCriadoPor());
            cliente.setCriadoEm(existente.getCriadoEm());
            cliente.setCnaes(existente.getCnaes());
            cliente.setCodigosServico(existente.getCodigosServico());
        } else {
            cliente.setCriadoPor(usuarioLogado);
            cliente.setCriadoEm(LocalDateTime.now());
            detalheAuditoria = "Cliente: " + cliente.getRazaoSocial();
        }

        cliente.setAtualizadoPor(usuarioLogado);
        cliente.setAtualizadoEm(LocalDateTime.now());

        Cliente salvo = clienteRepositoryPort.save(cliente);

        auditoriaUseCase.registrar(
                usuarioLogado,
                "CLIENTE",
                salvo.getId(),
                novo ? AcaoAuditoria.CRIACAO : AcaoAuditoria.EDICAO,
                detalheAuditoria
        );

        return salvo;
    }

    /**
     * Monta um resumo tipo "Cidade: 'Guarulhos' -> 'Sao Paulo'; Ativo: 'Sim' -> 'Nao'"
     * com so os campos que realmente mudaram -- usado pro historico de
     * auditoria mostrar o que foi alterado, nao so que "algo" foi alterado.
     * So compara os campos que o formulario de edicao realmente controla
     * (CNAEs/codigos de servico ja tem o proprio log especifico, mais acima).
     */
    private String compararCampos(Cliente antigo, Cliente novo) {
        StringBuilder diff = new StringBuilder();
        compararCampo(diff, "Razao social", antigo.getRazaoSocial(), novo.getRazaoSocial());
        compararCampo(diff, "Nome fantasia", antigo.getNomeFantasia(), novo.getNomeFantasia());
        compararCampo(diff, "Regime tributario", antigo.getRegimeTributario(), novo.getRegimeTributario());
        compararCampo(diff, "Cidade", antigo.getCidade(), novo.getCidade());
        compararCampo(diff, "Estado", antigo.getEstado(), novo.getEstado());
        compararCampo(diff, "Tipo de servico", antigo.getTipoServico(), novo.getTipoServico());
        compararCampo(diff, "Descricao do servico", antigo.getDescricaoServico(), novo.getDescricaoServico());
        compararCampo(diff, "Inscricao municipal", antigo.getInscricaoMunicipal(), novo.getInscricaoMunicipal());
        compararCampo(diff, "Inscricao estadual", antigo.getInscricaoEstadual(), novo.getInscricaoEstadual());
        compararCampo(diff, "Codigo em outro sistema", antigo.getCodigoSistemaExterno(), novo.getCodigoSistemaExterno());
        compararCampo(diff, "Ativo", antigo.isAtivo() ? "Sim" : "Nao", novo.isAtivo() ? "Sim" : "Nao");

        String responsavelAntigo = antigo.getResponsavel() != null ? antigo.getResponsavel().getNome() : null;
        String responsavelNovo = novo.getResponsavel() != null ? novo.getResponsavel().getNome() : null;
        compararCampo(diff, "Responsavel", responsavelAntigo, responsavelNovo);

        return diff.length() == 0 ? "nenhum campo alterado" : diff.toString();
    }

    private void compararCampo(StringBuilder diff, String nomeCampo, String valorAntigo, String valorNovo) {
        String a = valorAntigo == null ? "" : valorAntigo;
        String n = valorNovo == null ? "" : valorNovo;
        if (a.equals(n)) {
            return;
        }
        if (diff.length() > 0) {
            diff.append("; ");
        }
        diff.append(nomeCampo).append(": '").append(valorAntigo == null ? "-" : valorAntigo)
                .append("' -> '").append(valorNovo == null ? "-" : valorNovo).append("'");
    }

    @Transactional
    @Override
    public void excluir(Long id, UsuarioResumo usuarioLogado) {
        Cliente cliente = buscarPorId(id);
        clienteRepositoryPort.deleteById(id);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", id, AcaoAuditoria.EXCLUSAO,
                "Cliente: " + cliente.getRazaoSocial());
    }

    @Transactional
    @Override
    public void adicionarCnae(Long clienteId, ClienteCnae novaCnae, UsuarioResumo usuarioLogado) {
        // Salva o CNAE direto (sem passar pelo Cliente inteiro) -- ver
        // ClienteCnaeRepositoryPort pra entender por que esse desenho
        // substituiu o antigo, que tinha um bug real (nao persistia).
        ClienteCnae salvo = clienteCnaeRepositoryPort.save(clienteId, novaCnae);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", clienteId, AcaoAuditoria.EDICAO,
                "CNAE adicionado: " + salvo.getCodigo());
    }

    @Transactional
    @Override
    public void removerCnae(Long clienteId, Long cnaeId, UsuarioResumo usuarioLogado) {
        clienteCnaeRepositoryPort.deleteById(cnaeId);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", clienteId, AcaoAuditoria.EDICAO, "CNAE removido");
    }

    @Transactional
    @Override
    public void adicionarCodigoServico(Long clienteId, ClienteCodigoServico novoCodigo, UsuarioResumo usuarioLogado) {
        ClienteCodigoServico salvo = clienteCodigoServicoRepositoryPort.save(clienteId, novoCodigo);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", clienteId, AcaoAuditoria.EDICAO,
                "Codigo de servico adicionado: " + salvo.getCodigo());
    }

    @Transactional
    @Override
    public void removerCodigoServico(Long clienteId, Long codigoId, UsuarioResumo usuarioLogado) {
        clienteCodigoServicoRepositoryPort.deleteById(codigoId);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", clienteId, AcaoAuditoria.EDICAO,
                "Codigo de servico removido");
    }
}
