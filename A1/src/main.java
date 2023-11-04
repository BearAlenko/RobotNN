import java.io.IOException;
import java.util.Arrays;

public class main{
    public static boolean save_file = true;
    public static int trails = 1;
    public static int epochs = 10000;

    private static void nn(){
        int input_size = 2;
        int num_hiddens = 4;
        int output_size = 3;
        int num_layers = 2;
        double learning_rate = 0.2;
        int a = -1;
        int b = 1;

        //Dataset generating.........................
        double[][] X_sig;
        double[][] y_sig;
        double[][] X_bi;
        double[][] y_bi;
        X_sig = new double[][]{{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        y_sig = new double[][]{{0}, {1}, {1}, {0}};
        X_bi = new double[][]{{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        y_bi = new double[][]{{-1, -0.5, -0.5}, {1, 0.5, 0.5}, {1, 0.5, 0.5}, {-1, -0.5, -0.5}};
        //Dataset generating........................

        //Create models for tests...................
        //NeuralNet nn_sigmoid_noM = new NeuralNet(input_size,num_hiddens, output_size, num_layers, learning_rate, 0.0, a, b, "sigmoid", epochs, "sigmoid_noM");
        //NeuralNet nn_bipolar_noM = new NeuralNet(input_size,num_hiddens, output_size, num_layers, learning_rate, 0.0, a, b, "bipolar", epochs, "bipolar_noM");
        //NeuralNet nn_sigmoid_M = new NeuralNet(input_size,num_hiddens, output_size, num_layers, learning_rate, 0.9, a, b, "sigmoid", epochs, "sigmoid_M");
        NeuralNet nn_bipolar_M = new NeuralNet(input_size,num_hiddens, output_size, num_layers, learning_rate, 0.9, a, b, "bipolar", epochs, "bipolar_M");
        //Create models for tests...................

        //Training and evaluation..................
        int total_epochs_snoM = 0;
        int total_epochs_bnoM = 0;
        int total_epochs_sM = 0;
        int total_epochs_bM = 0;
        for(int t = 0; t < trails; t++) {
            //total_epochs_snoM += nn_sigmoid_noM.train(X_sig, y_sig, 4);
            //total_epochs_bnoM += nn_bipolar_noM.train(X_bi,y_bi,4);
            //total_epochs_sM += nn_sigmoid_M.train(X_sig,y_sig,4);
            total_epochs_bM += nn_bipolar_M.train(X_bi,y_bi,4);
            if (save_file) {
                //nn_sigmoid_noM.write_file();
                //nn_bipolar_noM.write_file();
                //nn_sigmoid_M.write_file();
                nn_bipolar_M.write_file();
            }
            //nn_sigmoid_noM.initializeWeights();
            //nn_bipolar_noM.initializeWeights();
            //nn_sigmoid_M.initializeWeights();
            nn_bipolar_M.initializeWeights();
        }
        //System.out.println("Average epoch number using sigmoid function without momentum: " + total_epochs_snoM/trails);
        //System.out.println("Average epoch number using bipolar function without momentum: " + total_epochs_bnoM/trails);
        //System.out.println("Average epoch number using sigmoid function with momentum: " + total_epochs_sM/trails);
        System.out.println("Average epoch number using bipolar function with momentum: " + total_epochs_bM/trails);
        for (int i = 0; i < 4; i++) {
            System.out.println(Arrays.toString(X_bi[i]) + " " + Arrays.toString(y_bi[i]));
        }

        //Training and evaluation..................
    }
    //public static NeuralNet nn;
    public static void main(String args[]){
        LookUpTable lut = new LookUpTable();
        try {
            lut.load("lut_for_nn.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[][] inputs = new double[405][5];
        double[][] labels = new double[405][1];
        int lines = 0;
        for (int de = 0; de < 3; de++){
            for (int dc = 0; dc < 3; dc++){
                for (int me = 0; me < 3; me++){
                    for (int ee = 0; ee < 3; ee++){
                        for (int ac = 0; ac < Action.action_length; ac++){
                            // create inputs from lut and normalize the quantization to (-1, 1)
                            inputs[lines][0] = de - 1;
                            inputs[lines][1] = dc - 1;
                            inputs[lines][2] = me - 1;
                            inputs[lines][3] = ee - 1;
                            inputs[lines][4] = (ac - 2)/2;
                            labels[lines][0] = lut.get_value_byIndex(de, dc, me, ee, ac);
                            lines++;
                        }
                    }
                }
            }
        }

        normalize(labels, 405);

        train_nn_lut(inputs, labels, 405);
    }

    static void normalize(double[][] labels, int size){
        double total = 0;
        double max = -100000;
        double min = 100000;
        for (int i = 0; i < size; i++){
            total += labels[i][0];
            if (labels[i][0] > max) max = labels[i][0];
            if (labels[i][0] < min) min = labels[i][0];
        }
        double mean = total/size;
        double range = max - min;
        for (int i = 0; i < size; i++){
            labels[i][0] = (labels[i][0] - mean)/range;
            System.out.println(labels[i][0]);
        }
    }

    static void train_nn_lut(double[][] inputs, double[][] outputs, int datasize){
        int input_size = 5;
        int num_hiddens = 32;
        int output_size = 1;
        int num_layers = 2;
        double learning_rate = 0.1;
        int a = -1;
        int b = 1;

        //Create models for tests...................
        NeuralNet nn_bipolar_M = new NeuralNet(input_size,num_hiddens, output_size, num_layers, learning_rate, 0.9, a, b, "bipolar", epochs, "bipolar_M");
        //Create models for tests...................

        //Training and evaluation..................

        for(int t = 0; t < trails; t++) {
            nn_bipolar_M.train(inputs,outputs,datasize);
            if (save_file) {
                nn_bipolar_M.write_RMSE_file("");
            }
            nn_bipolar_M.initializeWeights();
        }
    }
}