import task.DriverTask;
import task.TouchTask;
import task.UltraSoundTask;

import static util.ThreadUtil.interruptIfAlive;
import static util.ThreadUtil.startThreads;

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
