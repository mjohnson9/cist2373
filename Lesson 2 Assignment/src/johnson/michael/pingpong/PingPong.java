package johnson.michael.pingpong;

/** PingPong is a Runnable class that prints Ping or Pong a given number of times. */
public class PingPong implements Runnable {
  // We don't have to worry about thread safety for these variables because they're set in the
  // constructor, before the Runnable can be run.

  /** The number of times to print. */
  private final int numTimesToPrint;
  /**
   * Whether or not to print Pong. If true, this PingPong prints Pong. Otherwise, it prints Ping.
   */
  private final boolean printPong;

  /**
   * Constructs a PingPong object which prints Ping or Pong numTimesToPrint times when run.
   *
   * @param printPong Whether or not to print Pong. If true, this PingPong prints Pong. Otherwise,
   *     this PingPong prints Ping.
   * @param numTimesToPrint The number of times to print Ping or Pong.
   */
  public PingPong(final boolean printPong, final int numTimesToPrint) {
    this.numTimesToPrint = numTimesToPrint;
    this.printPong = printPong;
  }

  @Override
  public void run() {
    // Loop the given number of times
    for (int i = 0; i < this.numTimesToPrint; i++) {
      // Lock System.out, as it isn't guaranteed to be thread safe.
      synchronized (System.out) {
        // Print Pong if printPong is true or Ping otherwise
        System.out.println(this.printPong ? "Pong" : "Ping");
      }

      if (Thread.interrupted()) {
        // This thread has been interrupted. Politely exit.
        break;
      }

      // Since we're locking System.out, yield so that we can get the non-deterministic output of
      // multiple threads running simultaneously.
      Thread.yield();
    }
  }
}
