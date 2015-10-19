package assignment3.Bases;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.Random;

/**
 * Created by JMG on 10/11/2015.
 */
public class Screen extends Canvas implements Runnable{

    public static final double INTERVAL = 1000000000.0 / 60.0;

    public static final int WIDTH = 800; //400 CENTER
    public static final int HEIGHT = 600; //300 CENTER

    public static final int CARWIDTH = 10;
    public static final int CARHEIGHT = 10;
    public static final int LINEWIDTH = 2;
    //LINES FOR LANES
    public static int line[][][] = new int[2][3][4];
    public static int oldSignal;
    public static int signal;
    public static int signaltimer;
    boolean running;
    Sim sim;
    int spawnmod;
    //private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
    private JFrame frame;
    private Queue<Car>[] queue;
    private Queue<Car>[] mid;
    private Queue<Car>[] departing;
    private Random random = new Random();

    @SuppressWarnings("unchecked")
    public Screen(Sim sim){
        Dimension size = new Dimension (WIDTH, HEIGHT);
        setPreferredSize (size);

        running = true;
        this.sim = sim;
        departing = sim.departing;
        this.queue = sim.lane;
        this.mid = sim.mid;

        frame = new JFrame();
        frame.setTitle ("Assignment 3 ||");
        frame.setResizable (false);
        frame.add(this);
        frame.pack ();
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo (null);
        frame.setVisible (true);

    }
    @Override
    @SuppressWarnings("unchecked")
    /**
     * loop through the render and updater
     */
    public synchronized void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        int frames = 0;
        int updates = 0;

        signal = 0;
        signaltimer = 6;
        requestFocus();
        spawnmod = random.nextInt(3)+1;
        while(running) {
            long now = System.nanoTime ();
            delta += (now - lastTime) / INTERVAL;
            lastTime = now;
            while (delta >= 1) {
                sim.update(signal);
                updates++;
                delta = 0;
                render();
                frames++;
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frame.setTitle("Assignment 3 || " + frames + " fps ||  " + updates + " updates");
                updates = 0;
                frames = 0;
                signaltimer--;

                //spawn new car
                if (signaltimer % spawnmod == 0) sim.enqueue(random.nextInt(4));

                //signal transition delay
                if (signaltimer<1 && signal<4) {
                    oldSignal = signal;
                    signal = 4;
                    signaltimer = 3;
                }
                //signal chooser
                if (signaltimer<1 && signal==4) {
                    int greatest = 0;
                    int lane = 0;
                    for (int i = 0; i < queue.length; i++) {
                        if (queue[i].inQueue() > greatest) {
                            greatest = queue[i].inQueue();
                            lane = i;
                        }
                    }
                    spawnmod = random.nextInt(2)+3;
                    signal = lane;
                    signaltimer = random.nextInt(4) + 3;
                }
            }
        }
    }

    /**
     * render the window
     */
    @SuppressWarnings("unchecked")
    public synchronized void render(){
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        //SET BACKGROUND
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        //SET STREET LINES
        g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 325, 800, 50);
            g.fillRect(0, 225, 800, 50);
            g.fillRect(425, 0, 50, 600);
            g.fillRect(325, 0, 50, 600);
            g.fillRect(375,275,50,50);
        g.setColor(Color.BLACK);
            g.drawRect(325,225,150,150);

        //TRAFFIC LIGHTS
        g.setColor( (signal==0) ? Color.GREEN : Color.RED);
        g.fillOval(305,305,20,20);
        g.setColor( (signal==1) ? Color.GREEN : Color.RED);
        g.fillOval(375,205,20,20);
        g.setColor( (signal==2) ? Color.GREEN : Color.RED);
        g.fillOval(475,275,20,20);
        g.setColor( (signal==3) ? Color.GREEN : Color.RED);
        g.fillOval(405,375,20,20);

        g.setColor(Color.BLACK);
        //PRINT CURRENT OUTPUTS
        g.drawString("Signal timer: " + signaltimer,29,20);
        g.drawString("Signal: " + signal, 60,35);
        g.drawString("spawnmod: " + spawnmod,33,50);

        g.drawString("Lane", 140, 50);
        g.drawString("Incoming:", 50, 80);
        g.drawString("Transiting:", 50, 110);
        g.drawString("Outgoing:", 50, 140);

        //display cars in lanes
        Queue<Car>[] q = null;
        for (int i = 0; i < 3; i++) {
            if (i==0) q = queue;
                else if (i==1) q = mid;
                else if (i==2) q = departing;
            int iq;
            for (int ii = 0; ii < 4; ii++) {
                iq = q[ii].inQueue();
                for (int iii = 0; iii < iq; iii++) {
                    Car car = q[ii].getElement(iii);
                    if (car == null) continue;
                    int x = car.getX();
                    int y = car.getY();
                    int des = car.getDestination();
                    //color the cars based on direction of movement
                    if (ii == 0) g.setColor(Color.BLUE);
                        else if (ii == 1) g.setColor(Color.CYAN);
                        else if (ii == 2) g.setColor(Color.GREEN);
                        else if (ii == 3) g.setColor(Color.MAGENTA);
                    //display car
                    g.fillRect(x - CARWIDTH, y - CARHEIGHT, 2 * CARWIDTH, 2 * CARHEIGHT);

                    //color the output based on queue location
                    if (i==0) g.setColor(Color.BLACK);
                        else if (i==1) g.setColor(Color.RED);
                        else if (i==2) g.setColor(Color.YELLOW);
                    //display current lane, intended final lane
                    g.drawString(ii + " : " + des, x - (CARWIDTH+1), y + (CARHEIGHT / 2));
                    //display x, y values
                    g.drawString(x + ":" + y, x - (2 * CARWIDTH)-3, y - CARHEIGHT);
                    //display traffic spacing
                    if (i==0) g.drawString("" + car.getTraffic(), x - 7, y + (2 * CARHEIGHT));
                }
                //draw output table data
                g.setColor(Color.BLACK);
                if (i==0) g.drawString("" + iq, (ii * 20) + 120,80);
                    else if (i==1) g.drawString("" + iq,(ii * 20) + 120,110);
                    else if (i==2) g.drawString("" + iq,(ii * 20) + 120,140);
            }
        }

        g.dispose();
        bs.show();
    }
}
