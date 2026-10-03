package com.empresa.sistema.infrastructure.email;

import com.empresa.sistema.application.port.out.EmailPort;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Envia e-mails de notificacao via SMTP. Fica seguro mesmo se a empresa
 * ainda nao configurou o SMTP: usa ObjectProvider (nao @Autowired direto)
 * porque o Spring Boot so cria o bean JavaMailSender se spring.mail.host
 * estiver definido -- sem isso, nao ha bean nenhum, e travar a aplicacao
 * inteira por causa de e-mail nao configurado seria um exagero.
 *
 * Alem disso, so envia de verdade quando app.notificacoes.email-habilitado=true
 * -- assim da pra deixar o SMTP configurado mas as notificacoes desligadas
 * ate que alguem confirme que esta tudo certo.
 */
@Component
public class EmailAdapter implements EmailPort {

    private final JavaMailSender javaMailSender;
    private final boolean habilitado;
    private final String remetente;

    public EmailAdapter(ObjectProvider<JavaMailSender> javaMailSenderProvider,
                         @Value("${app.notificacoes.email-habilitado:false}") boolean habilitado,
                         @Value("${spring.mail.username:}") String remetente) {
        this.javaMailSender = javaMailSenderProvider.getIfAvailable();
        this.habilitado = habilitado;
        this.remetente = remetente;
    }

    @Override
    public void enviar(String destinatario, String assunto, String corpo) {
        if (!habilitado || javaMailSender == null) return;
        if (destinatario == null || destinatario.isBlank()) return;

        SimpleMailMessage mensagem = new SimpleMailMessage();
        if (remetente != null && !remetente.isBlank()) {
            mensagem.setFrom(remetente);
        }
        mensagem.setTo(destinatario);
        mensagem.setSubject(assunto);
        mensagem.setText(corpo);
        javaMailSender.send(mensagem);
    }
}
