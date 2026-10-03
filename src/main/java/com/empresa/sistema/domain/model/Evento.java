package com.empresa.sistema.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Evento de calendario: entrega de imposto, reuniao ou evento generico.
 *
 * Cada evento pertence a um funcionario (o "dono", quem criou o evento --
 * ver campo usuario). Isso e o que permite cada funcionario ver so os
 * proprios eventos, enquanto o administrador ve todos -- essa filtragem
 * acontece em EventoApplicationService, nao aqui no dominio.
 *
 * O vinculo a cliente (clienteId) e obrigatorio quando tipo=ENTREGA_IMPOSTO
 * e opcional nos demais tipos -- essa regra tambem e validada no service,
 * ja que depende da combinacao de dois campos.
 *
 * RECORRENCIA: quando recorrente=true, este evento faz parte de uma serie
 * mensal. O evento "mestre" da serie e aquele cujo id == serieId (ou seja,
 * o proprio primeiro evento criado aponta pra si mesmo depois de salvo).
 * So o mestre carrega as configuracoes validas da serie -- serieAtiva (se
 * false, para de gerar ocorrencias futuras) e diaMes (dia do mes em que a
 * ocorrencia deve cair, ajustado para o ultimo dia do mes quando o mes nao
 * tiver aquele dia, ex: dia 31 em fevereiro vira 28/29). As demais
 * ocorrencias soh guardam serieId (pra saber de qual serie vieram) e
 * serieAnoReferencia/serieMesReferencia (pra o gerador saber "este e o
 * evento de tal mes", mesmo que o usuario depois mude a data manualmente).
 *
 * NOTIFICACAO: todo evento avisa automaticamente no proprio dia. Os dias
 * extras de antecedencia (ex: 3 ou 4 dias antes) ficam em "lembretes".
 */
@Getter
@Setter
@NoArgsConstructor
public class Evento {

    private Long id;

    @NotBlank(message = "Informe o titulo")
    private String titulo;

    private String descricao;

    @NotNull(message = "Selecione o tipo do evento")
    private TipoEvento tipo;

    @NotNull(message = "Informe a data")
    private LocalDate data;

    /** Nulo = evento sem horario definido (dia todo). */
    private LocalTime horario;

    private Long clienteId;

    /** Preenchido so para exibicao (nome do cliente vinculado), nunca persistido diretamente. */
    private String clienteNome;

    /** O funcionario dono do evento -- quem ve e quem pode editar/excluir, alem do administrador. */
    private UsuarioResumo usuario;

    /** true = esta ocorrencia faz parte de uma serie recorrente mensal. */
    private boolean recorrente = false;

    /** Id do evento mestre da serie (o proprio id, no caso do mestre). Nulo se nao for recorrente. */
    private Long serieId;

    /** So tem efeito no evento mestre: se false, para de gerar novas ocorrencias futuras da serie. */
    private boolean serieAtiva = true;

    /** So tem efeito no evento mestre: dia do mes (1-31) em que a serie deve gerar a ocorrencia. */
    private Integer diaMes;

    /** Ano/mes que esta ocorrencia representa dentro da serie -- usado pelo gerador, nao pela data em si. */
    private Integer serieAnoReferencia;
    private Integer serieMesReferencia;

    /** Dias de antecedencia extras para notificar, alem do proprio dia do evento. Ex: [3, 4]. */
    private List<Integer> lembretes = new ArrayList<>();

    /** Ultima data em que o e-mail de notificacao foi enviado para este evento -- evita duplicar o envio no mesmo dia. */
    private LocalDate notificacaoEmailEnviadaEm;

    private LocalDateTime criadoEm;
    private UsuarioResumo criadoPor;
    private LocalDateTime atualizadoEm;
    private UsuarioResumo atualizadoPor;
}
