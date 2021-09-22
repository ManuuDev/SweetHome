package Test;

import javafx.util.Pair;
import junit.framework.TestCase;
import org.junit.Before;
import org.shdevelopment.Crypto.Crypto;
import org.shdevelopment.Structures.Contact;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;

import static org.mockito.MockitoAnnotations.initMocks;

public class CryptoTest extends TestCase {

    @Before
    public void before() {
        initMocks(this);
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

    public void testDecryptMessage() throws NoSuchAlgorithmException {
        String message = "A test message";
        Pair<PublicKey, PrivateKey> keypair = generateRSATestKeys();
        SecretKey key = generateSymmetricTestKey();
        Contact contact = new Contact("0.0.0.0", "ContactA", keypair.getKey());
        contact.setAes(key);

        byte[] encryptedMessage = Crypto.encryptMessage(message, contact);
        String result = Crypto.decryptMessage(encryptedMessage, key);
        assertEquals(message, result);
    }
    /*
    public void testBadKeyDecryptMessage() throws Exception {
        String message = "A test message";
        Pair<PublicKey, PrivateKey> keypair = generateRSATestKeys();
        SecretKey key1 = generateSymmetricTestKey();
        SecretKey key2 = generateSymmetricTestKey();
        Contact contact = new Contact("0.0.0.0", "ContactA", keypair.getKey());
        contact.setAes(key1);

        byte[] encryptedMessage = Crypto.encryptMessage(message, contact);

        String result = Crypto.decryptMessage(encryptedMessage, key2);
        //TODO Manejo de errores
        assertNull(result);
    }
    */
}