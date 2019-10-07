package johnson.michael.addrressbook;

public class Address {
  private String name;
  private String street;
  private String city;
  private String state;
  private String zip;

  /** Creates a new Address object with all fields initialized to empty strings. */
  public Address() {
    this.name = "";
    this.street = "";
    this.city = "";
    this.state = "";
    this.zip = "";
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = this.cleanString(name, AddressFile.NAME_LEN);
  }

  public String getStreet() {
    return this.street;
  }

  public void setStreet(final String street) {
    this.street = this.cleanString(street, AddressFile.STREET_LEN);
  }

  public String getCity() {
    return this.city;
  }

  public void setCity(final String city) {
    this.city = this.cleanString(city, AddressFile.CITY_LEN);
  }

  public String getState() {
    return this.state;
  }

  public void setState(final String state) {
    this.state = this.cleanString(state, AddressFile.STATE_LEN);
  }

  public String getZip() {
    return this.zip;
  }

  public void setZip(final String zip) {
    this.zip = this.cleanString(zip, AddressFile.ZIP_LEN);
  }

  private String cleanString(final String str, final int maxLength) {
    String cleanedString = str.trim(); // Trim whitespace
    if (cleanedString.length() > maxLength) {
      // If the given string is longer than maxLength, cut it back to maxLength
      cleanedString = cleanedString.substring(0, maxLength);
    }

    return cleanedString;
  }
}
