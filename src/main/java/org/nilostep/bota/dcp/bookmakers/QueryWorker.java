package org.nilostep.bota.dcp.bookmakers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nilostep.bota.dcp.data.domain.BookmakerEvent;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 * Created by nilo on 18/02/2017.
 */

public class QueryWorker {

    private static Logger logger = LogManager.getLogger();

    private final int id;
    private final Queue<IQuery> reqQ;
    final CountDownLatch countDownLatch;
    ParallelQuery pq;

    private Engine engine;
    private BrowserFacade browser;
    private boolean active = true;


    public QueryWorker(
            int id,
            Queue<IQuery> q,
            CountDownLatch finish,
            ParallelQuery pq) {
        this.id = id;
        this.reqQ = q;
        this.countDownLatch = finish;
        this.pq = pq;
        browser = new BrowserFacade();
        engine = new Engine();
    }

    public void stopEngine() {
        active = false;
        if (browser != null) {
            browser.quitDriver();
            browser = null;
        }
    }

    public int getId() {
        return this.id;
    }

    public Queue<IQuery> getReqQ() {
        return reqQ;
    }

    public Engine getEngine() {
        return engine;
    }

    class Engine implements Runnable {

        Engine() {
        }

        public void run() {
            while (active) {
                IQuery iQuery = reqQ.poll();
                if (iQuery != null) {

                    try {
                        if (iQuery instanceof BookmakerEvent) {
                            browser.addQueryResult(iQuery);
                        } else {
                            browser.addQueryResult(iQuery, true);
                        }

                    } catch (Throwable rte) {
                        //
                        logger.info(rte.getMessage());
                        logger.info("CRASHING... QueryWorker: " + id);
                        //
                        reqQ.add(iQuery);
                        QueryWorker.this.stopEngine();
                        //
                        active = false;
                        pq.restartWorker(QueryWorker.this);
                    }

                } else {
                    //
                    logger.info("Stopping QueryWorker: " + id);
                    //
                    active = false;
                    countDownLatch.countDown();
                }
            }
        }
    }
}