import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.opera.OperaDriver;

public class Testing {
    private WebDriver driver;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.edgedriver().setup();
    }

    @Before
    public void setupTest() {
        driver = new EdgeDriver();
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void test() {
        // Your test code here
    }
}
