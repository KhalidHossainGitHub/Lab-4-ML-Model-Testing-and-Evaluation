package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String filePath = "model.csv";
        FileReader filereader;
        List<String[]> allData;
        
        // Read the CSV file using OpenCSV
        try {
            filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        } catch(Exception e) {
            System.out.println("Error reading the CSV file");
            return;
        }
        
        // Confusion matrix (5x5 for 5 classes)
        int[][] confusionMatrix = new int[5][5];
        
        // Cross Entropy calculation
        double totalCrossEntropy = 0.0;
        int count = 0;
        
        // Loop through the data and calculate the required metrics
        for (String[] row : allData) {
            int y_true = Integer.parseInt(row[0]);  // True class label
            float[] y_predicted = new float[5];
            
            // Fill the predicted probabilities
            for (int i = 0; i < 5; i++) {
                y_predicted[i] = Float.parseFloat(row[i + 1]);
            }
            
            // Calculate Cross Entropy for this row
            double crossEntropy = calculateCrossEntropy(y_true, y_predicted);
            totalCrossEntropy += crossEntropy;
            
            // Get the predicted class (highest predicted probability)
            int predictedClass = getPredictedClass(y_predicted);
            
            // Update the confusion matrix
            confusionMatrix[y_true - 1][predictedClass - 1]++;
            
            count++;
        }
        
        // Calculate average Cross Entropy
        double averageCrossEntropy = totalCrossEntropy / count;
        System.out.println("Average Cross Entropy: " + averageCrossEntropy);
        
        // Print the Confusion Matrix
        printConfusionMatrix(confusionMatrix);
    }
    
    // Method to calculate Cross Entropy for a given true class and predicted probabilities
    public static double calculateCrossEntropy(int y_true, float[] y_predicted) {
        double probability = y_predicted[y_true - 1];  // True class is 1-based
        return -Math.log(probability);  // Cross Entropy formula
    }
    
    // Method to get the predicted class (the one with the highest predicted probability)
    public static int getPredictedClass(float[] y_predicted) {
        int predictedClass = 1;
        float maxProb = y_predicted[0];
        
        // Find the class with the highest probability
        for (int i = 1; i < y_predicted.length; i++) {
            if (y_predicted[i] > maxProb) {
                maxProb = y_predicted[i];
                predictedClass = i + 1;  // Classes are 1-based
            }
        }
        
        return predictedClass;
    }
    
    // Method to print the Confusion Matrix in the desired format
    public static void printConfusionMatrix(int[][] matrix) {
        System.out.println("Confusion Matrix:");
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("[");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j]);
                if (j < matrix[i].length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }
}
