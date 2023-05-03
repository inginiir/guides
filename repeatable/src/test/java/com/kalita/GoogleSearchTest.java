package com.kalita;

import org.junit.jupiter.api.*;

import java.util.List;

public class GoogleSearchTest extends BaseTest {

    private GooglePage googlePage;

    @BeforeEach
    void setUp() {
        googlePage = new GooglePage(driver);
    }

    @Test
    @DisplayName("Searching info about selenium")
    void searchTest(TestInfo testInfo) {
        testWithRepeat(testInfo, () -> {
                    TestResult result = googlePage.search("Selenium");
                    return List.of(result);
                },
                3);
    }
}
