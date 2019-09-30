package johnson.michael.cist2373.midterm;

import javafx.application.Application;
import javafx.stage.Stage;

public class NorthwindApplication extends Application {

  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Northwind");

    NorthwindScene testScene = new NorthwindScene();

    primaryStage.setScene(testScene);

    primaryStage.show();
  }
}
