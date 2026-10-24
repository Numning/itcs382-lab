"""Run after mvn package: python3 -m unittest discover -s tests -v."""

from pathlib import Path
import subprocess
import tempfile
import unittest


CLASSES = Path(__file__).resolve().parents[1] / "target" / "classes"


class ParcelCliTest(unittest.TestCase):
    def run_commands(self, commands, block_file=False):
        with tempfile.TemporaryDirectory(prefix="parcelpoint-test-") as folder:
            csv = Path(folder) / "parcels.csv"
            if block_file:
                csv.mkdir()
            result = subprocess.run(
                ["java", "-cp", str(CLASSES), "parcelpoint.Main"],
                input=commands,
                text=True,
                capture_output=True,
                cwd=folder,
                timeout=10,
            )
            saved = csv.read_text() if csv.is_file() else None
            return result, saved

    def test_required_workflow(self):
        result, saved = self.run_commands("store P1 6501 2\npickup P1 5\nstaff\nquit\n")
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(result.stdout.splitlines()[1:], [
            "SMS to 6501: parcel P1 is in locker 2",
            "Locker 2",
            "Fee: 30.0",
            "P1 6501 PICKED_UP",
            "Free lockers: 3",
        ])
        self.assertEqual(saved, "P1,PICKED_UP\n")

    def test_fee_boundaries_for_all_sizes(self):
        for size, daily_rate in [(1, 10), (2, 15), (3, 20)]:
            for days in [0, 3, 4, 5]:
                with self.subTest(size=size, days=days):
                    result, saved = self.run_commands(
                        f"store P1 6501 {size}\npickup P1 {days}\nquit\n"
                    )
                    self.assertEqual(result.returncode, 0, result.stderr)
                    expected_fee = float(max(0, days - 3) * daily_rate)
                    self.assertIn(f"Fee: {expected_fee}\n", result.stdout)
                    self.assertEqual(saved, "P1,PICKED_UP\n")

    def test_waiting_and_staff_reports(self):
        result, saved = self.run_commands(
            "store P1 6501 2\nlist\nstaff\npickup P1 5\nlist\nstaff\nquit\n"
        )
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertEqual(result.stdout.count("P1 is waiting\n"), 1)
        self.assertIn("P1 6501 STORED\nFree lockers: 2\n", result.stdout)
        self.assertIn("Fee: 30.0\nP1 6501 PICKED_UP\nFree lockers: 3\n", result.stdout)
        self.assertEqual(saved, "P1,PICKED_UP\n")

    def test_save_failure_is_visible(self):
        result, saved = self.run_commands("store P1 6501 2\nquit\n", block_file=True)
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("UncheckedIOException: Could not save parcels.csv", result.stderr)
        self.assertIn("Caused by: java.io.", result.stderr)
        self.assertNotIn("Locker 2\n", result.stdout)
        self.assertIsNone(saved)


if __name__ == "__main__":
    unittest.main()
