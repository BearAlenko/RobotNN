import java.util.ArrayList;

public class Layer{
    // The number of hidden units in this layer. The dimension of output vector is equal to num_Neuron
    private int num_Neuron = 4;
    // The dimension of input to this layer.
    private int input_size = 2;
    private double momentum = 0.9;
    private double learning_rate = 0.1;
    //If this is the last layer
    private boolean last_layer = false;
    // Activation funciton name
    private String activation_func = "sigmoid";
    private int a = -1;
    private int b = 1;
    // A layer contains num_Neurons neurons
    public Neuron[] neurons;
    // The dimension of weights is set to num_Neurons x input_size. (H x D)
    public double[][] weights;
    public double[][] prev_weightchanges;
    // store the inputs to this layer in forward
    public double[] inputs;
    // store the outputs from this layer in forward
    public double[] outputs;


    public Layer(int num_Neuron, int input_size, boolean last_layer, double momentum, double learning_rate, String activation_func,  int a, int b){
        this.num_Neuron = num_Neuron;
        this.input_size = input_size;
        this.last_layer = last_layer;
        this.momentum = momentum;
        this.learning_rate = learning_rate;
        this.activation_func = activation_func;
        neurons = new Neuron[num_Neuron];
        //initialize neurons
        for (int j = 0; j < num_Neuron; j++){
            neurons[j] = new Neuron();
        }
        // input_size + 1 for bias
        weights = new double[num_Neuron][input_size+1];
        prev_weightchanges = new double[num_Neuron][input_size+1];
        inputs = new double[input_size+1];
        outputs = new double[num_Neuron];
        this.a = a;
        this.b = b;
        // Initialize the values
        zero_outputs();
        zero_prevs();
        zero_inputs();
        initialize_weights();
    }
    public void forward(double[] new_inputs){
        update_inputs(new_inputs);
        zero_outputs();
        for(int j=0; j<num_Neuron; j++){
            for(int i=0; i<input_size+1; i++){
                outputs[j] += weights[j][i] * inputs[i];
            }
            if (activation_func.equals("sigmoid")) {
                outputs[j] = sigmoid(outputs[j]);
            }
            else {
                outputs[j] = customSigmoid(outputs[j]);
            }
            neurons[j].setOutput(outputs[j]);
        }
    }
    // error_signal = output[j] * (1- output[j]) * sum(next_layer.neurons[h].error_signal * next_layer.weights[h][j])
    public void backward(Layer next_layer){
        for (int j=0; j<num_Neuron; j++){
            double df = 0;
            if (activation_func.equals("sigmoid")){
                df = outputs[j] * (1 - outputs[j]);
            }
            else {
                df = 0.5 * (1 - Math.pow(outputs[j], 2));
            }
            double sum_aboves = 0;
            for (int h=0; h<next_layer.getNum_Neuron(); h++){
                sum_aboves += next_layer.weights[h][j] * next_layer.neurons[h].getError_signal();
            }
            double new_err = df*sum_aboves;
            neurons[j].setError_signal(new_err);
        }
    }
    // error_signal = output[j] * (1- output[j]) * error[j], error[j] = label[j] - output[j]
    public void backward_lastlayer(double[] errors){
        for (int j=0; j<num_Neuron; j++){
            double df;
            if (activation_func.equals("sigmoid")){
                df = outputs[j] * (1 - outputs[j]);
            }
            else {
                df = 0.5 * (1 - Math.pow(outputs[j], 2));
            }
            double r = errors[j];
            double new_err = df*r;
            neurons[j].setError_signal(new_err);
        }
    }
    // update weights: w[j][i] = w[j][i] + a*prev_dw[j][i] + lr*neurons[j].error_signal*inputs[i]
    public void update_weights(){
        for (int j=0; j<num_Neuron; j++){
            for (int i=0; i<input_size+1; i++){
                double gradient = neurons[j].getError_signal()*inputs[i];
                double dw = momentum*prev_weightchanges[j][i] + learning_rate*gradient;
                weights[j][i] += dw;
                prev_weightchanges[j][i] = dw;
            }
        }
    }
    // get the new inputs from previous layer
    public void update_inputs(double[] new_inputs){
        for(int i=0; i<input_size+1; i++){
            if(i == input_size){
                inputs[i] = NeuralNetInterface.bias;
            }
            else inputs[i] = new_inputs[i];
        }
    }
    public void zero_outputs(){
        for(int j=0; j<num_Neuron; j++){
            outputs[j] = 0;
        }
    }
    public void zero_inputs(){
        for(int i=0; i<input_size-1; i++){
            inputs[i] = 0;
        }
        inputs[input_size-1] = NeuralNetInterface.bias;
    }
    public void zero_prevs(){
        for (int j=0; j<num_Neuron; j++){
            for (int i=0; i<input_size+1; i++){
                prev_weightchanges[j][i] = 0;
            }
        }
    }
    // initialize random weights
    public void initialize_weights(){
        for(int j = 0; j < num_Neuron; j++) {
            for(int i = 0; i < input_size+1; i++) {
                //Math.random() return 0 to 1
                weights[j][i] = Math.random() - 0.5;
            }
        }
    }
    /**
     * Return a binary sigmoid of the input X(The activation function)
     * @param x The input
     * @return f(x) = 1 / (1+e(-x))
     */

    public double sigmoid(double x) {
        return (double)1 / (1 + Math.exp(-x));
    }
    /**
     * Return a bipolar sigmoid of the input X(The activation function)
     * @param x The input
     * @return f(x) = 2 / (1+e(-x)) - 1
     */

    public double customSigmoid(double x) {
        return (double)(b - a) / (1 + Math.exp(-x)) + a;
    }
    public int getNum_Neuron(){
        return num_Neuron;
    }
    public boolean getLast_layer(){
        return last_layer;
    }
}