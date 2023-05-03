package com.kalita;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.kalita.ConfigProperties.ConfigKeys.DRIVER_LOCATION;
import static com.kalita.ConfigProperties.ConfigKeys.TEST_PAGE;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class BaseTest {

    protected WebDriver driver;
    private final static String CHROME_DRIVER_PROPERTY_NAME = "webdriver.chrome.driver";

    @BeforeEach
    public void setupBeforeEach() {
        System.setProperty(CHROME_DRIVER_PROPERTY_NAME, ConfigProperties.getProperty(DRIVER_LOCATION.getKey()));
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(ConfigProperties.getProperty(TEST_PAGE.getKey()));
    }

    @AfterEach
    public void setUpAfterEach() {
        driver.quit();
    }

    protected void testWithRepeat(TestInfo testInfo, Supplier<List<TestResult>> testResult, Integer attempts) {
        int attempt = 1;
        boolean success = false;
        while (!success && attempt <= attempts) {
            try {
                log.info("Starting test {}. Attempt {}", testInfo.getDisplayName(), attempt);
                List<TestResult> testResults = testResult.get();
                success = testResults.stream().map(TestResult::success).reduce(Boolean::logicalAnd).orElse(false);
                if (!success) {
                    testResults.forEach(result -> log.info("Test: {}. Success: {}", result.message(), result.success()));
                    log.warn("Attempt {} for test {} is failed", attempt, testInfo.getDisplayName());
                    attempt++;
                    prepareForRestart();
                }
            } catch (Throwable e) {
                log.warn("Attempt {} for test {} is failed: {}", attempt, testInfo.getDisplayName(), e.getMessage());
                attempt++;
                prepareForRestart();
            }
        }
        logResults(testInfo, attempt, success);
        assertTrue(success);
    }

    private void prepareForRestart() {
        this.setUpAfterEach();
        this.setupBeforeEach();
    }

    private void logResults(TestInfo testInfo, int attempt, boolean success) {
        if (success) {
            log.info("Test {} passed on {} attempt", testInfo.getDisplayName(), attempt);
        } else {
            log.info("Attempts for test {} are over", testInfo.getDisplayName());
        }
    }

    record TestResult(boolean success, String message) {
    }
}