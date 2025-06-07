package com.automation.testcases;

import com.automation.components.Components_Amazon;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TestCase1SearchVerification {

    WebDriver driver;
    Components_Amazon amazon;
    boolean testPassed = true;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        amazon = new Components_Amazon(driver);
    }

    @Test
    public void testSearchAndValidateProducts() {
        try {
            System.out.println("▶ Step 1: Opening Amazon and searching for 'Laptop'");
            amazon.searchProduct("Laptop");

            // Unique timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            amazon.takeScreenshot("search_results_" + timestamp + ".png");

            // Step 2: Validate that search results appear
            List<WebElement> results = amazon.getSearchResults();
            Assert.assertTrue(results.size() > 0, "❌ No search results found.");
            System.out.println("✔ Search results found: " + results.size());

            // ✅ Step 3: Collect first 3 product links before navigation
            String[] productLinks = new String[3];
            for (int i = 0; i < 3 && i < results.size(); i++) {
                try {
                    // ✅ Get anchor tag that wraps the title
                    WebElement linkElement = results.get(i).findElement(By.xpath(".//a[@class='a-link-normal s-no-outline']"));
                    String href = linkElement.getAttribute("href");

                    // ✅ Amazon sometimes gives relative URL — convert to full
                    if (!href.startsWith("http")) {
                        href = "https://www.amazon.in" + href;
                    }

                    productLinks[i] = href;
                    System.out.println("✔ Product " + (i + 1) + " URL: " + href);

                } catch (Exception e) {
                    System.out.println("❌ Unable to extract link for product " + (i + 1) + ": " + e.getMessage());
                    testPassed = false;
                }
            }

            

            // ✅ Step 4: Visit each link and validate product detail
            for (int i = 0; i < productLinks.length; i++) {
                String url = productLinks[i];
                if (url == null || url.isEmpty()) continue;

                System.out.println("\n▶ Navigating to product " + (i + 1));
                try {
                    driver.navigate().to(url);
                    Thread.sleep(3000); // wait for page load

                    System.out.println("▶ Validating product detail page " + (i + 1));

                    String title = driver.findElement(By.id("productTitle")).getText().trim();
                    Assert.assertFalse(title.isEmpty(), "❌ Title missing");

                    String price = driver.findElement(
                    	    By.xpath("//span[@class='a-price aok-align-center reinventPricePriceToPayMargin priceToPay']")
                    	).getText().trim();

                    	Assert.assertFalse(price.isEmpty(), "❌ Price missing");
                    	System.out.println("✔ Price: " + price);


                    String availability = driver.findElement(By.id("availability")).getText().trim();
                    Assert.assertFalse(availability.isEmpty(), "❌ Availability missing");

                    System.out.println("✔ Title: " + title);
                    System.out.println("✔ Price: " + price);
                    System.out.println("✔ Availability: " + availability);

                    // Screenshot of product page
                    String detailTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    amazon.takeScreenshot("product" + (i + 1) + "_details_" + detailTimestamp + ".png");

                } catch (Exception e) {
                    System.out.println("❌ Error validating product " + (i + 1) + ": " + e.getMessage());
                    testPassed = false;
                }
            }

            // ✅ Final result logging
            if (testPassed) {
                System.out.println("✅ Test Case 1: Passed");
                amazon.logResultToFile("TestCase1,Passed");
            } else {
                System.out.println("❌ Test Case 1: Failed");
                amazon.logResultToFile("TestCase1,Failed");
            }

        } catch (Exception e) {
            System.out.println("❌ Unexpected Error: " + e.getMessage());
            amazon.logResultToFile("TestCase1,Failed");
        }
    }


    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
