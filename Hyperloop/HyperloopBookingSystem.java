import java.util.*;

class Route {
    String from, to;
    int distance;

    Route(String from, String to, int distance) {
        this.from = from;
        this.to = to;
        this.distance = distance;
    }
}

class Passenger implements Comparable<Passenger> {
    String name;
    int age;
    String destination;

    Passenger(String name, int age, String destination) {
        this.name = name;
        this.age = age;
        this.destination = destination;
    }

    @Override
    public int compareTo(Passenger other) {
        return Integer.compare(other.age, this.age); // Oldest first
    }
}

public class HyperloopBookingSystem {
    private static Map<String, List<Route>> routes = new HashMap<>();
    private static PriorityQueue<Passenger> passengerQueue = new PriorityQueue<>();
    private static boolean isInitialized = false;
    private static String startingStation;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String[] command = sc.nextLine().split(" ");
            switch (command[0]) {
                case "INIT":
                    initializeSystem(command, sc);
                    break;
                case "ADD_PASSENGER":
                    addPassengers(command, sc);
                    break;
                case "START_POD":
                    startPod(command);
                    break;
                case "PRINT_Q":
                    printQueue();
                    break;
                default:
                    System.out.println("Invalid command.");
            }
        }
    }

    private static void initializeSystem(String[] command, Scanner sc) {
        if (command.length < 3) {
            System.out.println("Invalid INIT command.");
            return;
        }

        int n = Integer.parseInt(command[1]);
        startingStation = command[2];
        routes.clear();
        for (int i = 0; i < n; i++) {
            String[] routeInfo = sc.nextLine().split(" ");
            String from = routeInfo[0];
            String to = routeInfo[1];
            int distance = Integer.parseInt(routeInfo[2]);

            routes.putIfAbsent(from, new ArrayList<>());
            routes.putIfAbsent(to, new ArrayList<>());

            routes.get(from).add(new Route(from, to, distance));
            routes.get(to).add(new Route(to, from, distance));
        }
        isInitialized = true;
        System.out.println("System initialized.");
    }

    private static void addPassengers(String[] command, Scanner sc) {
        if (!isInitialized) {
            System.out.println("System not initialized.");
            return;
        }
        int n = Integer.parseInt(command[1]);
        for (int i = 0; i < n; i++) {
            String[] passengerInfo = sc.nextLine().split(" ");
            String name = passengerInfo[0];
            int age = Integer.parseInt(passengerInfo[1]);
            String destination = passengerInfo[2];
            passengerQueue.offer(new Passenger(name, age, destination));
        }
        System.out.println("Passengers added.");
    }

    private static void startPod(String[] command) {
        if (!isInitialized) {
            System.out.println("System not initialized.");
            return;
        }
        int n = Integer.parseInt(command[1]);
        for (int i = 0; i < n; i++) {
            if (passengerQueue.isEmpty()) {
                System.out.println("No passengers in queue.");
                return;
            }
            Passenger passenger = passengerQueue.poll();
            String route = findShortestRoute(startingStation, passenger.destination);
            System.out.println(passenger.name + " " + route);
        }
    }

    private static String findShortestRoute(String from, String to) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Route> pq = new PriorityQueue<>(Comparator.comparingInt(r -> r.distance));

        distances.put(from, 0);
        pq.add(new Route(null, from, 0));

        while (!pq.isEmpty()) {
            Route current = pq.poll();
            String currentStation = current.to;

            if (!routes.containsKey(currentStation)) continue;
            for (Route r : routes.get(currentStation)) {
                int newDist = distances.getOrDefault(currentStation, Integer.MAX_VALUE) + r.distance;
                if (newDist < distances.getOrDefault(r.to, Integer.MAX_VALUE)) {
                    distances.put(r.to, newDist);
                    previous.put(r.to, currentStation);
                    pq.add(new Route(r.from, r.to, newDist));
                }
            }
        }

        List<String> path = new ArrayList<>();
        for (String at = to; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return String.join(" ", path);
    }

    private static void printQueue() {
        if (!isInitialized) {
            System.out.println("System not initialized.");
            return;
        }
        System.out.println(passengerQueue.size());
        for (Passenger passenger : passengerQueue) {
            System.out.println(passenger.name + " " + passenger.age);
        }
    }
}
