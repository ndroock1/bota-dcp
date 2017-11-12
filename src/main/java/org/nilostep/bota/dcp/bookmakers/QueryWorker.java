package org.nilostep.bota.dcp.bookmakers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

/**
 * Created by nilo on 18/02/2017.
 */

public class QueryWorker {

    private static Logger logger = LogManager.getLogger();

    private final int id;
    private final Queue<IQuery> reqQ;
    private final Queue<IQuery> resQ;

    private Engine engine;
    private BrowserFacade browser;
    private boolean active = true;

    final CountDownLatch countDownLatch;

    public QueryWorker(int id, Queue<IQuery> q, Queue<IQuery> r, CountDownLatch finish) {
        this.id = id;
        this.reqQ = q;
        this.resQ = r;
        this.countDownLatch = finish;
        browser = new BrowserFacade();
        engine = new Engine();
    }

    public void stopEngine() {
        active = false;
        browser.quitDriver();
        browser = null;
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

                    browser.addQueryResult(iQuery);
                    resQ.add(iQuery);

                } else {
                    active = false;
                }
            }
            countDownLatch.countDown();
        }
    }

}