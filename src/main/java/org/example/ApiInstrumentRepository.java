package org.example;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Instrument;
import java.io.IOException;
import java.util.List;

public class ApiInstrumentRepository implements InstrumentRepository {
  private final KiteConnect kiteConnect;

  ApiInstrumentRepository(KiteConnect kiteConnect) {
    this.kiteConnect = kiteConnect;
  }

  @Override
  public List<Instrument> fetchAll() throws InstrumentFetchException {
    try {
      return kiteConnect.getInstruments();
    } catch (KiteException e) {
      throw new InstrumentFetchException("Kite Related errors", e.message, e.code, e);
    } catch (IOException e) {
      throw new InstrumentFetchException("Network error", e);
    }
  }
}
