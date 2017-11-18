package org.nilostep.bota.dcp.export;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExportToOddsbrowser {

    private static Map<String, OddRecord> oddRecordMap = new ConcurrentHashMap<String, OddRecord>();

    public static class OddRecord {

        public String Bet;
        public String Odd;
        public String bookmaker;
        public String compName;
        public String competitionRegion;
        public String eventName;
        public String eventTypeName;
        public String marketType;
        public String openDate;
        public String timezone;

        public OddRecord() {
        }

        public OddRecord(
                String Bet,
                String Odd,
                String bookmaker,
                String compName,
                String competitionRegion,
                String eventName,
                String eventTypeName,
                String marketType,
                String openDate,
                String timezone) {
            this.Bet = Bet;
            this.Odd = Odd;
            this.bookmaker = bookmaker;
            this.compName = compName;
            this.competitionRegion = competitionRegion;
            this.eventName = eventName;
            this.eventTypeName = eventTypeName;
            this.marketType = marketType;
            this.openDate = openDate;
            this.timezone = timezone;
        }
    }

    public ExportToOddsbrowser() {
    }

    public int export() {
        int out = 0;
        try {

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/oddsbrowser",
                    "root",
                    "");

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM export");

            while (rs.next()) {
                oddRecordMap.put(rs.getString("bo_id"),
                        new OddRecord(
                                rs.getString("Bet"),
                                rs.getString("Odd"),
                                rs.getString("bookmaker"),
                                rs.getString("compName"),
                                rs.getString("competitionRegion"),
                                rs.getString("eventName"),
                                rs.getString("eventTypeName"),
                                rs.getString("marketType"),
                                rs.getString("openDate"),
                                rs.getString("timezone")
                        )
                );
            }

            Firebase fb = new Firebase();
            fb.update(oddRecordMap, "testdata");
            fb.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }
}