import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class BatteryAnalyzerApp extends Application {

    private TextArea reportArea = new TextArea();
    private List<Cell> cells = new ArrayList<>();
    private double bolCapacity;
    private double currentCapacity;

    @Override
    public void start(Stage stage) {

        stage.setTitle("Waste To Worth BMS Analyzer");

        // Buttons
        Button uploadBtn = new Button("Upload File");
        Button analyzeBtn = new Button("Analyze");

        HBox topButtons = new HBox(15, uploadBtn, analyzeBtn);
        topButtons.setPadding(new Insets(10));

        // Report Area
        reportArea.setPrefHeight(500);
        reportArea.setEditable(false);

        // Graph 1 - Cell Voltage
        CategoryAxis xAxis1 = new CategoryAxis();
        NumberAxis yAxis1 = new NumberAxis();
        BarChart<String, Number> voltageChart = new BarChart<>(xAxis1, yAxis1);
        voltageChart.setTitle("Cell Voltage Performance");

        // Graph 2 - SOH Performance
        NumberAxis xAxis2 = new NumberAxis();
        NumberAxis yAxis2 = new NumberAxis();
        LineChart<Number, Number> sohChart = new LineChart<>(xAxis2, yAxis2);
        sohChart.setTitle("SOH Performance");

        // Graph 3 - Degradation
        NumberAxis xAxis3 = new NumberAxis();
        NumberAxis yAxis3 = new NumberAxis();
        LineChart<Number, Number> degradationChart = new LineChart<>(xAxis3, yAxis3);
        degradationChart.setTitle("Degradation After First Use");

        // Layout for charts
        GridPane chartsGrid = new GridPane();
        chartsGrid.setPadding(new Insets(10));
        chartsGrid.setHgap(10);
        chartsGrid.setVgap(10);

        chartsGrid.add(voltageChart, 0, 0);
        chartsGrid.add(sohChart, 1, 0);
        chartsGrid.add(degradationChart, 0, 1, 2, 1);

        VBox root = new VBox(10, topButtons, reportArea, chartsGrid);

        uploadBtn.setOnAction(e -> uploadFile(stage));
        analyzeBtn.setOnAction(e -> analyzeBattery(voltageChart, sohChart, degradationChart));

        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
        stage.show();
    }

    private void uploadFile(Stage stage) {

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);

        if (file == null) return;

        cells.clear();

        try {
            Scanner scanner = new Scanner(file);

            bolCapacity = Double.parseDouble(scanner.nextLine().split("=")[1]);
            currentCapacity = Double.parseDouble(scanner.nextLine().split("=")[1]);
            scanner.nextLine();

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");
                int id = Integer.parseInt(data[0]);
                double voltage = Double.parseDouble(data[1]);
                double temp = Double.parseDouble(data[2]);
                double resistance = Double.parseDouble(data[3]);

                cells.add(new Cell(id, voltage, temp, resistance));
            }

            reportArea.setText("File uploaded successfully.\nCells loaded: " + cells.size());

        } catch (FileNotFoundException ex) {
            reportArea.setText("Error reading file.");
        }
    }

    private void analyzeBattery(BarChart<String, Number> voltageChart,
                                LineChart<Number, Number> sohChart,
                                LineChart<Number, Number> degradationChart) {

        if (cells.isEmpty()) {
            reportArea.setText("Please upload file first.");
            return;
        }

        BatteryPack pack = new BatteryPack(bolCapacity, currentCapacity, cells);

        boolean safe = SafetyPhase.checkSafety(pack);

        double soh = pack.calculateSOH();
        List<String> faults = DiagnosisPhase.diagnose(pack);
        String repair = RepairPhase.suggestRepair(soh, faults);
        double predicted = RepairPhase.simulateRepair(soh,faults);
        boolean validation = ValidationPhase.validate(predicted);

        // Report
    

        double avgResistance = DegradationModel.calculateAverageResistance(cells);
        double avgTemp = DegradationModel.calculateAverageTemperature(cells);
        double imbalance = DegradationModel.calculateVoltageImbalance(cells);

        // Convert degradation to yearly model
        double degradationRatePerCycle = DegradationModel.calculateDegradationRate(cells);
        double yearlyDegradation = degradationRatePerCycle * 300 / 100;

        if (yearlyDegradation > 10) yearlyDegradation = 10;

        // Calculate Remaining Useful Life
        double health = predicted;
        int remainingYears = 0;

        while (health > 70 && remainingYears <= 10) {
            health -= yearlyDegradation;
            remainingYears++;
        }

        // Build Report
        StringBuilder report = new StringBuilder();

        report.append("====================================\n");
        report.append(" WASTE TO WORTH BATTERY REPORT\n");
        report.append("====================================\n\n");

        report.append(">>> BATTERY OVERVIEW\n");
        report.append("BOL Capacity: ").append(bolCapacity).append(" Ah\n");
        report.append("Current Capacity: ").append(currentCapacity).append(" Ah\n");
        report.append("Initial SOH: ").append(String.format("%.2f", soh)).append(" %\n\n");

        report.append(">>> CELL STATISTICS\n");
        report.append("Number of Cells: ").append(cells.size()).append("\n");
        report.append("Average Resistance: ").append(String.format("%.4f", avgResistance)).append(" ohm\n");
        report.append("Average Temperature: ").append(String.format("%.2f", avgTemp)).append(" °C\n");
        report.append("Voltage Imbalance: ").append(String.format("%.3f", imbalance)).append(" V\n\n");

        report.append(">>> DIAGNOSIS RESULTS\n");
        for (String f : faults) {
            report.append("- ").append(f).append("\n");
        }
        report.append("\n");

        report.append(">>> REPAIR ANALYSIS\n");
        report.append("Repair Suggestion: ").append(repair).append("\n");
        report.append("Predicted SOH After Repair: ").append(predicted).append(" %\n");
        report.append("Validation: ").append(validation ? "Passed\n" : "Failed\n").append("\n");

        report.append(">>> DEGRADATION PREDICTION\n");
        report.append("Estimated Yearly Degradation: ")
              .append(String.format("%.2f", yearlyDegradation)).append(" % per year\n");
        report.append("Remaining Useful Life: ").append(remainingYears).append(" Years\n\n");

        report.append(">>> FINAL DECISION\n");
        if (remainingYears >= 3) {
            report.append("Battery Approved for Second-Life Use.\n");
        } else {
            report.append("Battery Not Recommended for Long-Term Reuse.\n");
        }

        report.append("\n====================================\n");

        reportArea.setText(report.toString());
        
        
        // Voltage Chart
        voltageChart.getData().clear();
        XYChart.Series<String, Number> voltageSeries = new XYChart.Series<>();

        for (Cell c : cells) {
            voltageSeries.getData().add(new XYChart.Data<>("Cell " + c.id, c.voltage));
        }

        voltageChart.getData().add(voltageSeries);

        // SOH Chart
        sohChart.getData().clear();
        XYChart.Series<Number, Number> sohSeries = new XYChart.Series<>();
        sohSeries.getData().add(new XYChart.Data<>(1, soh));
        sohSeries.getData().add(new XYChart.Data<>(2, predicted));
        sohChart.getData().add(sohSeries);

       
     // Degradation Chart
        degradationChart.getData().clear();

        XYChart.Series<Number, Number> degSeries = new XYChart.Series<>();
        degSeries.setName("Data-Driven Degradation");

        health = predicted;

        double degradationRate = DegradationModel.calculateDegradationRate(cells);

        for (int cycle = 1; cycle <= 10; cycle++) {
            health -= degradationRate;
            if (health < 0) health = 0;

            degSeries.getData().add(new XYChart.Data<>(cycle, health));
        }

        degradationChart.getData().add(degSeries);
    }
}