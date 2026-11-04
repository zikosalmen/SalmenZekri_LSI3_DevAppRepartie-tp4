package activite3.serverPackage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * Activite 3 - Etape 1 et 3 : Serveur de chat bidirectionnel (UDP)
 *                              avec diffusion a plusieurs clients
 *
 * Etape 1 : Le serveur maintient une liste des adresses (SocketAddress)
 *           de tous les clients qui ont envoye un message
 *           (pour simuler la "connexion").
 *
 * Etape 3 : Lorsqu'un message est recu chez le serveur :
 *           - Il enregistre l'adresse de l'expediteur s'il ne l'a pas deja.
 *           - Il transmet le message a tous les autres clients dont
 *             l'adresse est enregistree, a l'exception de l'expediteur.
 *
 * Le mode non-connecte (UDP) n'offre qu'une seule socket : le serveur
 * recoit et emet des datagrammes sur la meme DatagramSocket.
 */
public class Server {

    // ---------------------------------------------------------------
    // Adresse IP et port d'ecoute --- "0.0.0.0" pour ecouter sur toutes les interfaces
    // ---------------------------------------------------------------
    private static final String SERVER_IP  = "0.0.0.0";
    private static final int    SERVER_PORT = 1234;

    // Taille maximale du tampon de reception (octets)
    private static final int BUFFER_SIZE = 1024;

    // Liste des adresses des clients connectes (simulee par l'envoi d'un message)
    private static final List<SocketAddress> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            // --- Liaison avec InetAddress + InetSocketAddress ---
            InetAddress inetAddress = InetAddress.getByName(SERVER_IP);
            InetSocketAddress socketAddress = new InetSocketAddress(inetAddress, SERVER_PORT);

            DatagramSocket socket = new DatagramSocket(socketAddress);

            System.out.println("=== Serveur de chat UDP (Activite 3 - Diffusion) ===");
            System.out.println("Adresse IP  : " + SERVER_IP);
            System.out.println("Port        : " + SERVER_PORT);
            System.out.println("En attente de reception de datagrammes...");

            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                // Creation du DatagramPacket de reception
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                // Reception bloquante d'un datagramme envoye par un client
                socket.receive(packet);

                // Extraction du contenu du message (texte)
                String message = new String(packet.getData(), 0, packet.getLength());

                // Adresse de l'expediteur (IP + Port)
                SocketAddress expediteur = packet.getSocketAddress();

                // --- Etape 3 : Enregistrement de l'expediteur s'il est inconnu ---
                if (!clients.contains(expediteur)) {
                    clients.add(expediteur);
                    System.out.println("Nouveau client connecte : " + expediteur
                            + " (total : " + clients.size() + " client(s))");
                }

                // Affichage du message recu avec l'adresse de l'expediteur
                System.out.println("Message recu de " + expediteur + " : " + message);

                // --- Etape 3 : Diffusion du message a tous les autres clients ---
                byte[] donnees = message.getBytes();
                for (SocketAddress client : clients) {
                    // A l'exception de l'expediteur
                    if (!client.equals(expediteur)) {
                        DatagramPacket diffusion = new DatagramPacket(
                                donnees, donnees.length, client);
                        socket.send(diffusion);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
