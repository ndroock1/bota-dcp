package org.nilostep.bota.dcp.bookmakers;

import io.ddavison.conductor.Browser;
import io.ddavison.conductor.Config;
import io.ddavison.conductor.Locomotive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

//@Config(
//        url = "",
//        browser = Browser.PHANTOMJS,
//        hub = ""
//)

@Config(browser = Browser.CHROME, url = "http://ddavison.io/tests/getting-started-with-selenium.htm")
public class BrowserFacade extends Locomotive {

    private static Logger logger = LogManager.getLogger();

    public BrowserFacade() {
    }

    public static void main(String[] args) throws InterruptedException {
        BrowserFacade bf = new BrowserFacade();

        String url = "https://sportsbook.betsson.com/en/football/england/fa-premier-league";
        String css = "body > div:nth-child(3) > section > div > section > div > div > div > div.main-outer-view.theme-default > div.row.main-sportsbook-container > section > div > section > div > div > div > div:nth-child(1) > bssn-multiple-events-table > div";

        System.out.println(bf.getRawData(url, css));
        System.out.println(bf.getText(url, css));

        bf.driver.quit();
    }

    public String getRawData(String url, String css) {
        navigateTo(url);
        return waitForElement(By.cssSelector(css)).getAttribute("innerHTML");
    }

    public String getText(String url, String css) {
        navigateTo(url);
        return waitForElement(By.cssSelector(css)).getAttribute("innerText");
    }

    public void quitDriver() {
        driver.quit();
    }
}