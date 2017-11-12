package org.nilostep.bota.dcp.bookmakers;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nilostep.bota.dcp.data.domain.BookmakerEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by nilo on 18/02/2017.
 */
public class ParallelQuery {

    private static Logger logger = LogManager.getLogger();

    Queue<IQuery> reqQ = new ConcurrentLinkedQueue<>();
    Queue<IQuery> resQ = new ConcurrentLinkedQueue<>();
    List<QueryWorker> workers = new ArrayList<QueryWorker>();
    ExecutorService executor;

    public ParallelQuery() {
    }

    void submitQuery(Iterable<BookmakerEvent> requests) {

        for (BookmakerEvent bookmakerEvent : requests) {
            if (bookmakerEvent.getHasPayload() == 0) {
                reqQ.add(bookmakerEvent);
            }
        }

        if (reqQ.size() > 0) {

            int n = 4;
            final CountDownLatch finish = new CountDownLatch(n);
            for (int i = 0; i < n; i++) {
                workers.add(new QueryWorker(i, reqQ, resQ, finish));
            }

            executor = Executors.newFixedThreadPool(n);
            for (QueryWorker worker : workers) {
                executor.submit(worker.getEngine());
            }

            try {
                finish.await();
                for (QueryWorker worker : workers) {
                    worker.stopEngine();
                }
                executor.shutdown();

            } catch (InterruptedException ie) {
                logger.info(">> InterruptedException @ ");
            }

        }
    }
}