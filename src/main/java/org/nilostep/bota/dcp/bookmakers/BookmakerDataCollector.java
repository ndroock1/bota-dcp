package org.nilostep.bota.dcp.bookmakers;

import info.debatty.java.stringsimilarity.Jaccard;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BookmakerDataCollector {

    private static Logger logger = LogManager.getLogger();

    private ParallelQuery pq;

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

        cleanTables();

        pq = new ParallelQuery();
        for (int k = 0; k < 2; k++) {
            addConfigBCPayload();
        }
        unmatchedBCEtoBookmakerEvent();
        validateBookmakerEvent();

        pq = new ParallelQuery();
        // ToDo:
        for (int k = 0; k < 1; k++) {
            addBCEPayload();
        }

        return out;
    }

    private void cleanTables() {
        Iterable<ConfigBC> configBCS = configBCRepository.findAll();
        for (ConfigBC configBC : configBCS) {
            configBC.setHasPayload(0);
            configBCRepository.save(configBC);
        }
        bceMbORepository.deleteAll();
        bookmakerEventRepository.deleteAll();
        unmatchedBCERepository.deleteAll();
    }

    private void addConfigBCPayload() {
        Iterable<ConfigBC> configBCS =
                configBCRepository.findConfigBCSBySelectedEqualsAndHasPayloadEquals(1, 0);

        Iterable<Object> iQueries = (List) configBCS;
        if (iQueries.iterator().hasNext()) {
            pq.submitQuery(iQueries);
        }

        for (ConfigBC configBC : configBCS) {
            if (configBC.getQueryResult() != null) {
                configBCtoUnmatchedBCE(configBC);
                configBC.setHasPayload(1);
                configBCRepository.save(configBC);
            }
        }
    }

    private void configBCtoUnmatchedBCE(ConfigBC configBC) {
        if (configBC.getSelected() == 1) {

            Pattern p = Pattern.compile(configBC.getRegexEventUrl());

            for (String html : configBC.getQueryResult()) {

                try {
                    UnmatchedBCE unmatchedBCE = new UnmatchedBCE();

                    // Bookmaker
                    unmatchedBCE.setBookmaker(configBC.getBookmaker());
                    // Competition
                    unmatchedBCE.setCompetition(configBC.getCompetition());
                    // MarketCssSelector for Event
                    unmatchedBCE.setMarketCssSelector(configBC.getEventCssSelector());
                    // ConfigBC
                    unmatchedBCE.setConfigBC(configBC);
                    // GroupUrl
                    unmatchedBCE.setGroupUrl(configBC.getUrl());

                    Matcher m = p.matcher(html);

                    switch (configBC.getType()) {
                        case 3: // Coral
                            // EventJSPre for Event
                            unmatchedBCE.setEventJsPre(configBC.getEventJsPre());
                            // MarketURL for Event
                            m.find();
                            unmatchedBCE.setMarketUrl(configBC.getBookmaker().getBookmakerRootUrl() + m.group(0));
                            // Event description
                            unmatchedBCE.setUrlCssSelector("a[href='" +
                                    configBC.getBookmaker().getBookmakerRootUrl() + m.group(0) + "'");
                            m.find();
                            unmatchedBCE.setEventDescription(m.group(0));
                            break;

                        case 2: // William Hill
                            // EventJSPre for Event
                            unmatchedBCE.setEventJsPre(configBC.getEventJsPre());
                            // MarketURL for Event
                            m.find();
                            unmatchedBCE.setMarketUrl(configBC.getBookmaker().getBookmakerRootUrl() + m.group(0));
                            // URL CSS Selector
                            unmatchedBCE.setUrlCssSelector("a[href='" +
                                    configBC.getBookmaker().getBookmakerRootUrl() + m.group(0) + "'");
                            // Event description
                            m.find();
                            String desc = m.group(0);
                            desc = desc.replaceAll("&nbsp;", "");
                            unmatchedBCE.setEventDescription(desc);
                            break;

                        case 1: // 10Bet
                            // MarketURL for Event
                            unmatchedBCE.setMarketUrl(configBC.getBookmaker().getBookmakerRootUrl());
                            // EventJSPre for Event (1)
                            unmatchedBCE.setEventJsPre(configBC.getEventJsPre());
                            // EventJSPre for Event (2)
                            m.find();
                            unmatchedBCE.setEventJsPre(unmatchedBCE.getEventJsPre().concat(";").concat(m.group(0)));
                            // Event description
                            m.find();
                            unmatchedBCE.setEventDescription(m.group(0));
                            break;

                        default: // Others
                            // EventJSPre for Event
                            unmatchedBCE.setEventJsPre(configBC.getEventJsPre());
                            // MarketURL for Event
                            m.find();
                            unmatchedBCE.setMarketUrl(configBC.getBookmaker().getBookmakerRootUrl() + m.group(0));
                            // Event description
                            unmatchedBCE.setUrlCssSelector("a[href='" + m.group(0) + "'");
                            m.find();
                            unmatchedBCE.setEventDescription(m.group(0));
                    }

                    unmatchedBCERepository.save(unmatchedBCE);

                } catch (IllegalStateException e) {
                }

            }
        }
    }

    private void unmatchedBCEtoBookmakerEvent() {
        Iterable<UnmatchedBCE> unmatchedBCES = unmatchedBCERepository.findAll();

        for (UnmatchedBCE unmatchedBCE : unmatchedBCES) {

            try {
                if (unmatchedBCE.getEventDescription().indexOf(" v ") < 0 &&
                        unmatchedBCE.getEventDescription().indexOf(" - ") < 0) {
                    throw new InvalidBCEventException();
                }

                // Find all events in EventRepository for the unmatchedBCE competition
                Iterable<Event> events = eventRepository.findByCompetition(unmatchedBCE.getCompetition());

                BookmakerEvent bookmakerEventMaxSim = new BookmakerEvent();
                BookmakerEvent bookmakerEvent = new BookmakerEvent();

                for (Event event : events) {

                    bookmakerEvent.setEventDescriptionBookmaker(
                            unmatchedBCE.getEventDescription().replace(" - ", " v ")
                    );
                    bookmakerEvent.setUrl(unmatchedBCE.getMarketUrl());
                    bookmakerEvent.setGroupUrl(unmatchedBCE.getGroupUrl());
                    bookmakerEvent.setCssSelector(unmatchedBCE.getMarketCssSelector());
                    bookmakerEvent.setUrlCssSelector(unmatchedBCE.getUrlCssSelector());
                    bookmakerEvent.setEventJsPre(unmatchedBCE.getEventJsPre());
                    bookmakerEvent.setSimilarityType("Cosine");
                    // Similarity measure
//                    Cosine cosine = new Cosine();
//                    bookmakerEvent.setSimilarity(cosine.similarity(
//                            bookmakerEvent.getEventDescriptionBookmaker(),
//                            event.getName()
//                    ));

                    bookmakerEvent.setSimilarityType("Jaccard");
                    bookmakerEvent.setSimilarity(
                            calcSimilarity(bookmakerEvent.getEventDescriptionBookmaker(), event.getName())
                    );

//logger.info("+++");
//logger.info(bookmakerEvent.getEventDescriptionBookmaker());
//logger.info(event.getName());
//logger.info(bookmakerEvent.getSimilarity());
//logger.info("+++");


                    bookmakerEvent.setEvent(event);
                    bookmakerEvent.setBookmaker(unmatchedBCE.getBookmaker());
                    bookmakerEvent.setConfigBC(unmatchedBCE.getConfigBC());

                    if (bookmakerEvent.getSimilarity() > bookmakerEventMaxSim.getSimilarity()) {
                        bookmakerEventMaxSim.setEventDescriptionBookmaker(bookmakerEvent.getEventDescriptionBookmaker());
                        bookmakerEventMaxSim.setUrl(bookmakerEvent.getUrl());
                        bookmakerEventMaxSim.setGroupUrl(bookmakerEvent.getGroupUrl());
                        bookmakerEventMaxSim.setCssSelector(bookmakerEvent.getCssSelector());
                        bookmakerEventMaxSim.setUrlCssSelector(bookmakerEvent.getUrlCssSelector());
                        bookmakerEventMaxSim.setEventJsPre(bookmakerEvent.getEventJsPre());
                        bookmakerEventMaxSim.setSimilarityType(bookmakerEvent.getSimilarityType());
                        bookmakerEventMaxSim.setSimilarity(bookmakerEvent.getSimilarity());
                        bookmakerEventMaxSim.setEvent(bookmakerEvent.getEvent());
                        bookmakerEventMaxSim.setBookmaker(bookmakerEvent.getBookmaker());
                        bookmakerEventMaxSim.setConfigBC(bookmakerEvent.getConfigBC());
                    }

                    if (bookmakerEventMaxSim.getEventDescriptionBookmaker() != null) {
                        bookmakerEventRepository.save(bookmakerEventMaxSim);
                    }
                }

            } catch (InvalidBCEventException e) {

            }

        }
    }

    private double calcSimilarity(String be, String ev) {
//        Cosine sim = new Cosine();
        Jaccard sim = new Jaccard();

        String[] beParts = be.split(" v ");
        String[] evParts = ev.split(" v ");

        if (evParts.length > 1) {
            return (sim.similarity(beParts[0], evParts[0]) + sim.similarity(beParts[1], evParts[1])) / 2;
        } else {
            return 0;
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

    private void addBCEPayload2() {
        Iterable<BookmakerEvent> bookmakerEvents =
                bookmakerEventRepository.findBookmakerEventsByHasPayloadEquals(0);

        Iterable<IQuery> iQueries = (List) bookmakerEvents;
        if (iQueries.iterator().hasNext()) {
//            pq.submitQuery(iQueries, 8);
        }

        for (BookmakerEvent bookmakerEvent : bookmakerEvents) {
            if (bookmakerEvent.getQueryResult() != null) {
                bookmakerEventToBCEMbO(bookmakerEvent);
                bookmakerEvent.setHasPayload(1);
                bookmakerEventRepository.save(bookmakerEvent);
            }
        }
    }

    private void addBCEPayload() {
        Bookmaker bet10 = bookmakerRepository.findOne("bkm003"); // Bet10

        List<Object> out = new ArrayList<>();

        Iterable<ConfigBC> configBCS =
                configBCRepository.findConfigBCSBySelectedEqualsAndHasPayloadEquals(1, 1);

        for (ConfigBC configBC : configBCS) {
            Iterable<BookmakerEvent> bookmakerEvents = bookmakerEventRepository.findBookmakerEventsByConfigBC(configBC);

            if (configBC.getBookmaker().equals(bet10)) {
                for (BookmakerEvent bookmakerEvent : bookmakerEvents) {
                    out.add(bookmakerEvent);
                }
            } else {
                out.add(bookmakerEvents); // Added as List
            }
        }

        pq.submitQuery(out, 6);

        for (Object o : out) {
            String oName = o.getClass().getSimpleName();
            switch (oName) {
                case "BookmakerEvent":
                    BookmakerEvent be = (BookmakerEvent) o;
                    if (be.getQueryResult() != null) {
                        bookmakerEventToBCEMbO(be);
                        be.setHasPayload(1);
                        bookmakerEventRepository.save(be);
                    }
                    break;

                case "ArrayList":
                    for (Object obj : (ArrayList) o) {
                        be = (BookmakerEvent) obj;
                        if (be.getQueryResult() != null) {
                            bookmakerEventToBCEMbO(be);
                            be.setHasPayload(1);
                            bookmakerEventRepository.save(be);
                        }
                    }
            }
        }


    }

    private void bookmakerEventToBCEMbO(BookmakerEvent bookmakerEvent) {
        Iterable<ConfigBM> configBMS = configBMRepository.findByBookmaker(bookmakerEvent.getBookmaker());
        String[] oddsRaw = bookmakerEvent.getQueryResult().toArray(new String[0]);

        // For Each BookmakerMarket
        for (ConfigBM configBM : configBMS) {
            if (configBM.getSelected() == 1) {
                Pattern p = Pattern.compile(configBM.getRegexMarket());
                int address = 0;
                // For Each Element of the Array with Market Data ( = QueryResult )
                for (int i = 0; i < oddsRaw.length; i++) {
                    Matcher m = p.matcher(oddsRaw[i]);
                    // IF the Market matches THEN create BCEMbO ...
                    if (m.find()) {
                        address = i;
                        p = Pattern.compile(configBM.getRegexOdds());
                        m = p.matcher(oddsRaw[address]);
                        // ... for Each individual Bet
                        for (int j = 0; j < configBM.getBetCount(); j++) {
                            m.find();
                            BceMbO bceMbO = new BceMbO();
                            bceMbO.setBet("b" + String.valueOf(j + 1));

                            try {
                                bceMbO.setOdd(Double.valueOf(m.group()));
                            } catch (NumberFormatException e) {
                                bceMbO.setOdd(0d);
                            }

                            bceMbO.setMarkettype(configBM.getMarkettypeId().getMarkettypeId().getMarkettype());
                            bceMbO.setBookmakerEvent(bookmakerEvent);
                            //
                            logger.info(
                                    bookmakerEvent.getBookmaker().getBookmakerName() +
                                            " - " +
                                            bookmakerEvent.getEventDescriptionBookmaker() +
                                            " - " +
                                            configBM.getMarkettypeId().getMarkettypeId().getMarkettype() +
                                            " - " +
                                            bceMbO.getBet()
                            );
                            //
                            bceMbORepository.save(bceMbO);
                        }
                        break;
                    }
                }
            }
        }
    }
}