package org.nilostep.bota.dcp.bookmakers;

import info.debatty.java.stringsimilarity.Cosine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nilostep.bota.dcp.data.domain.*;
import org.nilostep.bota.dcp.data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BookmakerDataCollector {

    private static Logger logger = LogManager.getLogger();

    private BrowserFacade bf;

    @Autowired
    public BookmakerRepository bookmakerRepository;

    @Autowired
    public CompetitionRepository competitionRepository;

    @Autowired
    public ConfigBCRepository configBCRepository;

    @Autowired
    public UnmatchedBCERepository unmatchedBCERepository;

    @Autowired
    public EventRepository eventRepository;

    @Autowired
    public BookmakerEventRepository bookmakerEventRepository;

    @Autowired
    public ConfigBMRepository configBMRepository;

    @Autowired
    public BceMbORepository bceMbORepository;

    public BookmakerDataCollector() {
    }

    public int collectBookmakerData() {
        int out = 0;

        if (bf == null) {
            bf = new BrowserFacade();
        }

        cleanTables();
        configBCtoUnmatchedBCE();
        unmatchedBCEtoBookmakerEvent();
        bookmakerEventToBCEMbO();

        if (bf != null) {
            bf.driver.quit();
        }

        return out;
    }

    private void cleanTables() {
        bceMbORepository.deleteAll();
        bookmakerEventRepository.deleteAll();
        unmatchedBCERepository.deleteAll();
    }

    private void configBCtoUnmatchedBCE() {
        Iterable<ConfigBC> configBCS = configBCRepository.findAll();

        for (ConfigBC configBC : configBCS) {
            bf.addQueryResult(configBC, true);
        }

        for (ConfigBC configBC : configBCS) {
            Pattern p = Pattern.compile(configBC.getRegexEventUrl());

            for (String html : configBC.getQueryResult()) {
                UnmatchedBCE unmatchedBCE = new UnmatchedBCE();

                // Bookmaker
                unmatchedBCE.setBookmaker(configBC.getBookmaker());
                // Competition
                unmatchedBCE.setCompetition(configBC.getCompetition());
                // MarketCssSelector for Event
                unmatchedBCE.setMarketCssSelector(configBC.getEventCssSelector());
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

    private void unmatchedBCEtoBookmakerEvent() {
        Iterable<UnmatchedBCE> unmatchedBCES = unmatchedBCERepository.findAll();

        for (UnmatchedBCE unmatchedBCE : unmatchedBCES) {
            // Find all events in EventRepository for the unmatchedBCE competition
            Iterable<Event> events = eventRepository.findByCompetition(unmatchedBCE.getCompetition());

            BookmakerEvent bookmakerEventMaxSim = new BookmakerEvent();
            BookmakerEvent bookmakerEvent = new BookmakerEvent();

            for (Event event : events) {

                bookmakerEvent.setEventDescriptionBookmaker(unmatchedBCE.getEventDescription());
                bookmakerEvent.setUrl(unmatchedBCE.getMarketUrl());
                bookmakerEvent.setCssSelector(unmatchedBCE.getMarketCssSelector());
                bookmakerEvent.setSimilarityType("Cosine");
                // Similarity measure
                Cosine cosine = new Cosine();
                bookmakerEvent.setSimilarity(cosine.similarity(unmatchedBCE.getEventDescription(), event.getName()));
                bookmakerEvent.setEvent(event);
                bookmakerEvent.setBookmaker(unmatchedBCE.getBookmaker());

                if (bookmakerEvent.getSimilarity() > bookmakerEventMaxSim.getSimilarity()) {
                    bookmakerEventMaxSim.setEventDescriptionBookmaker(bookmakerEvent.getEventDescriptionBookmaker());
                    bookmakerEventMaxSim.setUrl(bookmakerEvent.getUrl());
                    bookmakerEventMaxSim.setCssSelector(bookmakerEvent.getCssSelector());
                    bookmakerEventMaxSim.setEventJsPre(bookmakerEvent.getEventJsPre());
                    bookmakerEventMaxSim.setSimilarityType(bookmakerEvent.getSimilarityType());
                    bookmakerEventMaxSim.setSimilarity(bookmakerEvent.getSimilarity());
                    bookmakerEventMaxSim.setEvent(bookmakerEvent.getEvent());
                    bookmakerEventMaxSim.setBookmaker(bookmakerEvent.getBookmaker());
                }

                if (bookmakerEventMaxSim.getEventDescriptionBookmaker() != null) {
                    bookmakerEventRepository.save(bookmakerEventMaxSim);
                }
            }
        }
    }

    private void bookmakerEventToBCEMbO() {
        Iterable<BookmakerEvent> bookmakerEvents = bookmakerEventRepository.findAll();

        for (BookmakerEvent bookmakerEvent : bookmakerEvents) {
            bf.addQueryResult(bookmakerEvent);
        }

        for (BookmakerEvent bookmakerEvent : bookmakerEvents) {
            Iterable<ConfigBM> configBMS = configBMRepository.findByBookmaker(bookmakerEvent.getBookmaker());
            String[] oddsRaw = bookmakerEvent.getQueryResult().toArray(new String[0]);

            for (ConfigBM configBM : configBMS) {
                Pattern p = Pattern.compile(configBM.getRegexOdds());
                Matcher m = p.matcher(oddsRaw[configBM.getAddress()]);
                for (int i = 0; i < configBM.getBetCount(); i++) {
                    m.find();
                    BceMbO bceMbO = new BceMbO();
                    bceMbO.setBet("b" + String.valueOf(i + 1));
                    bceMbO.setOdd(Double.valueOf(m.group()));
                    bceMbO.setMarkettype(configBM.getMarkettypeId().getMarkettypeId().getMarkettype());
                    bceMbO.setBookmakerEvent(bookmakerEvent);
                    bceMbORepository.save(bceMbO);
                }
            }
        }

    }
}