package johnson.michael.bmiserver;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BMIResponder implements Runnable {

  private final Socket socket;
  private final BMIServerApplication application;

  public BMIResponder(final BMIServerApplication app, final Socket socket) {
    this.socket = socket;
    this.application = app;
  }

  @Override
  public void run() {
    final String otherEndAddress = this.getOtherEndAddress();
    this.application.safeWriteMessage("[" + otherEndAddress + "] New connection\n");

    final ObjectInputStream input;
    final ObjectOutputStream output;

    try {
      // We have to start the output stream first or the connection will deadlock
      output = new ObjectOutputStream(socket.getOutputStream());
      input = new ObjectInputStream(socket.getInputStream());
    } catch (final IOException ex) {
      String exceptionMessage = ex.getLocalizedMessage();
      if (exceptionMessage == null || exceptionMessage.length() <= 0) {
        exceptionMessage = ex.getMessage();
      }

      this.application.safeWriteMessage(
          "["
              + otherEndAddress
              + "] Exception occurred establishing input and output streams: "
              + exceptionMessage
              + "\n");

      try {
        // Make sure the socket is closed
        this.socket.close();
      } catch (final IOException ex1) {
        // Ignore close failure
      }

      return;
    }

    while (!Thread.interrupted()) {
      // Answer requests until the other end hangs up or our thread is interrupted

      // Read the height and weight from the client
      final double height;
      final double weight;
      try {
        height = input.readDouble();
        weight = input.readDouble();
      } catch (final EOFException ex) {
        try {
          // Make sure the socket is closed
          this.socket.close();
        } catch (final IOException ex2) {
          // Ignore close failure
        }

        // Break out of the infinite loop
        break;
      } catch (final IOException ex) {
        // An IO exception occurred; inform the user and then try to close the socket and end the
        // thread gracefully
        String exceptionMessage = ex.getLocalizedMessage();
        if (exceptionMessage == null || exceptionMessage.length() <= 0) {
          exceptionMessage = ex.getMessage();
        }

        this.application.safeWriteMessage(
            "["
                + otherEndAddress
                + "] Exception occurred while reading input height and weight: "
                + exceptionMessage
                + "\n");

        try {
          // Make sure the socket is closed
          this.socket.close();
        } catch (final IOException ex2) {
          // Ignore close failure
        }

        break;
      }

      // Create an instance of BMI to calculate the BMI
      final BMI bmi = new BMI("Patient", weight, height);

      try {
        // Attempt to send back the BMI and status
        output.writeDouble(bmi.getBMI());
        output.writeObject(bmi.getStatus());
      } catch (IOException ex) {
        // An IO exception occurred; inform the user and then try to close the socket and end the
        // thread gracefully
        String exceptionMessage = ex.getLocalizedMessage();
        if (exceptionMessage == null || exceptionMessage.length() <= 0) {
          exceptionMessage = ex.getMessage();
        }

        this.application.safeWriteMessage(
            "["
                + otherEndAddress
                + "] Exception occurred while sending BMI and status: "
                + exceptionMessage
                + "\n");

        try {
          // Make sure the socket is closed
          this.socket.close();
        } catch (final IOException ex2) {
          // Ignore close failure
        }

        break;
      }

      this.application.safeWriteMessage(
          "["
              + otherEndAddress
              + "] Responded to BMI request. Weight: "
              + weight
              + " / Height: "
              + height
              + " / BMI: "
              + bmi.getBMI()
              + "\n");
    }
  }

  private String getOtherEndAddress() {
    return (this.socket.getInetAddress().getHostAddress()) + ":" + (this.socket.getPort());
  }
}
