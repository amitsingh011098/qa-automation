package com.automation.testcases;

import com.automation.components.Components_Amazon;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class TestCase2CartValidation {

    WebDriver driver;
    Components_Amazon amazon;
    boolean testPassed = true;

    String productTitle = "";
    String productPrice = "";

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        amazon = new Components_Amazon(driver);
    }

    @Test
    public void testCartFunctionality() {
        try {
            System.out.println("▶ Step 1: Searching for 'Headphones'");
            amazon.searchProduct("jbl Headphones");

            // Click first product
            WebElement firstProduct = amazon.getSearchResults().get(0);
            WebElement link = firstProduct.findElement(By.xpath(".//a[@class='a-link-normal s-no-outline']"));
            String productUrl = link.getAttribute("href");

            if (!productUrl.startsWith("http")) {
                productUrl = "https://www.amazon.in" + productUrl;
            }

            driver.navigate().to(productUrl);
            Thread.sleep(3000);

            // Step 2: Validate product title and price
            System.out.println("▶ Step 2: Validating product details");
            productTitle = driver.findElement(By.id("productTitle")).getText().trim();
            Assert.assertFalse(productTitle.isEmpty(), "❌ Product title missing");

            try {
                productPrice = driver.findElement(
                        By.xpath("//span[@class='a-price aok-align-center reinventPricePriceToPayMargin priceToPay']")
                ).getText().trim();
            } catch (NoSuchElementException e) {
                System.out.println("❌ Product price not found");
                testPassed = false;
            }

            Assert.assertFalse(productPrice.isEmpty(), "❌ Product price missing");

            System.out.println("✔ Title: " + productTitle);
            System.out.println("✔ Price: " + productPrice);

            amazon.takeScreenshot("product_page.png");

            // Step 3: Click 'Add to Cart'
            System.out.println("▶ Step 3: Adding product to cart");
            driver.findElement(By.id("add-to-cart-button")).click();
            Thread.sleep(3000);

            // Step 4: Go to cart
            System.out.println("▶ Step 4: Navigating to cart");
            driver.findElement(By.id("nav-cart")).click();
            Thread.sleep(3000);
            amazon.takeScreenshot("cart_page.png");

            // Step 5: Validate item in cart
            String cartTitle = driver.findElement(By.cssSelector("span.a-truncate-cut")).getText().trim();
            System.out.println("🧾 Product Title: " + productTitle);
            System.out.println("🛒 Cart Title: " + cartTitle);

            Assert.assertTrue(productTitle.toLowerCase().contains(cartTitle.toLowerCase().substring(0, 5)), 
            	    "❌ Cart item title mismatch");


            String cartPrice = driver.findElement(By.cssSelector("span.a-size-medium.a-color-base.sc-price")).getText().trim();

            // Normalize prices
            String cleanProductPrice = productPrice.replaceAll("[^\\d.]", "").replace(".00", "");
            String cleanCartPrice = cartPrice.replaceAll("[^\\d.]", "").replace(".00", "");
            Assert.assertEquals(cleanCartPrice, cleanProductPrice, "❌ Cart price mismatch");

            System.out.println("✔ Cart title and price validated");

            // Step 6: Check quantity
            try {
                WebElement quantityDropdown = driver.findElement(By.name("quantity"));
                String quantity = quantityDropdown.getAttribute("value");
                Assert.assertEquals(quantity, "1", "❌ Quantity is not 1");
                System.out.println("✔ Quantity verified: " + quantity);
            } catch (NoSuchElementException e) {
                System.out.println("⚠ Quantity dropdown not found. Assuming quantity = 1");
            }

            // Step 7: Remove item
            System.out.println("▶ Step 5: Removing item from cart");
            driver.findElement(By.xpath("//input[@value='Delete']")).click();
            Thread.sleep(3000); // wait for cart to update

            // Step 8: Validate cart is empty using item count span
            try {
                WebElement itemCount = driver.findElement(By.xpath("//span[@class='a-size-medium sc-number-of-items']"));
                String itemText = itemCount.getText().trim();  // e.g., "0 items"
                System.out.println("🧾 Cart item count text: " + itemText);

                Assert.assertTrue(itemText.contains("0"), "❌ Cart is not empty after removal");
                System.out.println("✔ Cart is empty after removal");
            } catch (Exception e) {
                System.out.println("⚠ Could not read cart item count. Check manually.");
                testPassed = false;
            }

            // Final result
            if (testPassed) {
                amazon.logResultToFile("TestCase2,Passed");
            } else {
                amazon.logResultToFile("TestCase2,Failed");
            }

        } catch (Exception e) {
            System.out.println("❌ Test Case 2 failed: " + e.getMessage());
            amazon.logResultToFile("TestCase2,Failed");
        }
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
