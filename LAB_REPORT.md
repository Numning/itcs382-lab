# Lab Report, Code Quality and Design Quality

Repository URL:

## Tools

| Item | Version or name |
| --- | --- |
| JDK | |
| Maven | |
| IDE | |
| SonarQube for IDE | |
| AI tool and exact model | |

## Task 1. SonarQube on the starter code

Commit of the starter, before any change: acd09f2

Table 1. SonarQube issues in the starter code

| No. | Rule | File:line | Message |
| --- | --- | --- | --- |
| 1 | java:S106 | Main.java:9 | Replace this use of System.out by a logger. |
| 2 | java:S106 | Main.java:15 | Replace this use of System.out by a logger. |
| 3 | java:S106 | Main.java:17 | Replace this use of System.out by a logger. |
| 4 | java:S106 | Main.java:22 | Replace this use of System.out by a logger. |
| 5 | java:S2143 | ParcelService.java:1 | Use the "java.time" API for date and time. |
| 6 | java:S1128 | ParcelService.java:6 | Remove this unused import 'java.util.Date'. |
| 7 | java:S1068 | ParcelService.java:12 | Remove this unused "maxParcels" private field. |
| 8 | java:S106 | ParcelService.java:30 | Replace this use of System.out by a logger. |
| 9 | java:S4973 | ParcelService.java:42 | Strings and Boxed types should be compared using "equals()". |
| 10 | java:S106 | ParcelService.java:67 | Replace this use of System.out by a logger. |
| 11 | java:S106 | ParcelService.java:69 | Replace this use of System.out by a logger. |
| 12 | java:S2095 | ParcelService.java:76 | Use try-with-resources or close this "FileWriter" in a "finally" clause. |
| 13 | java:S108 | ParcelService.java:80 | Remove this block of code, fill it in, or add a comment explaining why it is empty. |

## Task 2. AI review of the starter code

| Item | Value |
| --- | --- |
| Date | 24 October 2026 |
| AI tool and exact model | GPT-6.1 Sol Medium |
| Commit reviewed | acd09f2 |
| How the AI read the files (attached the files, opened the folder, or pasted the files) | Attach the files |
| Output file in the reviews folder | reviews/parcelpoint-review.md |

Table 2. All findings on the starter code

Rows 1–22 follow the numbered findings in the saved AI review. Rows 23–30 are additional SonarQube findings from Table 1. Overlapping findings appear once and are marked both. The two “not found” entries in the review are not findings.

| No. | File:line | Finding | Found by (SonarQube, AI or both) | SonarQube rule or course term | Correct? (yes or no, and why) |
| --- | --- | --- | --- | --- | --- |
| 1 | ParcelService.java:42 | Parcel codes are compared with `==` rather than by text value. | both | java:S4973 | yes: `==` compares object identity, so different String objects containing the same code can fail to match. |
| 2 | ParcelService.java:76–79 | The FileWriter is never flushed or closed. | both | java:S2095 | yes: the writer is created without try-with-resources or a close call, leaving buffered output and the resource unmanaged. |
| 3 | ParcelService.java:80–81 | An empty catch hides persistence failures. | both | java:S108 | yes: IOException is discarded, so the caller receives no indication that saving failed. |
| 4 | Main.java:13,17 | Missing arguments and invalid integers can terminate command processing. | AI | Input validation | yes: array elements are accessed and integers parsed without argument-count checks or exception handling. |
| 5 | Main.java:11 | Splitting on a single space mishandles repeated spaces and tabs. | AI | Input parsing | yes: `split(" ")` produces empty arguments between repeated spaces and does not split on tabs. |
| 6 | Parcel.java:9–13; Locker.java:8–10; ParcelService.java:26,56–61 | Parcel and locker sizes are not validated. | AI | Domain validation; connascence of convention | yes: constructors accept any integer; size 0 can satisfy the storage comparison, while fee calculation treats sizes other than 1 or 2 as large. |
| 7 | ParcelService.java:24–29,39–46 | Duplicate active parcel codes are accepted, making pickup ambiguous. | AI | Identifier uniqueness | yes: storage checks only locker availability and size, while pickup returns after the first matching code; codes need to identify active parcels uniquely. |
| 8 | ParcelService.java:6 | The Date import is unused. | both | java:S1128 | yes: no code in ParcelService references Date. |
| 9 | ParcelService.java:12 | The maxParcels field is unused and enforces no limit. | both | java:S1068 | yes: the field is initialized but never read. |
| 10 | Main.java:8–26 | The Scanner is never closed on normal termination. | AI | Resource management | yes: there is no close call or try-with-resources; closing it is appropriate if this standalone application owns System.in, as the review qualifies. |
| 11 | Main.java:6–26 | The review labels main() as logically cohesive because it dispatches different commands. | AI | Logical method cohesion | no: this is too broad; the branches support one coherent command-processing responsibility, and command selection alone does not establish that the method needs splitting. |
| 12 | ParcelService.java:64–71 | printParcels() selects two reporting behaviors with a Boolean flag. | AI | Logical method cohesion | yes: the flag selects either a staff listing or a waiting-parcel listing, so the method groups alternative reporting operations. |
| 13 | Main.java:19,21; ParcelService.java:64–68 | Main controls the service's reporting behavior through true/false arguments. | AI | Control coupling | yes: the caller passes a flag specifically to choose the callee's execution path. |
| 14 | ParcelService.java:24–32,52–61,64–81 | ParcelService combines allocation, notifications, pricing, reporting and persistence. | AI | Weak class cohesion | yes: locker rules, output formatting and file handling are separate responsibilities with different reasons to change. |
| 15 | ParcelService.java:52–61 | fee() does not use any ParcelService attributes. | AI | Weak class cohesion | yes: the calculation depends only on its parcel and days arguments, supporting extraction of a pricing responsibility; being stateless alone would not prove a design defect. |
| 16 | ParcelService.java:52,56,58 | fee() receives a whole Parcel but reads only its size. | AI | Stamp coupling | yes: passing only the size would provide the data the calculation actually needs. |
| 17 | Parcel.java:7; ParcelService.java:28,44,67–68,78 | Public status allows direct access to Parcel's representation. | AI | Content coupling; encapsulation | yes: ParcelService directly reads and writes the public field, bypassing any controlled status-transition operation. |
| 18 | ParcelService.java:20–21; Main.java:22 | getLockers() exposes the mutable internal list. | AI | Encapsulation; representation exposure | yes: callers receive the same list and can clear, add or remove lockers without service checks, although Main currently only reads it. |
| 19 | ParcelService.java:41–42 | Navigating from Locker to Parcel to its code is claimed to establish strong coupling. | AI | Object navigation; strong coupling | no: the getter chain exists, but it uses public accessors and does not itself establish excessive coupling or access to private representation; the review gives no concrete change scenario showing a problem. |
| 20 | Parcel.java:13; ParcelService.java:28,44,68 | Parcel states depend on shared string spellings. | AI | Connascence of convention | yes: producers and consumers must agree on strings such as STORED, and the compiler cannot reject misspelled state values. |
| 21 | Locker.java:5; Parcel.java:6; ParcelService.java:15–17,26,56–61; Main.java:9 | Multiple classes share integer size meanings and ordering. | AI | Connascence of convention | yes: construction, locker fitting, pricing and the command help must all agree that 1, 2 and 3 mean small, medium and large. |
| 22 | Locker.java:8; Parcel.java:9; ParcelService.java:15–17; Main.java:13 | Same-typed constructor arguments depend on their positions. | AI | Connascence of position | yes: swapping locker number and size or parcel code and student ID still compiles but changes meaning. |
| 23 | Main.java:9 | Replace this use of System.out by a logger. | SonarQube | java:S106 | yes: the direct console output matches the rule, although command help is intentional CLI output and can reasonably be kept. |
| 24 | Main.java:15 | Replace this use of System.out by a logger. | SonarQube | java:S106 | yes: the direct console output matches the rule, although showing the storage result is intentional CLI behavior. |
| 25 | Main.java:17 | Replace this use of System.out by a logger. | SonarQube | java:S106 | yes: the direct console output matches the rule, although showing the fee is intentional CLI behavior. |
| 26 | Main.java:22 | Replace this use of System.out by a logger. | SonarQube | java:S106 | yes: the direct console output matches the rule, although showing free lockers is intentional CLI behavior. |
| 27 | ParcelService.java:1 | Use the java.time API for date and time. | SonarQube | java:S2143 | yes: the file imports legacy java.util.Date; because the import is unused, removing it is sufficient and no date/time replacement is needed. |
| 28 | ParcelService.java:30 | Replace this use of System.out by a logger. | SonarQube | java:S106 | yes: the service prints the simulated SMS directly, coupling the notification output to the console. |
| 29 | ParcelService.java:67 | Replace this use of System.out by a logger. | SonarQube | java:S106 | yes: the service prints staff report lines directly; this matches the rule, though user-facing reporting is intentional. |
| 30 | ParcelService.java:69 | Replace this use of System.out by a logger. | SonarQube | java:S106 | yes: the service prints waiting-parcel report lines directly; this matches the rule, though user-facing reporting is intentional. |

2.1 Name two problems that only the AI review found.

- Missing arguments or invalid numbers can crash the program (Main.java:13,17).
- Main uses a Boolean flag to choose the report type, creating control coupling (Main.java:19,21).

2.2 Name one problem that only SonarQube found, or write none.

SonarQube flagged System.out at Main.java:9 (java:S106), but the AI did not. Keeping it makes sense because it displays the command-line help.

2.3 Name one AI finding that was wrong or too vague, and explain why. If every finding was right, explain how you checked them.

Finding 11 was too vague. It called main() poorly cohesive because it handles different commands, but those commands all belong to the same input loop. Having several command branches alone does not make it a bad design.

## Task 3. Fix and check

Table 3. What you did with each SonarQube issue in Table 1

| No. in Table 1 | Fixed or kept | Commit, or the reason for keeping it |
| --- | --- | --- |
| 1 | | |

Number of SonarQube issues that remain after your fixes:

The two design problems that you fixed

| Design problem (course term) | What you changed | Commit |
| --- | --- | --- |
| | | |
| | | |

Output of the program after your fixes, for the commands `store P1 6501 2`, `pickup P1 5` and `staff`, and the content of parcels.csv:

```text

```

## Task 4. AI-generated poor and good designs

| Item | bikeshare-poor | bikeshare-good |
| --- | --- | --- |
| Date | | |
| AI tool and exact model | | |
| Prompt (B or C as given, or your changed prompt in full) | | |
| Builds with mvn package (yes or no) | | |
| Number of classes, interfaces, enums and records | | |
| SonarQube issues, total | | |
| SonarQube issues, by rule | | |
| AI review findings in Check 1 | | |
| AI review findings in Check 2 | | |
| Review output file in the reviews folder | | |

4.1 Did SonarQube report any cohesion or coupling problem in bikeshare-poor? Quote the rule and the message if it did.

4.2 Did the AI review tell the two versions apart? Give one finding for bikeshare-poor that does not appear for bikeshare-good.

4.3 Which version would you rather extend with a new member type, such as a visitor who pays 4 baht per minute, and why?

## Task 5. Reflection

5.1 What does SonarQube check, and what does it not check? Answer with examples from this lab.

5.2 When should you not trust the AI review? Give an example from this lab.

5.3 What will you do differently in Project Phase 2 after this lab?

## AI usage declaration

| AI tool and exact model | Purpose | Files or parts that it produced or changed | How you checked its output |
| --- | --- | --- | --- |
| | | | |
