package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        // List of file paths for models
        String[] filePaths = {"model_1.csv", "model_2.csv", "model_3.csv"};
        
        // Track best model performance
        double bestAUC = 0.0;
        String bestModel = "";
        
        for (String filePath : filePaths) {
            System.out.println("Evaluating model: " + filePath);
            
            FileReader filereader;
            List<String[]> allData;
            
            try {
                filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                allData = csvReader.readAll();
            } catch (Exception e) {
                System.out.println("Error reading the CSV file: " + filePath);
                continue;
            }

            // Initialize variables
            int TP = 0, FP = 0, TN = 0, FN = 0; // Confusion matrix components
            double totalBCE = 0.0; // For Binary Cross-Entropy
            List<Double> predictedProbs = new ArrayList<>();
            List<Integer> actualValues = new ArrayList<>();

            // Loop through the data to populate confusion matrix and calculate BCE
            for (String[] row : allData) {
                int y_true = Integer.parseInt(row[0]);
                float y_predicted = Float.parseFloat(row[1]);
                predictedProbs.add((double) y_predicted); // Predicted probabilities
                actualValues.add(y_true); // Actual values

                // Calculate Binary Cross-Entropy for the current sample
                if (y_true == 1) {
                    totalBCE += -Math.log(y_predicted);
                } else {
                    totalBCE += -Math.log(1 - y_predicted);
                }

                // Update confusion matrix
                if (y_true == 1 && y_predicted >= 0.5) TP++; // True Positive
                if (y_true == 1 && y_predicted < 0.5) FN++; // False Negative
                if (y_true == 0 && y_predicted >= 0.5) FP++; // False Positive
                if (y_true == 0 && y_predicted < 0.5) TN++; // True Negative
            }

            // Calculate BCE (average)
            double BCE = totalBCE / allData.size();
            System.out.println("Binary Cross-Entropy (BCE): " + BCE);

            // Calculate metrics based on confusion matrix
            double accuracy = (double) (TP + TN) / (TP + TN + FP + FN);
            double precision = (double) TP / (TP + FP);
            double recall = (double) TP / (TP + FN);
            double f1Score = 2 * (precision * recall) / (precision + recall);

            System.out.println("Confusion Matrix:");
            System.out.println("TP: " + TP + " FP: " + FP + " TN: " + TN + " FN: " + FN);
            System.out.println("Accuracy: " + accuracy);
            System.out.println("Precision: " + precision);
            System.out.println("Recall: " + recall);
            System.out.println("F1 Score: " + f1Score);

            // Calculate AUC-ROC
            double auc = calculateAUCROC(predictedProbs, actualValues);
            System.out.println("AUC-ROC: " + auc);
            System.out.println();

            // Track the best performing model based on AUC-ROC
            if (auc > bestAUC) {
                bestAUC = auc;
                bestModel = filePath;
            }
        }

        // Output the model with better performance
        System.out.println("The model with the best performance is: " + bestModel);
    }

    /**
     * Calculate AUC-ROC
     */
    public static double calculateAUCROC(List<Double> predictedProbs, List<Integer> actualValues) {
        // Sort predicted probabilities and actual values together based on predicted probabilities
        List<Integer> sortedIndices = new ArrayList<>();
        for (int i = 0; i < predictedProbs.size(); i++) {
            sortedIndices.add(i);
        }
        sortedIndices.sort((i, j) -> Double.compare(predictedProbs.get(j), predictedProbs.get(i)));

        // Calculate True Positive Rate (TPR) and False Positive Rate (FPR)
        double auc = 0.0;
        double prevFPR = 0.0, prevTPR = 0.0;
        double totalPositive = 0, totalNegative = 0;
        for (int i = 0; i < actualValues.size(); i++) {
            if (actualValues.get(i) == 1) {
                totalPositive++;
            } else {
                totalNegative++;
            }
        }

        int TP = 0, FP = 0;
        for (int i = 0; i < sortedIndices.size(); i++) {
            int index = sortedIndices.get(i);
            if (actualValues.get(index) == 1) {
                TP++;
            } else {
                FP++;
            }

            double TPR = (double) TP / totalPositive;
            double FPR = (double) FP / totalNegative;
            auc += (TPR + prevTPR) * (FPR - prevFPR) / 2.0;

            prevFPR = FPR;
            prevTPR = TPR;
        }

        return auc;
    }
}