package keys;

import java.security.*;
import javax.crypto.Cipher;

/**
 * Classe responsável pela criação de chaves e métodos de criptografia
 * @author Cassiano
 * @author Henrique
 */

public class KeyManager {

    private PrivateKey priv;
    private PublicKey pub;

    public KeyManager() {
        try {
            // Gera as chaves pública e privada do processo pelo algoritmo RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
<<<<<<< HEAD
            keyGen.initialize(512);
=======
            keyGen.initialize(1024);
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e

            KeyPair pair = keyGen.generateKeyPair();
            priv = pair.getPrivate();
            pub = pair.getPublic();

        } catch (Exception e) {
            System.err.println("Caught exception: " + e.toString());
        }
    }
    
    // Método que criptografa uma String em um array de bytes usando a chave privada
    public static byte[] crypt(String texto, PrivateKey priv) {
        byte[] cipherText = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA"); 
            cipher.init(Cipher.ENCRYPT_MODE, priv);
            cipherText = cipher.doFinal(texto.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cipherText;
    }
    
    // Método que descriptografa um array de bytes em uma String usando a chave pública
    public static String decrypt(byte[] texto, PublicKey pub) {
        byte[] dectyptedText = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, pub);
            dectyptedText = cipher.doFinal(texto);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new String(dectyptedText);
    }

    public PrivateKey getPriv() {
        return priv;
    }

    public PublicKey getPub() {
        return pub;
    }

}
