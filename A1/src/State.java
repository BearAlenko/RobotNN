import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Created by 37919 on 2023/11/1.
 */
public class State {
    public static double max_distance_enemy = 1000;
    public static double max_distance_center = 500;
    public static double max_energy = 100;
    public enum value{low, med, high}
    public value distance_to_enemy;
    public value distance_to_center;
    public value my_energy;
    public value enemy_energy;
    public static int state_length = 4;

    public double distance_to_enemy_v;
    public double distance_to_center_v;
    public double my_energy_v;
    public double enemy_energy_v;

    public State(){
        distance_to_enemy = value.high;
        distance_to_center = value.med;
        my_energy = value.high;
        enemy_energy = value.high;
        distance_to_enemy_v = 0.5;
        distance_to_center_v = 0.5;
        my_energy_v = 1.0;
        enemy_energy_v = 1.0;
    }
    public int[] get_state_num(){
        int dis_e_n = 0;
        int dis_c_n = 0;
        int m_energy = 0;
        int e_energy = 0;
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
        switch (my_energy){
            case low:
                m_energy = 0;
                break;
            case med:
                m_energy = 1;
                break;
            case high:
                m_energy = 2;
                break;
            default:
                m_energy = 0;
                break;
        }
        switch (enemy_energy){
            case low:
                e_energy = 0;
                break;
            case med:
                e_energy = 1;
                break;
            case high:
                e_energy = 2;
                break;
            default:
                e_energy = 0;
                break;
        }
        int[] result = {dis_e_n, dis_c_n, m_energy, e_energy};
        return result;
    }

    public void setMy_energy(int energy_level){
        switch (energy_level){
            case 0:
                my_energy = value.low;
                break;
            case 1:
                my_energy = value.med;
                break;
            case 2:
                my_energy = value.high;
                break;
            default:
                my_energy = value.high;
                break;
        }
    }

    public void setDistance_to_enemy(int dis_level){
        switch (dis_level){
            case 0:
                distance_to_enemy = value.low;
                break;
            case 1:
                distance_to_enemy = value.med;
                break;
            case 2:
                distance_to_enemy = value.high;
                break;
            default:
                distance_to_enemy = value.high;
                break;
        }
    }

    public void setDistance_to_center(int dis_level){
        switch (dis_level){
            case 0:
                distance_to_center = value.low;
                break;
            case 1:
                distance_to_center = value.med;
                break;
            case 2:
                distance_to_center = value.high;
                break;
            default:
                distance_to_center = value.high;
                break;
        }
    }

    public void setEnemy_energy(int energy_level){
        switch (energy_level){
            case 0:
                enemy_energy = value.low;
                break;
            case 1:
                enemy_energy = value.med;
                break;
            case 2:
                enemy_energy = value.high;
                break;
            default:
                enemy_energy = value.high;
                break;
        }
    }

    public void copyCurState(State st){
        my_energy = st.my_energy;
        my_energy_v = st.my_energy_v;

        enemy_energy = st.enemy_energy;
        enemy_energy_v = st.enemy_energy_v;

        distance_to_enemy = st.distance_to_enemy;
        distance_to_enemy_v = st.distance_to_enemy_v;

        distance_to_center = st.distance_to_center;
        distance_to_center_v = st.distance_to_center_v;
    }

    // normalize the state value to (0, 1) or (-1, 1) according to the activation function:
    // the max distance to enemy in 800x600 field is 1000
    // the max distance to center in 800x600 field is 500
    // the max energy is 100
    public void setStateValue(double de, double dc, double me, double ee, String act_function){
        if (act_function.equals("sigmoid")) {
            distance_to_enemy_v = de / max_distance_enemy ;
            distance_to_center_v = dc / max_distance_center;
            my_energy_v = me / max_energy;
            enemy_energy_v = ee / max_energy;
        }
        else if (act_function.equals("bipolar")){
            distance_to_enemy_v = 2 * (de / max_distance_enemy) - 1 ;
            distance_to_center_v = 2 * (dc / max_distance_center) - 1;
            my_energy_v = 2 * (me / max_energy) - 1;
            enemy_energy_v = 2 * (ee / max_energy) - 1;
        }
        else System.out.println("Invalid activation function");
    }

    public double[] getStateValue(){
        double[] result = new double[state_length];
        result[0] = distance_to_enemy_v;
        result[1] = distance_to_center_v;
        result[2] = my_energy_v;
        result[3] = enemy_energy_v;
        return result;
    }
}
