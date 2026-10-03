package com.empresa.sistema.infrastructure.cnpj;

import com.empresa.sistema.application.port.out.ConsultaCnpjPort;
import com.empresa.sistema.domain.model.ClienteCnae;
import com.empresa.sistema.domain.model.DadosCnpj;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Consulta CNPJ na BrasilAPI (https://brasilapi.com.br) -- servico publico,
 * gratuito, sem precisar de chave/cadastro, mantido pela comunidade a
 * partir dos dados abertos da Receita Federal.
 *
 * Isso e so uma AJUDA de preenchimento -- se a consulta falhar por
 * qualquer motivo (CNPJ nao existe, servico fora do ar, sem internet),
 * devolve vazio e deixa a pessoa preencher manualmente. Nunca lanca
 * excecao pra fora, pra nao travar o cadastro do cliente por causa de um
 * servico externo.
 */
@Component
public class BrasilApiConsultaCnpjAdapter implements ConsultaCnpjPort {

    private static final Logger log = LoggerFactory.getLogger(BrasilApiConsultaCnpjAdapter.class);
    private static final String URL_BASE = "https://brasilapi.com.br/api/cnpj/v1/";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<DadosCnpj> consultar(String cnpjSomenteNumeros) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL_BASE + cnpjSomenteNumeros))
                    .timeout(Duration.ofSeconds(8))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.info("Consulta de CNPJ {} na BrasilAPI voltou status {} (nao encontrado ou invalido)",
                        cnpjSomenteNumeros, response.statusCode());
                return Optional.empty();
            }

            JsonNode raiz = objectMapper.readTree(response.body());
            DadosCnpj dados = new DadosCnpj();
            dados.setCnpj(cnpjSomenteNumeros);
            dados.setRazaoSocial(texto(raiz, "razao_social"));
            dados.setNomeFantasia(texto(raiz, "nome_fantasia"));
            dados.setCidade(texto(raiz, "municipio"));
            dados.setEstado(texto(raiz, "uf"));
            dados.setRegimeTributario(detectarRegimeTributario(raiz));
            dados.setSituacaoCadastral(texto(raiz, "descricao_situacao_cadastral"));

            // CNAE principal + secundarios -- a BrasilAPI manda o codigo cru
            // (so numeros, ex: 4711302), formatamos pro mesmo padrao que o
            // resto do sistema usa (ex: 4711-3/02).
            String cnaePrincipalCodigo = formatarCnae(texto(raiz, "cnae_fiscal"));
            if (cnaePrincipalCodigo != null) {
                dados.getCnaes().add(new ClienteCnae(null, cnaePrincipalCodigo, texto(raiz, "cnae_fiscal_descricao"), true));
            }
            JsonNode secundarios = raiz.get("cnaes_secundarios");
            if (secundarios != null && secundarios.isArray()) {
                for (JsonNode item : secundarios) {
                    String codigo = formatarCnae(texto(item, "codigo"));
                    if (codigo != null) {
                        dados.getCnaes().add(new ClienteCnae(null, codigo, texto(item, "descricao"), false));
                    }
                }
            }

            return Optional.of(dados);

        } catch (IOException e) {
            log.warn("Falha ao consultar CNPJ {} na BrasilAPI: {}", cnpjSomenteNumeros, e.getMessage());
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Consulta de CNPJ {} interrompida", cnpjSomenteNumeros);
            return Optional.empty();
        }
    }

    private String texto(JsonNode raiz, String campo) {
        JsonNode node = raiz.get(campo);
        return (node == null || node.isNull()) ? null : node.asText();
    }

    /** "4711302" (como a BrasilAPI manda) -> "4711-3/02" (padrao usado no resto do sistema). */
    private String formatarCnae(String numeroCru) {
        if (numeroCru == null) return null;
        String digitos = numeroCru.replaceAll("[^0-9]", "");
        if (digitos.length() != 7) return numeroCru; // formato inesperado -- devolve como veio, melhor que perder o dado
        return digitos.substring(0, 4) + "-" + digitos.substring(4, 5) + "/" + digitos.substring(5, 7);
    }

    /**
     * A Receita Federal informa se a empresa e optante do MEI ou do Simples
     * Nacional -- isso da pra detectar com seguranca. Ja Lucro Presumido x
     * Lucro Real NAO da pra distinguir por aqui (a Receita nao expoe essa
     * diferenca nesse endpoint), entao nesses casos deixamos em branco pra
     * pessoa escolher -- melhor nao preencher do que preencher errado.
     */
    private String detectarRegimeTributario(JsonNode raiz) {
        if (booleano(raiz, "opcao_pelo_mei")) {
            return "MEI";
        }
        if (booleano(raiz, "opcao_pelo_simples")) {
            return "Simples Nacional";
        }
        return null;
    }

    private boolean booleano(JsonNode raiz, String campo) {
        JsonNode node = raiz.get(campo);
        return node != null && node.isBoolean() && node.asBoolean();
    }
}
