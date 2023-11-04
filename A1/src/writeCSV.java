import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import robocode.RobocodeFileOutputStream;
import robocode.RobocodeFileWriter;

/**
 * Created by 37919 on 2023/9/30.
 */
public class writeCSV {
    double[] win_rate;
    public writeCSV(int test_num){
        win_rate = new double[test_num];
    }

    public void writeToFile(File fileToWrite, double[] table) {
        try{
            RobocodeFileWriter fileWriter = new RobocodeFileWriter(fileToWrite.getAbsolutePath(), true);
            for (double data: table) {
                fileWriter.write(" " + Double.toString(data) + "\r\n");
            }
            fileWriter.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    public static void write(String[][] content, String out_name) throws IOException {

        File csvFile = new File(out_name += ".csv");
        FileWriter fileWriter = new FileWriter(csvFile);

        for (String[] data : content) {
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < data.length; i++) {
                line.append("\"");
                line.append(data[i].replaceAll("\"","\"\""));
                line.append("\"");
                if (i != data.length - 1) {
                    line.append(',');
                }
            }
            line.append("\n");
            fileWriter.write(line.toString());
        }
        fileWriter.close();
    }
}