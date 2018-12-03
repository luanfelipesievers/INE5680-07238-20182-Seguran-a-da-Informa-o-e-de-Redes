package utils.crypto;

import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

import javax.crypto.Cipher;
import java.security.*;

public class RSA {

    private KeyPair parDeChaves;

    public KeyPair getParDeChaves() {
        return parDeChaves;
    }

    private Cipher cipher;

    public RSA() throws Exception {
        Security.addProvider(new BouncyCastleFipsProvider());
        this.parDeChaves = this.realizarCriacaoChavesAssimetricas();
        this.cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding", "BCFIPS");
    }

    public KeyPair realizarCriacaoChavesAssimetricas() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BCFIPS");
        keyGen.initialize(2048, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    public byte[] encriptar(byte[] texto, PublicKey chave) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, chave, new SecureRandom());
        return cipher.doFinal(texto);
    }

    public String decriptar(byte[] texto, PrivateKey chave) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, chave);
        return new String(cipher.doFinal(texto));
    }

}
