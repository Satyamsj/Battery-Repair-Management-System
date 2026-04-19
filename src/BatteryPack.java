import java.util.List;

public class BatteryPack {

    double bolCapacity;
    double currentCapacity;
    List<Cell> cells;

    public BatteryPack(double bolCapacity, double currentCapacity, List<Cell> cells) {
        this.bolCapacity = bolCapacity;
        this.currentCapacity = currentCapacity;
        this.cells = cells;
    }

    public double calculateSOH() {
        return (currentCapacity / bolCapacity) * 100;
    }
}