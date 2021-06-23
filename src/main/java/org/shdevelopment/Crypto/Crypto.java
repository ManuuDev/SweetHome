package org.shdevelopment.Crypto;

import org.shdevelopment.Structures.Contact;
import org.shdevelopment.SysInfo.Level;
import org.shdevelopment.SysInfo.Log;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;

import static org.shdevelopment.SysInfo.Level.ERROR;

public class Crypto {

    private static PublicKey publicKey;
    private static PrivateKey privateKey;
    private final static int IV_SIZE = 16;

    public static void init() {
        generateRSAKeyPair();
    }

    public static void generateRSAKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
        } catch (NoSuchAlgorithmException ex) {

        }
    }

    public static byte[] encryptWithRSA(PublicKey keyRSA, SecretKey secretKey) {

        try {
            byte[] encodedAES = secretKey.getEncoded();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, keyRSA);
            return cipher.doFinal(encodedAES);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException ex) {
            Log.addMessage(ex.getMessage(), Level.ERROR);
        }

        return null;
    }

    public static SecretKey decryptSymmetricKey(byte[] keyAES) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] encodedAES = cipher.doFinal(keyAES);
            return (new SecretKeySpec(encodedAES, 0, encodedAES.length, "AES"));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException ex) {
            Log.addMessage("Error al desencriptar por RSA: " + ex.getMessage(), ERROR);
        }
        return null;
    }

    public static SecretKey generateSymmetricKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256);
            return keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Log.addMessage(ex.getMessage(), Level.ERROR);
            return null;
        }
    }

    public static byte[] encryptMessage(String message, Contact contact) {

        try {

            byte[] messageArray = message.getBytes("UTF8");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            
            byte[] ivRandom = new byte[IV_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(ivRandom);

            IvParameterSpec iv = new IvParameterSpec(ivRandom);

            cipher.init(Cipher.ENCRYPT_MODE, contact.getAes(), iv);
            byte[] encryptedMessage = cipher.doFinal(messageArray);

            byte[] encryptedArray = new byte[encryptedMessage.length + IV_SIZE];
            System.arraycopy(ivRandom, 0, encryptedArray, 0, IV_SIZE);
            System.arraycopy(encryptedMessage, 0, encryptedArray, IV_SIZE, encryptedMessage.length);

            return encryptedArray;

        } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException
                | BadPaddingException | IllegalBlockSizeException
                | InvalidAlgorithmParameterException | NoSuchPaddingException ex) {
            Log.addMessage(ex.getMessage(), Level.ERROR);
        }
        return null;
    }

    public static String decryptMessage(byte[] message, SecretKey key) {
        byte[] decryptedMessage = Crypto.decryptMessageWithAES(message, key);
        return new String(decryptedMessage);
    }

    public static byte[] decryptMessageWithAES(byte[] encryptedArray, SecretKey key) {
        try {
            byte[] ivArray = new byte[IV_SIZE];
            System.arraycopy(encryptedArray, 0, ivArray, 0, IV_SIZE);
            IvParameterSpec iv = new IvParameterSpec(ivArray);

            int bytesOfMessage = encryptedArray.length - IV_SIZE;
            byte[] encryptedMessage = new byte[bytesOfMessage];
            System.arraycopy(encryptedArray, IV_SIZE, encryptedMessage, 0, bytesOfMessage);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            return cipher.doFinal(encryptedMessage);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException ex) {
            Log.addMessage(ex.getMessage(), Level.ERROR);
        }
        return null;
    }

    public static PublicKey getPublicKey() {
        return publicKey;
    }
}
