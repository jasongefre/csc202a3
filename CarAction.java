package assignment3.Runners;

import assignment3.Bases.Car;
import assignment3.Bases.Queue;
import assignment3.Bases.Sim;

/**
 * Created by JMG on 10/11/2015.
 */
public class CarAction implements Runnable{

    public Queue[] lane;
    public Queue[] departing;
    public Queue[] mid;
    int signal;
    Sim sim;
    public CarAction(Queue[] lane, Queue[] mid, Queue[] departing,int signal, Sim sim){
        this.lane = lane;
        this.mid = mid;
        this.departing = departing;
        this.signal = signal;
        this.sim = sim;
    }

    /**
     * synchronized method to update cars' coordinates.
     */
    @Override
    @SuppressWarnings("unchecked")
    public synchronized void run() {
        Queue<Car>[] q = null;
        Car car;
        for (int h = 0; h < 4; h++) {
            for (int i = 0; i < 3; i++) {
                if (i == 0) q = departing;
                if (i == 1) q = mid;
                if (i == 2) q = lane;
                int inq = q[h].inQueue();
                int t = 0;
                for (int ii = 0; ii < inq; ii++) {
                    car = q[h].getElement(t);
                    evalLocation(car, h, i);
                    if (q[h].getElement(t) != null) {
                        if (car.equals(q[h].getElement(t))) {
                            t++;
                        }
                    }
                }
                for (int ii = 0; ii < q[h].inQueue(); ii++) {
                    car = q[h].getElement(ii);
                    if (i == 2) {
                        car.setTraffic((ii * 60) + 50);
                    }
                    move(car, h, i);
                }
            }
        }
    }

    /**
     * method evaluated the current location of the car and determines if transfers are needed, or removal.
     */
    public synchronized void evalLocation(Car car, int direction, int pathID) {
        QueueAction QA = null;
        if (car != null) {
            int destination = car.getDestination();
            if (car.atTransfer()) {
                car.setTraffic(0);
                switch (pathID) {
                    case 2:
                        if ((direction + 1) % 4 == destination) {
                            move(car, (direction + 1) % 4,1);
                            QA = new QueueAction("transfer", lane, departing, direction, (direction + 1) % 4);

                        } else {
                            move(car, direction,1);
                            QA = new QueueAction("transfer", lane, mid, direction, direction);

                        }
                        break;
                    case 1:
                        if (direction == destination) {
                            move(car, direction,pathID);
                            QA = new QueueAction("transfer", mid, departing, direction, direction);

                        } else if ((direction + 1) % 4 == destination) {
                            //THIS SHOULDN'T HAPPEN
                            System.out.println("ERROR2");

                        } else if ((direction + 2) % 4 == destination) {
                            move(car, (direction + 3) % 4,pathID);
                            QA = new QueueAction("transfer", mid, mid, direction, (direction + 3) % 4);

                        } else if ((direction + 3) % 4 == destination) {
                            move(car, (direction + 3) % 4,pathID);
                            QA = new QueueAction("transfer", mid, mid, direction, (direction + 3) % 4);
                        } else {
                            //THIS SHOULDN'T HAPPEN
                            System.out.println("ERROR3");
                        }
                        break;
                    case 0:
                        //THIS SHOULDN'T HAPPEN
                        System.out.println("ERROR1");
                        break;
                }
            }
            else if (car.oob()) {
                QA = new QueueAction("dequeue", departing, null, direction, 0);
            }
            if (QA != null) {
                Thread QAT = new Thread(QA);
                QAT.start();
                try {
                    QAT.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //check x,y based on those in lane, set traffic= 50 per vehicle
        }
    }

    /**
     * moves the vehicle, and spaces based on traffic buffer
     */
    public synchronized void move(Car car, int direction, int pathID){
        if (car != null) {
            int x = car.getX();
            int y = car.getY();
            //move
            if (pathID == 2) {
                switch (direction) {
                    case 0:
                        if ((x < (350 - car.getTraffic()) || x > 300) || signal == direction) {
                            car.setX(x + 1);
                        }
                        break;
                    case 1:
                        if ((y < (250 - car.getTraffic()) || y > 200) || signal == direction) {
                            car.setY(y + 1);
                        }
                        break;
                    case 2:
                        if ((x > (450 + car.getTraffic()) || x < 500) || signal == direction) {
                            car.setX(x - 1);
                        }
                        break;
                    case 3:
                        if ((y > (350 + car.getTraffic()) || y < 400) || signal == direction) {
                            car.setY(y - 1);
                        }
                        break;
                }
            }
            else {
                switch (direction) {
                    case 0:
                            car.setX(x + 1);
                        break;
                    case 1:
                            car.setY(y + 1);
                        break;
                    case 2:
                            car.setX(x - 1);
                        break;
                    case 3:
                            car.setY(y - 1);
                        break;
                }
            }
        }
    }
}
