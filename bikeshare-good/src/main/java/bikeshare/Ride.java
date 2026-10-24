package bikeshare;

public record Ride(int bikeId, String memberId, MemberType memberType,
                   String station, int minutes, long fee) {
}
