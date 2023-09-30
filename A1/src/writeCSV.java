import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by 37919 on 2023/9/30.
 */
public class writeCSV {
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