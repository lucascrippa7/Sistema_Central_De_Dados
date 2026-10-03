package com.empresa.sistema.application.port.out;

import com.empresa.sistema.domain.model.NotaFiscalEntrada;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NotaFiscalEntradaRepositoryPort {

    NotaFiscalEntrada save(NotaFiscalEntrada notaFiscalEntrada);

    Optional<NotaFiscalEntrada> findById(Long id);

    boolean existsByClienteIdAndChaveAcesso(Long clienteId, String chaveAcesso);

    List<NotaFiscalEntrada> findByClienteId(Long clienteId);

    List<NotaFiscalEntrada> findByClienteIdAndPeriodo(Long clienteId, LocalDate inicio, LocalDate fim);

    void deleteById(Long id);
}
