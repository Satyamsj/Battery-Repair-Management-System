public class SafetyPhase {

    public static boolean checkSafety(BatteryPack pack) {

        for (Cell c : pack.cells) {
            if (c.voltage < 2.0) {
                return false;
            }
            if (c.temperature > 70) {
                return false;
            }
        }

        return true;
    }
}