package com.ontariotechu.sofe3980U;


import java.io.FileReader;
import java.util.ArrayList;
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
		String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};

        double[] bceArr = new double[3];
        double[] accArr = new double[3];
        double[] precArr = new double[3];
        double[] recallArr = new double[3];
        double[] f1Arr = new double[3];
        double[] aucArr = new double[3];

        for (int i = 0; i < files.length; i++) {

            Result r = evaluate(files[i]);

            bceArr[i] = r.bce;
            accArr[i] = r.accuracy;
            precArr[i] = r.precision;
            recallArr[i] = r.recall;
            f1Arr[i] = r.f1;
            aucArr[i] = r.auc;

            System.out.println("for " + files[i]);
            System.out.println("\tBCE =" + r.bce);

            System.out.println("\tConfusion matrix");
            System.out.println("\t\t\ty=1\t\ty=0");
            System.out.println("\t\ty^=1\t" + r.TP + "\t" + r.FP);
            System.out.println("\t\ty^=0\t" + r.FN + "\t" + r.TN);

            System.out.println("\tAccuracy =" + r.accuracy);
            System.out.println("\tPrecision =" + r.precision);
            System.out.println("\tRecall =" + r.recall);
            System.out.println("\tf1 score =" + r.f1);
            System.out.println("\tauc roc =" + r.auc);
        }

        // Best models
        System.out.println("According to BCE, The best model is " + files[minIndex(bceArr)]);
        System.out.println("According to Accuracy, The best model is " + files[maxIndex(accArr)]);
        System.out.println("According to Precision, The best model is " + files[maxIndex(precArr)]);
        System.out.println("According to Recall, The best model is " + files[maxIndex(recallArr)]);
        System.out.println("According to F1 score, The best model is " + files[maxIndex(f1Arr)]);
        System.out.println("According to AUC ROC, The best model is " + files[maxIndex(aucArr)]);
    }

    static class Result {
        double bce, accuracy, precision, recall, f1, auc;
        int TP, TN, FP, FN;
    }

    public static Result evaluate(String filePath) {

        List<String[]> allData;

        try {
            FileReader fr = new FileReader(filePath);
            CSVReader reader = new CSVReaderBuilder(fr).withSkipLines(1).build();
            allData = reader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading file");
            return new Result();
        }

        double epsilon = 1e-10;
        double bce = 0;

        int TP = 0, TN = 0, FP = 0, FN = 0;

        int nPositive = 0, nNegative = 0;

        List<Integer> yTrueList = new ArrayList<>();
        List<Double> yPredList = new ArrayList<>();

        for (String[] row : allData) {

            int y_true = Integer.parseInt(row[0]);
            double y_pred = Double.parseDouble(row[1]);

            yTrueList.add(y_true);
            yPredList.add(y_pred);

            // BCE
            if (y_true == 1)
                bce += Math.log(y_pred + epsilon);
            else
                bce += Math.log(1 - y_pred + epsilon);

            // Threshold = 0.5
            int y_hat = (y_pred >= 0.5) ? 1 : 0;

            if (y_true == 1 && y_hat == 1) TP++;
            else if (y_true == 0 && y_hat == 0) TN++;
            else if (y_true == 0 && y_hat == 1) FP++;
            else if (y_true == 1 && y_hat == 0) FN++;

            if (y_true == 1) nPositive++;
            else nNegative++;
        }

        int n = allData.size();
        bce = -bce / n;

        double accuracy = (double)(TP + TN) / n;
        double precision = (double)TP / (TP + FP + epsilon);
        double recall = (double)TP / (TP + FN + epsilon);
        double f1 = 2 * precision * recall / (precision + recall + epsilon);

        // ROC + AUC
        double[] x = new double[101];
        double[] y = new double[101];

        for (int i = 0; i <= 100; i++) {
            double th = i / 100.0;

            int TP_t = 0, FP_t = 0;

            for (int j = 0; j < n; j++) {
                int yt = yTrueList.get(j);
                double yp = yPredList.get(j);

                if (yp >= th) {
                    if (yt == 1) TP_t++;
                    else FP_t++;
                }
            }

            y[i] = (double)TP_t / nPositive; // TPR
            x[i] = (double)FP_t / nNegative; // FPR
        }

        double auc = 0;
        for (int i = 1; i <= 100; i++) {
            auc += (y[i-1] + y[i]) * Math.abs(x[i-1] - x[i]) / 2;
        }

        Result r = new Result();
        r.bce = bce;
        r.accuracy = accuracy;
        r.precision = precision;
        r.recall = recall;
        r.f1 = f1;
        r.auc = auc;
        r.TP = TP;
        r.TN = TN;
        r.FP = FP;
        r.FN = FN;

        return r;
    }

    public static int minIndex(double[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++)
            if (arr[i] < arr[idx]) idx = i;
        return idx;
    }

    public static int maxIndex(double[] arr) {
        int idx = 0;
        for (int i = 1; i < arr.length; i++)
            if (arr[i] > arr[idx]) idx = i;
        return idx;
    }
}
