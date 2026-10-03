package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.AnexoClienteRepositoryPort;
import com.empresa.sistema.domain.model.AnexoCliente;
import com.empresa.sistema.infrastructure.persistence.mapper.AnexoClienteMapper;
import com.empresa.sistema.infrastructure.persistence.repository.AnexoClienteJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class AnexoClienteRepositoryAdapter implements AnexoClienteRepositoryPort {

    private final AnexoClienteJpaRepository anexoClienteJpaRepository;
    private final AnexoClienteMapper anexoClienteMapper;

    public AnexoClienteRepositoryAdapter(AnexoClienteJpaRepository anexoClienteJpaRepository,
                                          AnexoClienteMapper anexoClienteMapper) {
        this.anexoClienteJpaRepository = anexoClienteJpaRepository;
        this.anexoClienteMapper = anexoClienteMapper;
    }

    @Override
    public List<AnexoCliente> findResumoPorClienteId(Long clienteId) {
        return anexoClienteJpaRepository.findByClienteIdOrderByCriadoEmDesc(clienteId).stream()
                .map(p -> anexoClienteMapper.toDomainResumo(p, clienteId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AnexoCliente> findComConteudoById(Long id) {
        return anexoClienteJpaRepository.findById(id).map(anexoClienteMapper::toDomainCompleto);
    }

    @Override
    public AnexoCliente save(AnexoCliente anexo) {
        // Anexos nunca sao editados, so criados/removidos -- nao precisa do
        // "buscar existente + aplicar mudancas" que Evento/Cliente usam.
        return anexoClienteMapper.toDomainCompleto(
                anexoClienteJpaRepository.save(anexoClienteMapper.paraNovaEntidade(anexo)));
    }

    @Override
    public void deleteById(Long id) {
        anexoClienteJpaRepository.deleteById(id);
    }
}
