public class Neuron{
    private double error_signal = 0.0;
    private double output = 0.0;
    public Neuron(){
        error_signal = 0.0;
    }

    public double getError_signal(){
        return error_signal;
    }
    public void setError_signal(double newError){
        error_signal = newError;
    }
    public double getOutput(){
        return output;
    }
    public void setOutput(double new_out){
        output = new_out;
    }
}