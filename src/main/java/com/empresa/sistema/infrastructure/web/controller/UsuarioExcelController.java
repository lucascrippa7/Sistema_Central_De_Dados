package com.empresa.sistema.infrastructure.web.controller;

import com.empresa.sistema.application.port.in.UsuarioExcelUseCase;
import com.empresa.sistema.domain.model.ColunaUsuarioPlanilha;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

/**
 * Rotas /usuarios/**: ja restritas a ADMINISTRADOR pelo SecurityConfig
 * desde o inicio do projeto, entao nao precisa de regra nova aqui.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioExcelController {

    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final UsuarioExcelUseCase usuarioExcelUseCase;

    public UsuarioExcelController(UsuarioExcelUseCase usuarioExcelUseCase) {
        this.usuarioExcelUseCase = usuarioExcelUseCase;
    }

    @GetMapping("/exportar/opcoes")
    public String opcoesExportar(Model model) {
        model.addAttribute("colunasDisponiveis", ColunaUsuarioPlanilha.values());
        return "usuarios/exportar-opcoes";
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@RequestParam(required = false) Set<ColunaUsuarioPlanilha> colunas) {
        byte[] planilha = usuarioExcelUseCase.exportar(colunas);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"usuarios.xlsx\"")
                .contentType(XLSX)
                .body(planilha);
    }
}
