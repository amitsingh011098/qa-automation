package com.automation.components;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import org.openqa.selenium.*;



public class Components_Amazon {

    WebDriver driver;

    public Components_Amazon(WebDriver driver) {
        this.driver = driver;
    }

    public void searchProduct(String product) {
        driver.get("https://www.amazon.in/");
        driver.findElement(By.id("twotabsearchtextbox")).sendKeys(product);
        driver.findElement(By.id("nav-search-submit-button")).click();
    }

    public List<WebElement> getSearchResults() {
        return driver.findElements(By.cssSelector("div.s-main-slot div[data-component-type='s-search-result']"));
    }

    public void validateResult(WebElement result) {
        try {
            String title = result.findElement(By.cssSelector("h2")).getText();
            String price = result.findElement(By.cssSelector("span.a-price")).getText();
            String rating = result.findElement(By.cssSelector("span.a-icon-alt")).getText();
            System.out.println("✔ Title: " + title);
            System.out.println("✔ Price: " + price);
            System.out.println("✔ Rating: " + rating);
        } catch (Exception e) {
            System.out.println("❌ Missing title/price/rating in result.");
        }
    }

    public void takeScreenshot(String baseName) {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        String dirPath = "screenshots";
        String fullPath = dirPath + "/" + baseName + ".png";  // Overwrite mode

        try {
            Files.createDirectories(Paths.get(dirPath)); // Ensures folder exists
            Files.copy(src.toPath(), Paths.get(fullPath), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✔ Screenshot saved to: " + fullPath);
        } catch (IOException e) {
            System.out.println("⚠ Screenshot failed to save: " + fullPath);
            e.printStackTrace();
        }
    }

    




    public void logResultToFile(String result) {
        try (FileWriter writer = new FileWriter("testResults.txt", true)) {
            writer.write(result + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
