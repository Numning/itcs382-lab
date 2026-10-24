package bikeshare;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BikeShare {
    private final Map<String, Deque<Integer>> stations = new LinkedHashMap<>();
    private final Map<Integer, Rental> rentals = new LinkedHashMap<>();
    private final RideStore store;

    public BikeShare(RideStore store) {
        this.store = store;
        stations.put("library", new ArrayDeque<>(List.of(1, 2)));
        stations.put("canteen", new ArrayDeque<>(List.of(3, 4)));
        stations.put("dorm", new ArrayDeque<>(List.of(5, 6)));
    }

    public int rent(String memberId, MemberType memberType, String station) {
        if (memberId == null || !memberId.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Member ID must use letters, digits, underscores or hyphens");
        }
        if (memberType == null) {
            throw new IllegalArgumentException("Member type is required");
        }
        if (rentals.values().stream().anyMatch(rental -> rental.memberId().equals(memberId))) {
            throw new IllegalArgumentException("Member already has a bike");
        }
        Deque<Integer> bikes = stationBikes(station);
        if (bikes.isEmpty()) {
            throw new IllegalArgumentException("No bikes at this station");
        }
        int bike = bikes.removeFirst();
        rentals.put(bike, new Rental(memberId, memberType));
        return bike;
    }

    public Ride returnBike(int bike, String station, int minutes) throws IOException {
        Deque<Integer> bikes = stationBikes(station);
        Rental rental = rentals.get(bike);
        if (rental == null) {
            throw new IllegalArgumentException("Bike is not rented");
        }
        long fee = rental.memberType().fee(minutes);
        Ride ride = new Ride(bike, rental.memberId(), rental.memberType(),
                station.toLowerCase(Locale.ROOT), minutes, fee);
        store.append(ride);
        rentals.remove(bike);
        bikes.addLast(bike);
        return ride;
    }

    public Map<String, List<Integer>> availableBikes() {
        Map<String, List<Integer>> snapshot = new LinkedHashMap<>();
        stations.forEach((name, bikes) -> snapshot.put(name, List.copyOf(bikes)));
        return java.util.Collections.unmodifiableMap(snapshot);
    }

    private Deque<Integer> stationBikes(String station) {
        if (station == null) {
            throw new IllegalArgumentException("Station is required");
        }
        Deque<Integer> bikes = stations.get(station.toLowerCase(Locale.ROOT));
        if (bikes == null) {
            throw new IllegalArgumentException("Unknown station");
        }
        return bikes;
    }
}
