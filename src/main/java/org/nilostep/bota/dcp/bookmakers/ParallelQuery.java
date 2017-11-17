package org.nilostep.bota.dcp.bookmakers;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nilostep.bota.dcp.data.domain.BookmakerEvent;
import org.nilostep.bota.dcp.data.domain.ConfigBC;

import java.util.HashMap;
import java.util.Map;
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

    private final int DEFAULT_NUMBER_OF_QUERYWORKERS = 2;
    private int workerCount;

    Queue<IQuery> reqQ = new ConcurrentLinkedQueue<>();
    Queue<IQuery> resQ = new ConcurrentLinkedQueue<>();
    Map<Integer, QueryWorker> workers = new HashMap<>();
    ExecutorService executor;
    CountDownLatch finish;

    public ParallelQuery() {
    }

    void submitQuery(Iterable<ConfigBC> requests) {
        for (ConfigBC configBC : requests) {
            if (configBC.getSelected() == 1) {
                if (configBC.getHasPayload() == 0) {
                    reqQ.add(configBC);
                }
            }
        }
        submitQuery(DEFAULT_NUMBER_OF_QUERYWORKERS);
    }

    void submitQuery(Iterable<BookmakerEvent> requests, int n) {
        for (BookmakerEvent bookmakerEvent : requests) {
            if (bookmakerEvent.getHasPayload() == 0) {
                reqQ.add(bookmakerEvent);
            }
        }
        submitQuery(n);
    }

    void submitQuery(int n) {
        workerCount = n;

        if (reqQ.size() > 0) {
            finish = new CountDownLatch(n);
            for (int i = 0; i < n; i++) {
                workers.put(i, new QueryWorker(i, reqQ, resQ, finish, this));
            }

            executor = Executors.newFixedThreadPool(n);
            for (QueryWorker worker : workers.values()) {
                executor.submit(worker.getEngine());
            }

            try {
                finish.await();
                for (QueryWorker worker : workers.values()) {
                    worker.stopEngine();
                }
                executor.shutdown();

            } catch (InterruptedException ie) {
                logger.info(">> InterruptedException @ ");
            }

        }
    }

    void restartWorker(QueryWorker qw) {
        //
        logger.info("Restarting... QueryWorker: " + qw.getId());
        //
        workers.remove(qw.getId());
        workerCount = workerCount + 1;
        QueryWorker worker = new QueryWorker(workerCount, reqQ, resQ, finish, this);
        workers.put(workerCount, worker);
        executor.submit(worker.getEngine());
    }
}