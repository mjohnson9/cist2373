package johnson.michael.bmiserver;

// Body Mass Index ( BMI )
// uses height and weight to calculate an index
// that determines if you are under/over weight or obese

public class BMI {
  private String name;
  private int age;
  private double weight; // in pounds
  private double height; // in inches
  public static final double KILOGRAMS_PER_POUND = 0.45359237;
  public static final double METERS_PER_INCH = 0.0254;

  public BMI(String name, int age, double weight, double height) {
    this.name = name;
    this.age = age >= 21 ? age : 21; // not valid for children or teens
    this.weight = weight;
    this.height = height > 60 ? height : 60; // must be 5 ft (60 inches) tall
  }

  public BMI(String name, double weight, double height) {
    this(name, 21, weight, height);
  }

  public double getBMI() {
    double bmi =
        weight * KILOGRAMS_PER_POUND / ((height * METERS_PER_INCH) * (height * METERS_PER_INCH));
    return Math.round(bmi * 100) / 100.0;
  }

  public String getStatus() {
    double bmi = getBMI();
    if (bmi < 18.5) return "Underweight";
    else if (bmi < 25) return "Normal";
    else if (bmi < 30) return "Overweight";
    else return "Obese";
  }

  public String getName() {
    return name;
  }

  public int getAge() {
    return age;
  }

  public double getWeight() {
    return weight;
  }

  public double getHeight() {
    return height;
  }
}
