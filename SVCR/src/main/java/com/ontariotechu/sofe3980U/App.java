package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

public class App {
    public static void main(String[] args) {
        // Define file paths for the CSV files
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
        // To hold the best model
        String bestModel = "";
        double bestMSE = Double.MAX_VALUE;
        double bestMAE = Double.MAX_VALUE;
        double bestMARE = Double.MAX_VALUE;

        // Iterate through each file
        for (String filePath : filePaths) {
            System.out.println("Evaluating " + filePath + "...");
            // Read CSV file and calculate metrics
            try (FileReader filereader = new FileReader(filePath); 
                 CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build()) {

                List<String[]> allData = csvReader.readAll();
                double mse = 0, mae = 0, mare = 0;
                int count = 0;

                // Calculate MSE, MAE, and MARE
                for (String[] row : allData) {
                    float y_true = Float.parseFloat(row[0]);
                    float y_predicted = Float.parseFloat(row[1]);

                    // MSE (Mean Squared Error)
                    mse += Math.pow(y_true - y_predicted, 2);
                    // MAE (Mean Absolute Error)
                    mae += Math.abs(y_true - y_predicted);
                    // MARE (Mean Absolute Relative Error)
                    mare += Math.abs((y_true - y_predicted) / y_true);

                    count++;
                }

                mse /= count;
                mae /= count;
                mare /= count;

                // Print the calculated metrics for this model
                System.out.println("MSE: " + mse);
                System.out.println("MAE: " + mae);
                System.out.println("MARE: " + mare);
                System.out.println();

                // Compare the errors to find the model with the lowest error
                if (mse < bestMSE) {
                    bestMSE = mse;
                    bestModel = filePath;
                }

                if (mae < bestMAE) {
                    bestMAE = mae;
                    bestModel = filePath;
                }

                if (mare < bestMARE) {
                    bestMARE = mare;
                    bestModel = filePath;
                }

            } catch (Exception e) {
                System.out.println("Error reading the file: " + filePath);
            }
        }

        // Recommend the model with the lowest error
        System.out.println("The model with the lowest error is: " + bestModel);
    }
}