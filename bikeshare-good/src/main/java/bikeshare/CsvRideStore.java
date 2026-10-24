package bikeshare;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

public class CsvRideStore implements RideStore {
    private final Path path;

    public CsvRideStore(Path path) {
        this.path = path;
    }

    @Override
    public void append(Ride ride) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(ride.bikeId() + "," + ride.memberId() + ","
                    + ride.memberType().name().toLowerCase(Locale.ROOT) + "," + ride.station()
                    + "," + ride.minutes() + "," + ride.fee());
            writer.newLine();
        }
    }
}
