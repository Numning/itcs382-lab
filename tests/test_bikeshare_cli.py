"""Build both projects first, then run: python3 -m unittest discover -s tests -v."""

from pathlib import Path
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
PROJECTS = ("bikeshare-poor", "bikeshare-good")


class BikeShareCliTest(unittest.TestCase):
    def run_commands(self, project, commands, block_file=False):
        with tempfile.TemporaryDirectory(prefix=project + "-") as folder:
            csv = Path(folder) / "rides.csv"
            if block_file:
                csv.mkdir()
            result = subprocess.run(
                ["java", "-cp", str(ROOT / project / "target/classes"), "bikeshare.Main"],
                input=commands, text=True, capture_output=True, cwd=folder, timeout=10,
            )
            saved = csv.read_text() if csv.is_file() else None
            self.assertEqual(result.returncode, 0, result.stderr)
            return result, saved

    def test_student_staff_cross_station_returns_and_append(self):
        for project in PROJECTS:
            with self.subTest(project=project):
                result, saved = self.run_commands(project,
                    "list\nrent M1 student Library\nreturn 1 Dorm 10\n"
                    "rent M2 staff Canteen\nreturn 3 Library 5\nlist\nquit\n")
                self.assertEqual(result.stderr, "")
                self.assertIn("library: [1, 2]\ncanteen: [3, 4]\ndorm: [5, 6]", result.stdout)
                self.assertIn("Receipt: bike 1, 10 minutes, 20 baht", result.stdout)
                self.assertIn("Receipt: bike 3, 5 minutes, 15 baht", result.stdout)
                self.assertIn("library: [2, 3]\ncanteen: [4]\ndorm: [5, 6, 1]", result.stdout)
                self.assertEqual(saved, "1,M1,student,dorm,10,20\n3,M2,staff,library,5,15\n")

    def test_empty_station_and_duplicate_member(self):
        for project in PROJECTS:
            with self.subTest(project=project):
                result, saved = self.run_commands(project,
                    "rent M1 student Library\nrent M1 staff Dorm\n"
                    "rent M2 staff Library\nrent M3 student Library\nlist\nquit\n")
                self.assertIn("Member already has a bike", result.stderr)
                self.assertIn("No bikes at this station", result.stderr)
                self.assertIn("library: []\ncanteen: [3, 4]\ndorm: [5, 6]", result.stdout)
                self.assertIsNone(saved)

    def test_invalid_input_does_not_end_the_loop(self):
        for project in PROJECTS:
            with self.subTest(project=project):
                result, saved = self.run_commands(project,
                    "\nrent\nrent M0 visitor Library\nrent M0 student Unknown\n"
                    "rent M1 student Library\nreturn x Dorm 1\nreturn 1 Dorm -1\n"
                    "return 6 Dorm 1\nreturn 1 Dorm 0\nreturn 1 Library 1\nquit\n")
                self.assertGreaterEqual(result.stderr.count("Invalid command:"), 7)
                self.assertIn("Receipt: bike 1, 0 minutes, 0 baht", result.stdout)
                self.assertEqual(saved, "1,M1,student,dorm,0,0\n")

    def test_failed_save_keeps_bike_rented(self):
        for project in PROJECTS:
            with self.subTest(project=project):
                result, saved = self.run_commands(project,
                    "rent M1 student Library\nreturn 1 Dorm 5\n"
                    "rent M1 staff Canteen\nlist\nquit\n", block_file=True)
                self.assertIn("Could not save ride:", result.stderr)
                self.assertIn("Member already has a bike", result.stderr)
                self.assertNotIn("Receipt:", result.stdout)
                self.assertIn("library: [2]\ncanteen: [3, 4]\ndorm: [5, 6]", result.stdout)
                self.assertIsNone(saved)


if __name__ == "__main__":
    unittest.main()
