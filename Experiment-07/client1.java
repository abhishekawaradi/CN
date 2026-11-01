import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** Requests a text file from server1 and prints its contents. */
public class client1 {
    private static final int PORT = 4000;

    public static void main(String[] args) throws Exception {
        String fileName;
        if (args.length > 0) {
            fileName = args[0];
        } else {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Enter the file name: ");
                fileName = scanner.nextLine().trim();
            }
        }

        try (Socket socket = new Socket("127.0.0.1", PORT);
             PrintWriter request = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader response = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            request.println(fileName);
            String status = response.readLine();
            if (status == null) {
                System.out.println("The server closed the connection without a response.");
                return;
            }
            if (!"OK".equals(status)) {
                System.out.println(status);
                return;
            }

            String line;
            while ((line = response.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
