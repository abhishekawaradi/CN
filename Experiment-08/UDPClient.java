import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** Sends one message to UDPServer and displays its reply. */
public class UDPClient {
    private static final int PORT = 9876;
    private static final int MAX_DATAGRAM_SIZE = 2048;

    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a message (type quit to stop the server): ");
            String message = scanner.nextLine();
            byte[] requestBytes = message.getBytes(StandardCharsets.UTF_8);
            if (requestBytes.length > MAX_DATAGRAM_SIZE) {
                System.out.println("Message is too long for this lab datagram.");
                return;
            }

            InetAddress loopback = InetAddress.getByName("127.0.0.1");
            try (DatagramSocket client = new DatagramSocket()) {
                client.setSoTimeout(5000);
                DatagramPacket request = new DatagramPacket(requestBytes, requestBytes.length,
                        loopback, PORT);
                client.send(request);

                byte[] buffer = new byte[MAX_DATAGRAM_SIZE];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                client.receive(reply);
                String response = new String(reply.getData(), reply.getOffset(),
                        reply.getLength(), StandardCharsets.UTF_8);
                System.out.println("FROM SERVER: " + response);
            }
        }
    }
}
