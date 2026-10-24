package bikeshare;

import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Desk {
    public static final Map<String, List<Integer>> stations = new LinkedHashMap<>();
    public static final Map<Integer, String> members = new LinkedHashMap<>();
    public static final Map<Integer, Integer> types = new LinkedHashMap<>();

    static {
        stations.put("library", new ArrayList<>(List.of(1, 2)));
        stations.put("canteen", new ArrayList<>(List.of(3, 4)));
        stations.put("dorm", new ArrayList<>(List.of(5, 6)));
    }

    private Desk() {
    }

    public static String process(boolean returning, String[] words) throws IOException {
        String station = words[returning ? 2 : 3].toLowerCase(java.util.Locale.ROOT);
        if (!stations.containsKey(station)) {
            throw new IllegalArgumentException("Unknown station");
        }
        if (returning) {
            int bike = Integer.parseInt(words[1]);
            int minutes = Integer.parseInt(words[3]);
            if (!members.containsKey(bike)) {
                throw new IllegalArgumentException("Bike is not rented");
            }
            if (minutes < 0) {
                throw new IllegalArgumentException("Minutes must be non-negative");
            }
            long fee = (long) minutes * (types.get(bike) == 1 ? 2 : 3);
            String memberType = types.get(bike) == 1 ? "student" : "staff";
            try (BufferedWriter writer = Files.newBufferedWriter(Path.of("rides.csv"),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(bike + "," + members.get(bike) + "," + memberType + ","
                        + station + "," + minutes + "," + fee);
                writer.newLine();
            }
            stations.get(station).add(bike);
            members.remove(bike);
            types.remove(bike);
            return "Receipt: bike " + bike + ", " + minutes + " minutes, " + fee + " baht";
        }
        String member = words[1];
        if (!member.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Member ID must use letters, digits, underscores or hyphens");
        }
        if (members.containsValue(member)) {
            throw new IllegalArgumentException("Member already has a bike");
        }
        int type = typeCode(words[2]);
        List<Integer> bikes = stations.get(station);
        if (bikes.isEmpty()) {
            throw new IllegalArgumentException("No bikes at this station");
        }
        int bike = bikes.remove(0);
        members.put(bike, member);
        types.put(bike, type);
        return "Rented bike " + bike;
    }

    private static int typeCode(String text) {
        if (text.equalsIgnoreCase("student")) {
            return 1;
        }
        if (text.equalsIgnoreCase("staff")) {
            return 2;
        }
        throw new IllegalArgumentException("Member type must be student or staff");
    }
}
