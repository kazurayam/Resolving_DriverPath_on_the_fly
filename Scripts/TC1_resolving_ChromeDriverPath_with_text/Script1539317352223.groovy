import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

// The path to chromedriver.exe bundled in the Katalon Stduio
String chromeDriverPath = 'C:\\Katalon_Studio_Windows_64-5.7.0\\configuration\\resources\\drivers\\chromedriver_win32\\chromedriver.exe'
// I DO NOT LIKE HAVING THIS HARD-CODED

// start Chrome
System.setProperty("webdriver.chrome.driver", chromeDriverPath)
WebDriver driver = new ChromeDriver()

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