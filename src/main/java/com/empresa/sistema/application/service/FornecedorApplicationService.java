package com.empresa.sistema.application.service;

import com.empresa.sistema.application.port.in.AuditoriaUseCase;
import com.empresa.sistema.application.port.in.ConsultarCnpjUseCase;
import com.empresa.sistema.application.port.in.FornecedorUseCase;
import com.empresa.sistema.application.port.out.FornecedorRepositoryPort;
import com.empresa.sistema.domain.exception.FornecedorNaoEncontradoException;
import com.empresa.sistema.domain.model.AcaoAuditoria;
import com.empresa.sistema.domain.model.DadosCnpj;
import com.empresa.sistema.domain.model.Fornecedor;
import com.empresa.sistema.domain.model.UsuarioResumo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FornecedorApplicationService implements FornecedorUseCase {

    private final FornecedorRepositoryPort fornecedorRepositoryPort;
    private final ConsultarCnpjUseCase consultarCnpjUseCase;
    private final AuditoriaUseCase auditoriaUseCase;

    public FornecedorApplicationService(FornecedorRepositoryPort fornecedorRepositoryPort,
                                         ConsultarCnpjUseCase consultarCnpjUseCase,
                                         AuditoriaUseCase auditoriaUseCase) {
        this.fornecedorRepositoryPort = fornecedorRepositoryPort;
        this.consultarCnpjUseCase = consultarCnpjUseCase;
        this.auditoriaUseCase = auditoriaUseCase;
    }

    @Override
    public List<Fornecedor> listarPorCliente(Long clienteId) {
        return fornecedorRepositoryPort.findByClienteId(clienteId);
    }

    @Transactional
    @Override
    public Fornecedor adicionar(Long clienteId, String cnpj, UsuarioResumo usuarioLogado) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setClienteId(clienteId);
        fornecedor.setCnpj(cnpj);
        fornecedor.setCriadoEm(LocalDateTime.now());
        fornecedor.setCriadoPor(usuarioLogado);

        // A consulta e so uma ajuda -- se falhar, o fornecedor ainda e
        // salvo (so sem razao social/situacao), pra nao bloquear o
        // cadastro por causa de um servico externo fora do ar.
        Optional<DadosCnpj> dados = consultarCnpjUseCase.consultar(cnpj);
        dados.ifPresent(d -> {
            fornecedor.setRazaoSocial(d.getRazaoSocial());
            fornecedor.setRegimeTributario(d.getRegimeTributario());
            fornecedor.setSituacaoCadastral(d.getSituacaoCadastral());
            fornecedor.setUltimaConsultaEm(LocalDateTime.now());
        });

        Fornecedor salvo = fornecedorRepositoryPort.save(fornecedor);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", clienteId, AcaoAuditoria.EDICAO,
                "Fornecedor adicionado: " + cnpj
                        + (salvo.getSituacaoCadastral() != null ? " (situacao: " + salvo.getSituacaoCadastral() + ")" : " (situacao nao consultada)"));

        return salvo;
    }

    @Transactional
    @Override
    public Fornecedor reconsultarSituacao(Long fornecedorId, UsuarioResumo usuarioLogado) {
        Fornecedor fornecedor = fornecedorRepositoryPort.findById(fornecedorId)
                .orElseThrow(() -> new FornecedorNaoEncontradoException(fornecedorId));

        Optional<DadosCnpj> dados = consultarCnpjUseCase.consultar(fornecedor.getCnpj());
        dados.ifPresent(d -> {
            fornecedor.setRazaoSocial(d.getRazaoSocial());
            fornecedor.setRegimeTributario(d.getRegimeTributario());
            fornecedor.setSituacaoCadastral(d.getSituacaoCadastral());
            fornecedor.setUltimaConsultaEm(LocalDateTime.now());
        });

        Fornecedor salvo = fornecedorRepositoryPort.save(fornecedor);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", fornecedor.getClienteId(), AcaoAuditoria.EDICAO,
                "Situacao do fornecedor " + fornecedor.getCnpj() + " reconsultada: "
                        + (salvo.getSituacaoCadastral() != null ? salvo.getSituacaoCadastral() : "nao encontrada"));

        return salvo;
    }

    @Transactional
    @Override
    public void remover(Long fornecedorId, UsuarioResumo usuarioLogado) {
        Fornecedor fornecedor = fornecedorRepositoryPort.findById(fornecedorId)
                .orElseThrow(() -> new FornecedorNaoEncontradoException(fornecedorId));

        fornecedorRepositoryPort.deleteById(fornecedorId);

        auditoriaUseCase.registrar(usuarioLogado, "CLIENTE", fornecedor.getClienteId(), AcaoAuditoria.EDICAO,
                "Fornecedor removido: " + fornecedor.getCnpj());
    }
}
