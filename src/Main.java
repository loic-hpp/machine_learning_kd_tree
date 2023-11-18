import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Reading the csv file
            double[][] data = new double[569][11];
            String line;
            try (BufferedReader br = new BufferedReader(new FileReader("data.csv"))) {
                // Here is the array structure by index
                // 0 diagnosis	1 radius_mean	2 texture_mean	3 perimeter_mean	4 area_mean
                // 5 smoothness_mean	6 compactness_mean	7 concavity_mean	8 concave points_mean	9 symmetry_mean
                // 10 fractal_dimension_mean

                // Read the first line of the csv but we don't use it
                String[] row = br.readLine().split(",");
                int index = 0;
                while ((line = br.readLine()) != null) {
                    row = line.split(",");
                    double[] rowDouble = new double[11];
                    rowDouble[0] = row[1].equals("M")? 1.0:0.0;
                    for(int i=1; i< 11; i++)
                        rowDouble[i] = Double.parseDouble(row[i+1]);
                    data[index++] = rowDouble;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }