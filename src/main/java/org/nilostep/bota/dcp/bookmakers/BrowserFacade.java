package org.nilostep.bota.dcp.bookmakers;

import io.ddavison.conductor.Browser;
import io.ddavison.conductor.Config;
import io.ddavison.conductor.Locomotive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.fail;


@Config(browser = Browser.CHROME)
public class BrowserFacade extends Locomotive {

    private static Logger logger = LogManager.getLogger();

    public BrowserFacade() {
        this.MAX_ATTEMPTS = 60;
        this.MAX_TIMEOUT = 30;
    }

    private void executeJS(String js, int wait) {
        //
        logger.info("executeJS : " + js);
        //

        try {
            Thread.sleep(wait);
            ((JavascriptExecutor) driver).executeScript(js);
            Thread.sleep(wait);
        } catch (InterruptedException inte) {
            System.out.println("Thread interrupted while executing JS : " + inte.getMessage());
        }
    }

    private void executeJS(String js) {
        executeJS(js, 0);
    }

    private List<WebElement> waitForElements(By by) {
        int attempts = 0;
        int size = driver.findElements(by).size();

        while (size == 0) {
            size = driver.findElements(by).size();

//            if (attempts == MAX_ATTEMPTS) fail(String.format("Could not find %s after %d seconds", by.toString(), MAX_ATTEMPTS));
            if (attempts == MAX_ATTEMPTS) {
                throw new TooManyAttemptsException();
            }
            attempts++;

            try {
                Thread.sleep(1000); // sleep for 1 second.
            } catch (Exception x) {
                fail("Failed due to an exception during Thread.sleep!");
                x.printStackTrace();
            }
        }

        if (size == 1) System.err.println("WARN: There is only 1 " + by.toString() + " 's!");
        return driver.findElements(by);
    }

    private List<String> getQueryResult(IQuery query, String attribute) {
        List<String> out = new ArrayList<String>();

        navigateTo(query.getUrl());

        //
        logger.info("Scraping : " + query.getUrl() + query.getEventJsPre());
        //

        if (query.getEventJsPre() != null && query.getEventJsPre() != "") {
            String[] sArr = query.getEventJsPre().split(";");
            for (int i = 0; i < sArr.length; i++) {
                executeJS(sArr[i], 1500);
            }
        }

        List<WebElement> rows = waitForElements(By.cssSelector(query.getCssSelector()));
        Iterator<WebElement> itr = rows.iterator();
        while (itr.hasNext()) {
            out.add(itr.next().getAttribute(attribute));
        }
        return out;
    }

    public void addQueryResult(IQuery query) {
        query.setQueryResult(getQueryResult(query, "innerText"));
    }

    public void addQueryResult(IQuery query, boolean raw) {
        query.setQueryResult(getQueryResult(query, "innerHTML"));
    }

    public void quitDriver() {
        driver.quit();
    }
}