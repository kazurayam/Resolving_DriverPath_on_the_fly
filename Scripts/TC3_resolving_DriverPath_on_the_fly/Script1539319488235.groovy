import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

WebDriver driver = CustomKeywords.'my.CustomWebDriverFactory.createWebDriver'()

// search Google
driver.get("https://www.google.com/")
WebElement element = driver.findElement(By.name("q"))
element.sendKeys("katalon studio")
element.submit()
// wait for a few seconds
Thread.sleep(3000)
// verify response
assert driver.getTitle().contains('katalon studio')
// Bye
driver.quit()