package johnson.michael.bmiclient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BMIClient {
  private final Socket socket;
  private final ObjectInputStream input;
  private final ObjectOutputStream output;

  public BMIClient(final String address, final int port) throws IOException {
    super();

    this.socket = new Socket(address, port);

    // We have to start the output stream first or the connection will deadlock
    this.output = new ObjectOutputStream(this.socket.getOutputStream());
    this.input = new ObjectInputStream(this.socket.getInputStream());
  }

  public BMI getBMI(final double height, final double weight)
      throws IOException, ClassNotFoundException {
    // Write the two doubles and then wait for a response
    this.output.writeDouble(height);
    this.output.writeDouble(weight);
    this.output.flush(); // Ensure it gets sent immediately

    final double bmi = this.input.readDouble();
    final String status = (String) this.input.readObject();

    final BMI returnedBmi = new BMI(bmi, status);
    return returnedBmi;
  }
}
