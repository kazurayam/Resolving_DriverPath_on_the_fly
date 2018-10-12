import com.kms.katalon.core.webui.driver.DriverFactory
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI

WebUI.comment("executedBrowser:${DriverFactory.getExecutedBrowser().getName()}")
WebUI.comment("chromeDriverPath:${DriverFactory.getChromeDriverPath()}")
WebUI.comment("edgeDriverPath:${DriverFactory.getEdgeDriverPath()}")
WebUI.comment("geckoDriverPath:${DriverFactory.getGeckoDriverPath()}")
WebUI.comment("ieDriverPath:${DriverFactory.getIEDriverPath()}")

