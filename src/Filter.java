import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Filter implements Lock, LockType {
  volatile int[] level;
  volatile int[] victim;


  public Filter(int numThreads) {
    this.level = new int[numThreads];
    this.victim = new int[numThreads]; // use 1...n-1

    for (int i = 0; i < numThreads; i++) {
      level[i] = 0;
    }
  }

  @Override
  public void lock() {
    int me = ThreadID.get();
    for (int i = 1; i < level.length; i++) { //attempt level 1
      level[me] = i;
      victim[i] = me;

      for (int k = 0; k < level.length; k++) {
        while ((k != me) && (level[k] >= i && victim[i] == me)) {
          //wait
        }
      }
    }
  }

  @Override
  public void lockInterruptibly() throws InterruptedException {

  }

  @Override
  public boolean tryLock() {
    return false;
  }

  @Override
  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
    return false;
  }

  @Override
  public void unlock() {
    int me = ThreadID.get();
    level[me] = 0;
  }

  @Override
  public Condition newCondition() {
    return null;
  }

}
