package org.faraone;

import com.google.common.base.Preconditions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Properties;

public class Main {
    final static String elementForUsername = "fw_username";
    final static String elementForPassword = "fw_password";
    final static String elementForSubmit = "submit";
    final static String link = "https://fw.elis.org:4100/logon.shtml";
    static Properties systemProperties = null;
    static WebDriver driver;
    static Boolean headlessMode = true;

    public static void main(String[] args) {
        systemProperties = System.getProperties();
        System.out.println(systemProperties.getProperty("sun.desktop"));

        String browser = args[0];
        String username = args[1];
        String password = args[2];

        Preconditions.checkArgument(StringUtils.isBlank(browser), "Empty browser name.");
        Preconditions.checkArgument(StringUtils.isBlank(username), "Empty username.");
        Preconditions.checkArgument(StringUtils.isBlank(password), "Empty password.");

        driver = getDriver(browser);

        if (checkConnection()) {
            System.exit(0);
        }
        if (!isFireWallOnLine()) {
            connectToWiFi();
        }
        if (checkConnection()) {
            System.exit(0);
        } else {
            performLogin(username, password);
        }
        System.exit(0);
    }

    public static WebDriver getDriver(String browser) {
        WebDriver driver;
        browser = browser.trim().toLowerCase();
        switch (browser) {
            case "firefox": {
                FirefoxOptions options = new FirefoxOptions();
                options.setHeadless(headlessMode);
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(options);
                break;
            }
            case "chrome": {
                ChromeOptions options = new ChromeOptions();
                options.setHeadless(headlessMode);
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(options);
                break;
            }
            case "edge": {
                EdgeOptions options = new EdgeOptions();
                options.setHeadless(headlessMode);
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver(options);
                break;
            }
            case "opera": {
                OperaOptions options = new OperaOptions();
                //headless mode is not available in opera browser
                WebDriverManager.operadriver().setup();
                driver = new OperaDriver(options);
                break;
            }
            case "ie": {
                InternetExplorerOptions options = new InternetExplorerOptions();
                //headless mode is not available in internet explorer browser
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver();
                break;
            }
            default: {
                throw new RuntimeException("Syntax error, browser not recognized");
            }
        }
        return driver;
    }

    public static void performLogin(String username, String password) {
        driver.get(link);
        WebElement usernameElement = driver.findElement(By.name(elementForUsername));
        usernameElement.sendKeys(username);
        WebElement passwordElement = driver.findElement(By.name(elementForPassword));
        passwordElement.sendKeys(password);
        WebElement loginButton = driver.findElement(By.name(elementForSubmit));
        loginButton.click();
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            System.out.println("error while trying to sleep...");
        }
        driver.quit();
    }

    public static void connectToWiFi() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe", "/c", "netsh wlan connect name=\"ELIS.org - studenti e ospiti\"");
        try {

            Process process = processBuilder.start();
            Thread.sleep(2000);

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
            } else {
                System.out.println("error while connecting");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkConnection() {
        try (Socket s = new Socket()) {
            s.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress("8.8.8.8", 80);
            s.connect(sa, 2000);
            s.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isFireWallOnLine() {
        try (Socket s = new Socket()) {
            s.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress("172.16.16.1", 4100);
            s.connect(sa, 1000);
            s.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
