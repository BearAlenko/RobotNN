import robocode.*;

/**
 * Created by 37919 on 2023/11/1.
 */
public class myRobot extends AdvancedRobot {
    public final static double base_degree = 360;
    private State state;
    private LookUpTable lut;
    private Action action;
    private boolean scanned_enemy;
    private double random_rate = 0.3;
    private double win_Reward = Double.POSITIVE_INFINITY;
    private double lose_Reward =  Double.NEGATIVE_INFINITY;
    private double hit_bonus = 20;
    private double miss_bonus = -10;
    private double get_hit_bonus = -20;
    private double hit_tank_bonus = -10;
    private double hit_wall_bonus = -10;
    private double reward;
    private boolean on_off_policy;

    public myRobot(){
        state = new State();
        lut = new LookUpTable();
        action = Action.idle;
        scanned_enemy = false;
        reward = 0;
        on_off_policy = false;
    }

    @Override
    public void run() {

        while (true) {
            if (scanned_enemy){
                action = lut.choose_action(random_rate, state);
                lut.update_value(reward, on_off_policy);
                scanned_enemy = false;

                // Perform Action
                switch (action){
                    case fire:
                        // TO DO;
                        fire(30);
                        break;
                    case forward:
                        ahead(20);
                        break;
                    case backward:
                        back(20);
                        break;
                    case left:
                        turnLeft(base_degree/12);
                        ahead(20);
                        break;
                    case right:
                        turnRight(base_degree/12);
                        ahead(20);
                        break;
                    case idle:
                        break;
                    default:
                        break;
                }
            }
            else {
                reset_reward();
                turnRadarLeft(base_degree/4);
            }
        }
    }
    @Override
    public void onScannedRobot(ScannedRobotEvent e) {
        scanned_enemy = true;
        state = cal_state();
        //fire(1);
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        reward += hit_bonus;
    }

    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        reward += miss_bonus;
    }

    @Override
    public void onDeath(DeathEvent event) {
        lut.update_value(lose_Reward, on_off_policy);
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        reward += get_hit_bonus;
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        reward += hit_tank_bonus;
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        reward += hit_wall_bonus;
    }

    @Override
    public void onWin(WinEvent event) {
        lut.update_value(win_Reward, on_off_policy);
    }

    private State cal_state(){
        //To Do
        return new State();
    }

    private void reset_reward(){
        reward = 0.0;
    }
}
