package org.nilostep.bota.dcp.shell;

import info.debatty.java.stringsimilarity.*;
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
        // #1
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

        // #2
        String sField = "Excelsior v Roda JC Kerkrade";
        String[] sArr = sField.split("\\sv\\s");
        for (int i = 0; i < sArr.length; i++) {
            System.out.println(" > " + sArr[i]);
        }

        sField = "String 1";
        String sField1 = "Next";
        System.out.println(sField.concat(";").concat(sField1));

        printSimilarities("Manchester United v Brighton", "Man Utd v Brighton");
        printSimilarities("Wolfsburg v Freiburg", "Augsburg v Wolfsburg");
        printSimilarities("Mainz v FC Koln", "Mainz v Cologne");
        printSimilarities("Hertha BSC Berlin v Borussion Monchengladbach", "Hertha Berlin v Mgladbach");
        printSimilarities("Malaga v Dep La Coruna", "Malaga v Deportivo");
        printSimilarities("Valencia v Barcelona", "Leganes v Barcelona");

        System.out.println(isSimilar("Manchester United v Brighton", "Man Utd v Brighton"));
        System.out.println(isSimilar("Wolfsburg v Freiburg", "Augsburg v Wolfsburg"));
        System.out.println(isSimilar("Mainz v FC Koln", "Mainz v Cologne"));
        System.out.println(isSimilar("Hertha BSC Berlin v Borussion Monchengladbach", "Hertha Berlin v Mgladbach"));
        System.out.println(isSimilar("Malaga v Dep La Coruna", "Malaga v Deportivo"));
        System.out.println(isSimilar("Valencia v Barcelona", "Leganes v Barcelona"));

        return 0;
    }

    private void printSimilarities(String sBCE, String sCE) {
        String[] sNamesBCE = sBCE.split(" v ");
        String[] sNamesCE = sCE.split(" v ");

        System.out.println(sBCE);
        System.out.println(sCE);
        System.out.println(">>");

        Cosine cosine = new Cosine();
        System.out.println("Cosine");
        System.out.println(cosine.similarity(sNamesBCE[0], sNamesCE[0]));
        System.out.println(cosine.similarity(sNamesBCE[1], sNamesCE[1]));

        JaroWinkler jaroWinkler = new JaroWinkler();
        System.out.println("Jaro-Winkler");
        System.out.println(jaroWinkler.similarity(sNamesBCE[0], sNamesCE[0]));
        System.out.println(jaroWinkler.similarity(sNamesBCE[1], sNamesCE[1]));

        System.out.println("Jaro-Winkler - CROSS");
        System.out.println(jaroWinkler.similarity(sNamesBCE[0], sNamesCE[1]));
        System.out.println(jaroWinkler.similarity(sNamesBCE[1], sNamesCE[0]));

        SorensenDice sorensenDice = new SorensenDice();
        System.out.println("Sorensen-Dice");
        System.out.println(sorensenDice.similarity(sNamesBCE[0], sNamesCE[0]));
        System.out.println(sorensenDice.similarity(sNamesBCE[1], sNamesCE[1]));

        NormalizedLevenshtein normalizedLevenshtein = new NormalizedLevenshtein();
        System.out.println("Normalized-Levenshtein");
        System.out.println(normalizedLevenshtein.similarity(sNamesBCE[0], sNamesCE[0]));
        System.out.println(normalizedLevenshtein.similarity(sNamesBCE[1], sNamesCE[1]));

        MetricLCS metricLCS = new MetricLCS();
        System.out.println("Metric Longest Common Subsequence");
        System.out.println(1 - metricLCS.distance(sNamesBCE[0], sNamesCE[0]));
        System.out.println(1 - metricLCS.distance(sNamesBCE[1], sNamesCE[1]));

        Levenshtein levenshtein = new Levenshtein();
        System.out.println("Levenshtein");
        System.out.println(levenshtein.distance(sNamesBCE[0], sNamesCE[0]));
        System.out.println(levenshtein.distance(sNamesBCE[1], sNamesCE[1]));

    }

    private boolean isSimilar(String sBCE, String sCE) {
        System.out.println(sBCE);
        System.out.println(sCE);
        System.out.println(">>");

        String[] sNamesBCE = sBCE.split(" v ");
        String[] sNamesCE = sCE.split(" v ");

        JaroWinkler jaroWinkler = new JaroWinkler();
        if (jaroWinkler.similarity(sNamesBCE[0], sNamesCE[0]) < 0.45d ||
                jaroWinkler.similarity(sNamesBCE[1], sNamesCE[1]) < 0.45d) {

            // Rejected :
            Cosine cosine = new Cosine();
            return cosine.similarity(sNamesBCE[0], sNamesCE[0]) > 0.45d &&
                    cosine.similarity(sNamesBCE[1], sNamesCE[1]) > 0.45d;

            // Accepted :
        } else {
            // Check if acccept is correct
            double d01 = jaroWinkler.similarity(sNamesBCE[0], sNamesCE[1]);
            double d10 = jaroWinkler.similarity(sNamesBCE[1], sNamesCE[0]);
            if (d01 > 0.95d || d10 > 0.95d) {

                return jaroWinkler.similarity(sNamesBCE[0], sNamesCE[0]) > 0.9d ||
                        jaroWinkler.similarity(sNamesBCE[1], sNamesCE[1]) > 0.9d;
            } else {
                return true;
            }
        }
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