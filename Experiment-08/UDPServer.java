import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/** Receives a UTF-8 datagram and replies with its uppercase form. */
public class UDPServer {
    private static final int PORT = 9876;
    private static final int MAX_DATAGRAM_SIZE = 2048;

    public static void main(String[] args) throws Exception {
        InetAddress loopback = InetAddress.getByName("127.0.0.1");
        try (DatagramSocket server = new DatagramSocket(new InetSocketAddress(loopback, PORT))) {
            System.out.println("UDP server is listening on 127.0.0.1:" + PORT);
            byte[] buffer = new byte[MAX_DATAGRAM_SIZE];

            while (true) {
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                server.receive(incoming);
                String message = new String(incoming.getData(), incoming.getOffset(),
                        incoming.getLength(), StandardCharsets.UTF_8);
                System.out.println("Received: " + message);

                String reply = "quit".equalsIgnoreCase(message.trim())
                        ? "Server stopping" : message.toUpperCase();
                byte[] replyBytes = reply.getBytes(StandardCharsets.UTF_8);
                DatagramPacket outgoing = new DatagramPacket(replyBytes, replyBytes.length,
                        incoming.getAddress(), incoming.getPort());
                server.send(outgoing);

                if ("quit".equalsIgnoreCase(message.trim())) {
                    break;
                }
            }
        }
    }
}
