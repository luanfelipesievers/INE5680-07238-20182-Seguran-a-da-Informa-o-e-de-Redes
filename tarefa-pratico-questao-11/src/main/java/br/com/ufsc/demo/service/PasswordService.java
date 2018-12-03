package br.com.ufsc.demo.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.xml.bind.DatatypeConverter;

@Service
public class PasswordService {

    
    public String createHash(String password) throws Exception {
        return createHash(password.toCharArray());
    }

    private String createHash(char[] password) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[24];
        random.nextBytes(salt);

        int PBKDF2_ITERATIONS = 64000;
        byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, 18);
        int hashSize = hash.length;

        // format: algorithm:iterations:hashSize:salt:hash
        return "sha1:" +
                PBKDF2_ITERATIONS +
                ":" + hashSize +
                ":" +
                toBase64(salt) +
                ":" +
                toBase64(hash);
    }

    public boolean verifyPassword(String password, String correctHash) throws Exception {
        return verifyPassword(password.toCharArray(), correctHash);
    }

    private boolean verifyPassword(char[] password, String correctHash) throws Exception {
        String[] params = correctHash.split(":");
        if (params.length != 5) {
            throw new Exception("Fields are missing from the password hash.");
        }

        if (!params[0].equals("sha1")) {
            throw new Exception("Unsupported hash type.");
        }

        int iterations = Integer.parseInt(params[1]);

        if (iterations < 1) {
            throw new Exception("Invalid number of iterations. Must be >= 1.");
        }

        byte[] salt = fromBase64(params[3]);

        byte[] hash = fromBase64(params[4]);

        int storedHashSize = Integer.parseInt(params[2]);

        if (storedHashSize != hash.length) {
            throw new Exception("Hash length doesn't match stored hash length.");
        }

        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        return slowEquals(hash, testHash);
    }

    private boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }

    private static byte[] fromBase64(String hex) throws IllegalArgumentException {
        return DatatypeConverter.parseBase64Binary(hex);
    }

    private static String toBase64(byte[] array) {
        return DatatypeConverter.printBase64Binary(array);
    }
}
