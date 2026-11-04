package activite2.clientPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Scanner;

/**
 * Activite 2 - Etape 1 : Client de chat unidirectionnel (UDP)
 *
 * Le client :
 *   - Demande a l'utilisateur d'entrer un Nom d'utilisateur.
 *   - Lit les messages saisis par l'utilisateur et les envoie au serveur
 *     sous forme de datagrammes UDP.
 *   - Chaque message est prefixe par le nom d'utilisateur
 *     (Ex : [NomUtilisateur] : Bonjour).
 *
 * Caracteristiques du mode non-connecte (UDP) :
 *   - Pas de connexion prealable avec le serveur (pas de connect()).
 *   - Chaque datagramme porte l'adresse de destination (IP + Port)
 *     dans l'objet DatagramPacket.
 */
public class Client {

    // ---------------------------------------------------------------
    // Adresse IP du serveur ("127.0.0.1" pour tester en local sur la meme machine)
    // ---------------------------------------------------------------
    private static final String SERVER_IP  = "127.0.0.1";
    private static final int    SERVER_PORT = 1234;

    public static void main(String[] args) {
        try {
            System.out.println("=== Client de chat UDP (Activite 2 - Etape 1) ===");

            // Saisie du nom d'utilisateur
            Scanner scanner = new Scanner(System.in);
            System.out.print("Veuillez entrer votre nom d'utilisateur : ");
            String nomUtilisateur = scanner.nextLine();

            // Socket UDP cliente (port attribue automatiquement par le systeme)
            DatagramSocket socket = new DatagramSocket();

            // Adresse de destination : serveur (IP + Port)
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            InetSocketAddress serverSocketAddress = new InetSocketAddress(serverAddress, SERVER_PORT);

            System.out.println("Envoi des messages au serveur : " + SERVER_IP + ":" + SERVER_PORT);

            while (true) {
                // Lecture du message saisi par l'utilisateur
                System.out.print("Votre message : ");
                String message = scanner.nextLine();

                // Prefixage du message par le nom d'utilisateur
                String messageComplet = "[" + nomUtilisateur + "] : " + message;

                // Conversion du message en tableau d'octets
                byte[] donnees = messageComplet.getBytes();

                // Creation du DatagramPacket d'envoi (donnees + adresse destination)
                DatagramPacket packet = new DatagramPacket(
                        donnees, donnees.length, serverSocketAddress);

                // Envoi du datagramme au serveur
                socket.send(packet);
                System.out.println("Message envoye au serveur.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
