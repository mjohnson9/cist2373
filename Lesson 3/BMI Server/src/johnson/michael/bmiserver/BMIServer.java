package johnson.michael.bmiserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BMIServer extends Thread {

  private final ServerSocket serverSocket;
  private final BMIServerApplication application;

  public BMIServer(final BMIServerApplication app, final int port) throws IOException {
    super();

    this.serverSocket = new ServerSocket(port);
    this.application = app;
  }

  @Override
  public void run() {
    final ThreadGroup socketThreads = new ThreadGroup("Socket threads");

    this.application.safeWriteMessage(
        "Waiting for connection from clients on port " + this.serverSocket.getLocalPort() + "\n");
    while (true) {
      try {
        final Socket newSocket = this.serverSocket.accept();
        final BMIResponder responder = new BMIResponder(this.application, newSocket);

        // Start a new responder thread
        new Thread(socketThreads, responder).start();
      } catch (IOException ex) {
        // An unknown IO exception has occurred; exit gracefully and record the error
        break;
      }
    }

    // Interrupt all of the socket threads so that we clean up as we're stopping execution of this
    // thread
    socketThreads.interrupt();
  }

  public void close() {
    if (this.serverSocket == null) {
      return;
    }

    try {
      this.serverSocket.close();
    } catch (IOException ex) {
      // Ignore close failures
    }
  }
}
