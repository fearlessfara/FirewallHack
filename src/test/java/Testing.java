import io.github.bonigarcia.wdm.WebDriverManager;
import org.faraone.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static reactor.core.publisher.Mono.when;

public class Testing {

    private WebDriver realDriver;

    @Mock
    private WebDriver webDriver;


    @BeforeClass
    public static void setupClass() {
        WebDriverManager.edgedriver().setup();
    }

    @Before
    public void setupTest() {
        realDriver = new EdgeDriver();
    }

    @After
    public void teardown() {
        if (realDriver != null) {
            realDriver.quit();
        }
    }

    @Test
    public void test() {
        // Your test code here
    }
}
