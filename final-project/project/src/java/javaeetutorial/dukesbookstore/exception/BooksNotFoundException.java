/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>You may not modify, use, reproduce, or distribute this software except in compliance with the
 * terms of the License at: http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package javaeetutorial.dukesbookstore.exception;

/** This application exception indicates that books have not been found. */
public class BooksNotFoundException extends Exception {

  private static final long serialVersionUID = 4156679691884326238L;

  public BooksNotFoundException() {}

  public BooksNotFoundException(String msg) {
    super(msg);
  }
}
