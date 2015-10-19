# package assignment3.Bases;

import assignment3.Runners.CarAction;
import assignment3.Runners.QueueAction;

/**
 * Created by JMG on 10/11/2015.
 */
public class Sim {
    static Sim sim;
    public Queue[] lane;
    public Queue[] departing;
    public Queue[] mid;
    Runnable QA;
    Runnable CA;
    Runnable R;
    Thread QAT;
    Thread CAT;
    Thread RT;
    public int created = 0;
    public static void main(String[] args){
        sim= new Sim();
    }
    /**
     * Creates the first set of cars, initiate the renderer.
     */
    @SuppressWarnings("unchecked")
    public Sim() {
        lane = new Queue[4];
        departing  = new Queue[4];
        mid = new Queue[4];
        for (int  i =0; i < 4; i++) {
            lane[i] = new Queue<Car>();
            departing[i] = new Queue<Car>();
            mid[i] = new Queue<Car>();
            enqueue(i);
        }
        R = new Screen(this);
        RT = new Thread(R);
        RT.start();
    }
    /**
     * Update method to update each car - to include determining whether to perform other operations.
     */
    @SuppressWarnings("unchecked")
    public synchronized void update(int signal){
        CA = new CarAction(lane,mid,departing,signal, this);
        CAT = new Thread(CA);
        CAT.start();
        try {
            CAT.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    /**
     * Method to add a car.
     */
    @SuppressWarnings("unchecked")
    public synchronized void enqueue(int direction){
        QA = new QueueAction("enqueue",lane,null,direction, direction);
        QAT = new Thread(QA);
        QAT.start();
        try {
            QAT.join();
            created++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
