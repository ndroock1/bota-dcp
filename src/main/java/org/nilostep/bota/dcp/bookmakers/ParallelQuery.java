package org.nilostep.bota.dcp.bookmakers;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    Map<Integer, QueryWorker> workers = new HashMap<>();
    ExecutorService executor;
    CountDownLatch finish;

    public ParallelQuery() {
    }

    void submitQuery(Iterable<IQuery> requests) {
        submitQuery(requests, DEFAULT_NUMBER_OF_QUERYWORKERS);
    }

    void submitQuery(Iterable<IQuery> requests, int n) {
        for (IQuery iQuery : requests) {
            reqQ.add(iQuery);
        }

        if (reqQ.size() > 0) {
            workerCount = n;
            finish = new CountDownLatch(n);
            for (int i = 0; i < n; i++) {
                workers.put(i, new QueryWorker(i, reqQ, finish, this));
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
        Queue<IQuery> tmp = qw.getReqQ();
        workers.remove(qw.getId());

        workerCount = workerCount + 1;
        QueryWorker worker = new QueryWorker(workerCount, tmp, finish, this);
        workers.put(workerCount, worker);
        executor.submit(worker.getEngine());
    }
}