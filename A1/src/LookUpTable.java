/**
 * Created by 37919 on 2023/11/1.
 */
public class LookUpTable {
    private double[][][][][] values;
    public State pre_State;
    public State cur_State;
    public Action pre_Action;
    public Action cur_Action;
    public LookUpTable(){
        initialize_lut();
        pre_State = new State();
        cur_State = new State();
        pre_Action = Action.idle;
        cur_Action = Action.idle;
    }
    public void initialize_lut(){
        values = new double[3][3][3][3][6];
    }
    public Action get_random_action(){
        int random = 1 + (int)(Math.random() * ((6 - 1) + 1));
        switch (random){
            case 1:
                return Action.fire;
            case 2:
                return Action.forward;
            case 3:
                return Action.backward;
            case 4:
                return Action.left;
            case 5:
                return Action.right;
            case 6:
                return Action.idle;
            default:
                return Action.idle;
        }
    }

    public Action get_max_action(){
        double max_value = Double.NEGATIVE_INFINITY;
        int max_index = 0;
        for(int i = 0; i < 6; i++){
            int[] state_num = cur_State.get_state_num();
            double cur_State_act_value = values[state_num[0]][state_num[1]][state_num[2]][state_num[3]][i];
            if (cur_State_act_value > max_value){
                max_value = cur_State_act_value;
                max_index = i;
            }
        }
        return Action.get_action(max_index);
    }

    public void set_value_onpolicy(double reward){
        //TO DO
    }

    public void set_value_offpolicy(double reward){
        //TO DO
    }

    public void update_value(double reward, boolean on_off_policy){
        if (on_off_policy){
            set_value_onpolicy(reward);
        }
        else set_value_offpolicy(reward);
    }

    // This method update the state with new_state and choose the new action.
    public Action choose_action(double random_rate, State new_state){
        pre_State = cur_State;
        pre_Action = cur_Action;
        cur_State = new_state;
        double random_number = Math.random();
        // if it is less than the random_rate, then explotrary walk.
        if (random_number <= random_rate){
            cur_Action = get_random_action();
        }
        else cur_Action = get_max_action();
        return cur_Action;
    }
}
