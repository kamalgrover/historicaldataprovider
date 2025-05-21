package org.example;

import java.io.IOException;

public class InstrumentFetchException extends Exception {
  public InstrumentFetchException(String s, IOException e) {
    super(s, e);
  }

  public InstrumentFetchException(String s, String message, int code, Throwable e) {
    super(s + " " + message + " " + code, e);
  }
}
