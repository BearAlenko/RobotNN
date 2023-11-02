/**
 * Created by 37919 on 2023/11/1.
 */
public enum  Action {
    fire, forward, backward, left, right, idle;
    public static int get_index(Action a){
        switch (a){
            case fire:
                return 0;
            case forward:
                return 1;
            case backward:
                return 2;
            case left:
                return 3;
            case right:
                return 4;
            case idle:
                return 5;
            default:
                return 5;
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
                return left;
            case 4:
                return right;
            case 5:
                return idle;
            default:
                return idle;
        }
    }
}
