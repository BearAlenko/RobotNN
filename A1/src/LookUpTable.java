import robocode.RobocodeFileOutputStream;

import java.io.*;
import java.util.Random;
import java.io.File;

/**
 * Created by 37919 on 2023/11/1.
 */
public class LookUpTable implements CommonInterface{
    // the q values in lookuptable
    private double[][][][][] values;
    public LookUpTable(){
        //values = new double[3][3][3][3][6];
        initialize_lut();
    }

    // randomly initialize the table values
    public void initialize_lut(){
        values = new double[3][3][3][3][5];
        for (int de = 0; de < 3; de++){
            for(int dc = 0; dc < 3; dc++){
                for(int mh = 0; mh < 3; mh++){
                    for (int eh = 0; eh < 3; eh++){
                        for (int act = 0; act < 5; act++){
                            values[de][dc][mh][eh][act] = Math.random();
                        }
                    }
                }
            }
        }
        System.out.println("Look Up Table initialized");
    }

    public double get_value(State state, int action_index){
        int[] index = state.get_state_num();
        return values[index[0]][index[1]][index[2]][index[3]][action_index];
    }

    public double get_value_byIndex(int state0, int state1, int state2, int state3, int action_index){
        return values[state0][state1][state2][state3][action_index];
    }

    public void set_value(State state, int action_index, double value){
        int[] index = state.get_state_num();
        values[index[0]][index[1]][index[2]][index[3]][action_index] = value;
    }

    // This method choose the new action.
    public Action choose_action(double random_rate, State state){
        double random_number = Math.random();
        Action cur_action;
        // if it is less than the random_rate, then explotrary walk.
        if (random_number <= random_rate){
            cur_action = get_random_action();
        }
        else cur_action = get_max_action(state);
        return cur_action;
    }

    public Action get_random_action(){
        Random rand = new Random();
        return Action.get_action(rand.nextInt(5));
    }

    public Action get_max_action(State state){
        double max_value = Double.NEGATIVE_INFINITY;
        int max_index = 0;
        for(int i = 0; i < 5; i++){
            double cur_State_act_value = get_value(state, i);
            if (cur_State_act_value > max_value){
                max_value = cur_State_act_value;
                max_index = i;
            }
        }
        return Action.get_action(max_index);
    }

    @Override
    public double outputFor(double[] X) {
        //dis_e_n, dis_c_n, m_energy, e_energy
        int dis_e_n = (int)X[0];
        int dis_c_n = (int)X[1];
        int m_energy = (int)X[2];
        int e_energy = (int)X[3];
        int act = (int)X[4];
        return values[dis_e_n][dis_c_n][m_energy][e_energy][act];
    }

    @Override
    public double train(double[] X, double argValue) {
        int dis_e_n = (int)X[0];
        int dis_c_n = (int)X[1];
        int m_energy = (int)X[2];
        int e_energy = (int)X[3];
        int act = (int)X[4];
        values[dis_e_n][dis_c_n][m_energy][e_energy][act] = argValue;
        return 1;
    }

    @Override
    public void save(File argFile) {
        PrintStream stream = null;
        try {
            stream = new PrintStream(new RobocodeFileOutputStream(argFile));
            for (int de = 0; de < 3; de++){
                for (int dc = 0; dc < 3; dc++){
                    for (int me = 0; me < 3; me++){
                        for (int ee = 0; ee < 3; ee++){
                            for (int ac = 0; ac < 5; ac++){
                                stream.println(de+"\t"+dc+"\t"+me+"\t"+ee+"\t"+ac+"\t"+values[de][dc][me][ee][ac]);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.flush();
            stream.close();
        }
    }

    @Override
    public void load(String argFileName) throws IOException {
        BufferedReader read_file = new BufferedReader(new FileReader(argFileName));
        String nextline = read_file.readLine();
        while (nextline != null){
            String[] numbers = nextline.split("\t");
            int de = Integer.valueOf(numbers[0]);
            int dc = Integer.valueOf(numbers[1]);
            int me = Integer.valueOf(numbers[2]);
            int ee = Integer.valueOf(numbers[3]);
            int ac = Integer.valueOf(numbers[4]);
            double value = Double.valueOf(numbers[5]);
            nextline = read_file.readLine();
            values[de][dc][me][ee][ac] = value;
        }
        read_file.close();
    }

    public void load(File argFileName) throws IOException {
        BufferedReader read_file = new BufferedReader(new FileReader(argFileName));
        String nextline = read_file.readLine();
        while (nextline != null){
            String[] numbers = nextline.split("\t");
            int de = Integer.valueOf(numbers[0]);
            int dc = Integer.valueOf(numbers[1]);
            int me = Integer.valueOf(numbers[2]);
            int ee = Integer.valueOf(numbers[3]);
            int ac = Integer.valueOf(numbers[4]);
            double value = Double.valueOf(numbers[5]);
            nextline = read_file.readLine();
            values[de][dc][me][ee][ac] = value;
        }
        read_file.close();
    }

}
