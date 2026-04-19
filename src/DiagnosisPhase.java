import java.util.ArrayList;
import java.util.List;

public class DiagnosisPhase {

    public static List<String> diagnose(BatteryPack pack) {

        List<String> faults = new ArrayList<>();

        double totalResistance = 0;

        for (Cell c : pack.cells) {
            totalResistance += c.resistance;
        }

        double avgResistance = totalResistance / pack.cells.size();

        for (Cell c : pack.cells) {

            if (c.voltage < 2.5) {
                faults.add("Cell " + c.id + " is Over-discharged");
            }

            if (c.voltage > 4.25) {
                faults.add("Cell " + c.id + " is Overcharged");
            }

            if (c.temperature > 60) {
                faults.add("Cell " + c.id + " has High Temperature");
            }

            if (c.resistance > 1.3 * avgResistance) {
                faults.add("Cell " + c.id + " has High Internal Resistance");
            }
        }

        return faults;
    }
}