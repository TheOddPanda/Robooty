package obstacle;

import obstacle.task.DriverTask;
import obstacle.task.TouchTask;
import obstacle.task.UltraSoundTask;

import static obstacle.util.ThreadUtil.interruptIfAlive;
import static obstacle.util.ThreadUtil.startThreads;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread(new UltraSoundTask());
        Thread thread2 = new Thread(new TouchTask());
        Thread thread3 = new Thread(new DriverTask());

        startThreads(thread1, thread2, thread3);

        thread2.join();

        interruptIfAlive(thread1);
        interruptIfAlive(thread3);
    }

}
