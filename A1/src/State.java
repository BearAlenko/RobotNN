import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Created by 37919 on 2023/11/1.
 */
public class State {
    public enum value{low, med, high}
    public value distance_to_enemy;
    public value distance_to_center;
    public value my_hp;
    public value enemy_hp;

    public State(){
        distance_to_enemy = value.high;
        distance_to_center = value.med;
        my_hp = value.high;
        enemy_hp = value.high;
    }

    public int[] get_state_num(){
        int dis_e_n = 0;
        int dis_c_n = 0;
        int m_hp = 0;
        int e_hp = 0;
        switch (distance_to_enemy){
            case low:
                dis_e_n = 0;
                break;
            case med:
                dis_e_n = 1;
                break;
            case high:
                dis_e_n = 2;
                break;
            default:
                dis_e_n = 0;
                break;
        }
        switch (distance_to_center){
            case low:
                dis_c_n = 0;
                break;
            case med:
                dis_c_n = 1;
                break;
            case high:
                dis_c_n = 2;
                break;
            default:
                dis_c_n = 0;
                break;
        }
        switch (my_hp){
            case low:
                m_hp = 0;
                break;
            case med:
                m_hp = 1;
                break;
            case high:
                m_hp = 2;
                break;
            default:
                m_hp = 0;
                break;
        }
        switch (enemy_hp){
            case low:
                e_hp = 0;
                break;
            case med:
                e_hp = 1;
                break;
            case high:
                e_hp = 2;
                break;
            default:
                e_hp = 0;
                break;
        }
        int[] result = {dis_e_n, dis_c_n, m_hp, e_hp};
        return result;
    }
}
