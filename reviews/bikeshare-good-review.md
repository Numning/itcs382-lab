Files read: bikeshare-good/src/main/java/bikeshare/Main.java; BikeShare.java; MemberType.java; Rental.java; Ride.java; RideStore.java; CsvRideStore.java (all under the same source directory).

| No. | Check | Code location (file:line) | Finding | Severity | Suggested fix |
| --- | --- | --- | --- | --- | --- |
| 1 | 2 | BikeShare.java:49–50; Ride.java:3–4 | **Connascence of position:** the six-component Ride constructor includes two int arguments and two String arguments, so swapping bikeId with minutes or memberId with station can compile with the wrong meaning. | Note | If this record gains more components or callers, use distinct value types or a named construction API to reduce positional mistakes. |
| — | 1 | All seven files | Probable bugs, unclosed resources, empty exception handlers, unused imports/fields/parameters, identity-based string comparisons, string literals repeated many times and methods clearly too long or complex: **not found**. | Note | No change suggested. |
| — | 2 | Main.java:9–55; BikeShare.java:16–71; MemberType.java:8–16; CsvRideStore.java:14–26; Rental.java:3; Ride.java:3–4; RideStore.java:6 | Methods with procedural-or-weaker cohesion, classes mixing unrelated responsibilities, control/common/content coupling, unnecessary stamp coupling, excessive dependencies, non-private data fields and exposed mutable collections: **not found**. | Note | No change suggested. |

Check 1 findings: **0**.

Check 2 findings: **1**.

The “not found” rows are not included in the counts. Main handles console interaction and command dispatch; BikeShare coordinates rental state; MemberType owns rates; CsvRideStore writes completed rides through RideStore. Record component fields are implicitly private and final. availableBikes() copies both the outer map and its nested bike collections. Persistence receives a completed Ride whose components are all written, rather than an unrelated object carrying unused data.
