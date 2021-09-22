package Test;

import javafx.util.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.shdevelopment.Crypto.Crypto;
import org.shdevelopment.Structures.Contact;
import org.shdevelopment.Structures.CustomException;
import org.shdevelopment.SysInfo.Log;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class CryptoTest {

    private static MockedStatic<Log> mockedSettings;

    @BeforeClass
    public static void init(){
        mockedSettings = mockStatic(Log.class);
    }

    @AfterClass
    public static void close() {
        mockedSettings.close();
    }

    private Pair<PublicKey, PrivateKey> generateRSATestKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        return new Pair<>(keyPair.getPublic(), keyPair.getPrivate());
    }

    private SecretKey generateSymmetricTestKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    @Test
    public void testDecryptMessage() throws NoSuchAlgorithmException, CustomException.ErrorDecryptingMessage {
        String message = "A test message";
        Pair<PublicKey, PrivateKey> keypair = generateRSATestKeys();
        SecretKey key = generateSymmetricTestKey();
        Contact contact = new Contact("0.0.0.0", "ContactA", keypair.getKey());
        contact.setAes(key);

        byte[] encryptedMessage = Crypto.encryptMessage(message, contact);
        String result = Crypto.decryptMessage(encryptedMessage, key);
        assertEquals(message, result);
    }

    @Test(expected = CustomException.ErrorDecryptingMessage.class)
    public void testBadKeyDecryptMessage() throws Exception {
        String message = "A test message";
        Pair<PublicKey, PrivateKey> keypair = generateRSATestKeys();
        SecretKey key1 = generateSymmetricTestKey();
        SecretKey key2 = generateSymmetricTestKey();
        Contact contact = new Contact("0.0.0.0", "ContactA", keypair.getKey());
        contact.setAes(key1);

        byte[] encryptedMessage = Crypto.encryptMessage(message, contact);

        Crypto.decryptMessage(encryptedMessage, key2);
    }

}