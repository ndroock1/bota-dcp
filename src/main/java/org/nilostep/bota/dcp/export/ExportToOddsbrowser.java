package org.nilostep.bota.dcp.export;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Component
public class ExportToOddsbrowser {

    private static Logger logger = LogManager.getLogger();

    private static final String DATABASE_URL = "https://bota-313fb.firebaseio.com/";

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

    public int export(InputStream serviceAccount) {
        int out = 0;
        try {

            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/oddsbrowser",
                    "root",
                    "");

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM export");

            while (rs.next()) {
                int x = rs.getInt("bo_id");
                String s = rs.getString("eventName");
                double d = rs.getDouble("Odd");

//                //
//                logger.info("Export : " + x + ":" + s + ":" + d);
//                //

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

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);

            DatabaseReference ref = FirebaseDatabase
                    .getInstance(DATABASE_URL)
                    .getReference("testdata");

            final CountDownLatch latch = new CountDownLatch(1);
            ref.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            System.out.println("onDataChange: " + dataSnapshot);
                            latch.countDown();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
//                            System.out.println("onCanceled: " + databaseError);
                            latch.countDown();
                        }
                    });
            latch.await();

            //
            logger.info("Exporting : " + oddRecordMap.size());
            //

//            ref.setValueAsync(oddRecordMap);

            ref.setValue(oddRecordMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        System.out.println("Data could not be saved " + databaseError.getMessage());
                    } else {
                        System.out.println("Data saved successfully.");
                    }
                }
            });

            ref.getDatabase().getApp().delete();
        } catch (Exception e) {
            System.out.println("Export FAIL " + e.getMessage());
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
        ExportToOddsbrowser exportToOddsbrowser = new ExportToOddsbrowser();
        exportToOddsbrowser.export(exportToOddsbrowser.getClass().getResourceAsStream("/bota-6e0b33e3f1fe.json"));
    }
}