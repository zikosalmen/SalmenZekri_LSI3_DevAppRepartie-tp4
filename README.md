# TP 4 : Sockets en mode non-connecté UDP (Chat C/S)

**Matière :** Développement d'applications réparties — LSI 3 (FSS)  
**Étudiant :** Salmen Zekri  
**Enseignantes :** Amal ABID & Zeineb DAOUD  
**Dépôt GitHub :** [https://github.com/zikosalmen/SalmenZekri_LSI3_DevAppRepartie-tp4](https://github.com/zikosalmen/SalmenZekri_LSI3_DevAppRepartie-tp4)

---

## 1. Synthèse

Le protocole **UDP** fonctionne en mode **non-connecté** :
* Aucun circuit virtuel ni connexion préalable (absence de `accept()` et de `connect()`).
* Les unités de données sont des **datagrammes** (`DatagramPacket`), chacun contenant la charge utile (octets) ainsi que l'adresse IP et le port destinataire.
* Une unique socket UDP (`DatagramSocket`) sert simultanément à la réception et à l'émission.

---

## 2. Étape 2 : Chat Unidirectionnel (Client vers Serveur)

### 2.1. Côté Serveur
Le serveur lie sa `DatagramSocket` au port `1234` avec `InetSocketAddress` et attend les datagrammes en boucle bloquante.

```java
// Extrait : Server.java (activite2)
InetSocketAddress socketAddress = new InetSocketAddress("0.0.0.0", 1234);
DatagramSocket socket = new DatagramSocket(socketAddress);

byte[] buffer = new byte[1024];
while (true) {
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    socket.receive(packet); // Réception bloquante

    String message = new String(packet.getData(), 0, packet.getLength());
    System.out.println("Message recu de " + packet.getAddress().getHostAddress()
            + ":" + packet.getPort() + " : " + message);
}
```

**Capture d'écran du terminal Serveur (Étape 2) :**
![Serveur Etape 2](captures/etape2_serveur.png)

---

### 2.2. Côté Client
Le client saisit son pseudonyme, formate chaque message sous la forme `[NomUtilisateur] : message` et l'envoie encapsulé dans un `DatagramPacket`.

```java
// Extrait : Client.java (activite2)
DatagramSocket socket = new DatagramSocket();
InetSocketAddress serverSocketAddress = new InetSocketAddress(InetAddress.getByName(SERVER_IP), 1234);

while (true) {
    String message = scanner.nextLine();
    String messageComplet = "[" + nomUtilisateur + "] : " + message;
    byte[] donnees = messageComplet.getBytes();

    DatagramPacket packet = new DatagramPacket(donnees, donnees.length, serverSocketAddress);
    socket.send(packet);
}
```

**Capture d'écran du terminal Client (Étape 2) :**
![Client Etape 2](captures/etape2_client.png)

---

## 3. Étape 3 : Chat Bidirectionnel et Diffusion Multi-Clients

### 3.1. Côté Serveur (Gestion des clients et diffusion)
Pour simuler la connexion en mode non-connecté, le serveur maintient une liste `List<SocketAddress>` des clients émetteurs. Dès réception d'un message, il le rediffuse à **tous les clients connus, excepté l'expéditeur**.

```java
// Extrait : Server.java (activite3)
private static final List<SocketAddress> clients = new ArrayList<>();

// 1. Enregistrement de l'adresse de l'expéditeur si non présente
SocketAddress expediteur = packet.getSocketAddress();
if (!clients.contains(expediteur)) {
    clients.add(expediteur);
    System.out.println("Nouveau client connecte : " + expediteur);
}

// 2. Diffusion à tous les autres clients enregistrés
byte[] donnees = message.getBytes();
for (SocketAddress client : clients) {
    if (!client.equals(expediteur)) {
        DatagramPacket diffusion = new DatagramPacket(donnees, donnees.length, client);
        socket.send(diffusion);
    }
}
```

**Capture d'écran du terminal Serveur avec diffusion (Étape 3) :**
![Serveur Etape 3](captures/etape3_serveur.png)

---

### 3.2. Côté Client (Architecture concurrente multi-thread)
Pour permettre d'envoyer et de recevoir simultanément en temps réel :
* Un **Thread de réception en arrière-plan** écoute en continu les datagrammes diffusés par le serveur.
* Le **Thread principal** gère la saisie clavier et l'envoi des messages.

```java
// Extrait : Client.java (activite3)
// Thread de réception asynchrone
Thread threadReception = new Thread(() -> {
    byte[] buffer = new byte[BUFFER_SIZE];
    while (true) {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet); // Réception du message diffusé
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("\n" + message);
        System.out.print("Votre message : ");
    }
});
threadReception.setDaemon(true);
threadReception.start();

// Thread principal : envoi des messages
while (true) {
    System.out.print("Votre message : ");
    String message = scanner.nextLine();
    String messageComplet = "[" + nomUtilisateur + "] : " + message;
    byte[] donnees = messageComplet.getBytes();
    socket.send(new DatagramPacket(donnees, donnees.length, serverSocketAddress));
}
```

**Captures d'écran des terminaux Clients (Étape 3 - Échange temps réel) :**

* **Client 1 — Alice :**
![Client 1](captures/etape3_client1.png)

* **Client 2 — Bob :**
![Client 2](captures/etape3_client2.png)

---

## 4. Test automatisé de validation de la diffusion

Un script autonome valide le bon fonctionnement de la diffusion UDP entre plusieurs sockets locales :

```java
// Extrait : TestDiffusion.java
// 1. Client B s'enregistre auprès du serveur
clientB.send(new DatagramPacket(joinData, joinData.length, serverAddr));

// 2. Client A diffuse son message
clientA.send(new DatagramPacket(data, data.length, serverAddr));

// 3. Client B vérifie la réception du message de Client A
clientB.receive(received);
System.out.println("TEST DIFFUSION OK : client B a recu \"" + recvMsg + "\"");
```

**Capture d'écran du résultat du test :**
![Test Diffusion](captures/test_diffusion.png)

---

## 5. Livrables (Plateforme ENT)

1. **Lien du repository GitHub :** [https://github.com/zikosalmen/SalmenZekri_LSI3_DevAppRepartie-tp4](https://github.com/zikosalmen/SalmenZekri_LSI3_DevAppRepartie-tp4)
2. **Fichier `README.md` :** Présente la synthèse, les extraits de code requis et les captures d'écran des terminaux pour chaque phase.
