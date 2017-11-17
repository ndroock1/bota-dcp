package org.nilostep.bota.dcp.export;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

@Component
public class OddsBrowserUtil {

    private static Logger logger = LogManager.getLogger();

    private static final String DATABASE_URL = "https://bota-313fb.firebaseio.com/";

    public OddsBrowserUtil() {
    }

    public int export(InputStream serviceAccount) {
        int out = 0;
        try {

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);

            DatabaseReference ref = FirebaseDatabase
                    .getInstance(DATABASE_URL)
                    .getReference("testdata");

            final CountDownLatch latch = new CountDownLatch(1);
            ref.setValue("", new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {

                        System.out.println("Data could not be saved " + databaseError.getMessage());
                        latch.countDown();

                    } else {

                        System.out.println("Data saved successfully.");
                        latch.countDown();

                    }
                }
            });
            latch.await();

            ref.getDatabase().getApp().delete();
        } catch (Exception e) {
            System.out.println("Export FAIL " + e.getMessage());
        }
        return out;
    }

    public static void main(String[] args) throws Exception {
        OddsBrowserUtil oddsBrowserUtil = new OddsBrowserUtil();
        oddsBrowserUtil.export(oddsBrowserUtil.getClass().getResourceAsStream("/bota-6e0b33e3f1fe.json"));
    }
}
