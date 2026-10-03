package com.empresa.sistema.infrastructure.persistence.adapter;

import com.empresa.sistema.application.port.out.LogAuditoriaRepositoryPort;
import com.empresa.sistema.domain.model.FiltroAuditoria;
import com.empresa.sistema.domain.model.LogAuditoria;
import com.empresa.sistema.infrastructure.persistence.mapper.LogAuditoriaMapper;
import com.empresa.sistema.infrastructure.persistence.repository.LogAuditoriaJpaRepository;
import com.empresa.sistema.infrastructure.persistence.specification.LogAuditoriaSpecifications;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LogAuditoriaRepositoryAdapter implements LogAuditoriaRepositoryPort {

    /** Limite de linhas trazidas de uma vez -- o log pode crescer bastante com o tempo;
     * quem precisar de mais do que isso deve estreitar o filtro (periodo, usuario, etc). */
    private static final int LIMITE_RESULTADOS = 500;

    private final LogAuditoriaJpaRepository logAuditoriaJpaRepository;
    private final LogAuditoriaMapper logAuditoriaMapper;

    public LogAuditoriaRepositoryAdapter(LogAuditoriaJpaRepository logAuditoriaJpaRepository, LogAuditoriaMapper logAuditoriaMapper) {
        this.logAuditoriaJpaRepository = logAuditoriaJpaRepository;
        this.logAuditoriaMapper = logAuditoriaMapper;
    }

    @Override
    public void save(LogAuditoria log) {
        logAuditoriaJpaRepository.save(logAuditoriaMapper.toEntity(log));
    }

    @Override
    public List<LogAuditoria> buscar(FiltroAuditoria filtro) {
        PageRequest paginaMaisRecentes = PageRequest.of(0, LIMITE_RESULTADOS, Sort.by("dataHora").descending());
        return logAuditoriaJpaRepository.findAll(LogAuditoriaSpecifications.apartirDoFiltro(filtro), paginaMaisRecentes)
                .getContent().stream()
                .map(logAuditoriaMapper::toDomain)
                .collect(Collectors.toList());
    }
}
