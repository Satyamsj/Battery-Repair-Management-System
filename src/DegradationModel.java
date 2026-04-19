import java.util.List;

public class DegradationModel {

    public static double calculateAverageResistance(List<Cell> cells) {

        double total = 0;

        for (Cell c : cells) {
            total += c.resistance;
        }

        return total / cells.size();
    }

    public static double calculateAverageTemperature(List<Cell> cells) {

        double total = 0;

        for (Cell c : cells) {
            total += c.temperature;
        }

        return total / cells.size();
    }

    public static double calculateVoltageImbalance(List<Cell> cells) {

        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;

        for (Cell c : cells) {
            if (c.voltage > max) max = c.voltage;
            if (c.voltage < min) min = c.voltage;
        }

        return max - min;
    }

    public static double calculateDegradationRate(List<Cell> cells) {

        double avgResistance = calculateAverageResistance(cells);
        double avgTemp = calculateAverageTemperature(cells);
        double imbalance = calculateVoltageImbalance(cells);

        // Weighted degradation model
        double rate = 0;

        rate += avgResistance * 2000;     // resistance impact
        rate += (avgTemp - 25) * 0.05;    // temperature impact
        rate += imbalance * 2;            // voltage imbalance impact

        if (rate < 0.5) rate = 0.5; // minimum degradation

        return rate;
    }
}