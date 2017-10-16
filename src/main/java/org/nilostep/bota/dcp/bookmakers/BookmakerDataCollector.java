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
    public MatchCandidateRepository matchCandidateRepository;

    @Autowired
    public BookmakerEventRepository bookmakerEventRepository;

    public BookmakerDataCollector() {
    }

    public int collectBookmakerData() {
        int out = 0;

        if (bf == null) {
            bf = new BrowserFacade();
        }

        configBCtoUnmatchedBCE();
        unmatchedBCEtoMatchCandidate();
        unmatchedBCEtoBookmakerEvent();

        if (bf != null) {
            bf.driver.quit();
        }

        return out;
    }

    private void configBCtoUnmatchedBCE() {
        Iterable<ConfigBC> configBCS = configBCRepository.findAll();

        for (ConfigBC configBC : configBCS) {
            logger.info(
                    "Scraping : " +
                            configBC.getBookmaker().getBookmakerRootUrl() +
                            " - " +
                            configBC.getCompetition().getCompetitionName()
            );
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

    private void unmatchedBCEtoMatchCandidate() {
        Iterable<UnmatchedBCE> unmatchedBCES = unmatchedBCERepository.findAll();

        for (UnmatchedBCE unmatchedBCE : unmatchedBCES) {
            // Find all events in EventRepository for the unmatchedBCE competition
            Iterable<Event> events = eventRepository.findByCompetition(unmatchedBCE.getCompetition());

            MatchCandidate matchCandidateMaxSim = new MatchCandidate();
            MatchCandidate matchCandidate = new MatchCandidate();

            for (Event event : events) {

                matchCandidate.setEventDescriptionBookmaker(unmatchedBCE.getEventDescription());
                matchCandidate.setEventDescriptionBetfair(event.getName());
                matchCandidate.setSimilarityType("Cosine");
                // Similarity measure
                Cosine cosine = new Cosine();
                matchCandidate.setSimilarity(cosine.similarity(unmatchedBCE.getEventDescription(), event.getName()));
                matchCandidate.setUnmatchedBCE(unmatchedBCE);
                matchCandidate.setEvent(event);

                if (matchCandidate.getSimilarity() > matchCandidateMaxSim.getSimilarity()) {
                    matchCandidateMaxSim.setEventDescriptionBookmaker(matchCandidate.getEventDescriptionBookmaker());
                    matchCandidateMaxSim.setEventDescriptionBetfair(matchCandidate.getEventDescriptionBetfair());
                    matchCandidateMaxSim.setSimilarityType(matchCandidate.getSimilarityType());
                    matchCandidateMaxSim.setSimilarity(matchCandidate.getSimilarity());
                    matchCandidateMaxSim.setUnmatchedBCE(matchCandidate.getUnmatchedBCE());
                    matchCandidateMaxSim.setEvent(matchCandidate.getEvent());
                }

            }
            if (matchCandidateMaxSim.getEventDescriptionBetfair() != null) {
                matchCandidateRepository.save(matchCandidateMaxSim);
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
                bookmakerEvent.setMarketUrl(unmatchedBCE.getMarketUrl());
                bookmakerEvent.setSimilarityType("Cosine");
                // Similarity measure
                Cosine cosine = new Cosine();
                bookmakerEvent.setSimilarity(cosine.similarity(unmatchedBCE.getEventDescription(), event.getName()));
                bookmakerEvent.setEvent(event);
                bookmakerEvent.setBookmaker(unmatchedBCE.getBookmaker());

                if (bookmakerEvent.getSimilarity() > bookmakerEventMaxSim.getSimilarity()) {
                    bookmakerEventMaxSim.setEventDescriptionBookmaker(bookmakerEvent.getEventDescriptionBookmaker());
                    bookmakerEventMaxSim.setMarketUrl(bookmakerEvent.getMarketUrl());
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
}