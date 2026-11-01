import java.util.Scanner;

/** Leaky-bucket congestion-control simulation using the manual's fixed settings. */
public class lab7 {
    private static final int BUCKET_CAPACITY = 4;
    private static final int LEAK_RATE = 3;
    private static final int MAX_CLOCKS = 20;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the number of clock intervals (1-" + MAX_CLOCKS + "): ");
            if (!scanner.hasNextInt()) {
                System.out.println("Interval count must be an integer.");
                return;
            }
            int intervalCount = scanner.nextInt();
            if (intervalCount < 1 || intervalCount > MAX_CLOCKS) {
                System.out.println("Enter between 1 and " + MAX_CLOCKS + " intervals.");
                return;
            }

            int[] arrivals = new int[intervalCount];
            System.out.println("Enter the arriving packet counts for each interval:");
            for (int i = 0; i < intervalCount; i++) {
                if (!scanner.hasNextInt()) {
                    System.out.println("Packet counts must be integers.");
                    return;
                }
                arrivals[i] = scanner.nextInt();
                if (arrivals[i] < 0) {
                    System.out.println("Packet counts cannot be negative.");
                    return;
                }
            }

            int queued = 0;
            System.out.println("\nClock\tIncoming\tAccepted\tSent\tRemaining");
            for (int i = 0; i < intervalCount; i++) {
                int accepted = arrivals[i];
                boolean dropped = queued + accepted > BUCKET_CAPACITY;
                if (dropped) {
                    accepted = 0; // The lab model drops the whole arrival burst.
                } else {
                    queued += accepted;
                }

                int sent = Math.min(queued, LEAK_RATE);
                queued -= sent;
                System.out.printf("%d\t%d\t\t%s\t\t%d\t%d%n", i + 1, arrivals[i],
                        dropped ? "dropped" : Integer.toString(accepted), sent, queued);
            }
            System.out.println("Bucket capacity = " + BUCKET_CAPACITY
                    + ", leak rate = " + LEAK_RATE + " packets per interval.");
        }
    }
}
