package utils.crypto;

import utils.PrintUtils;
import utils.Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class DH {

    protected Cipher cipher;
    protected byte iv[];
    protected IvParameterSpec ivSpec;
    private SecretKeySpec aesKey;
    private byte[] encodedParams;

    public byte[] getEncodedParams() {
        return encodedParams;
    }

    public DH() throws Exception {
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        iv = new byte[16];

        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        random.nextBytes(iv);
        ivSpec = new IvParameterSpec(iv);
    }

    public void gerarChaveAes(byte[] chaveCompartilhada) {
        aesKey = new SecretKeySpec(chaveCompartilhada, 0, 16, "AES");
    }

    public byte[] encriptar(String texto) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] result =  cipher.doFinal(texto.getBytes("UTF-8"));
        encodedParams = cipher.getParameters().getEncoded();
        return result;
    }

    public String decriptar(byte[] texto, byte[] encodedParams) throws Exception {
        AlgorithmParameters aesParams = AlgorithmParameters.getInstance("AES");
        aesParams.init(encodedParams);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, aesParams);
        return new String(cipher.doFinal(texto));

    }

    public class DHAlice {

        private KeyAgreement aliceKeyAgree;
        private byte[] aliceSharedSecret;

        public byte[] getAliceSharedSecret() {
            return aliceSharedSecret;
        }

        public byte[] obterChavePublicaEncoded() throws Exception{
            KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
            aliceKpairGen.initialize(2048);
            KeyPair aliceKpair = aliceKpairGen.generateKeyPair();
            aliceKeyAgree = KeyAgreement.getInstance("DH");
            aliceKeyAgree.init(aliceKpair.getPrivate());
            return aliceKpair.getPublic().getEncoded();
        }

        public void realizarAcordoDeChave(byte[] bobPubKeyEnc) throws Exception{
            KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(bobPubKeyEnc);
            PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);
            aliceKeyAgree.doPhase(bobPubKey, true);
        }

        public int gerarChaveSecreta(){
            aliceSharedSecret = aliceKeyAgree.generateSecret();
            new PrintUtils().printNormal("Chave secreta de Alice:" + Utils.toHexString(aliceSharedSecret));
            return aliceSharedSecret.length;
        }
    }

    public class DHBob {

        private KeyAgreement bobKeyAgree;
        private PublicKey alicePubKey;
        private byte[] bobSharedSecret;

        public byte[] getBobSharedSecret() {
            return bobSharedSecret;
        }

        public byte[] obterChavePublicaEncoded(byte[] alicePubKeyEnc) throws Exception{
            KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(alicePubKeyEnc);

            alicePubKey = bobKeyFac.generatePublic(x509KeySpec);
            DHParameterSpec dhParamFromAlicePubKey = ((DHPublicKey)alicePubKey).getParams();

            KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
            bobKpairGen.initialize(dhParamFromAlicePubKey);
            KeyPair bobKpair = bobKpairGen.generateKeyPair();

            bobKeyAgree = KeyAgreement.getInstance("DH");
            bobKeyAgree.init(bobKpair.getPrivate());
            return bobKpair.getPublic().getEncoded();
        }

        public void realizarAcordoDeChave() throws Exception{
            bobKeyAgree.doPhase(alicePubKey, true);
        }

        public void gerarChaveSecreta(int aliceLength) throws Exception{
            bobSharedSecret = new byte[aliceLength];
            /** Não é usado ?
            int bobLen;
            bobLen = bobKeyAgree.generateSecret(bobSharedSecret, 0);
            */
            new PrintUtils().printNormal("Chave secreta de Bob:" + Utils.toHexString(bobSharedSecret));
        }
    }

}
