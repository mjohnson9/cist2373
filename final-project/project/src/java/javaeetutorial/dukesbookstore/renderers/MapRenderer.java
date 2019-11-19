/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * <p>You may not modify, use, reproduce, or distribute this software except in compliance with the
 * terms of the License at: http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */
package javaeetutorial.dukesbookstore.renderers;

import java.io.IOException;
import javaeetutorial.dukesbookstore.components.MapComponent;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

/** Renderer for {@link MapComponent} in an HTML environment. */
@FacesRenderer(componentFamily = "Map", rendererType = "DemoMap")
public class MapRenderer extends Renderer {

  public MapRenderer() {}

  /**
   * Decode the incoming request parameters to determine which hotspot (if any) has been selected.
   *
   * @param context <code>FacesContext</code>for the current request
   * @param component <code>UIComponent</code> to be decoded
   */
  @Override
  public void decode(FacesContext context, UIComponent component) {
    if ((context == null) || (component == null)) {
      throw new NullPointerException();
    }

    MapComponent map = (MapComponent) component;

    String key = getName(context, map);
    String value = (String) context.getExternalContext().getRequestParameterMap().get(key);

    if (value != null) {
      map.setCurrent(value);
    }
  }

  /**
   * Encode the beginning of this component.
   *
   * @param context <code>FacesContext</code> for the current request
   * @param component <code>UIComponent</code> to be decoded
   * @throws java.io.IOException
   */
  @Override
  public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
    if ((context == null) || (component == null)) {
      throw new NullPointerException();
    }

    MapComponent map = (MapComponent) component;
    ResponseWriter writer = context.getResponseWriter();

    writer.startElement("map", map);
    writer.writeAttribute("name", map.getId(), "id");
  }

  /**
   * Encode the children of this component.
   *
   * @param context <code>FacesContext</code> for the current request
   * @param component <code>UIComponent</code> to be decoded
   * @throws java.io.IOException
   */
  @Override
  public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
    if ((context == null) || (component == null)) {
      throw new NullPointerException();
    }
  }

  /**
   * Encode the ending of this component.
   *
   * @param context <code>FacesContext</code> for the current request
   * @param component <code>UIComponent</code> to be encoded
   * @throws java.io.IOException
   */
  @Override
  public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    if ((context == null) || (component == null)) {
      throw new NullPointerException();
    }

    MapComponent map = (MapComponent) component;
    ResponseWriter writer = context.getResponseWriter();

    writer.startElement("input", map);
    writer.writeAttribute("type", "hidden", null);
    writer.writeAttribute("name", getName(context, map), "clientId");
    writer.endElement("input");
    writer.endElement("map");
  }

  // --------------------------------------------------------- Private Methods
  /**
   * Return the calculated name for the hidden input field.
   *
   * @param context <code>FacesContext</code> for the current request
   * @param component <code>UIComponent</code> we are rendering
   */
  private String getName(FacesContext context, UIComponent component) {
    return (component.getId() + "_current");
  }

  /**
   * Return the context-relative path for the current page.
   *
   * @param context <code>FacesContext</code> for the current request
   */
  private String getURI(FacesContext context) {
    StringBuilder sb = new StringBuilder();
    sb.append(context.getExternalContext().getRequestContextPath());
    sb.append("/faces");
    sb.append(context.getViewRoot().getViewId());

    return (sb.toString());
  }
}
