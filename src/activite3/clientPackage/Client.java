package activite3.clientPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.Scanner;

/**
 * Activite 3 - Etape 2 : Client de chat bidirectionnel (UDP)
 *
 * Le client doit etre capable non seulement d'envoyer des messages
 * mais aussi de les recevoir en temps reel. Cela implique l'utilisation
 * de threads pour gerer l'envoi et la reception de maniere concurrente :
 *
 *   - Thread principal : lecture clavier et envoi des messages au serveur.
 *   - Thread de reception : boucle bloquante socket.receive() qui affiche
 *     les messages diffuses par le serveur.
 */
public class Client {

    // ---------------------------------------------------------------
    // Adresse IP du serveur ("127.0.0.1" pour tester en local sur la meme machine)
    // ---------------------------------------------------------------
    private static final String SERVER_IP  = "127.0.0.1";
    private static final int    SERVER_PORT = 1234;

    // Taille maximale du tampon de reception (octets)
    private static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) {
        try {
            System.out.println("=== Client de chat UDP (Activite 3 - Diffusion) ===");

            // Saisie du nom d'utilisateur
            Scanner scanner = new Scanner(System.in);
            System.out.print("Veuillez entrer votre nom d'utilisateur : ");
            String nomUtilisateur = scanner.nextLine();

            // Socket UDP cliente (port attribue automatiquement par le systeme)
            DatagramSocket socket = new DatagramSocket();

            // Adresse de destination : serveur (IP + Port)
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);
            InetSocketAddress serverSocketAddress = new InetSocketAddress(serverAddress, SERVER_PORT);

            // Message initial d'enregistrement pour simuler la connexion au serveur
            String messageConnexion = "[" + nomUtilisateur + "] a rejoint le chat.";
            byte[] donneesConnexion = messageConnexion.getBytes();
            DatagramPacket packetConnexion = new DatagramPacket(
                    donneesConnexion, donneesConnexion.length, serverSocketAddress);
            socket.send(packetConnexion);

            System.out.println("Chat connecte au serveur : " + SERVER_IP + ":" + SERVER_PORT);

            // --- Thread de reception : affiche les messages diffuses par le serveur ---
            Thread threadReception = new Thread(() -> {
                try {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while (true) {
                        // Creation du DatagramPacket de reception
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        // Reception bloquante du datagramme
                        socket.receive(packet);

                        // Extraction et affichage du message recu
                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("\n" + message);
                        System.out.print("Votre message : ");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            threadReception.setDaemon(true);
            threadReception.start();

            // --- Thread principal : lecture clavier et envoi des messages ---
            while (true) {
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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
