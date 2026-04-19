import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Create sample battery data
        double bolCapacity = 82.5;
        double currentCapacity = 72.6;

        List<Cell> cells = new ArrayList<>();

        cells.add(new Cell(1, 3.92, 32, 0.003));
        cells.add(new Cell(2, 3.90, 31, 0.0031));
        cells.add(new Cell(3, 2.40, 38, 0.0065));
        cells.add(new Cell(4, 3.91, 30, 0.003));

        BatteryPack pack = new BatteryPack(bolCapacity, currentCapacity, cells);

        // PHASE 1: Safety
        boolean safe = SafetyPhase.checkSafety(pack);

        if (!safe) {
            System.out.println("Battery is Unsafe. Send to Recycling.");
            return;
        } else {
            System.out.println("Battery is Safe for Diagnosis.");
        }

        // PHASE 2: Diagnosis
        double soh = pack.calculateSOH();
        List<String> faults = DiagnosisPhase.diagnose(pack);

        // PHASE 3: Repair
        String repairSuggestion = RepairPhase.suggestRepair(soh, faults);
        double predictedSOH = RepairPhase.simulateRepair(soh,faults);

        // PHASE 4: Validation
        boolean validationResult = ValidationPhase.validate(predictedSOH);

        // Generate Final Report
        ReportGenerator.generateReport(
                soh,
                faults,
                repairSuggestion,
                predictedSOH,
                validationResult
        );
    }
}