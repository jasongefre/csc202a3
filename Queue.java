package assignment3.Bases;

import assignment3.CustomException;

/**
 * Created by JMG on 10/10/2015.
 */
public class Queue<T> {
    private T[] queue;
    private int front = 0;
    private int rear = -1;
    private int count = 0;

    /**
     * Default constructor to initialize the queue array
     */
    @SuppressWarnings("unchecked")
    public  Queue()
    {
        int cap = 10;
        queue = (T[]) new Object[cap];
    }
    /**
     * Enlarges the queue array by one.
     */
    @SuppressWarnings("unchecked")
    private synchronized void enlarge()
    {
        T[] NEW = (T[]) new  Object[queue.length + 1];
        int j = front;
        for (int i = 0; i < count; i++)
        {
            NEW[i] = queue[j];
            j = (front + i + 1) % queue.length;
        }
        queue = NEW;
        front = 0;
        rear = count-1;
    }
    /**
     * @param data Adds data(T) to end of queue.
     */
    public synchronized void enqueue (T data)
    {
        if (isFull())
        {
            try {
            enlarge();
            if (isFull())
            {
            throw new CustomException("Cannot add to full Queue.");
            }
            }
            catch (CustomException e)
            {
                e.printStackTrace();
            }
        }
        queue[(rear + 1) % queue.length] = data;
        rear = (rear + 1) % queue.length;
        count++;
    }

    /**
     * @return T at front of queue.
     */
    public synchronized T dequeue()
    {
        if (!isEmpty())
        {
            T remove = queue[front];
            queue[front] = null;
            front = (front + 1) % queue.length;
            count--;
            return remove;
        }
        else
        {
            try {
            throw new CustomException("Cannot remove from empty Queue");
            }
            catch (CustomException e)
            {
            e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * @return empty queue.
     */
    public synchronized boolean isEmpty()
    {
        return (count==0);
    }
    /**
     * @return full queue.
     */
    public synchronized boolean isFull()
    {
        return (count==queue.length);
    }

    /**
     * @return count
     */
    public synchronized int inQueue()
    {
        return count;
    }
    public synchronized T getElement(int index){
        if (index > count)
        {
            return null;
        }
        else
            return queue[(index + front)%queue.length];
    }
}


