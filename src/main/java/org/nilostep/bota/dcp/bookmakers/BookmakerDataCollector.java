package org.nilostep.bota.dcp.bookmakers;

import info.debatty.java.stringsimilarity.Cosine;
import org.nilostep.bota.dcp.data.domain.ConfigBC;
import org.nilostep.bota.dcp.data.domain.Event;
import org.nilostep.bota.dcp.data.domain.MatchCandidate;
import org.nilostep.bota.dcp.data.domain.UnmatchedBCE;
import org.nilostep.bota.dcp.data.repository.*;
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

    @Autowired
    public EventRepository eventRepository;

    @Autowired
    public MatchCandidateRepository matchCandidateRepository;

    public BookmakerDataCollector() {
    }

    public int collectBookmakerData() {
        int out = 0;

        if (bf == null) {
            bf = new BrowserFacade();
        }

        configBCtoUnmatchedBCE();
        unmatchedBCEtoMatchCandidate();

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

    private void unmatchedBCEtoMatchCandidate() {
        Iterable<UnmatchedBCE> unmatchedBCES = unmatchedBCERepository.findAll();

        for (UnmatchedBCE unmatchedBCE : unmatchedBCES) {
            // Find all events in EventRepository for the unmatchedBCE competition
            Iterable<Event> events = eventRepository.findByCompetition(unmatchedBCE.getCompetition());

            MatchCandidate matchCandidateMaxSim = new MatchCandidate();
            MatchCandidate matchCandidate = new MatchCandidate();

            for (Event event : events) {
                // Bookmaker eventdescription
                matchCandidate.setEventDescriptionBookmaker(unmatchedBCE.getEventDescription());
                // Betfair eventdescription
                matchCandidate.setEventDescriptionBetfair(event.getName());
                // Similarity type
                matchCandidate.setSimilarityType("Cosine");
                // Similarity measure
                Cosine cosine = new Cosine();
                matchCandidate.setSimilarity(cosine.similarity(unmatchedBCE.getEventDescription(), event.getName()));
                // UnmatchedBCE
                matchCandidate.setUnmatchedBCE(unmatchedBCE);
                // Event
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
            matchCandidateRepository.save(matchCandidateMaxSim);
        }
    }
}