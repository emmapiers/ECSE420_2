import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Bakery implements Lock, LockType {
  volatile boolean[] flag;
  volatile Label[] label;

  public Bakery (int numThreads) {
    flag = new boolean[numThreads];
    label = new Label[numThreads];
    for (int i = 0 ; i < label.length; i++) {
      flag[i] = false;
      label[i] = new Label();
    }

  }

  @Override
  public void lock() {
    int i = ThreadID.get();
    flag[i] = true;
    int max = Label.max(label);
    label[i] = new Label(max+1);


    while(conflict(i)) {
      //wait
    }
  }
  private boolean conflict(int me) {
    for (int i = 0; i < label.length; i++) {
      if (i != me && flag[i] && label[me].compareTo(label[i]) > 0 ) {
        return true;
      }
    }
    return false;
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
    flag[me] =false;
  }

  @Override
  public Condition newCondition() {
    return null;
  }

  static class Label implements Comparable<Label> {
    int counter;
    int id;
    Label() {
      counter = 0;
      id = ThreadID.get();
    }
    Label (int c) {
      counter = c;
      id = ThreadID.get();
    }

    static int max(Label[] labels) {
      int c = 0;
      for (Label label: labels) {
        c = Math.max(c, label.counter);
      }
      return c;
    }
    public int compareTo(Bakery.Label other) {
      if (this.counter < other.counter) {
        return -1;
      }
      else if (this.counter > other.counter
          || (this.counter == other.counter && this.id > other.id )) {
          return 1;
      }else {
        return 0;
      }
    }
  }

}
