package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.AnexoClienteUseCase;
import com.empresa.sistema.domain.exception.ArquivoMuitoGrandeException;
import com.empresa.sistema.domain.model.AnexoCliente;
import com.empresa.sistema.infrastructure.security.UsuarioAutenticado;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UncheckedIOException;

@Controller
public class AnexoClienteController {

    private final AnexoClienteUseCase anexoClienteUseCase;

    public AnexoClienteController(AnexoClienteUseCase anexoClienteUseCase) {
        this.anexoClienteUseCase = anexoClienteUseCase;
    }

    /** Adicionar/remover fica restrito pelo SecurityConfig (ADMINISTRADOR/EDITOR/EDITOR_SENHAS). */
    @PostMapping("/clientes/{clienteId}/anexos")
    public String adicionar(@PathVariable Long clienteId,
                             @RequestParam("arquivo") MultipartFile arquivo,
                             @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado,
                             RedirectAttributes redirectAttributes) {
        if (arquivo.isEmpty()) {
            redirectAttributes.addFlashAttribute("erroGeral", "Selecione um arquivo antes de enviar.");
            return "redirect:/clientes/" + clienteId;
        }
        try {
            anexoClienteUseCase.adicionar(clienteId, arquivo.getOriginalFilename(), arquivo.getContentType(),
                    arquivo.getBytes(), usuarioAutenticado.getUsuarioResumo());
        } catch (ArquivoMuitoGrandeException e) {
            redirectAttributes.addFlashAttribute("erroGeral", e.getMessage());
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao ler o arquivo enviado", e);
        }
        return "redirect:/clientes/" + clienteId;
    }

    /** Baixar e liberado pra qualquer funcionario autenticado (mesma logica das senhas: uso interno). */
    @GetMapping("/anexos/{id}/baixar")
    public ResponseEntity<byte[]> baixar(@PathVariable Long id) {
        AnexoCliente anexo = anexoClienteUseCase.baixar(id);
        MediaType tipo = anexo.getTipoConteudo() != null
                ? MediaType.parseMediaType(anexo.getTipoConteudo())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + anexo.getNomeArquivo() + "\"")
                .contentType(tipo)
                .body(anexo.getConteudo());
    }

    @PostMapping("/anexos/{id}/excluir")
    public String remover(@PathVariable Long id,
                           @RequestParam Long clienteId,
                           @AuthenticationPrincipal UsuarioAutenticado usuarioAutenticado) {
        anexoClienteUseCase.remover(id, usuarioAutenticado.getUsuarioResumo());
        return "redirect:/clientes/" + clienteId;
    }
}
