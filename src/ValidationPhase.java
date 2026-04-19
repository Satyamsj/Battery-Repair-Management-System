public class ValidationPhase {

    public static boolean validate(double predictedSOH) {

        if (predictedSOH > 90) {
            return true;
        } else {
            return false;
        }
    }
}