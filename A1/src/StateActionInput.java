/**
 * Created by 37919 on 2023/11/3.
 */
public class StateActionInput {
    public double[] state_values;
    public double act_value;
    public double q_value;

    public StateActionInput(State state, Action act, double qvalue){
        this.state_values = state.getStateValue();
        this.act_value = Action.get_bipolar_value(act);
        this.q_value = qvalue;
    }

}
