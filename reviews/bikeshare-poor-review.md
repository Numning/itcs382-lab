Files read: bikeshare-poor/src/main/java/bikeshare/Main.java; bikeshare-poor/src/main/java/bikeshare/Desk.java.

| No. | Check | Code location (file:line) | Finding | Severity | Suggested fix |
| --- | --- | --- | --- | --- | --- |
| 1 | 1 | Desk.java:28–70 | process() combines parsing, validation, two operations, pricing, CSV output, state changes and receipt formatting in a 43-line method, making its execution paths harder to follow. | Should fix | Extract separate rent and return operations with small persistence and formatting helpers. |
| 2 | 2 | Desk.java:28–70 | **Logical method cohesion:** process() groups renting and returning under a Boolean selector instead of representing one operation. | Should fix | Replace process(boolean, String[]) with separately named rent and return methods. |
| 3 | 2 | Desk.java:14–80 | **Weak class cohesion:** Desk owns inventory, active rentals, member-type conversion, fee rules, CSV persistence and receipt formatting. | Should fix | Separate the fee rules and file persistence from rental coordination. |
| 4 | 2 | Main.java:22; Desk.java:28,33 | **Control coupling:** Main passes a Boolean value to select which operation Desk performs. | Should fix | Call an explicit rent or return method instead of passing a behavior flag. |
| 5 | 2 | Main.java:24–25; Desk.java:15,20–22,30,50,63,67 | **Common coupling:** Main reads the global station map that Desk also reads and mutates. | Should fix | Keep station state inside a service and return a read-only snapshot for listing. |
| 6 | 2 | Desk.java:15–17 | **Non-private fields and representation exposure:** all three public static maps are mutable, so callers can remove bikes or break the correspondence between members and types. | Should fix | Make the maps private instance fields and expose only rental operations and immutable snapshots. |
| 7 | 2 | Desk.java:42–43,73–79 | **Connascence of convention:** typeCode() and process() must agree that 1 means student and 2 means staff, and the same interpretation is repeated for pricing and CSV labels. | Should fix | Replace the integer codes with an enum that owns the fee rate. |
| 8 | 2 | Main.java:18–22; Desk.java:28–35,55,62 | **Stamp coupling and connascence of position:** Main passes the entire command array, including an unused command token, and Desk relies on different positions depending on the Boolean flag. | Should fix | Parse commands in Main and pass only named, typed values to the selected operation. |
| — | 1 | Main.java:9–37; Desk.java:44–49 | Resource leaks, empty exception handlers, unused imports/fields/parameters, object-identity string comparisons and string literals repeated many times: **not found**. | Note | No change suggested. |
| — | 2 | Both files | Excessive numbers of class dependencies and duplicated algorithms between classes: **not found**. | Note | No change suggested. |

Check 1 findings: **1**.

Check 2 findings: **7**.

The “not found” rows are not included in the counts. Command dispatch in Main is treated as one command-line responsibility; the problematic logical cohesion is the service method that combines two domain operations.
