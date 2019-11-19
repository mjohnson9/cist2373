/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>You may not modify, use, reproduce, or distribute this software except in compliance with the
 * terms of the License at: http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package javaeetutorial.dukesbookstore.exception;

/** This application exception indicates that a book has not been found. */
public class BookNotFoundException extends Exception {

  private static final long serialVersionUID = 8712363279947073702L;

  public BookNotFoundException() {}

  public BookNotFoundException(final String msg) {
    super(msg);
  }

  public BookNotFoundException(final String msg, final Throwable cause) {
    super(msg, cause);
  }
}
