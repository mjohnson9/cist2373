package johnson.michael.addrressbook;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class AddressFile extends RandomAccessFile {
  public static final int NAME_LEN = 32;
  public static final int STREET_LEN = 32;
  public static final int CITY_LEN = 20;
  public static final int STATE_LEN = 2;
  public static final int ZIP_LEN = 5;
  private static final int TOTAL_LEN = NAME_LEN + STREET_LEN + CITY_LEN + STATE_LEN + ZIP_LEN;

  private long currentIndex;

  /**
   * Opens a new AddressFile.
   * @param fileName The file path to the address database.
   * @throws FileNotFoundException When the file can not be opened because of permission issues,
   *     folders not existing, or other IO errors that prevent the file from being created.
   */
  public AddressFile(final String fileName) throws FileNotFoundException {
    super(fileName, "rw");

    // Set the current index to 0 to indicate that we're at the beginning of the file
    this.currentIndex = 0;
  }

  /**
   * Reads the next address in the file.
   * @return The read address.
   * @throws EOFException Whenever the end of the file is unexpectedly reached.
   */
  public Address readAddress() throws IOException {
    final Address address = new Address();

    address.setName(this.readFixedLengthString(NAME_LEN));
    address.setStreet(this.readFixedLengthString(STREET_LEN));
    address.setCity(this.readFixedLengthString(CITY_LEN));
    address.setState(this.readFixedLengthString(STATE_LEN));
    address.setZip(this.readFixedLengthString(ZIP_LEN));

    // Increase the current index by 1 to indicate that we've read through a record and are now at
    // the end of that record.
    this.currentIndex += 1;

    return address;
  }

  /**
   * Reads the Address at index.
   * @param index The index to read the address at.
   * @return The read Address.
   * @throws EOFException Whenever the end of the file is unexpectedly reached.
   */
  public Address readAddress(final long index) throws IOException {
    this.seekIndex(index);

    return this.readAddress();
  }

  /**
   * Writes the Address at the current index.
   * @param address The Address to write.
   */
  public void writeAddress(final Address address) throws IOException {
    this.writeFixedLengthString(address.getName(), NAME_LEN);
    this.writeFixedLengthString(address.getStreet(), STREET_LEN);
    this.writeFixedLengthString(address.getCity(), CITY_LEN);
    this.writeFixedLengthString(address.getState(), STATE_LEN);
    this.writeFixedLengthString(address.getZip(), ZIP_LEN);

    // Increase the current index by 1 to reflect that we've written a record and are now at the end
    // of that record
    this.currentIndex += 1;
  }

  /**
   * Writes the Address at the given index.
   * @param index The index to write the address at.
   * @param address The Address to be written.
   */
  public void writeAddress(final long index, final Address address) throws IOException {
    this.seekIndex(index);

    this.writeAddress(address);
  }

  /**
   * Seeks to the given index.
   * @param index The index to seek to.
   */
  public void seekIndex(final long index) throws IOException {
    // Calculate the character position by multiplying the index by the record length
    final long newCharPos = index * TOTAL_LEN;
    // Calculate what position to seek to by multiplying the character position by the number of
    // bytes in a character
    final long newBytePos = newCharPos * Character.BYTES;

    // Seek the file to the calculated byte position
    this.seek(newBytePos);

    // Set the current index appropriately
    this.currentIndex = index;
  }

  public long getCurrentIndex() {
    return this.currentIndex;
  }

  /**
   * Retrieves the number of addresses in the database.
   * @return The number of addresses in the database.
   */
  public long getNumAddresses() throws IOException {
    // length() returns bytes, so we have to divide by the number of bytes in a Character to find
    // the number of addresses
    return (this.length() / Character.BYTES) / TOTAL_LEN;
  }

  private String readFixedLengthString(final int length) throws IOException {
    // Create a StringBuilder to read the characters into.
    final StringBuilder buf = new StringBuilder();

    // Read each char and append it to the StringBuffer
    for (int i = 0; i < length; i++) {
      final char c = this.readChar();

      if (c == '\0') { // a null character indicates the end of the string
        // Skip over the rest of the null bytes so that the file offset is correct for the next read
        // We have to calculate the number of characters to skip first, and then multiply it by the
        // number of bytes per character
        this.skipFully((length - i - 1) * Character.BYTES);
        break;
      }

      buf.append(c);
    }

    return buf.toString();
  }

  private void writeFixedLengthString(final String str, final int length) throws IOException {
    // Convert str to a char array.
    final char[] strChars = str.toCharArray();

    // Calculate the writeLength as the lesser of length of strChars.length. This means that if
    // strChars is longer than length, we'll only write the first length chars.
    final int writeLength = Math.min(length, strChars.length);

    // Calculate the number of null characters to put at the end of the string as filler.
    final long nullChars = length - writeLength;

    // Write the chars from str to the file
    for (int i = 0; i < writeLength; i++) {
      this.writeChar(strChars[i]);
    }

    // Write null chars after the string as filler
    for (int i = 0; i < nullChars; i++) {
      this.writeChar('\0');
    }
  }

  private void skipFully(final int n) throws IOException {
    for (int skipped = 0; skipped < n;) {
      final int iterationSkipped = this.skipBytes(n - skipped);
      if (iterationSkipped <= 0) {
        // Technically, skipping 0 or fewer bytes doesn't always indicate the end of file,
        // particularly with buffered files. However, for simplicity, we're going to assume that
        // skipping 0 bytes indicates the end of the file.

        throw new EOFException(String.format(
            "reached end of file after %d bytes while attempting to skip %d bytes", skipped, n));
      }

      skipped += iterationSkipped;
    }
  }
}
