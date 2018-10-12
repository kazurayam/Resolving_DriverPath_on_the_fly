Resolving DriverPath on the fly
========

## What is this?

This is a small [Katalon Studio](https://www.katalon.com/) project for demostration purpose. You can clone this out to your PC and execute with your Katalon Studio.

This project was developed using Katalon Studio 5.7.0.

## Problem to solve

In short, I want my test case script to be able to find out the path to the `chromedriver.exe`, `geckodriver.exe`, `IEDriverServer.exe`, `MicrosoftWebDriver.exe` bundled in the Katalon Studio.

Why? The reasons includes:

1. I want to execute a test project in Katalon Studio using browsers with customized DesiredCapabilities/Profiles.
1. I want my test project executable on Windows, Mac and Linux. Different platforms will have different paths of driver binaries.
2. I want my test project executable using all browsers that Katalon Studio supports: Chrome, Firefox, IE and Edge. I want to be able to specify which type of browser to use by the drop-down menu equipped in the Katalon GUI. Even then I want to create browser with customized DesiredCapabilities.

---

One day I wrote a test case as follows:

- [TC1_resolving_ChromeDriverPath_with_text](Scripts/TC1_resolving_ChromeDriverPath_with_text/Script1539317352223.groovy)
```
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
```

`TC1` worked OK, but I did not like it. I disliked having a path to `chromedriver.exe` as a fixed string.

I wanted to find out an alternative approach.

## Study

I wrote a test script [`TC2_retrieve_DriverFactory.getXxxDriverPath_values`](Scripts/TC2_retrieve_DriverFactory.getXxxDriverPath_values/Script1539317291291.groovy)

```
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.comment("executedBrowser:${DriverFactory.getExecutedBrowser().getName()}")
WebUI.comment("chromeDriverPath:${DriverFactory.getChromeDriverPath()}")
WebUI.comment("edgeDriverPath:${DriverFactory.getEdgeDriverPath()}")
WebUI.comment("geckoDriverPath:${DriverFactory.getGeckoDriverPath()}")
WebUI.comment("ieDriverPath:${DriverFactory.getIEDriverPath()}")
```


When I run it with Chrome browser, I got the following output:
```
10-12-2018 03:06:57 PM - [INFO]   - executedBrowser:CHROME_DRIVER
10-12-2018 03:06:57 PM - [INFO]   - chromeDriverPath:C:\Katalon_Studio_Windows_64-5.7.0\configuration\resources\drivers\chromedriver_win32\chromedriver.exe
10-12-2018 03:06:57 PM - [INFO]   - edgeDriverPath:null
10-12-2018 03:06:57 PM - [INFO]   - geckoDriverPath:null
10-12-2018 03:06:57 PM - [INFO]   - ieDriverPath:null
```

When I run `TC2` with Firefox browser, I got the following output:
```
10-12-2018 03:05:46 PM - [INFO]   - executedBrowser:FIREFOX_DRIVER
10-12-2018 03:05:46 PM - [INFO]   - chromeDriverPath:null
10-12-2018 03:05:46 PM - [INFO]   - edgeDriverPath:null
10-12-2018 03:05:46 PM - [INFO]   - geckoDriverPath:C:\Katalon_Studio_Windows_64-5.7.0\configuration\resources\drivers\firefox_win64\geckodriver.exe
10-12-2018 03:05:46 PM - [INFO]   - ieDriverPath:null
```


This experiment revealed:

1. `DriverFactory.getChromeDriverPath()` returns a valid path string when I used Chrome browser to run the test case. But it returns null when other type of drivers are used.
2. Also `DriverFactory.getGeckoDriverPath()` returns a valid path string when I used Firefox browser to run the test case. But it returns null when other type of drivers are used.
3. Same for other types of browsers

## Solution

I developed a custom keyword [`my.CustomWebDriverFactory`](Keywords/my/CustomWebDriverFactory.groovy)

```
package my

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.ie.InternetExplorerDriver

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

public class CustomWebDriverFactory {

	@Keyword
	WebDriver createWebDriver() {
		String executedBrowser = DriverFactory.getExecutedBrowser().getName()
		WebDriver driver
		switch (executedBrowser) {
			case 'FIREFOX_DRIVER':
				String geckoDriverPath = DriverFactory.getGeckoDriverPath()
				WebUI.comment(">>> geckoDriverPath=${geckoDriverPath}")
				System.setProperty("webdriver.gecko.driver", geckoDriverPath)
				// browser customization with DesiredCapabilities here --- TODO
				driver = new FirefoxDriver()
				break
			case 'CHROME_DRIVER':
				String chromeDriverPath = DriverFactory.getChromeDriverPath()
				WebUI.comment(">>> chromeDriverPath=${chromeDriverPath}")
				System.setProperty("webdriver.chrome.driver", chromeDriverPath)
				// browser customization with DesiredCapabilities here --- TODO
				driver = new ChromeDriver()
				break
			case 'IE_DRIVER':
				String ieDriverPath = DriverFactory.getIEDriverPath()
				WebUI.comment(">>> ieDriverPath=${ieDriverPath}")
				System.setProperty("webdriver.ie.driver", ieDriverPath)
				driver = new InternetExplorerDriver()
				break
			case 'EDGE_DRIVER':
				String edgeDriverPath = DriverFactory.getEdgeDriverPath()
				WebUI.comment(">>> edgeDriverPath=${edgeDriverPath}")
				System.setProperty("webdriver.edge.driver", edgeDriverPath)
				// you can insert code for browser customization here --- TODO
				driver = new EdgeDriver()
				break
			default:
				throw new IllegalStateException("unsupported browser type: ${executedBrowser}")
		}
		return driver
	}
}
```

And a test case script [`TC3_resolving_DriverPath_on_the_fly`](Scripts/TC3_resolving_DriverPath_on_the_fly/Script1539319488235.groovy)

```
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
```

## Conclusion

I feel comfortable with [`TC3_resolving_DriverPath_on_the_fly`](Scripts/TC3_resolving_DriverPath_on_the_fly/Script1539319488235.groovy) in that:

1. it has no path string to driver binaries hard-coded.
2. it works on Windows, MacOS and Linux without any code change.
3. it works when I choose Chrome, Firefox, IE and possibly Edge. (through I haven't really checked Edge yet)
