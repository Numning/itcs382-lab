package bikeshare;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Commands: rent <memberId> <student|staff> <station>, return <bikeId> <station> <minutes>, list, quit");
        try (Scanner input = new Scanner(System.in)) {
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] words = line.split("\\s+");
                try {
                    switch (words[0]) {
                        case "rent", "return" -> {
                            if (words.length != 4) {
                                throw new IllegalArgumentException("Expected three arguments");
                            }
                            System.out.println(Desk.process(words[0].equals("return"), words));
                        }
                        case "list" -> Desk.stations.forEach((station, bikes) ->
                                System.out.println(station + ": " + bikes));
                        case "quit" -> {
                            return;
                        }
                        default -> System.err.println("Unknown command");
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid command: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Could not save ride: " + e.getMessage());
                }
            }
        }
    }
}
