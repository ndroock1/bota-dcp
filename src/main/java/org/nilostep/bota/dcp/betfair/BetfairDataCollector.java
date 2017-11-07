package org.nilostep.bota.dcp.betfair;


import com.jbetfairng.BetfairClient;
import com.jbetfairng.entities.*;
import com.jbetfairng.enums.Exchange;
import com.jbetfairng.exceptions.LoginException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nilostep.bota.dcp.data.domain.Competition;
import org.nilostep.bota.dcp.data.domain.Event;
import org.nilostep.bota.dcp.data.domain.*;
import org.nilostep.bota.dcp.data.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
public class BetfairDataCollector {
    private static Logger logger = LogManager.getLogger();

    private BetfairClient client;

    @Autowired
    public EventtypeRepository eventtypeRepository;

    @Autowired
    public CompetitionRepository competitionRepository;

    @Autowired
    public EventRepository eventRepository;

    @Autowired
    public MarkettypeRepository markettypeRepository;
    public MarkettypeId markettypeId;

    @Autowired
    public BceMbORepository bceMbORepository;

    @Autowired
    public BookmakerEventRepository bookmakerEventRepository;

    @Autowired
    public UnmatchedBCERepository unmatchedBCERepository;

    @Autowired
    private Environment env;


    public BetfairDataCollector() {
    }

    public String test() {
        return env.getProperty("bota.version");
    }

    public int collectBetfairData() {
        int out = 0;

        if (client == null) {
            client = new BetfairClient(Exchange.UK, env.getProperty("bota.appkey"));
            try {
                client.login(
                        env.getProperty("bota.p12certificatelocation"),
                        env.getProperty("bota.p12certificatepassword"),
                        env.getProperty("bota.username"),
                        env.getProperty("bota.password"));
            } catch (LoginException e) {
                e.printStackTrace();
            }
        }

        cleanTables();
        updateEventtypes();
        updateCompetitions();
        updateEvents();
        updateMarketTypes();

        return out;
    }

    private void cleanTables() {
        bceMbORepository.deleteAll();
        bookmakerEventRepository.deleteAll();
        unmatchedBCERepository.deleteAll();

        eventRepository.deleteAll();
    }

    private void updateEventtypes() {
        //
        logger.info("Betfair : EventTypes");
        //
        MarketFilter filter = new MarketFilter();
        List<EventTypeResult> eventTypes = client.listEventTypes(filter).getResponse();

        for (EventTypeResult eventTypeResult : eventTypes) {
            Eventtype bfT = new Eventtype();
            bfT.setEventtype(eventTypeResult.getEventType().getId());
            bfT.setName(eventTypeResult.getEventType().getName());
            eventtypeRepository.save(bfT);
        }
    }

    private void updateCompetitions() {
        //
        logger.info("Betfair : Competitions");
        //
        MarketFilter filter = new MarketFilter();
        List<EventTypeResult> eventTypes = client.listEventTypes(filter).getResponse();

        for (EventTypeResult eventTypeResult : eventTypes) {
            filter = new MarketFilter();
            filter.setEventTypeIds(new HashSet<String>(Collections.singleton(eventTypeResult.getEventType().getId())));
            List<CompetitionResult> competitions = client.listCompetitions(filter).getResponse();

            for (CompetitionResult competitionResult : competitions) {
                Competition bfC = new Competition();
                bfC.setCompetitionId(competitionResult.getCompetition().getId());
                bfC.setCompetitionName(competitionResult.getCompetition().getName());
                bfC.setCompetitionRegion(competitionResult.getCompetitionRegion());
                bfC.setEventtype(eventtypeRepository.findOne(eventTypeResult.getEventType().getId()));
                competitionRepository.save(bfC);
            }
        }
    }

    private void updateEvents() {
        //
        logger.info("Betfair : Events");
        //
        MarketFilter filter = new MarketFilter();
        List<CompetitionResult> competitions = client.listCompetitions(filter).getResponse();

        for (CompetitionResult competitionResult :competitions) {
            filter = new MarketFilter();
            filter.setCompetitionIds(new HashSet<String>(Collections.singleton(competitionResult.getCompetition().getId())));
            List<EventResult> events = client.listEvents(filter).getResponse();

            for (EventResult eventResult : events) {
                Event bfE = new Event();
                bfE.setId(eventResult.getEvent().getId());
                bfE.setName(eventResult.getEvent().getName());
                bfE.setCountryCode(eventResult.getEvent().getCountryCode());
                bfE.setTimezone(eventResult.getEvent().getTimezone());
                bfE.setVenue(eventResult.getEvent().getVenue());
                bfE.setOpenDate(eventResult.getEvent().getOpenDate());
                bfE.setCompetition(competitionRepository.findOne(competitionResult.getCompetition().getId()));
                eventRepository.save(bfE);
            }
        }
    }

    private void updateMarketTypes() {
        //
        logger.info("Betfair : MarketTypes");
        //
        MarketFilter filter = new MarketFilter();
        List<EventTypeResult> eventTypes = client.listEventTypes(filter).getResponse();

        for (EventTypeResult eventTypeResult : eventTypes) {
            filter = new MarketFilter();
            filter.setEventTypeIds(new HashSet<String>(Collections.singleton(eventTypeResult.getEventType().getId())));
            List<MarketTypeResult> marketTypeResults = client.listMarketTypes(filter).getResponse();

            for (MarketTypeResult marketTypeResult : marketTypeResults) {
                Markettype markettype = new Markettype();

                MarkettypeId markettypeId = new MarkettypeId();
                markettypeId.setEventtype(eventtypeRepository.findOne(eventTypeResult.getEventType().getId()));
                markettypeId.setMarkettype(marketTypeResult.getMarketType());

                markettype.setMarkettypeId(markettypeId);
                markettypeRepository.save(markettype);
            }
        }
    }
}