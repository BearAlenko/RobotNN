public class main{
    public static boolean save_file = true;
    public static int trails = 1;
    public static int epochs = 10000;
    //public static NeuralNet nn;
    public static void main(String args[]){
        int input_size = 2;
        int num_hiddens = 4;
        int output_size = 1;
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
        y_bi = new double[][]{{-1}, {1}, {1}, {-1}};
        //Dataset generating........................

        //Create models for tests...................
        NeuralNet nn_sigmoid_noM = new NeuralNet(input_size,num_hiddens, output_size, num_layers, learning_rate, 0.0, a, b, "sigmoid", epochs, "sigmoid_noM");
        NeuralNet nn_bipolar_noM = new NeuralNet(input_size,num_hiddens, output_size, num_layers, learning_rate, 0.0, a, b, "bipolar", epochs, "bipolar_noM");
        NeuralNet nn_sigmoid_M = new NeuralNet(input_size,num_hiddens, output_size, num_layers, learning_rate, 0.9, a, b, "sigmoid", epochs, "sigmoid_M");
        NeuralNet nn_bipolar_M = new NeuralNet(input_size,num_hiddens, output_size, num_layers, learning_rate, 0.9, a, b, "bipolar", epochs, "bipolar_M");
        //Create models for tests...................

        //Training and evaluation..................
        int total_epochs_snoM = 0;
        int total_epochs_bnoM = 0;
        int total_epochs_sM = 0;
        int total_epochs_bM = 0;
        for(int t = 0; t < trails; t++) {
            total_epochs_snoM += nn_sigmoid_noM.train(X_sig, y_sig, 4);
            total_epochs_bnoM += nn_bipolar_noM.train(X_bi,y_bi,4);
            total_epochs_sM += nn_sigmoid_M.train(X_sig,y_sig,4);
            total_epochs_bM += nn_bipolar_M.train(X_bi,y_bi,4);
            if (save_file) {
                nn_sigmoid_noM.write_file();
                nn_bipolar_noM.write_file();
                nn_sigmoid_M.write_file();
                nn_bipolar_M.write_file();
            }
            nn_sigmoid_noM.initializeWeights();
            nn_bipolar_noM.initializeWeights();
            nn_sigmoid_M.initializeWeights();
            nn_bipolar_M.initializeWeights();
        }
        System.out.println("Average epoch number using sigmoid function without momentum: " + total_epochs_snoM/trails);
        System.out.println("Average epoch number using bipolar function without momentum: " + total_epochs_bnoM/trails);
        System.out.println("Average epoch number using sigmoid function with momentum: " + total_epochs_sM/trails);
        System.out.println("Average epoch number using bipolar function with momentum: " + total_epochs_bM/trails);

        //Training and evaluation..................
    }
}