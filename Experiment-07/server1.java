import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** TCP file server for the lab; requested text files live in ./shared_files. */
public class server1 {
    private static final int PORT = 4000;
    private static final Path SHARED_DIRECTORY = Paths.get("shared_files").toAbsolutePath().normalize();

    private static void serve(Socket socket) throws IOException {
        try (Socket connection = socket;
             BufferedReader request = new BufferedReader(
                     new java.io.InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter response = new PrintWriter(connection.getOutputStream(), true)) {
            String fileName = request.readLine();
            if (fileName == null || fileName.trim().isEmpty()) {
                response.println("ERROR: no file name was supplied.");
                return;
            }

            Path requestedFile = SHARED_DIRECTORY.resolve(fileName.trim()).normalize();
            if (!requestedFile.startsWith(SHARED_DIRECTORY) || !Files.isRegularFile(requestedFile)) {
                response.println("ERROR: file not found in the server's shared_files directory.");
                return;
            }

            response.println("OK");
            try (BufferedReader file = Files.newBufferedReader(requestedFile, StandardCharsets.UTF_8)) {
                String line;
                while ((line = file.readLine()) != null) {
                    response.println(line);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Files.createDirectories(SHARED_DIRECTORY);
        InetAddress loopback = InetAddress.getByName("127.0.0.1");
        try (ServerSocket server = new ServerSocket(PORT, 50, loopback)) {
            System.out.println("Server ready on 127.0.0.1:" + PORT);
            System.out.println("Place text files in: " + SHARED_DIRECTORY);
            while (true) {
                try {
                    serve(server.accept());
                } catch (IOException error) {
                    System.err.println("Could not serve request: " + error.getMessage());
                }
            }
        }
    }
}
