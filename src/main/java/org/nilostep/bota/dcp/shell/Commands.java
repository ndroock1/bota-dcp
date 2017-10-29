package org.nilostep.bota.dcp.shell;

import org.nilostep.bota.dcp.betfair.BetfairDataCollector;
import org.nilostep.bota.dcp.bookmakers.BookmakerDataCollector;
import org.nilostep.bota.dcp.data.domain.Competition;
import org.nilostep.bota.dcp.data.domain.Event;
import org.nilostep.bota.dcp.data.domain.Eventtype;
import org.nilostep.bota.dcp.data.repository.CompetitionRepository;
import org.nilostep.bota.dcp.data.repository.EventRepository;
import org.nilostep.bota.dcp.data.repository.EventtypeRepository;
import org.nilostep.bota.dcp.export.ExportToOddsbrowser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@ShellComponent()
public class Commands {

    @Autowired
    private EventtypeRepository eventtypeRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    public BetfairDataCollector betfairDataCollector;

    @Autowired
    public BookmakerDataCollector bookmakerDataCollector;

    @Autowired
    public ExportToOddsbrowser exportToOddsbrowser;

    @Autowired
    private Environment env;

    @ShellMethod("Version.")
    public String version() {
        return env.getProperty("bota.version") + " : " + betfairDataCollector.test();
    }

    @ShellMethod("Collect Betfair Data.")
    public int doBetfair() {
        return betfairDataCollector.collectBetfairData();
    }

    @ShellMethod("Collect Bookmakers Data.")
    public int doBookmakers() {
        return bookmakerDataCollector.collectBookmakerData();
    }

    @ShellMethod("Export Data to Firebase.")
    public int exportToOddsbrowser() {
        return exportToOddsbrowser.export(this.getClass().getResourceAsStream("/bota-6e0b33e3f1fe.json"));
    }

    @ShellMethod("This is only a test.")
    public int readFileFromJar() throws IOException {

        System.out.println(System.getProperty("java.class.path"));

        InputStream serviceAccount = this.getClass().getResourceAsStream("/bota-6e0b33e3f1fe.json");

        if (serviceAccount == null) {
            return 1;
        }
        try {
            System.out.println(serviceAccount.available());
        } catch (IOException ioe) {
            System.out.println("ERROR : " + ioe.getMessage());
        }

        return 0;
    }

    @ShellMethod("List Betfair EventTypes.")
    public Table listEventTypes() {
        TableModel model = new BeanListTableModel<Eventtype>(eventtypeRepository.findAll(),
                getFieldNames(Eventtype.class));
        TableBuilder tableBuilder = new TableBuilder(model);

        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
    }

    @ShellMethod("List Betfair Competitions.")
    public Table listCompetitions() {
        TableModel model = new BeanListTableModel<Competition>(competitionRepository.findAll(),
                getFieldNames(Competition.class));
        TableBuilder tableBuilder = new TableBuilder(model);

        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
    }

    @ShellMethod("List Betfair Events.")
    public Table listEvents() {
        TableModel model = new BeanListTableModel<Event>(eventRepository.findAll(),
                getFieldNames(Event.class));
        TableBuilder tableBuilder = new TableBuilder(model);

        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
    }

    private String[] getFieldNames(Class clazz) {
        List<String> al = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for(Field f:declaredFields){
            al.add(f.getName());
        }
        return al.toArray(new String[0]);
    }

}