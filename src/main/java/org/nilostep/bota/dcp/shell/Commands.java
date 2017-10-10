package org.nilostep.bota.dcp.shell;

//import org.nilostep.bota.dcp.betfair.BetfairFacade;
//import org.nilostep.bota.dcp.data.domain.Competition;
//import org.nilostep.bota.dcp.data.domain.Event;
//import org.nilostep.bota.dcp.data.domain.Eventtype;
//import org.nilostep.bota.dcp.data.repository.CompetitionRepository;
//import org.nilostep.bota.dcp.data.repository.EventRepository;
//import org.nilostep.bota.dcp.data.repository.EventtypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.*;

@ShellComponent()
public class Commands {

//    @Autowired
//    private EventtypeRepository eventtypeRepository;
//
//    @Autowired
//    private CompetitionRepository competitionRepository;
//
//    @Autowired
//    private EventRepository eventRepository;
//
//    @Autowired
//    public BetfairFacade client;

    @Autowired
    private Environment env;


    @ShellMethod("Version.")
//    public String version() {
//        return env.getProperty("bota.version") + " : " + client.test();
//    }
    public String version() {
        return env.getProperty("bota.version") + " : " + " TBD";
    }

//    @ShellMethod("Do Betfair Update.")
//    public int doUpdate() {
//        return client.updateBetfair();
//    }
//
//    @ShellMethod("List Betfair EventTypes.")
//    public Table listEventTypes() {
//        TableModel model = new BeanListTableModel<Eventtype>(eventtypeRepository.findAll(),
//                new String[]{"eventtype", "name"});
//        TableBuilder tableBuilder = new TableBuilder(model);
//
//        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
//    }
//
//    //ToDo: Add eventName to query
//    @ShellMethod("List Betfair Competitions.")
//    public Table listCompetitions() {
//        TableModel model = new BeanListTableModel<Competition>(competitionRepository.findAll(),
//                new String[]{"Id", "Name", "CompetitionRegion"});
//        TableBuilder tableBuilder = new TableBuilder(model);
//
//        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
//    }
//
//    //ToDo: Add competitionName, eventName to query
//    @ShellMethod("List Betfair Events.")
//    public Table listEvents() {
//        TableModel model = new BeanListTableModel<Event>(eventRepository.findAll(),
//                new String[]{"Id", "Name"});
//        TableBuilder tableBuilder = new TableBuilder(model);
//
//        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
//    }

}