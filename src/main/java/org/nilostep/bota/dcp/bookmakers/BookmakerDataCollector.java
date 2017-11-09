package org.nilostep.bota.dcp.bookmakers;

import info.debatty.java.stringsimilarity.Cosine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nilostep.bota.dcp.data.domain.*;
import org.nilostep.bota.dcp.data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BookmakerDataCollector {

    private static Logger logger = LogManager.getLogger();

    private static final double COSINE_THRESHOLD = 0.4d;

    private static final double JAROWINKLER_THRESHOLD = 0.4d;

    private static final double NORMALIZED_LEVENSHTEIN_THRESHOLD = 0.4d;

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
        validateBookmakerEvent();
        for (int k = 0; k < 2; k++) {
            addBCEPayload();
        }

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
            if (configBC.getSelected() == 1) {
                try {
                    bf.addQueryResult(configBC, true);
                }
//
// ToDo: Handle the skipped competition
                catch (org.openqa.selenium.StaleElementReferenceException e) {
                    System.out.println(">> StaleCrash @ " +
                            configBC.getUrl() + " : " + e.getMessage());
                } catch (TooManyAttemptsException tmae) {
                    System.out.println(">> Too many attemts @ " +
                            configBC.getUrl() + " : " + tmae.getMessage());
                }
//
            }
        }

        for (ConfigBC configBC : configBCS) {
            if (configBC.getSelected() == 1) {

                Pattern p = Pattern.compile(configBC.getRegexEventUrl());

                for (String html : configBC.getQueryResult()) {
                    UnmatchedBCE unmatchedBCE = new UnmatchedBCE();

                    // Bookmaker
                    unmatchedBCE.setBookmaker(configBC.getBookmaker());
                    // Competition
                    unmatchedBCE.setCompetition(configBC.getCompetition());
                    // MarketCssSelector for Event
                    unmatchedBCE.setMarketCssSelector(configBC.getEventCssSelector());

                    switch (configBC.getType()) {
                        case 1:
                            // MarketURL for Event
                            unmatchedBCE.setMarketUrl(configBC.getBookmaker().getBookmakerRootUrl());
                            // EventJSPre for Event (1)
                            unmatchedBCE.setEventJsPre(configBC.getEventJsPre());
                            // EventJSPre for Event (2)
                            Matcher m = p.matcher(html);
                            m.find();
                            unmatchedBCE.setEventJsPre(unmatchedBCE.getEventJsPre().concat(";").concat(m.group(0)));
                            // Event description
                            m.find();
                            unmatchedBCE.setEventDescription(m.group(0));
                            break;

                        default:
                            // MarketURL for Event
                            m = p.matcher(html);
                            m.find();
                            unmatchedBCE.setMarketUrl(configBC.getBookmaker().getBookmakerRootUrl() + m.group(0));
                            // Event description
                            m.find();
                            unmatchedBCE.setEventDescription(m.group(0));
                    }

                    unmatchedBCERepository.save(unmatchedBCE);
                }
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

                bookmakerEvent.setEventDescriptionBookmaker(
                        unmatchedBCE.getEventDescription().replace(" - ", " v ")
                );
                bookmakerEvent.setUrl(unmatchedBCE.getMarketUrl());
                bookmakerEvent.setCssSelector(unmatchedBCE.getMarketCssSelector());
                bookmakerEvent.setEventJsPre(unmatchedBCE.getEventJsPre());
                bookmakerEvent.setSimilarityType("Cosine");
                // Similarity measure
                Cosine cosine = new Cosine();
                bookmakerEvent.setSimilarity(cosine.similarity(
                        bookmakerEvent.getEventDescriptionBookmaker(),
                        event.getName()
                ));
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

    private void validateBookmakerEvent() {
        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/oddsbrowser",
                    "root",
                    "");

            CallableStatement statement = con.prepareCall("{call sp_delete_duplicates()}");
            statement.execute();
            statement.close();

        } catch (SQLException se) {
            System.out.println("Duplicate FAIL " + se.getMessage());
        }
    }

    private void addBCEPayload() {
        Iterable<BookmakerEvent> bookmakerEvents = bookmakerEventRepository.findAll();

        for (BookmakerEvent bookmakerEvent : bookmakerEvents) {
            if (bookmakerEvent.getHasPayload() == 0) {

                try {
                    bf.addQueryResult(bookmakerEvent);
                    bookmakerEventToBCEMbO(bookmakerEvent);
                    bookmakerEvent.setHasPayload(1);
                    bookmakerEventRepository.save(bookmakerEvent);

                } catch (org.openqa.selenium.StaleElementReferenceException e) {
                    logger.info(">> StaleElementReferenceException @ " +
                            bookmakerEvent.getEventDescriptionBookmaker());
                } catch (TooManyAttemptsException tmae) {
                    logger.info(">> TooManyAttemptsException @ " +
                            bookmakerEvent.getEventDescriptionBookmaker());
                }

            }
        }
    }

    private void bookmakerEventToBCEMbO(BookmakerEvent bookmakerEvent) {

        Iterable<ConfigBM> configBMS = configBMRepository.findByBookmaker(bookmakerEvent.getBookmaker());

        String[] oddsRaw = bookmakerEvent.getQueryResult().toArray(new String[0]);

        //
        logger.info("BCEMbO's : " +
                bookmakerEvent.getBookmaker().getBookmakerName() +
                " - " +
                bookmakerEvent.getEventDescriptionBookmaker());
        //

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