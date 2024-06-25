import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

public class AmazonEgyptTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.manage().window().maximize();
    }

    @Test
    public void testAmazonEgypt() {
        // a. Open Amazon Egypt webpage
        driver.get("https://www.amazon.eg/-/en/ref=nav_logo");

        // b. Search for the word "iPad"
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
        searchBox.sendKeys("iPad");
        searchBox.submit();

        // c. Sort the result with filter "Price: high to low"
        WebElement sortDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"a-autoid-0-announce\"]")));
        sortDropdown.click();
        WebElement highToLowOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Price: High to Low')]")));
        highToLowOption.click();

        // d. Open the first search result
        WebElement firstResult = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"search\"]/div[1]/div[1]/div/span[1]/div[1]/div[2]/div/div/span/div/div/div[1]/span/a/div/img")));
        firstResult.click();

        // e. Save the name and price
        String itemName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle"))).getText();

        String itemPrice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"corePriceDisplay_desktop_feature_div\"]/div[1]"))).getText();
        String itempri =normalizeCurrency(itemPrice);

        // f. Add the item to cart
        WebElement addToCartButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"add-to-cart-button\"]")));
        addToCartButton.click();

        WebElement cancelButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"attachSiNoCoverage\"]/span/input")));
        cancelButton.click();

        // g. Navigate to cart
        WebElement cartButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"sw-gtc\"]/span/a")));
        cartButton.click();
       // assert on price listed in the cart
        WebElement cartItemPrice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class=\"sc-item-price-block\"]")));
        WebElement cartItemName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class=\"a-truncate-cut\"]")));
        String priceCart =normalizeCurrency(cartItemPrice.getText());

        // assert on the name listed in the cart
        String mainPartOfName = "Apple 2023 MacBook Air laptop with M2 chip: 15.3-inch Liquid Retina display, 8GB GB RAM, 256GB;GB SSD storage, Touch ID. Works with iPhone/iPad; Space Grey;";
        Assert.assertEquals(priceCart, itempri);
        Assert.assertTrue(cartItemName.getText().contains(mainPartOfName));
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private static String normalizeCurrency(String currencyString) {
        // Remove non-numeric characters and any trailing ".00"
        String normalized = currencyString.replaceAll("[^0-9]", "");
        if (normalized.endsWith("00")) {
            normalized = normalized.substring(0, normalized.length() - 2);
        }
        return normalized;

    }

}
