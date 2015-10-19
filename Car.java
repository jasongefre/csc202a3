package assignment3.Bases;

/**
 * Created by JMG on 10/10/2015.
 */
public class Car {
    int x, y;
    int destination;
    int traffic=0;

    public Car(int x, int y, int destination){
        this.x = x;
        this.y = y;
        this.destination = destination;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDestination() {
        return destination;
    }

    public boolean atTransfer(){
        return ((x==350 && y==250) || (x==350 && y==350) || (x==450 && y==250) || (x==450 && y==350));
    }

    public boolean oob(){
        return (x>900 || x<-100 || y>700 || y<-100);
    }

    public int getTraffic() {
        return traffic;
    }

    public void setTraffic(int traffic) {
        this.traffic = traffic;
    }

}

