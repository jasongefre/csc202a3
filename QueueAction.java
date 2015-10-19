package assignment3.Runners;

import assignment3.Bases.Car;
import assignment3.Bases.Queue;

import java.util.Random;

/**
 * Created by JMG on 10/11/2015.
 */
public class QueueAction implements Runnable{
    String action;
    Queue[] lane;
    Queue[] transfer;
    int direction1;
    int direction2;
    int start[][] = new int[2][4];
    Random random = new Random();

    public QueueAction(String action, Queue[] lane,Queue[] transfer,int direction1, int direction2){
        this.action = action;
        this.lane = lane;
        this.transfer = transfer;
        this.direction1 = direction1;
        this.direction2 = direction2;

        //X values
        start[0][0] = -99;
        start[0][1] = 350;
        start[0][2] = 899;
        start[0][3] = 450;
        //Y values
        start[1][0] = 350;
        start[1][1] = -99;
        start[1][2] = 250;
        start[1][3] = 699;
    }

    /**
     * Synchronized method to enqueue, dequeue, or transfer.
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized void run() {
        if (action.contains("enqueue")) {
            lane[direction1].enqueue(new Car(start[0][direction1],start[1][direction1],random.nextInt(4) % 4));
        }
        else if (action.contains("dequeue")) {
            lane[direction1].dequeue();
        }
        else if (action.contains("transfer")) {
            transfer[direction2].enqueue(lane[direction1].dequeue());
        }
    }
}
