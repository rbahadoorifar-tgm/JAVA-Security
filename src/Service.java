import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Ramin on 21.10.2016.
 */
public class Service {

    public static void main(String[] args) throws Exception{
        LDAPConnector ldapConnector = new LDAPConnector();
        Encryption encryption = new Encryption("RSA");

        //Der Public Key wird dem Server übergeben
        ldapConnector.setPublicKey(encryption.getPublicKey());

        //Erstellen eines Servers
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server erstellt! Warte auf Client...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client gefunden!\n");

        //Erstellen der Kanäle
        DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());

        //Warten auf Nachricht
        String encryptedMessage = dIn.readUTF();

        //Nachricht entschlüsseln und speichern als symmetrischen Key von Client
        String symmetricKeyOfClient = encryption.decrypt(encryptedMessage);

        System.out.println("Client sagt: " + symmetricKeyOfClient);


        //Der symmetrische Key wird verwendet um die Antwort zu verschlüsseln, die Nachricht wird zuerst in Hex umgewandelt
        String decryptedMessage = encryption.encrypt(stringToHex("Hey I see you!"), symmetricKeyOfClient, "AES");

        //Verschlüsselter Text wird gesendet
        dOut.writeUTF(decryptedMessage);
    }

    /**
     * Das Problem war, dass ich die lesbare Nachricht zuerst in das Hexadezimalsystem umwandeln musste, damit die Verschlüsselung funktionieren kann.
     * Leider konnte ich das Problem ohne diese Methode nicht lösen, da ich nicht wusste, wie man ein Text in das Hexadezimalsystem umwandeln kann.
     * Quelle: https://www.mkyong.com/java/how-to-convert-hex-to-ascii-in-java/
     * @param str Lesbarer Text
     * @return Lesbarer Text in Hexadezimalsystem
     */
    public static String stringToHex(String str){

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for(int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString((int)chars[i]));
        }

        return hex.toString();
    }
}
