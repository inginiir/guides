package com.kalita;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class GooglePage {

    private final WebDriver driver;
    public GooglePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = "#APjFqb")
    private WebElement searchInput;

    public BaseTest.TestResult search(String request) {
        searchInput.sendKeys(request);
        searchInput.sendKeys(Keys.ENTER);
        sleep(2000);
        WebElement description = driver.findElement(By.cssSelector("#kp-wp-tab-overview"));
        boolean result = description.getText().contains("Selenium WebDriver");
        return new BaseTest.TestResult(result, "Search success");
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
