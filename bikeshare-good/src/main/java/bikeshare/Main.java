package bikeshare;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BikeShare service = new BikeShare(new CsvRideStore(Path.of("rides.csv")));
        System.out.println("Commands: rent <memberId> <student|staff> <station>, return <bikeId> <station> <minutes>, list, quit");
        try (Scanner input = new Scanner(System.in)) {
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] words = line.split("\\s+");
                if (words[0].equals("quit")) {
                    return;
                }
                try {
                    execute(service, words);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid command: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Could not save ride: " + e.getMessage());
                }
            }
        }
    }

    private static void execute(BikeShare service, String[] words) throws IOException {
        switch (words[0]) {
            case "rent" -> {
                requireArguments(words);
                MemberType type = MemberType.valueOf(words[2].toUpperCase(Locale.ROOT));
                int bike = service.rent(words[1], type, words[3]);
                System.out.println("Rented bike " + bike);
            }
            case "return" -> {
                requireArguments(words);
                Ride ride = service.returnBike(Integer.parseInt(words[1]), words[2], Integer.parseInt(words[3]));
                System.out.printf("Receipt: bike %d, %d minutes, %d baht%n", ride.bikeId(), ride.minutes(), ride.fee());
            }
            case "list" -> service.availableBikes().forEach((station, bikes) ->
                    System.out.println(station + ": " + bikes));
            default -> System.err.println("Unknown command");
        }
    }

    private static void requireArguments(String[] words) {
        if (words.length != 4) {
            throw new IllegalArgumentException("Expected three arguments");
        }
    }
}
