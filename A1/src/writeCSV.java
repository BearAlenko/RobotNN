import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;

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

    public static void save_data(File file_name, double[] data) throws IOException {
        RobocodeFileOutputStream rfos= new RobocodeFileOutputStream(file_name);
        PrintStream out_stream = new PrintStream(rfos);
        for (double s_data: data) {
            out_stream.format(Double.toString(s_data) + "\n");
        }
        out_stream.close();
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