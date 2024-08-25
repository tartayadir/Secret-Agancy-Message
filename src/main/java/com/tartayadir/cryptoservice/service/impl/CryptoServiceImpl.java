package com.tartayadir.cryptoservice.service.impl;

import com.tartayadir.cryptoservice.service.CryptoService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@Service
public class CryptoServiceImpl implements CryptoService {

    private static final int KEY_SIZE = 128; // 128 bits for AES
    private static final int IV_SIZE = 12;   // 12 bytes for GCM IV
    private static final int TAG_SIZE = 128; // 128 bits authentication tag length

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String PROVIDER_NAME = "BC";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public String generateKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM, PROVIDER_NAME);
        keyGen.init(KEY_SIZE, new SecureRandom());
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    @Override
    public String encrypt(String message, String key) throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER_NAME);

        byte[] iv = new byte[IV_SIZE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_SIZE, iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);

        byte[] cipherText = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        byte[] combinedIvAndCipherText = new byte[IV_SIZE + cipherText.length];
        System.arraycopy(iv, 0, combinedIvAndCipherText, 0, IV_SIZE);
        System.arraycopy(cipherText, 0, combinedIvAndCipherText, IV_SIZE, cipherText.length);

        return Base64.getEncoder().encodeToString(combinedIvAndCipherText);
    }

    @Override
    public String decrypt(String encryptedMessage, String key) throws NoSuchAlgorithmException, NoSuchProviderException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION, PROVIDER_NAME);

        byte[] combinedIvAndCipherText = Base64.getDecoder().decode(encryptedMessage);
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(combinedIvAndCipherText, 0, iv, 0, IV_SIZE);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_SIZE, iv);
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);

        byte[] cipherText = new byte[combinedIvAndCipherText.length - IV_SIZE];
        System.arraycopy(combinedIvAndCipherText, IV_SIZE, cipherText, 0, cipherText.length);

        byte[] decryptedText = cipher.doFinal(cipherText);
        return new String(decryptedText, StandardCharsets.UTF_8);
    }
}
