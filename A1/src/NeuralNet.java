import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NeuralNet implements NeuralNetInterface{
    private int input_size = 2;
    private int num_hiddens = 4;
    private int output_size = 1;
    private int num_layers = 2;
    private Layer[] layers;
    public double learning_rate = 0.2;
    private double momentum = 0;
    private int a = -1;
    private int b = 1;
    private String activation_func = "sigmoid";
    private double error_threshold = 0.05;
    private int epochs = 0;
    private List<Double> error_logs;
    private String file_name;
    private List<Double> RMSE_logs;

    public NeuralNet(int input_size, int num_hiddens, int output_size, int num_layers, double learning_rate, double momentum, int a, int b, String activation_func, int epochs, String file_name) {
        this.input_size = input_size;
        this.num_hiddens = num_hiddens;
        this.output_size = output_size;
        this.num_layers = num_layers;
        layers = new Layer[num_layers];
        //Create the hidden layers
        layers[0] = new Layer(num_hiddens, input_size, false, momentum, learning_rate, activation_func, a, b);
        //Create output layer
        layers[1] = new Layer(output_size, num_hiddens, true, momentum, learning_rate, activation_func, a, b);
        this.learning_rate = learning_rate;
        this.momentum = momentum;
        this.a = a;
        this.b = b;
        this.activation_func = activation_func;
        this.epochs = epochs;
        error_logs = new ArrayList<Double>();
        RMSE_logs = new ArrayList<Double>();
        this.file_name = file_name;
        //epoch = 0;
    }

    public void forward(double[] inputs){
        double[] x = inputs;
        for (int i = 0; i < num_layers; i++){
            layers[i].forward(x);
            x = layers[i].outputs;
        }
    }
    //This backward func updates all weights after updating all of the error
    public void backward(double[] errors){
        //for each layer call backward (backward order int i=layer_num-1, i>=0, i--)
        for (int i = num_layers-1; i>=0; i--){
            if (i == num_layers-1 && layers[i].getLast_layer()){
                layers[i].backward_lastlayer(errors);
            }
            else {
                layers[i].backward(layers[i + 1]);
            }
        }
        //for each layer call update_weights
        for (int i = num_layers-1; i>=0; i--){
            layers[i].update_weights();
        }
    }

    //This backward func updates weights of the layer after updating the error of the layer
    public void backward2(double[] errors){
        //for each layer call backward (backward order int i=layer_num-1, i>=0, i--) and update_weights
        for (int i = num_layers-1; i>=0; i--){
            if (i == num_layers-1 && layers[i].getLast_layer()){
                layers[i].backward_lastlayer(errors);
                layers[i].update_weights();
            }
            else {
                layers[i].backward(layers[i + 1]);
                layers[i].update_weights();
            }
        }
    }

    // for each epoch, feed the train data to model and calculate the errors and then do the backpropagation
    public int train(double[][] inputs, double[][] labels, int len_dataset){
        double total_error = 0.0;
        double[] d_error = new double[output_size];
        for (int e = 0; e < epochs; e++){
            //reset loss value
            //System.out.println(String.valueOf(e));
            total_error = 0.0;
            for (int i = 0; i < len_dataset; i++){
                forward(inputs[i]);
                for (int j = 0; j < output_size; j++){
                    //d_error[j] = Math.sqrt(Math.pow(labels[j][0] - layers[num_layers-1].outputs[j], 2));
                    d_error[j] = labels[i][j] - layers[num_layers-1].outputs[j];
                    total_error += Math.pow(d_error[j], 2);
                }
                backward2(d_error);
            }
            total_error /= 2;
            //System.out.println("loss at epoch " + e + "is: " + total_error);
            error_logs.add(total_error);
            RMSE_logs.add(Math.sqrt(total_error/len_dataset));
            /*if (total_error < error_threshold){
                return e;
            }*/
        }
        return epochs;
    }
    /**
     * Return a binary sigmoid of the input X(The activation function)
     * @param x The input
     * @return f(x) = 1 / (1+e(-x))
     */
    @Override
    public double sigmoid(double x) {
        return (double)1 / (1 + Math.exp(-x));
    }
    /**
     * Return a bipolar sigmoid of the input X(The activation function)
     * @param x The input
     * @return f(x) = 2 / (1+e(-x)) - 1
     */
    @Override
    public double customSigmoid(double x) {
        return (double)(b - a) / (1 + Math.exp(-x)) + a;
    }
    /**
     * Initialization step 2 : Initialize the weights to random values.
     * For say 2 inputs, the input vector is [0] & [1]. We add [2] for the bias.
     * Like wise for hidden units. For say 2 hidden units which are stored in an array.
     * [0] & [1] are the hidden & [2] the bias.
     * We also initialise the last weight change arrays. This is to implement the alpha term.
     */
    @Override
    public void initializeWeights() {
        for (int l = 0; l < num_layers; l++){
            layers[l].initialize_weights();
        }
    }

    @Override
    public void zeroWeights(){
        //
    }
    @Override
    public double train(double [] X, double argValue){
        return 0.0;
    }


    // Only one output
    @Override
    public double outputFor(double[] X){return 0.0;}

    public double infer(double [] state_values, double action_values){
        double[] input = new double[State.state_length + 1];
        for (int i = 0; i < State.state_length; i++){
            input[i] = state_values[i];
        }
        input[State.state_length] = action_values;
        double[] output = input;
        for (int i = 0; i < num_layers; i++){
            layers[i].forward(output);
            output = layers[i].outputs;
        }
        return output[0];
    }
    @Override
    public void save(File argFile){

    }
    @Override
    public void load(String argFileName) throws IOException{

    }

    public void write_file(){
        String[][] content = new String[error_logs.size()][2];
        System.out.println("the written csv is " + error_logs.size() + "long.");
        for (int i = 0; i < error_logs.size(); i++){
            content[i][0] = String.valueOf(i);
            content[i][1] = String.valueOf(error_logs.get(i));
        }
        try {
            writeCSV.write(content, file_name);
        }
        catch (Exception e){
            System.out.println("Write file error");
        }
    }

    public void write_RMSE_file(String path){
        String[][] content = new String[RMSE_logs.size()][2];
        System.out.println("the written csv is " + RMSE_logs.size() + "long.");
        String new_filename = path+"RMSE_lr="+String.valueOf(learning_rate)+"_mom="
                +String.valueOf(momentum)+"_neu="+String.valueOf(num_hiddens) +".csv";
        for (int i = 0; i < RMSE_logs.size(); i++){
            content[i][0] = String.valueOf(i);
            content[i][1] = String.valueOf(RMSE_logs.get(i));
        }
        try {
            writeCSV.write(content, new_filename);
        }
        catch (Exception e){
            System.out.println("Write file error");
        }
    }

}