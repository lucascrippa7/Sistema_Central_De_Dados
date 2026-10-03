package com.empresa.sistema.infrastructure.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Criptografa/descriptografa automaticamente o campo "senha_criptografada"
 * da entidade Credencial, usando AES-GCM.
 *
 * A senha NUNCA fica em texto puro no banco de dados. A chave vem de
 * app.seguranca.chave-criptografia (application.properties), que por sua
 * vez deve vir de uma variavel de ambiente em producao (APP_CHAVE_CRIPTOGRAFIA).
 *
 * IMPORTANTE: se essa chave for perdida ou trocada, as senhas ja gravadas
 * no banco NAO poderao mais ser descriptografadas. Guarde-a com cuidado
 * (ex: cofre de senhas da empresa), fora do controle de versao.
 */
@Component
@Converter
public class CriptografiaConverter implements AttributeConverter<String, String> {

    private static final String TRANSFORMACAO = "AES/GCM/NoPadding";
    private static final int TAMANHO_IV_BYTES = 12;
    private static final int TAMANHO_TAG_BITS = 128;

    private final SecretKeySpec chave;
    private final SecureRandom random = new SecureRandom();

    public CriptografiaConverter(@Value("${app.seguranca.chave-criptografia}") String chaveConfigurada) {
        this.chave = derivarChave(chaveConfigurada);
    }

    private SecretKeySpec derivarChave(String passphrase) {
        try {
            // Deriva uma chave AES-256 a partir da passphrase configurada.
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] chaveBytes = sha256.digest(passphrase.getBytes("UTF-8"));
            return new SecretKeySpec(chaveBytes, "AES");
        } catch (Exception e) {
            throw new IllegalStateException("Nao foi possivel preparar a chave de criptografia", e);
        }
    }

    @Override
    public String convertToDatabaseColumn(String senhaTextoPuro) {
        if (senhaTextoPuro == null || senhaTextoPuro.isBlank()) {
            return null;
        }
        try {
            byte[] iv = new byte[TAMANHO_IV_BYTES];
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
            cipher.init(Cipher.ENCRYPT_MODE, chave, new GCMParameterSpec(TAMANHO_TAG_BITS, iv));
            byte[] cifrado = cipher.doFinal(senhaTextoPuro.getBytes("UTF-8"));

            // Guarda IV + texto cifrado juntos, para poder descriptografar depois
            ByteBuffer buffer = ByteBuffer.allocate(iv.length + cifrado.length);
            buffer.put(iv);
            buffer.put(cifrado);

            return Base64.getEncoder().encodeToString(buffer.array());
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao criptografar senha do portal", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String valorNoBanco) {
        if (valorNoBanco == null || valorNoBanco.isBlank()) {
            return null;
        }
        try {
            byte[] dados = Base64.getDecoder().decode(valorNoBanco);

            ByteBuffer buffer = ByteBuffer.wrap(dados);
            byte[] iv = new byte[TAMANHO_IV_BYTES];
            buffer.get(iv);
            byte[] cifrado = new byte[buffer.remaining()];
            buffer.get(cifrado);

            Cipher cipher = Cipher.getInstance(TRANSFORMACAO);
            cipher.init(Cipher.DECRYPT_MODE, chave, new GCMParameterSpec(TAMANHO_TAG_BITS, iv));
            byte[] textoPuro = cipher.doFinal(cifrado);

            return new String(textoPuro, "UTF-8");
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao descriptografar senha do portal", e);
        }
    }
}
