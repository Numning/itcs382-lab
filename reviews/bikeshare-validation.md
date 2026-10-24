# BikeShare validation

Date: 24 October 2026.

The same input was used in both projects:

```text
list
rent M1 student Library
return 1 Dorm 10
rent M2 staff Canteen
return 3 Library 5
list
quit
```

## bikeshare-poor

`mvn package`: BUILD SUCCESS.

```text
Commands: rent <memberId> <student|staff> <station>, return <bikeId> <station> <minutes>, list, quit
library: [1, 2]
canteen: [3, 4]
dorm: [5, 6]
Rented bike 1
Receipt: bike 1, 10 minutes, 20 baht
Rented bike 3
Receipt: bike 3, 5 minutes, 15 baht
library: [2, 3]
canteen: [4]
dorm: [5, 6, 1]

rides.csv:
1,M1,student,dorm,10,20
3,M2,staff,library,5,15
```

## bikeshare-good

`mvn package`: BUILD SUCCESS.

```text
Commands: rent <memberId> <student|staff> <station>, return <bikeId> <station> <minutes>, list, quit
library: [1, 2]
canteen: [3, 4]
dorm: [5, 6]
Rented bike 1
Receipt: bike 1, 10 minutes, 20 baht
Rented bike 3
Receipt: bike 3, 5 minutes, 15 baht
library: [2, 3]
canteen: [4]
dorm: [5, 6, 1]

rides.csv:
1,M1,student,dorm,10,20
3,M2,staff,library,5,15
```

## Automated checks

From the repository root, run:

```sh
mvn -f bikeshare-poor/pom.xml package
mvn -f bikeshare-good/pom.xml package
PYTHONDONTWRITEBYTECODE=1 python3 -m unittest discover -s tests -v
```

All four tests passed, each checking both projects:

- Initial station inventory, student/staff fees, returns to a different station, and appending two CSV records.
- Duplicate active members and exhausted stations.
- Blank input, missing arguments, unknown types/stations, invalid numbers, negative minutes, inactive bikes, zero-minute returns, and repeated returns.
- A directory at rides.csv to force a save failure: the command loop continues, no success receipt appears, and the bike remains rented.

The automated tests use temporary folders, so they do not modify either project's sample rides.csv. CSV files and Maven target folders are excluded by .gitignore. The snapshots above preserve the sample results in the repository.
