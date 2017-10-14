package org.nilostep.bota.dcp.bookmakers;

import org.nilostep.bota.dcp.data.domain.ConfigBC;
import org.nilostep.bota.dcp.data.domain.UnmatchedBCE;
import org.nilostep.bota.dcp.data.repository.BookmakerRepository;
import org.nilostep.bota.dcp.data.repository.CompetitionRepository;
import org.nilostep.bota.dcp.data.repository.ConfigBCRepository;
import org.nilostep.bota.dcp.data.repository.UnmatchedBCERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BookmakerDataCollector {

    private BrowserFacade bf;

    @Autowired
    public BookmakerRepository bookmakerRepository;

    @Autowired
    public CompetitionRepository competitionRepository;

    @Autowired
    public ConfigBCRepository configBCRepository;

    @Autowired
    public UnmatchedBCERepository unmatchedBCERepository;

    public BookmakerDataCollector() {
    }

    public int collectBookmakerData() {
        int out = 0;

        if (bf == null) {
            bf = new BrowserFacade();
        }

        configBCtoUnmatchedBCE();

        if (bf != null) {
            bf.driver.quit();
        }

        return out;
    }

    private void configBCtoUnmatchedBCE() {
        Iterable<ConfigBC> configBCS = configBCRepository.findAll();

        for (ConfigBC configBC : configBCS) {
            bf.addQueryResult(configBC);
        }

        for (ConfigBC configBC : configBCS) {
            Pattern p = Pattern.compile(configBC.getRegexEventUrl());

            for (String html : configBC.getQueryResult()) {
                UnmatchedBCE unmatchedBCE = new UnmatchedBCE();

                // Bookmaker
                unmatchedBCE.setBookmaker(configBC.getBookmaker());
                // Competition
                unmatchedBCE.setCompetition(configBC.getCompetition());
                // MarketURL for Event
                Matcher m = p.matcher(html);
                m.find();
                unmatchedBCE.setMarketUrl(configBC.getBookmaker().getBookmakerRootUrl() + m.group(0));
                // Event description
                m.find();
                unmatchedBCE.setEventDescription(m.group(0));

                unmatchedBCERepository.save(unmatchedBCE);
            }
        }
    }

}