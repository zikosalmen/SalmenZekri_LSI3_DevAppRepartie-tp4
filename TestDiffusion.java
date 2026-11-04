import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Test automatise de la diffusion (Activite 3) :
 * simule 2 clients UDP et verifie que le message du client A
 * est bien diffuse au client B (mais pas a A lui-meme).
 */
public class TestDiffusion {
    public static void main(String[] args) throws Exception {
        String serverIp = args.length > 0 ? args[0] : "127.0.0.1";
        int serverPort = args.length > 1 ? Integer.parseInt(args[1]) : 1234;

        DatagramSocket clientA = new DatagramSocket();
        DatagramSocket clientB = new DatagramSocket();
        InetSocketAddress serverAddr = new InetSocketAddress(serverIp, serverPort);

        // 1. Client B envoie un premier message pour enregistrer son adresse aupres du serveur
        String joinMsg = "[Bob] a rejoint le chat.";
        byte[] joinData = joinMsg.getBytes();
        clientB.send(new DatagramPacket(joinData, joinData.length, serverAddr));

        // Petit delai pour s'assurer que le serveur a traite l'inscription de B
        Thread.sleep(200);

        // 2. Client A envoie un message au serveur
        String msg = "[Alice] : Bonjour tout le monde";
        byte[] data = msg.getBytes();
        clientA.send(new DatagramPacket(data, data.length, serverAddr));

        // 3. Le serveur doit diffuser le message de Alice au client B
        byte[] buffer = new byte[1024];
        DatagramPacket received = new DatagramPacket(buffer, buffer.length);
        clientB.setSoTimeout(5000);
        try {
            clientB.receive(received);
            String recvMsg = new String(received.getData(), 0, received.getLength());
            if (msg.equals(recvMsg)) {
                System.out.println("TEST DIFFUSION OK : client B a recu \"" + recvMsg + "\"");
            } else {
                System.out.println("TEST DIFFUSION KO : message inattendu \"" + recvMsg + "\"");
            }
        } catch (java.net.SocketTimeoutException e) {
            System.out.println("TEST DIFFUSION KO : client B n'a rien recu (timeout)");
        }

        clientA.close();
        clientB.close();
        System.exit(0);
    }
}
