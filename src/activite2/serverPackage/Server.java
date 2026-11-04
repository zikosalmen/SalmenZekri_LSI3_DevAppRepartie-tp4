package activite2.serverPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.IOException;

/**
 * Activite 2 - Etape 1 : Serveur de chat unidirectionnel (UDP)
 *
 * Le serveur ecoute sur le port 1234 en mode non-connecte (UDP).
 * Il recoit des datagrammes envoyes par le client et les affiche
 * en indiquant l'adresse (IP et Port) de l'expediteur et le contenu
 * du message.
 *
 * Caracteristiques du mode non-connecte (UDP) :
 *   - Pas de procedure d'etablissement de connexion (pas de accept()).
 *   - Les donnees sont transmises sous forme de datagrammes
 *     (tableaux d'octets) via la classe DatagramPacket.
 *   - La socket serveur est unique : DatagramSocket liee au port d'ecoute.
 */
public class Server {

    // ---------------------------------------------------------------
    // Adresse IP et port d'ecoute --- MEME VALEUR cote Client
    // "0.0.0.0" permet d'ecouter sur toutes les interfaces (localhost, Wi-Fi, LAN).
    // ---------------------------------------------------------------
    private static final String SERVER_IP  = "0.0.0.0";
    private static final int    SERVER_PORT = 1234;

    // Taille maximale du tampon de reception (octets)
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try {
            // --- Liaison avec InetAddress + InetSocketAddress ---
            InetAddress inetAddress = InetAddress.getByName(SERVER_IP);
            InetSocketAddress socketAddress = new InetSocketAddress(inetAddress, SERVER_PORT);

            DatagramSocket socket = new DatagramSocket(socketAddress);

            System.out.println("=== Serveur de chat UDP (Activite 2 - Etape 1) ===");
            System.out.println("Adresse IP  : " + SERVER_IP);
            System.out.println("Port        : " + SERVER_PORT);
            System.out.println("En attente de reception de datagrammes...");

            // Tampon de reception des donnees du datagramme
            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                // Creation du DatagramPacket de reception
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Reception bloquante d'un datagramme envoye par un client
                socket.receive(packet);

                // Extraction du contenu du message (texte)
                String message = new String(packet.getData(), 0, packet.getLength());

                // Adresse (IP et Port) de l'expediteur
                InetAddress clientIP  = packet.getAddress();
                int          clientPort = packet.getPort();

                // Affichage du message recupere avec l'adresse de l'expediteur
                System.out.println("Message recu de " + clientIP.getHostAddress()
                        + ":" + clientPort + " : " + message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
