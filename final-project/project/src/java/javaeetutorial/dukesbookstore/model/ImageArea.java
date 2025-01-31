/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>You may not modify, use, reproduce, or distribute this software except in compliance with the
 * terms of the License at: http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package javaeetutorial.dukesbookstore.model;

import java.io.Serializable;

/**
 * {@link ImageArea} is a JavaBeans component that represents a hotspot in an image map. Within a
 * particular image map, no two hotspots may have the same alternate text, because this is treated
 * as a key.
 */
public final class ImageArea implements Serializable {

  private static final long serialVersionUID = -2382340811618738146L;
  private String alt = null;
  private String coords = null;
  private String shape = null;

  /** Construct an uninitialized {@link ImageArea} instance. */
  public ImageArea() {}

  /**
   * Construct an {@link ImageArea} initialized with the specified property values.
   *
   * @param alt Alternate text for this hotspot
   * @param coords Coordinate positions for this hotspot
   * @param shape Shape of this hotspot (default, rect, circle, poly)
   */
  public ImageArea(String alt, String coords, String shape) {
    setAlt(alt);
    setCoords(coords);
    setShape(shape);
  }

  /** @return the alternate text for this hotspot */
  public String getAlt() {
    return (this.alt);
  }

  /**
   * Set the alternate text for this hotspot.
   *
   * @param alt The new alternate text
   */
  public void setAlt(String alt) {
    this.alt = alt;
  }

  /** @return the coordinate positions for this hotspot */
  public String getCoords() {
    return (this.coords);
  }

  /**
   * Set the coordinate positions for this hotspot.
   *
   * @param coords The new coordinate positions
   */
  public void setCoords(String coords) {
    this.coords = coords;
  }

  /** @return the shape for this hotspot */
  public String getShape() {
    return (this.shape);
  }

  /**
   * Set the shape for this hotspot.
   *
   * @param shape The new shape
   */
  public void setShape(String shape) {
    this.shape = shape;
  }
}
