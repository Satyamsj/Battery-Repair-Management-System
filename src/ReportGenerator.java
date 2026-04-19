import java.util.List;

public class ReportGenerator {

    public static void generateReport(double soh,
                                      List<String> faults,
                                      String repairSuggestion,
                                      double predictedSOH,
                                      boolean validationResult) {

        System.out.println("\n---------------------------------------");
        System.out.println("WASTE TO WORTH BATTERY REPORT");
        System.out.println("---------------------------------------");

        System.out.println("Initial SOH: " + soh + "%");

        System.out.println("\nPHASE 2: Diagnosis Results");

        if (faults.size() == 0) {
            System.out.println("No faults detected.");
        } else {
            for (String fault : faults) {
                System.out.println(fault);
            }
        }

        System.out.println("\nPHASE 3: Repair Suggestion");
        System.out.println(repairSuggestion);

        System.out.println("Predicted SOH After Repair: " + predictedSOH + "%");

        System.out.println("\nPHASE 4: Validation");

        if (validationResult) {
            System.out.println("Validation Passed.");
        } else {
            System.out.println("Validation Failed.");
            System.out.println("Re-analyze Battery.");
        }

        System.out.println("---------------------------------------");
    }
}