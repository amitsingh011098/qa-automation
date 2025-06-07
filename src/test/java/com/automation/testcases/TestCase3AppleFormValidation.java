package com.automation.testcases;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import org.testng.annotations.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestCase3AppleFormValidation {

    WebDriver driver;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    @Test
    public void testSubmitButtonValidation() {
        try {
            driver.get("https://account.apple.com/account#");
            Thread.sleep(15000); // Let the page fully render

            WebElement submitBtn = driver.findElement(By.xpath("//button[@form='create']"));
            submitBtn.click();

            Thread.sleep(10000); // Wait for validation messages
            takeScreenshot("required_fields_error");

            System.out.println("✔ Submit button clicked. Screenshot captured.");
        } catch (Exception e) {
            System.out.println("❌ Failed to locate or click submit button: " + e.getMessage());
        }
    }


    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    public void takeScreenshot(String baseName) {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String path = "screenshots/" + baseName + "_" + timestamp + ".png";
        try {
            Files.copy(src.toPath(), Paths.get(path));
        } catch (IOException e) {
            System.out.println("⚠ Screenshot failed to save: " + path);
        }
    }

    public void logResult(String result) {
        try (FileWriter writer = new FileWriter("testResults.txt", true)) {
            writer.write(result + "\n");
        } catch (IOException e) {
            System.out.println("⚠ Could not write to results file");
        }
    }
}
