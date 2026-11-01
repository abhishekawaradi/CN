import java.util.Scanner;

/** Bellman-Ford shortest paths with a path vector (the full route per node). */
public class ford {
    private static final long INFINITY = Long.MAX_VALUE / 4;

    private final int vertexCount;
    private final long[][] cost;
    private final long[] distance;
    private final int[] predecessor;

    private ford(int vertexCount, long[][] cost) {
        this.vertexCount = vertexCount;
        this.cost = cost;
        this.distance = new long[vertexCount + 1];
        this.predecessor = new int[vertexCount + 1];
    }

    private boolean evaluate(int source) {
        for (int vertex = 1; vertex <= vertexCount; vertex++) {
            distance[vertex] = INFINITY;
            predecessor[vertex] = -1;
        }
        distance[source] = 0;

        // Relax every edge at most V - 1 times; stop early if no route changes.
        for (int pass = 1; pass < vertexCount; pass++) {
            boolean changed = false;
            for (int from = 1; from <= vertexCount; from++) {
                if (distance[from] == INFINITY) {
                    continue;
                }
                for (int to = 1; to <= vertexCount; to++) {
                    if (cost[from][to] == INFINITY) {
                        continue;
                    }
                    long candidate = distance[from] + cost[from][to];
                    if (candidate < distance[to]) {
                        distance[to] = candidate;
                        predecessor[to] = from;
                        changed = true;
                    }
                }
            }
            if (!changed) {
                break;
            }
        }

        // A reachable negative cycle means no finite shortest path exists.
        for (int from = 1; from <= vertexCount; from++) {
            if (distance[from] == INFINITY) {
                continue;
            }
            for (int to = 1; to <= vertexCount; to++) {
                if (cost[from][to] != INFINITY
                        && distance[from] + cost[from][to] < distance[to]) {
                    return false;
                }
            }
        }
        return true;
    }

    private String pathTo(int source, int destination) {
        int[] reversePath = new int[vertexCount + 1];
        int length = 0;
        int current = destination;
        while (current != -1 && length < reversePath.length) {
            reversePath[length++] = current;
            if (current == source) {
                break;
            }
            current = predecessor[current];
        }
        if (length == 0 || reversePath[length - 1] != source) {
            return "unavailable";
        }

        StringBuilder path = new StringBuilder();
        for (int i = length - 1; i >= 0; i--) {
            if (path.length() > 0) {
                path.append(" -> ");
            }
            path.append(reversePath[i]);
        }
        return path.toString();
    }

    private void printRoutes(int source) {
        System.out.println("Destination\tDistance\tPath vector");
        for (int destination = 1; destination <= vertexCount; destination++) {
            if (distance[destination] == INFINITY) {
                System.out.println(destination + "\t\tunreachable\t-");
            } else {
                System.out.println(destination + "\t\t" + distance[destination]
                        + "\t\t" + pathTo(source, destination));
            }
        }
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the number of vertices: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Vertex count must be an integer.");
                return;
            }
            int vertexCount = scanner.nextInt();
            if (vertexCount < 1 || vertexCount > 100) {
                System.out.println("Enter between 1 and 100 vertices.");
                return;
            }

            long[][] cost = new long[vertexCount + 1][vertexCount + 1];
            System.out.println("Enter the adjacency matrix (0 means no edge off the diagonal):");
            for (int from = 1; from <= vertexCount; from++) {
                for (int to = 1; to <= vertexCount; to++) {
                    if (!scanner.hasNextLong()) {
                        System.out.println("The adjacency matrix must contain integers.");
                        return;
                    }
                    long value = scanner.nextLong();
                    cost[from][to] = from == to ? 0 : (value == 0 ? INFINITY : value);
                }
            }

            System.out.print("Enter the source vertex (1-" + vertexCount + "): ");
            if (!scanner.hasNextInt()) {
                System.out.println("Source vertex must be an integer.");
                return;
            }
            int source = scanner.nextInt();
            if (source < 1 || source > vertexCount) {
                System.out.println("Source vertex is outside the graph.");
                return;
            }

            ford routes = new ford(vertexCount, cost);
            if (!routes.evaluate(source)) {
                System.out.println("The graph contains a reachable negative-weight cycle.");
                return;
            }
            routes.printRoutes(source);
        }
    }
}
