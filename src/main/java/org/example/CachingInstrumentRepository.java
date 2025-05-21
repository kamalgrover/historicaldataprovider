package org.example;

import com.zerodhatech.models.Instrument;

import java.util.List;
import java.util.Optional;

public class CachingInstrumentRepository implements InstrumentRepository {
    private final InstrumentRepository apiRepo;
    private final JsonInstrumentRepository diskRepo;
    private final java.util.Map<Long, Instrument> tokenIndex = new java.util.HashMap<>();
    private final java.util.Map<String, Instrument> symbolIndex = new java.util.HashMap<>();

    CachingInstrumentRepository(InstrumentRepository apiRepo, JsonInstrumentRepository diskRepo) {
        this.apiRepo = apiRepo;
        this.diskRepo = diskRepo;
    }

    @Override
    public List<Instrument> fetchAll() throws InstrumentFetchException {
        List<Instrument> fromDisk = diskRepo.fetchAll();
        if (!fromDisk.isEmpty()) {
            index(fromDisk);
            return fromDisk;
        }
        List<Instrument> fromApi = apiRepo.fetchAll();
        index(fromApi);
        diskRepo.saveAll(fromApi);
        return fromApi;
    }

    private void index(List<Instrument> list) {
        list.forEach(i -> {
            tokenIndex.put(i.instrument_token, i);
            symbolIndex.put(i.tradingsymbol, i);
        });
    }

    Optional<Instrument> findByToken(long token) {
        return Optional.ofNullable(tokenIndex.get(token));
    }

    Optional<Instrument> findBySymbol(String symbol) {
        return Optional.ofNullable(symbolIndex.get(symbol));
    }
}
