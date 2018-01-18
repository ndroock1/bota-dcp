package org.nilostep.bota.dcp.bookmakers;

import io.ddavison.conductor.Browser;
import io.ddavison.conductor.Config;
import io.ddavison.conductor.Locomotive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    private List<WebElement> waitForElements1(By by) {
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
                //
                logger.info("Sleeping@ :" + by.toString());
                //
                Thread.sleep(1000); // sleep for 1 second.
            } catch (Exception x) {
                fail("Failed due to an exception during Thread.sleep!");
                x.printStackTrace();
            }
        }

        if (size == 1) System.err.println("WARN: There is only 1 " + by.toString() + " 's!");
        return driver.findElements(by);
    }


    private List<WebElement> waitForElements(By by) {

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        return driver.findElements(by);
    }




    private void getQueryResult(IQuery query, String attribute) {
        List<String> out = new ArrayList<String>();
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
        //
        logger.info("Scraping : " + query.getUrl() + query.getEventJsPre());
        //
        query.setQueryResult(out);
    }

    public void addQueryResult(IQuery query, String attribute) {
        navigateTo(query.getUrl());
        getQueryResult(query, attribute);
    }

    public void addQueryResult(Iterable<IQuery> queries, String attribute) {
        IQuery iQuery = queries.iterator().next();
        navigateTo(iQuery.getGroupUrl());

        String prejs = iQuery.getEventJsPre();
        if (prejs != null && prejs != "") {
            String[] sArr = prejs.split(";");
            for (int i = 0; i < sArr.length; i++) {
                executeJS(sArr[i], 1500);
            }
        }

        for (IQuery query : queries) {
            WebElement element = waitForElements(By.cssSelector(query.getUrlCssSelector())).get(0);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);

            List<WebElement> rows = waitForElements(By.cssSelector(query.getCssSelector()));

            List<String> out = new ArrayList<String>();
            Iterator<WebElement> itr = rows.iterator();
            while (itr.hasNext()) {
                out.add(itr.next().getAttribute("innerText"));
            }
            //
            logger.info("Scraping : " + query.getUrl());
            //
            query.setQueryResult(out);
            // back to competition page
            ((JavascriptExecutor) driver).executeScript("history.back();");
        } // End event-query
    }

//    public void addQueryResult(List<Iterable<IQuery>> querielists) {
//        for (Iterable<IQuery> queries : querielists) {
//
//            IQuery iQuery = queries.iterator().next();
//            navigateTo(iQuery.getGroupUrl());
//
//            String prejs = iQuery.getEventJsPre();
//            if (prejs != null && prejs != "") {
//                String[] sArr = prejs.split(";");
//                for (int i = 0; i < sArr.length; i++) {
//                    executeJS(sArr[i], 1500);
//                }
//            }
//
//            List<String> out = new ArrayList<String>();
//            for(IQuery query : queries) {
//                WebElement element = waitForElements(By.cssSelector(query.getUrlCssSelector())).get(0);
//                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
//
//                List<WebElement> rows = waitForElements(By.cssSelector(query.getCssSelector()));
//
//                Iterator<WebElement> itr = rows.iterator();
//                while (itr.hasNext()) {
//                    out.add(itr.next().getAttribute("innerText"));
//                }
//                //
//                logger.info("Scraping : " + query.getUrl());
//                //
//                query.setQueryResult(out);
//                // back to competition page
//                ((JavascriptExecutor) driver).executeScript("history.back();");
//            } // End event-query
//        } // End list
//    }

//    public void test(String competition, String prejs, String[] events, String selector) {
//        navigateTo(competition);
//        if (!prejs.isEmpty()) {
//            ((JavascriptExecutor) driver).executeScript(prejs);
//        }
//        for (String s : events) {
//            WebElement element = waitForElements(By.cssSelector(s)).get(0);
//            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
//            //
//            List<WebElement> rows = waitForElements(By.cssSelector(selector));
//            //
//            Iterator<WebElement> itr = rows.iterator();
//            while (itr.hasNext()) {
//                System.out.println(itr.next().getAttribute("innerText"));
//            }
//            //
//            ((JavascriptExecutor) driver).executeScript("history.back();");
//        }
//    }

    public void addQueryResult(Object o) {
        String oName = o.getClass().getSimpleName();
        switch (oName) {
            case "ConfigBC":
                addQueryResult((IQuery) o, "innerHTML");
                break;
            case "BookmakerEvent":
                addQueryResult((IQuery) o, "innerText");
                break;
            case "ArrayList":
                Iterable<IQuery> iQueries = (List) o;
                addQueryResult(iQueries, "innerText");
        }

    }


    public void quitDriver() {
        driver.quit();
    }

//    public List<WebElement> getElements(String s) {
//        By css = By.cssSelector(s);
//        WebElement element = waitForElements(css).get(0);
//        ((JavascriptExecutor)driver).executeScript("arguments[0].click();" , element);
//        return waitForElements(By.cssSelector(".bet-group-grouping"));
//    }


    public static void main(String[] args) {
        BrowserFacade browserFacade = new BrowserFacade();

        // PROTOTYPE BETSSON
//        browserFacade.navigateTo("https://sportsbook.betsson.com/en/football/england/fa-premier-league");
//        String[] arrs1 = {
//                "a[href='/en/football/england/fa-premier-league/west-ham-chelsea']",
//                "a[href='/en/football/england/fa-premier-league/tottenham-stoke']",
//                "a[href='/en/football/england/fa-premier-league/burnley-watford']",
//                "a[href='/en/football/england/fa-premier-league/newcastle-leicester']",
//                "a[href='/en/football/england/fa-premier-league/liverpool-everton']"
//        };
//        for (String s : arrs1) {
//            WebElement element = browserFacade.waitForElements(By.cssSelector(s)).get(0);
//            ((JavascriptExecutor) browserFacade.driver).executeScript("arguments[0].click();", element);
//
//            List<WebElement> rows = browserFacade.waitForElements(By.cssSelector(".bet-group-grouping .columns"));
//
//            Iterator<WebElement> itr = rows.iterator();
//            while (itr.hasNext()) {
//                System.out.println(itr.next().getAttribute("innerText"));
//            }
//            ((JavascriptExecutor) browserFacade.driver).executeScript("history.back();");
//
//        }

//        String competition = "https://sportsbook.betsson.com/en/football/england/fa-premier-league";
//        String[] events = {
//                "a[href='/en/football/england/fa-premier-league/burnley-stoke']"
//        };
//        String selector = ".bet-group-grouping .columns";
//        String prejs= "";
//        browserFacade.test(competition, prejs, events, selector);
//
//
//        competition = "http://sports.coral.co.uk/football/england/premier-league";
//        events = new String[]{
//                "a[href='http://sports.coral.co.uk/football/england/premier-league/burnley-v-stoke-8987766.html'"
//        };
//        selector = ".ev-layout";
//        prejs = "ob.pref.change_odds('DECIMAL');";
//        browserFacade.test(competition, prejs, events, selector);
//
//
//        competition = "http://sports.williamhill.com/bet/en-gb/betting/t/295/English+Premier+League.html";
//        events = new String[]{
//                "a[href='http://sports.williamhill.com/bet/en-gb/betting/e/12080224/Burnley+v+Stoke.html'"
//        };
//        selector = ".marketHolderExpanded";
//        prejs = "document.site.set_pref('price_display','DECIMAL');";
//        browserFacade.test(competition, prejs, events, selector);


        // PROTOTYPE CORAL
//        browserFacade.navigateTo("http://sports.coral.co.uk/football/england/premier-league");
//        ((JavascriptExecutor) browserFacade.driver).executeScript("ob.pref.change_odds('DECIMAL');");
//        String[] arrs2 = {
//                "a[href='http://sports.coral.co.uk/football/england/premier-league/chelsea-v-newcastle-8804436.html']",
//                "a[href='http://sports.coral.co.uk/football/england/premier-league/brighton-v-liverpool-8804438.html']",
//                "a[href='http://sports.coral.co.uk/football/england/premier-league/leicester-v-burnley-8804440.html']"
//        };
//        for (String s : arrs2) {
//            By css = By.cssSelector(s);
//            WebElement element = browserFacade.waitForElements(css).get(0);
//            ((JavascriptExecutor) browserFacade.driver).executeScript("arguments[0].click();", element);
//            List<WebElement> rows = browserFacade.waitForElements(By.cssSelector(".ev-layout"));

//            Iterator<WebElement> itr = rows.iterator();
//            while (itr.hasNext()) {
//                System.out.println(itr.next().getAttribute("innerText"));
//            }
//            ((JavascriptExecutor) browserFacade.driver).executeScript("history.back();");
//        }


        // PROTOTYPE 10BET
//        browserFacade.navigateTo("https://www.10bet.co.uk/sports/football/");
//        ((JavascriptExecutor) browserFacade.driver).executeScript("BranchWindow.showLeague(1,40253)");
//        String[][] arrs3 = {
//                {"a[href='javascript:LeagueWindow.openMasterEvent(40253,8801309)']","8801309"},
//                {"a[href='javascript:LeagueWindow.openMasterEvent(40253,8801302)']","8801302"}
//        };
//        for (String s[] : arrs3) {
//            By css = By.cssSelector(s[0]);
//            WebElement element = browserFacade.waitForElements(css).get(0);
//            ((JavascriptExecutor) browserFacade.driver).executeScript("arguments[0].click();", element);
//            List<WebElement> rows = browserFacade.waitForElements(By.cssSelector(".bet_type"));

//            Iterator<WebElement> itr = rows.iterator();
//            while (itr.hasNext()) {
//                System.out.println(itr.next().getAttribute("innerText"));
//            }
//            String js = "MasterEventWindow.closeWindow(" + s[1] + ");";
//            ((JavascriptExecutor) browserFacade.driver).executeScript(js);
//        }

        // PROTOTYPE WilliamHill
//        browserFacade.navigateTo("http://sports.williamhill.com/bet/en-gb/betting/t/295/English+Premier+League.html");
//        ((JavascriptExecutor) browserFacade.driver).executeScript("document.site.set_pref('price_display','DECIMAL');");
//
//        // http://sports.williamhill.com/bet/en-gb/betting/t/306/Dutch+Eredivisie.html
//
//
//        String[] arrs2 = {
//                "a[href='http://sports.williamhill.com/bet/en-gb/betting/e/12035774/Chelsea+v+Newcastle.html']",
//                "a[href='http://sports.williamhill.com/bet/en-gb/betting/e/12035780/Brighton+v+Liverpool.html']",
//                "a[href='http://sports.williamhill.com/bet/en-gb/betting/e/12035781/Watford+v+Tottenham.html']"
//        };
//
//        for (String s : arrs2) {
//            By css = By.cssSelector(s);
//            WebElement element = browserFacade.waitForElements(css).get(0);
//            ((JavascriptExecutor) browserFacade.driver).executeScript("arguments[0].click();", element);
//            List<WebElement> rows = browserFacade.waitForElements(By.cssSelector(".marketHolderExpanded"));
//
//            Iterator<WebElement> itr = rows.iterator();
//            while (itr.hasNext()) {
//                System.out.println(itr.next().getAttribute("innerText"));
//            }
//            ((JavascriptExecutor) browserFacade.driver).executeScript("history.back();");
//        }

        browserFacade.driver.quit();
    }
}