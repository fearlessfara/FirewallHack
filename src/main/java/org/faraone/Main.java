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

import static org.faraone.Constants.CHROME;
import static org.faraone.Constants.EDGE;
import static org.faraone.Constants.ELEMENT_PASSWORD;
import static org.faraone.Constants.ELEMENT_SUBMIT;
import static org.faraone.Constants.ELEMENT_USERNAME;
import static org.faraone.Constants.FIREFOX;
import static org.faraone.Constants.FIREWALL_IP;
import static org.faraone.Constants.FIREWALL_PORT;
import static org.faraone.Constants.FIREWALL_TIMEOUT;
import static org.faraone.Constants.FIREWALL_URL;
import static org.faraone.Constants.IE;
import static org.faraone.Constants.OPERA;
import static org.faraone.Constants.PROBE_IP;
import static org.faraone.Constants.PROBE_PORT;
import static org.faraone.Constants.PROBE_TIMEOUT;
import static org.faraone.Constants.WIFI_SSID;
import static org.faraone.Constants.WINDOWS;

public class Main {
    static Properties systemProperties = null;
    static WebDriver driver;
    static Boolean headlessMode = true;
    static int exitCode = 1;

    public static void main(String[] args) {
        systemProperties = System.getProperties();
        if (args.length != 3) {
            System.out.println("Error while parsing arguments. Arguments must be 'browser' 'username' 'password'.\n" +
                    "Check github documentation at https://github.com/fearlessfara/FirewallHack for more references.\n");
            System.exit(exitCode = 1);
        }
        String browser = args[0];
        String username = args[1];
        String password = args[2];

        Preconditions.checkArgument(StringUtils.isNotBlank(browser), "Empty browser name.");
        Preconditions.checkArgument(StringUtils.isNotBlank(username), "Empty username.");
        Preconditions.checkArgument(StringUtils.isNotBlank(password), "Empty password.");

        driver = getDriver(browser);

        if (checkConnection(PROBE_IP, PROBE_PORT, PROBE_TIMEOUT)) {
            System.exit(exitCode = 0);
        }
        if (!checkConnection(FIREWALL_IP, FIREWALL_PORT, FIREWALL_TIMEOUT)) {
            connectToWiFi();
        }
        if (checkConnection(PROBE_IP, PROBE_PORT, PROBE_TIMEOUT)) {
            System.exit(exitCode = 0);
        } else {
            performLogin(username, password);
        }
        System.exit(exitCode = 0);
    }

    public static WebDriver getDriver(String browser) {
        WebDriver driver;
        browser = browser.trim().toLowerCase();
        switch (browser) {
            case FIREFOX: {
                FirefoxOptions options = new FirefoxOptions();
                options.setHeadless(headlessMode);
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(options);
                break;
            }
            case CHROME: {
                ChromeOptions options = new ChromeOptions();
                options.setHeadless(headlessMode);
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(options);
                break;
            }
            case EDGE: {
                EdgeOptions options = new EdgeOptions();
                options.setHeadless(headlessMode);
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver(options);
                break;
            }
            case OPERA: {
                OperaOptions options = new OperaOptions();
                //headless mode is not available in opera browser
                WebDriverManager.operadriver().setup();
                driver = new OperaDriver(options);
                break;
            }
            case IE: {
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
        driver.get(FIREWALL_URL);
        WebElement usernameElement = driver.findElement(By.name(ELEMENT_USERNAME));
        usernameElement.sendKeys(username);
        WebElement passwordElement = driver.findElement(By.name(ELEMENT_PASSWORD));
        passwordElement.sendKeys(password);
        WebElement loginButton = driver.findElement(By.name(ELEMENT_SUBMIT));
        loginButton.click();
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            System.out.println("error while trying to sleep...");
            System.exit(exitCode = 1);
        }
        driver.quit();
    }

    public static void connectToWiFi() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (systemProperties.getProperty("sun.desktop").equals(WINDOWS)) {
            processBuilder.command("cmd.exe", "/c", "netsh wlan connect name=\"" + WIFI_SSID + "\"");
        } else {
            System.out.println("Cannot automatically connect to the Wi-Fi on this OS.\n" +
                    "Please connect to the wireless network and then launch this programme again.\n");
        }

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
                System.out.println("Error while connecting to the wireless network");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(exitCode = 1);
        }
    }

    public static boolean checkConnection(String hostname, Integer port, Integer timeout) {
        try (Socket s = new Socket()) {
            s.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress(hostname, port);
            s.connect(sa, timeout);
            s.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
