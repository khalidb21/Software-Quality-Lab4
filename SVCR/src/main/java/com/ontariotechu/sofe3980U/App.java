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

		// Array to store file names
		String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};

		// Arrays to store evaluation metrics for each model
        double[] mseArr = new double[3];
        double[] maeArr = new double[3];
        double[] mareArr = new double[3];

		// Evaluate each model and store results
        for (int i = 0; i < files.length; i++) {
            double[] results = evaluateModel(files[i]);

            mseArr[i] = results[0];
            maeArr[i] = results[1];
            mareArr[i] = results[2];

            System.out.println("for " + files[i]);
            System.out.println("\tMSE =" + mseArr[i]);
            System.out.println("\tMAE =" + maeArr[i]);
            System.out.println("\tMARE =" + mareArr[i]);
        }

        // Find best models according to each metric
        int bestMSE = findMinIndex(mseArr);
        int bestMAE = findMinIndex(maeArr);
        int bestMARE = findMinIndex(mareArr);

		// Print best models
        System.out.println("According to MSE, The best model is " + files[bestMSE]);
        System.out.println("According to MAE, The best model is " + files[bestMAE]);
        System.out.println("According to MARE, The best model is " + files[bestMARE]);
    }
	
	public static double[] evaluateModel(String filePath) {
        List<String[]> allData;

        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading file: " + filePath);
            return new double[]{0,0,0};
        }

		// Initialize metrics
        double mse = 0, mae = 0, mare = 0;
        double epsilon = 1e-10;
        int n = 0;

		// For each row, calculate errors and update metrics
        for (String[] row : allData) {
            double y_true = Double.parseDouble(row[0]);
            double y_pred = Double.parseDouble(row[1]);

            double error = y_true - y_pred;

			// Summation for MSE, MAE, and MARE
            mse += Math.pow(error, 2);
            mae += Math.abs(error);
            mare += Math.abs(error) / (Math.abs(y_true) + epsilon);

            n++;
        }

		// Average the metrics
        mse /= n;
        mae /= n;
        mare /= n;

		// Return metrics as an array
        return new double[]{mse, mae, mare};
    }

	// Helper method to find index of minimum value in an array
    public static int findMinIndex(double[] arr) {
        int minIndex = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }
}
