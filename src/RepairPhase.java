import java.util.List;

public class RepairPhase {

    public static String suggestRepair(double soh, List<String> faults) {

        if (soh < 70) {
            return "Battery SOH too low. Recommend Recycling.";
        }

        if (faults.size() == 0) {
            return "No major fault detected.";
        } 
        else if (faults.size() <= 2) {
            return "Recommend: Replace Faulty Cell(s).";
        } 
        else {
            return "Recommend: Replace Module.";
        }
    }

    public static double simulateRepair(double soh, List<String> faults) {

        double improvement = 0;

        // Base improvement based on faults
        if (faults.size() == 0) {
            improvement = 2; // already healthy
        } 
        else if (faults.size() <= 2) {
            improvement = 10; // minor repair
        } 
        else {
            improvement = 5; // major issue, limited improvement
        }

        // Adjust based on current SOH
        if (soh < 60) {
            improvement -= 2; // very degraded battery
        } else if (soh > 85) {
            improvement -= 3; // already near max
        }

        double newSoh = soh + improvement;

        // Limit max to 100
        if (newSoh > 100) {
            newSoh = 100;
        }

        return newSoh;
    }
}