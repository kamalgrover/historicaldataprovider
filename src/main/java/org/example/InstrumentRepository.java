package org.example;

import com.zerodhatech.models.Instrument;
import java.util.List;

public interface InstrumentRepository {
  List<Instrument> fetchAll() throws InstrumentFetchException;
}
