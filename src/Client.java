import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Created by Ramin on 21.10.2016.
 */
public class Client {

    public static void main(String[] args) throws Exception{
        LDAPConnector ldapConnector = new LDAPConnector();
        Encryption encryption = new Encryption("AES");

        //Anfordern des Public Keys vom LDAP-Server
        String publicKeyOfService = ldapConnector.getPublicKey();

        System.out.println("Verbinde zum Server...");
        //Verbinden zum Server
        Socket socket = new Socket("localhost", 8080);
        System.out.println("Verbunden!\n");

        //Erstellen der Kanäle
        DataInputStream dIn = new DataInputStream(socket.getInputStream());
        DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());

        //Eigener symmetrischer Key wird mit dem Public Key vom Service verschlüsselt
        String encryptedMessage = encryption.encrypt(encryption.getSymmetricKey(), publicKeyOfService, "RSA");

        System.out.println(encryptedMessage);
        //Senden der verschlüsselten Nachricht
        dOut.writeUTF(encryptedMessage);

        //Warten auf Antwort vom Service
        String encryptedResponse = dIn.readUTF();

        //Antwort mit eigenem symmetrischen Key entschlüsseln, danach wird die Antwort noch von Hex in ASCII umgewandelt.
        String decryptedMessage = hexToString(encryption.decrypt(encryptedResponse));

        System.out.println("Service antwortet: " + decryptedMessage);
    }

    /**
     * Das Problem war, dass ich die lesbare Nachricht zuerst in das Hexadezimalsystem umwandeln musste, damit die Verschlüsselung funktionieren kann.
     * Leider konnte ich das Problem ohne diese Methode nicht lösen, da ich nicht wusste, wie man ein Text in das Hexadezimalsystem umwandeln kann.
     * Quelle: https://www.mkyong.com/java/how-to-convert-hex-to-ascii-in-java/
     * @param hex Text in Hex
     * @return Lesbarer Text
     */
    public static String hexToString(String hex){

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);

            temp.append(decimal);
        }

        return sb.toString();
    }
}
