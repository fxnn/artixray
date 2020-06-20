package de.fxnn.artixray.util.boundary;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class Throwables {

  private Throwables() {}

  public static String stackTraceToString(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    throwable.printStackTrace(new PrintWriter(stringWriter));
    return stringWriter.toString();
  }

}
