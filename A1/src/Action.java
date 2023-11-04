/**
 * Created by 37919 on 2023/11/1.
 */
public enum  Action {
    fire, forward, backward, spin_forward, spin_backward;
    public static int action_length = 5;
    public static int get_index(Action a){
        switch (a){
            case fire:
                return 0;
            case forward:
                return 1;
            case backward:
                return 2;
            case spin_forward:
                return 3;
            case spin_backward:
                return 4;
            default:
                return 1;
        }
    }
    public static Action get_action(int index){
        switch (index){
            case 0:
                return fire;
            case 1:
                return forward;
            case 2:
                return backward;
            case 3:
                return spin_forward;
            case 4:
                return spin_backward;
            default:
                return forward;
        }
    }

    public static double get_bipolar_value(Action a){
        switch (a){
            case fire:
                return 1;
            case forward:
                return 0.5;
            case backward:
                return 0;
            case spin_forward:
                return -0.5;
            case spin_backward:
                return -1;
            default:
                return 1;
        }
    }
}
