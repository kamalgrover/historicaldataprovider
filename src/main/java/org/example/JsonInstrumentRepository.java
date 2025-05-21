package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zerodhatech.models.Instrument;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JsonInstrumentRepository implements InstrumentRepository {
  private final File file;
  private final ObjectMapper mapper = new ObjectMapper();

  JsonInstrumentRepository(File file) {
    this.file = file;
  }

  @Override
  public List<Instrument> fetchAll() throws InstrumentFetchException {
    if (!file.exists()) return Collections.emptyList();
    Instrument[] arr;
    try {
      arr = mapper.readValue(file, Instrument[].class);
    } catch (IOException e) {
      throw new InstrumentFetchException("Unable to read value from json file", e);
    }
    return Arrays.asList(arr);
  }

  void saveAll(List<Instrument> list) throws InstrumentFetchException {
    try {
      mapper.writerWithDefaultPrettyPrinter().writeValue(file, list);
    } catch (IOException e) {
      throw new InstrumentFetchException("Unable to write values to file", e);
    }
  }
}
