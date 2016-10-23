/**
 * Created by Ramin on 21.10.2016.
 */

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Encryption {

    //Für RSA-Verschlüsselung (Asymmetrisch)
    private PublicKey publicKey;
    private PrivateKey privateKey;

    //Für AES-Verschlüsselung (Symmetrisch)
    private SecretKey symmetricKey;

    //Der Ver- und Entschlüssler
    private Cipher cipher;

    public Encryption(String mode) {
        if(mode.equals("RSA")) {
            try {
                //Erstellt einen Private und Public Key
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                //SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
                generator.initialize(1024);
                KeyPair keyPair = generator.generateKeyPair();

                publicKey = keyPair.getPublic();
                privateKey = keyPair.getPrivate();

                //Initialisierung des Ciphers auf das RSA-Verfahren
                cipher = Cipher.getInstance("RSA");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(mode.equals("AES")) {
            try {
                //Erstellen eines Generators zum Generieren von einem Schlüssel
                KeyGenerator generator = KeyGenerator.getInstance("AES");
                generator.init(128);

                //Generiert den symmetrischen Schlüssel und speichert diesen
                this.symmetricKey = generator.generateKey();

                //Initialisierung des Ciphers auf das AES-Verfahren
                cipher = Cipher.getInstance("AES");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Entschlüsselt einen Text mit dem eigenem Private bzw. symmetrischen Key, je nachdem welches Verfahren man gewählt hat.
     * @param text Verschlüsselter Text
     * @return Entschlüsselter Text, null, wenn Fehler aufgetreten ist.
     */
    public String decrypt (String text) {
        Key key;
        //Prüft ob das Objekt auf das AES- oder RAS-Verfahren initialisiert wurde
        if(symmetricKey != null)
            key = symmetricKey;
        else
            key = privateKey;
        try {
            //Führt die Entschlüsselung durch
            cipher.init(Cipher.DECRYPT_MODE, key);
            return DatatypeConverter.printHexBinary(cipher.doFinal(DatatypeConverter.parseHexBinary(text)));
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verschlüsselt einen Text mit dem, als Parameter übergebenem, Key.
     * @param text Zu verschlüsselnder Text
     * @param keyAsString Schlüssel, entweder symmetrisch oder der Public Key
     * @param mode Das Verfahren, welches zur Verschlüsselung eingesetzt werden soll
     * @return Verschlüsselter Text, null, wenn Fehler aufgetreten ist.
     */
     public static String encrypt (String text, String keyAsString, String mode) {
        try {
            Cipher encryptor = Cipher.getInstance(mode);
            Key key = null;
            //Prüft welches Verfahren gewünscht ist
            if(mode.equals("RSA")) {
                key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(DatatypeConverter.parseHexBinary(keyAsString)));
            } else if(mode.equals("AES")) {
                key = new SecretKeySpec(DatatypeConverter.parseHexBinary(keyAsString),"AES");

            }
            encryptor.init(Cipher.ENCRYPT_MODE, key);
            return DatatypeConverter.printHexBinary(encryptor.doFinal(DatatypeConverter.parseHexBinary(text)));

        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSymmetricKey () {
        return DatatypeConverter.printHexBinary(symmetricKey.getEncoded());
    }

    public String getPublicKey () {
        return DatatypeConverter.printHexBinary(publicKey.getEncoded());
    }

    public String getPrivateKey () {
        return DatatypeConverter.printHexBinary(privateKey.getEncoded());
    }


}
