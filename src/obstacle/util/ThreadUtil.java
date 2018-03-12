package obstacle.util;

public class ThreadUtil {
    public static void interruptIfAlive(Thread thread) {
        if (thread.isAlive()) {
            thread.interrupt();
        }
    }

    public static void startThreads(Thread... threads) {
        for (Thread thread : threads) {
            thread.start();
        }
    }
}
