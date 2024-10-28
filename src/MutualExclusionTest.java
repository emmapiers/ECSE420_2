
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MutualExclusionTest {
  private final static int NTHREADS = 8;
  private final static int PER_THREAD = 128;

  private final static int COUNT = NTHREADS * PER_THREAD;

  static int lockCounter = 0;

  static int[] counterArray = new int[COUNT];

  static int[] threadIDArray = new int[COUNT];

  private static LockType lock;

  public static class lockTestThread implements Runnable {

    @Override
    public void run() {
      for (int i = 0; i < PER_THREAD; i++) {
        lock.lock();
        try {
          lockCounter = lockCounter+1;
          counterArray[lockCounter-1] = lockCounter;
          threadIDArray[lockCounter-1] = ThreadID.get();
        }
        finally {
          lock.unlock();
        }
      }
    }
  }

  public static void test(LockType lockToTest) {
    lock = lockToTest;
    ThreadID.reset();
    ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);

    for (int i = 0; i < NTHREADS; i++) {
      executor.execute(new lockTestThread());
    }
    executor.shutdown();

    while (! executor.isTerminated()) {
    }

    System.out.flush();
    for (int i = 0; i < COUNT ; i++) {
      System.out.println("ID: " + threadIDArray[i] + " counter = " + counterArray[i]);
    }
    System.out.println("Final value of shared counter (should be: " + COUNT +
        ") = " + lockCounter);

  }

    public static void main(String[] args) {
      // Test with Filter Lock
      System.out.println("Testing Filter Lock...");
      test(new Filter(NTHREADS));

      // Reset for the next test
      lockCounter = 0;
      counterArray = new int[COUNT];
      threadIDArray = new int[COUNT];

      // Test with Bakery Lock
      System.out.println("Testing Bakery Lock...");
      test(new Bakery(NTHREADS));
    }


}
