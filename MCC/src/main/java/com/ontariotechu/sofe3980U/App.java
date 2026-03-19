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
		String filePath="model.csv";
		FileReader filereader;
		List<String[]> allData;
		try{
			filereader = new FileReader(filePath); 
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
			allData = csvReader.readAll();
		}
		catch(Exception e){
			System.out.println( "Error reading the CSV file" );
			return;
		}
		
		double ce = 0.0;
        double epsilon = 1e-10;

        int[][] confusion = new int[5][5]; // [predicted][actual]

        for (String[] row : allData) {

            int y_true = Integer.parseInt(row[0]) - 1; // convert to 0-based index
            double[] y_pred = new double[5];

            for(int i = 0; i < 5; i++){
                y_pred[i] = Double.parseDouble(row[i+1]);
            }

            // Cross Entropy
            ce += Math.log(y_pred[y_true] + epsilon);

            // Prediction = argmax
            int y_hat = 0;
            double max = y_pred[0];
            for(int i = 1; i < 5; i++){
                if(y_pred[i] > max){
                    max = y_pred[i];
                    y_hat = i;
                }
            }

            // Confusion matrix
            confusion[y_hat][y_true]++;
        }

        ce = -ce / allData.size();

        // Print CE
        System.out.println("CE =" + ce);

        // Print Confusion Matrix
        System.out.println("Confusion matrix");
        System.out.println("\t\ty=1\t y=2\t y=3\t y=4\t y=5");

        for(int i = 0; i < 5; i++){
            System.out.print("\ty^=" + (i+1) + "\t");
            for(int j = 0; j < 5; j++){
                System.out.print(confusion[i][j] + "\t");
            }
            System.out.println();
        }
    }
}