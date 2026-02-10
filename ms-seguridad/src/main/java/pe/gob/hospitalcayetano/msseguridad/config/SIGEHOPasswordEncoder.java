package pe.gob.hospitalcayetano.msseguridad.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

@Slf4j
@RefreshScope
public class SIGEHOPasswordEncoder implements PasswordEncoder {

    @Value("${key_encriptacion}")
    private String keyEncriptacion;
    @Override
    public String encode(CharSequence rawPassword) {
        String txtPassword = rawPassword.toString();

        String base64EncryptedString = "";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(keyEncriptacion.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword,24);

            SecretKey key = new SecretKeySpec(keyBytes,"DESede");
            Cipher cipher = Cipher.getInstance("DESede");
            cipher.init(Cipher.ENCRYPT_MODE,key);

            byte[] plainTextBytes = txtPassword.getBytes("utf-8");
            byte[] buf = cipher.doFinal(plainTextBytes);
            base64EncryptedString = Base64.encodeBase64String(buf);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return base64EncryptedString;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
