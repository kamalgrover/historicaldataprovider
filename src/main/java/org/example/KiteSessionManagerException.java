package org.example;

import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class KiteSessionManagerException extends Throwable {
  public KiteSessionManagerException(String s, IOException e) {
    super(s + " " + e.getMessage(), e);
  }

  public KiteSessionManagerException(String s, ExecutionException e) {
    super(s + " " + e.getMessage(), e);
  }

  public KiteSessionManagerException(String s, TimeoutException e) {
    super(s + " " + e.getMessage(), e);
  }

  public KiteSessionManagerException(String s, InterruptedException e) {
    super(s + " " + e.getMessage(), e);
  }

  public KiteSessionManagerException(String s, KiteException e) {
    super(s + " " + e.getMessage(), e);
  }
}
