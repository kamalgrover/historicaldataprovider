package org.example;

import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.Instrument;
import java.io.File;
import java.util.List;

public class Application {
  public static void main(String[] args) throws Exception {
    // 1. Load config
    AppConfig config = new AppConfig();

    // 2. Kick off login
    KiteSessionManager sessionMgr =
        new KiteSessionManager(
            config.getApiKey(),
            config.getApiSecret(),
            config.getCallbackPort(),
            config.getCallbackPath(),
            config.getUserId());
    KiteConnect kc;
    try {
      kc = sessionMgr.login();
    } catch (KiteSessionManagerException e) {
      throw new RuntimeException(e);
    }

    // 3. Set up your cache/repo
    File cacheFile = new File(config.getCacheFilePath());
    JsonInstrumentRepository diskRepo = new JsonInstrumentRepository(cacheFile);
    CachingInstrumentRepository cacheRepo =
        new CachingInstrumentRepository(new ApiInstrumentRepository(kc), diskRepo);

    // 4. Use it
    List<Instrument> instruments = cacheRepo.fetchAll();
    System.out.println("Loaded " + instruments.size() + " instruments");

    cacheRepo
        .findBySymbol("HDFCBANK")
        .ifPresent(i -> System.out.println("Found instrument: " + i.tradingsymbol));
  }
}
