/**
 * Created by 37919 on 2023/11/3.
 */
import robocode.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by 37919 on 2023/11/1.
 */
public class superRobot extends AdvancedRobot {
    // Base degree used for angles
    public final static double base_degree = 360;
    public final static int replay_size = 100;
    // replay memory
    public static ReplayMemory<StateActionInput> replay_memory = new ReplayMemory<StateActionInput>(replay_size);

    // State-action pairs of previous and current
    private State pre_state = new State();
    private State cur_state = new State();
    private Action pre_action = Action.fire;
    private Action cur_action = Action.fire;

    // stored previous qvalues for all actions for next learning epoch
    private double pre_value = 0.0;

    // the flag of enemy detected
    private boolean scanned_enemy;

    // hyperparameters
    private static double initial_random_rate = 0.35; //best 0.35
    private double random_rate = initial_random_rate;
    public static double init_learning_rate = 0.6;
    public static double learning_rate = init_learning_rate;
    public static double gamma = 0.5;
    // terminal rewards and intermediate rewards
    private double win_Reward = 0.8;
    private double lose_Reward =  -0.8;
    private double hit_bonus = 0.28;
    private double get_hit_bonus = -0.28;
    private double miss_hit_bonus = -0.16;
    private double hit_tank_bonus = -0.16;
    private double hit_wall_bonus = -0.16;


    // initial reward
    private double reward = 0.0;

    // control flag of on/off-policy and using intermediate rewards
    private boolean on_off_policy = false;
    private boolean terminal_only = false;

    // threshold of different levels of center distances
    private double center_distance_threshold = 200;

    // Neural Net
    private static int input_size = 5;
    private static int num_neurons = 32;
    private static int num_layers = 2;
    private static int dataset_size = 20;
    private static int output_size = 1;
    private static int epochs = 20;
    private static double nn_lr = 0.6;
    private static double nn_momentum = 0.9;
    private static String activation_func = "bipolar";
    public static NeuralNet nn = new NeuralNet(input_size, num_neurons,
    output_size, num_layers, nn_lr, nn_momentum, -1, 1, activation_func, epochs, "test");
    private double enemy_bearing = 0;

    // warm the agent for the number of dataset_size updating unless it has no enough training data

    // Statistics parameters
    private static int test_rounds = 50;
    private static int games_per_test = 100;
    private static double[] win_rate = new double[test_rounds];
    private static double[] total_rewards = new double[test_rounds*games_per_test];
    private static int[] win_loss_table = new int[test_rounds*games_per_test];
    private static double[] avg_error = new double[test_rounds];
    private double accum_error = 0.0;

    private static writeCSV writing = new writeCSV(test_rounds);
    //private boolean new_battle = true;



    @Override
    public void run() {
        super.run();
        // free radar and gun from other components
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);
        /*if (new_battle){
            nn.initializeWeights();
            replay_memory.
        }*/
        while (true) {
            if (scanned_enemy){
                // choose the action based on random rate
                //cur_action = lut.choose_action(random_rate, cur_state);
                cur_action = choose_action(cur_state);
                // Perform Action
                switch (cur_action){
                    // fire at will
                    case fire: {
                        turnGunRight(normalize_bearing(getHeading() - getGunHeading() + enemy_bearing));
                        fire(3);
                        break;
                    }
                    // go forward
                    case forward: {
                        setAhead(100);
                        execute();
                        break;
                    }
                    // go back
                    case backward: {
                        setBack(100);
                        execute();
                        break;
                    }
                    // forward in spin pattern
                    case spin_forward: {
                        setTurnRight(base_degree / 12);
                        setAhead(50);
                        execute();
                        break;
                    }
                    // save energy and spin backward
                    case spin_backward: {
                        setTurnRight(base_degree / 12);
                        setBack(80);
                        execute();
                        break;
                    }
                }
                // back update using reward
                update_value(reward);
                scanned_enemy = false;
            }
            else {

                turnRadarLeft(base_degree/4);
            }
            total_rewards[getRoundNum()] += reward;
            reset_reward();
        }
    }

    // normalize the gun angle
    double normalize_bearing(double degree) {
        while (degree >  base_degree/2) {
            degree -= base_degree;
        }
        while (degree < -base_degree/2) {
            degree += base_degree;
        }
        return degree;
    }

    // Events handlers
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        super.onScannedRobot(e);
        scanned_enemy = true;
        enemy_bearing = e.getBearing(); //the angle
        cal_state(e);
        //fire(1);
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        if (!terminal_only) reward += hit_bonus;
    }

    @Override
    public void onBulletMissed(BulletMissedEvent event){
        if (!terminal_only) reward += miss_hit_bonus;
    }
    @Override
    public void onDeath(DeathEvent event) {
        update_value(lose_Reward);
        total_rewards[getRoundNum()] += reward;
        reset_reward();
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        if (!terminal_only) reward += get_hit_bonus;
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        if (!terminal_only) reward += hit_tank_bonus;
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        if (!terminal_only) reward += hit_wall_bonus;
    }

    @Override
    public void onWin(WinEvent event) {
        update_value(win_Reward);
        win_rate[getRoundNum()/games_per_test]++;
        total_rewards[getRoundNum()] += reward;
        reset_reward();

    }

    @Override
    public void onRoundEnded(RoundEndedEvent event){
        // decrease the learning rate and random rate by 2 for each period ( 1/5 rounds of tests)
        if (getRoundNum()% 1500 == 0 && getRoundNum() != 0){
            learning_rate = learning_rate / 2;
            nn_lr = nn_lr/2;
            random_rate = random_rate / 2;
            System.out.println("rate change: " + Double.toString(learning_rate) + " " + Double.toString(random_rate));
        }
    }

    @Override
    public void onBattleEnded(BattleEndedEvent e){
        // calculate win_rate over certain period
        for (int i = 0; i < test_rounds; i++){
            win_rate[i] = win_rate[i]/games_per_test;
        }
        // save win_rate to csv
        System.out.println(Arrays.toString(win_rate));
        System.out.println(Double.toString(random_rate));
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new java.util.Date());
        File newfile = getDataFile("win_rate_onpolicy_"+ String.valueOf(on_off_policy) +"_terminalOnly_" +
                String.valueOf(terminal_only) + "_e_" +String.valueOf(initial_random_rate)+"_" + timeStamp+ ".csv");
        File total_rewards_w = getDataFile("total_rewards.csv");
        writing.writeToFile(newfile, win_rate);
        writing.writeToFile(total_rewards_w, total_rewards);
        System.out.println("files saved to " + newfile.getAbsolutePath());

        //to do: save weights
    }

    // calculate the new state when scanned the enemy
    private void cal_state(ScannedRobotEvent e){
        double my_x_position = getX();
        double my_y_position = getY();
        double my_energy_points = getEnergy();
        double enemy_energy_points = e.getEnergy();
        double distance_me_enemy = e.getDistance();
        double distance_center = get_center_distance(my_x_position, my_y_position);

        cur_state.setStateValue(distance_me_enemy, distance_center, my_energy_points, enemy_energy_points, activation_func);


        pre_state.copyCurState(cur_state);
        pre_action = cur_action;

        cur_state.setMy_energy(get_energy_level(my_energy_points));
        cur_state.setDistance_to_enemy(get_distance_level(distance_me_enemy));
        cur_state.setDistance_to_center(get_center_distance_level(my_x_position, my_y_position));
        cur_state.setEnemy_energy(get_energy_level(enemy_energy_points));
    }

    private void reset_reward(){
        reward = 0.0;
    }

    // cut the energy into 3 levels
    public int get_energy_level(double energy){
        return (int)Math.floor(energy/34);
    }

    // cut the distance into three levels
    public int get_distance_level(double distance){ return (int)Math.floor(distance/301);}

    // cut the distance to center into three levels
    public int get_center_distance_level(double my_x_position, double my_y_position){
        double center_x = getBattleFieldWidth()/2;
        double center_y = getBattleFieldHeight()/2;
        double distance = Math.pow((my_x_position - center_x), 2) + Math.pow((my_y_position - center_y), 2);
        // x^2 + y^2 < a as a circle
        double center_low = Math.pow(center_distance_threshold, 2);
        double center_med = Math.pow(2*center_distance_threshold, 2);
        if (distance <= center_low){
            return 0;
        }
        else if (distance <= center_med){
            return 1;
        }
        else return 2;
    }

    public double get_center_distance(double my_x_position, double my_y_position){
        double center_x = getBattleFieldWidth()/2;
        double center_y = getBattleFieldHeight()/2;
        return Math.pow((my_x_position - center_x), 2) + Math.pow((my_y_position - center_y), 2);
    }

    // Train NN using replay memory
    public void train_nn(){
        Object[] dataset = replay_memory.sample(dataset_size);
        if (dataset.length == 0) return;
        double[][] inputs = new double[dataset_size][State.state_length+1]; // datasize x 6, 6 is state+action
        double[][] labels = new double[dataset_size][1];
        for (int i = 0; i < dataset.length; i++){
            StateActionInput data = (StateActionInput) dataset[i];
            for (int j = 0; j < State.state_length; j++){
                inputs[i][j] = data.state_values[j];
            }
            inputs[i][State.state_length] = data.act_value;
            double[] new_label = {data.q_value};
            labels[i] = new_label;
        }
        nn.train(inputs, labels, dataset_size);
    }

    // calculate the q value and choose the corresponding formula
    public void update_value(double reward){
        double cur_value = 0.0;
        double local_learning_rate = learning_rate;
        if (on_off_policy){ // if on policy
            cur_value = nn.infer(cur_state.getStateValue(), Action.get_bipolar_value(cur_action));
            double on_value = pre_value + learning_rate * (reward + gamma * cur_value - pre_value);
            replay_memory.add(new StateActionInput(pre_state, pre_action, on_value));
        } // else off policy
        else {
            int max_actindex = get_max_action(cur_state);
            cur_value = nn.infer(cur_state.getStateValue(), Action.get_bipolar_value(Action.get_action(max_actindex)));
            double off_value = pre_value + learning_rate * (reward + gamma * cur_value - pre_value);
            replay_memory.add(new StateActionInput(pre_state, pre_action, off_value));
        }
        pre_value = cur_value;
        train_nn();
    }

    /*private int get_max_index_fromvalues(double[] values){
        int action_index = 0;
        double max_value = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < values.length; i++){
            if (values[i] > max_value){
                max_value = values[i];
                action_index = i;
            }
        }
        return action_index;
    }*/

    private Action choose_action(State state){
        double random_number = Math.random();
        Action new_action;
        // if it is less than the random_rate, then explotrary walk. //test
        if (random_number <= random_rate){
            Random rand = new Random();
            new_action = Action.get_action(rand.nextInt(Action.action_length));
        }
        else new_action = Action.get_action(get_max_action(state));
        return new_action;
    }

    public int get_max_action(State state){
        int action_index = 0;
        double max_value = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < Action.action_length; i++){
            double new_value = nn.infer(state.getStateValue(), Action.get_bipolar_value(Action.get_action(i)));
            if (new_value > max_value){
                max_value = new_value;
                action_index = i;
            }
        }

        return action_index;
    }
}

