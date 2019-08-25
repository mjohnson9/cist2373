package johnson.michael.pingpong;

/**
 * PingPongDriver drives a command line program. The program spawns two threads, which both print
 * Ping or Pong a predetermined number of times.
 */
public class PingPongDriver {
  public static void main(final String[] args) {
    // Create the ping and pong printers that will be run
    final Runnable ping = new PingPong(false, 10);
    final Runnable pong = new PingPong(true, 10);

    // Instantiate the threads with their respective names
    final Thread pingThread = new Thread(ping, "Ping");
    final Thread pongThread = new Thread(pong, "Pong");

    // Start both threads
    pingThread.start();
    pongThread.start();

    // Wait for the threads to end
    try {
      pingThread.join();
      pongThread.join();
    } catch (final InterruptedException ex) {
      // This block should never run, as nothing is going to call the interrupt() method against the
      // main thread.
      throw new AssertionError("The main thread should never be interrupted.", ex);
    }
  }
}
