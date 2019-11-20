
package in.flobizAPI.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;

import com.google.common.base.Predicate;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;

public class BasePage extends JavaUtils {

	public static AndroidDriver<MobileElement> mdriver;
	public static WebDriver wdriver;
	public Map<String, String> dataMap;
	public String destFile;

	// @AndroidFindBy(id = "android:id/alertTitle")
	// public MobileElement alertPopupTitle ;

	// @AndroidFindBy(id = "android:id/button1")
	// public MobileElement alertPopupOkButton;

	public BasePage(AndroidDriver<MobileElement> mdriver) {
		this.mdriver = mdriver;
	}

	public BasePage(WebDriver wdriver) {
		this.wdriver = wdriver;
	}

	/**
	 * @return AndroidDriver, To launch the app using its activity and package on a
	 *         particular device mentioned across the test case in the test-data
	 */
	public AndroidDriver<MobileElement> launchApp(String deviceName) throws IOException {

		//dataMap = readDeviceConfig(deviceName);
		String server = JavaUtils.configProperties.get("appiumServer");
		String port = dataMap.get("PORT");
	//	super.startAppiumServer(server, port);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		if (deviceName.equalsIgnoreCase("Emulator1")) {
			DefaultExecutor executor = new DefaultExecutor();
			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

			CommandLine launchEmul = new CommandLine("C:/Program Files/Genymobile/Genymotion/player");
			launchEmul.addArgument("--vm-name");
			launchEmul.addArgument("\"" + dataMap.get("DEVICENAME") + "\"");
			executor.setExitValue(1);
			executor.execute(launchEmul, resultHandler);
		}
		try {
			Thread.sleep(60000);
			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(MobileCapabilityType.UDID, dataMap.get("UDID"));
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, dataMap.get("DEVICENAME"));
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
			capabilities.setCapability(MobileCapabilityType.VERSION, dataMap.get("ANDROIDVERSION"));
			capabilities.setCapability("browserName", "Android");
			capabilities.setCapability(MobileCapabilityType.APP_PACKAGE, "in.novopay.sli");
			capabilities.setCapability(MobileCapabilityType.APP_ACTIVITY, ".ui.activities.LoginActivity");
			Thread.sleep(3000);
			mdriver = new AndroidDriver<MobileElement>(new URL("http://" + server + ":" + port + "/wd/hub"),
					capabilities);
			Log.info("Launched the SLI application successfully");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return mdriver;
	}

	/**
	 * @return The web driver instance.
	 */
	public WebDriver launchBrowser() {

		// local machine
		String browser = JavaUtils.configProperties.get("browser");

		if (browser.equalsIgnoreCase("Firefox")) {
			System.setProperty("webdriver.gecko.marionette", "./drivers/geckodriver.exe");
			wdriver = new FirefoxDriver();

		} else if (browser.equalsIgnoreCase("Chrome")) {

			if (JavaUtils.configProperties.get("OperatingSystem").equalsIgnoreCase("WINDOWS")) {
				System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
			} else if (JavaUtils.configProperties.get("OperatingSystem").equalsIgnoreCase("MAC")) {
				System.setProperty("webdriver.chrome.driver", "/Applications/chromedriver");
			}
			ChromeOptions options = new ChromeOptions();
			options.setHeadless(Boolean.parseBoolean(configProperties.get("chromeHeadless")));
			wdriver = new ChromeDriver(options);
		}
		String url = JavaUtils.configProperties.get("baseUrl");
		System.err.println(url);
		wdriver.get(url);
		wdriver.manage().window().maximize();
		return wdriver;
	}

	/*
	 * wait untill element disappers here toaster message
	 */
	public void waitUntillElementDisappears(String xpath) {
		WebDriverWait wait = new WebDriverWait(wdriver, 20);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
	}

	public void waitUntilElementNotDisplayed(final String xpath) {
		WebDriverWait wait = new WebDriverWait(wdriver, 20);
		ExpectedCondition elementIsDisplayed = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver arg0) {
				try {
					wdriver.findElement(By.xpath(xpath)).isDisplayed();
					return false;
				} catch (NoSuchElementException e) {
					return true;
				} catch (StaleElementReferenceException f) {
					return true;
				}
			}
		};
		wait.until(elementIsDisplayed);
	}

	/*
	 * wait untill element disappers here toaster message
	 */
	public void waitUntillElementDisappears(WebElement... elements) {
		WebDriverWait wait = new WebDriverWait(wdriver, 20);
		List<WebElement> lst = new ArrayList<>();
		Collections.addAll(lst, elements);
		wait.until(ExpectedConditions.invisibilityOfAllElements(lst));
	}

	public void waitUntilElementAppears(String xpath) {
		WebDriverWait wait = new WebDriverWait(wdriver, 10);
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
	}

	public String formatXpath(String xpath, String replacingString) throws NoSuchElementException {
		return String.format(xpath, replacingString);
	}

	/**
	 * Wait until web element appears
	 */
	public void waitUntilElementAppears(WebElement element) {

		WebDriverWait wait = new WebDriverWait(wdriver, 10);
		wait.until(ExpectedConditions.visibilityOf(element));
	}
	// /**
	// * Wait until mobile element appears
	// */
	// public void waitTillElementVisible(WebElement element) {
	// WebDriverWait wait = new WebDriverWait(mdriver, 20);
	// wait.until(ExpectedConditions.visibilityOf(element));
	// }

	/**
	 * Highlight the mobile element
	 */
	public void highlightElement(MobileElement element) {

		JavascriptExecutor jse = (JavascriptExecutor) mdriver;
		;
		jse.executeScript("arguments[0],setAttribute('style,, 'background: yellow; border: 2px solid red;');", element);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}

		jse.executeScript("arguments[0],setAttribute('style,, 'background: yellow; border: 2px solid white;');",
				element);

	}

	/**
	 * Wait until mobile element is clickable
	 */
	public void waitUntilElementClickable(WebElement element) {
		// two attempts to click
		int attempts = 3;
		boolean flag = true;
		do {
			try {
				WebDriverWait wait = new WebDriverWait(wdriver, 20);
				wait.until(ExpectedConditions.elementToBeClickable(element));
				flag = false;
			} catch (WebDriverException e) {
				--attempts;
				Reporter.log("Element not enabled to click, Trying again", true);
			}
		} while (attempts > 0 && flag);
		// WebDriverWait wait = new WebDriverWait(wdriver, 10);
		// ExpectedCondition elementIsDisplayed = new ExpectedCondition<Boolean>() {
		// public Boolean apply(WebDriver arg0) {
		// try {
		// element.isEnabled();
		// return true;
		// }
		// catch (NoSuchElementException e ) {
		// return false;
		// }
		// catch (StaleElementReferenceException f) {
		// return false;
		// }
		// }
		// };
		// wait.until(elementIsDisplayed);

	}

	public WebElement waitUntilElementClickable(String xpath) {
		// two attempts to click
		int attempts = 3;
		boolean flag = true;
		do {
			try {
				WebDriverWait wait = new WebDriverWait(wdriver, 20);
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
				flag = false;
			} catch (WebDriverException e) {
				--attempts;
				Reporter.log("Element not enabled to click, Trying again", true);
			}
		} while (attempts > 0 && flag);
		if (flag == false) {
			return wdriver.findElement(By.xpath(xpath));
		}
		return null;
	}

	/**
	 * Navigate you back by one page on the browser’s history.
	 */
	public void navigateback() {
		mdriver.navigate().back();
		// sReporter.log("Navigating Back to Screen", true);
	}

	/**
	 * Close the mobile application
	 */
	public void closeApp() {

		mdriver.closeApp();
		Reporter.log("Shutdown the Mobile Application");
	}

	/**
	 * Close the web browser
	 */
	public void closeBrowser() {

		wdriver.quit();
		Reporter.log("Shutdown the Web Application");
	}

	/**
	 * @return The test execution status
	 */
	public String getExecutionResultStatus(int statusCode) {

		String testStatus = null;
		if (statusCode == 1) {
			testStatus = "PASS";
		} else if (statusCode == 2) {
			testStatus = "FAIL";
		} else if (statusCode == 3) {
			testStatus = "SKIPPED";
		}
		return testStatus;
	}

	public void click(WebElement ele) {

		JavascriptExecutor executor = (JavascriptExecutor) wdriver;
		executor.executeScript("arguments[0].click();", ele);

	}

	public void mouseHoverAndClick(WebElement element) {

		System.out.println(element.isEnabled());
		Actions act = new Actions(wdriver);
		act.moveToElement(element).build().perform();
		JavascriptExecutor js = (JavascriptExecutor) wdriver;
		js.executeScript("arguments[0].focus();", element);
		System.out.println(element.isEnabled());
		element.click();

	}

	/**
	 * @return The Object[][] values
	 */
	public Object[][] returnRowsUniqueValueFromSheet(String sheetName) {

		ArrayList<String> allValues = new ArrayList<String>();
		try {
			FileInputStream file = new FileInputStream("./TestData/MobileData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();

			Row headers = it.next();
			while (it.hasNext()) {

				Row record = it.next();
				allValues.add(record.getCell(0).toString());
			}

			Object[][] values = new Object[allValues.size()][1];
			for (int i = 0; i < allValues.size(); i++) {
				values[i][0] = allValues.get(i);
			}

			return values;
		} catch (NullPointerException e) {
			System.out.println("Caught NullPointerException");
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			System.out.println("Caught EncryptedDocumentException");
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			System.out.println("Caught InvalidFormatException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Caught IOException");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Swipe vertical in mobile
	 * 
	 * @param yStart,
	 *            yEnd, totalSwipes
	 */
	public void swipeVertical(int yStart, int yEnd, int totalSwipes) {

		for (int i = 0; i < totalSwipes; i++) {
			try {
				mdriver.swipe(300, yStart, 300, yEnd, 3000);
			} catch (Exception e) {
			}
		}

	}

	/**
	 * Swipe horizontal in mobile
	 * 
	 * @param xStart,
	 *            xEnd, totalSwipes
	 */
	public void swipehorizontal(int xStart, int xEnd, int totalSwipes) {
		try {
			for (int i = 0; i < totalSwipes; i++) {
				mdriver.swipe(xStart, 250, xEnd, 250, 3000);
			}
		} catch (Exception e) {

		}

	}

	/**
	 * Swipe until mobile element is visible
	 * 
	 * @param element
	 */
	public void swipeUntilElementVisible(WebElement element) {
		for (int i = 1; i < 10; i++) {
			mdriver.swipe(100, 600, 100, 500, 1);
			try {
				element.isDisplayed();
				break;
			} catch (Exception e) {
				mdriver.swipe(100, 700, 100, 500, 1);
			}
		}
	}

	/**
	 * Select drop down value
	 * 
	 * @param element,
	 *            text
	 */
	public void selectDropdown(WebElement element, String text) {
		Select dropdown = new Select(element);
		dropdown.selectByVisibleText(text);
	}

	public void selectDropdownByIndex(WebElement element, int number) throws InterruptedException {
		Thread.sleep(2000);
		Select dropdown = new Select(element);
		dropdown.selectByIndex(number);
	}

	public void selectStageDropdown(WebElement element, String value) {
		waitingForTheElementToLoad(element);
		Select dropdown = new Select(element);
		dropdown.selectByVisibleText(value);
		System.err.println("Dropdown value ----> " + value);

		// Print all the options for the selected drop down and select one
		// option of your choice
		// Get the size of the Select element
		List<WebElement> oSize = dropdown.getOptions();
		int iListSize = oSize.size();
		// Setting up the loop to print all the options
		for (int i = 0; i < iListSize; i++) {
			// Storing the value of the option
			String sValue = dropdown.getOptions().get(i).getText();
			// Printing the stored value
			System.err.println("Dropdown value ----> " + sValue);
			// Putting a check on each option that if any of the option is equal
			// to "VALUE" then select it
			if (sValue.equals(value)) {
				dropdown.selectByIndex(i);
				break;
			}
		}
	}

	/**
	 * Select drop down value, here checking option values
	 * 
	 * @param element,
	 *            value
	 */
	public void selectDropdown2(WebElement element, String value) {

		Select dropdown = new Select(element);

		List<WebElement> oSize = dropdown.getOptions();
		int iListSize = oSize.size();
		System.out.println(oSize.size());
		// Setting up the loop to print all the options
		for (int i = 0; i < iListSize; i++) {
			// Storing the value of the option
			String sValue = dropdown.getOptions().get(i).getText();
			System.out.println(sValue);

			if (sValue.equals(value)) {
				dropdown.selectByVisibleText(value);
				break;
			}
		}

	}

	/**
	 * @return current date in dd mm yyyy format
	 */
	public String currentdate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
		String cd = sdf.format(date);
		return cd;
	}

	/**
	 * @return current date in dd/MMMM/yy hh:mm:ss format
	 */
	public String currentdatemins() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yy hh:mm:ss");
		String cd = sdf.format(date);
		return cd;
	}

	/**
	 * @return string from float value
	 * @param String
	 *            value
	 */
	public String convertfloat_to_string(String ben) {
		float d = Float.parseFloat(ben);
		int c = (int) d;
		String benAcc = Integer.toString(c);
		return benAcc;

	}

	/**
	 * This method for converting double to string value
	 */
	public void doubletoFormat(String balance) {
		double balvalue = Double.parseDouble(balance);
		DecimalFormat df2 = new DecimalFormat("#.00");
		String balstr = df2.format(balvalue);
	}

	/**
	 * @return float from string value
	 */
	public float covertStringtoFloat(String s) {
		float f = Float.parseFloat(s);
		return f;

	}

	/**
	 * @return int from string value
	 */
	public int convertString_Float_int(String s) {
		float f = covertStringtoFloat(s);
		int i = (int) f;
		return i;

	}

	/**
	 * @return int from string value
	 */
	public int covertStringtoint(String s) {
		int f = Integer.parseInt(s);
		return f;

	}

	/**
	 * This method will assert string values
	 */
	public void assertvalue(String actual, String expected, String errorMsg) {
		try {
			Assert.assertEquals(actual, expected, errorMsg);
			Reporter.log("Expected :: " + actual + "  Expected:: " + expected, true);
		} catch (AssertionError e) {
			Reporter.log("Assertion failed");
		}

	}

	/**
	 * This method will assert float values
	 */
	public void assertfloatvalue(float actual, float expected) {
		Assert.assertEquals(actual, expected);
		Reporter.log("Expected :: " + actual + "  Expected :: " + expected, true);
	}

	/**
	 * This method will assert float values
	 */
	public void assertintvalue(float actual, float expected) {
		Assert.assertEquals(actual, expected);
		Reporter.log("Expected :: " + actual + "  Expected:: " + expected, true);
	}

	/**
	 * @return class name
	 * @param class
	 */
	public String getClassName(Class<?> className) {

		String[] clsParts = className.getName().split("\\.");
		String clsName = clsParts[(clsParts.length) - 1];
		System.out.println("Class Name is : " + clsName);
		return clsName;
	}

	/**
	 * This method will wait page to load
	 * 
	 * @param sec
	 */
	public void waitForthePageToLoad(int sec) {
		wdriver.manage().timeouts().implicitlyWait(sec, TimeUnit.SECONDS);

	}

	/**
	 * This method will wait for next element
	 * 
	 * @param sec
	 */
	public void waitFortheNextElement(int sec) {
		wdriver.manage().timeouts().implicitlyWait(sec, TimeUnit.MILLISECONDS);

	}

	/**
	 * This method will check for next element with polling time
	 * 
	 * @param webElement,
	 *            waitsec, pollsec
	 */
	public void waitandCheckfortheElement(WebElement wb, int waitsec, int pollsec) {
		new FluentWait<WebElement>(wb).withTimeout(waitsec, TimeUnit.SECONDS).pollingEvery(pollsec, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);
	}

	/**
	 * To send keys in text box on mobile
	 * 
	 * @param MobileElement,
	 *            value
	 */
	public void sendKeys(MobileElement element, String value) {
		waitUntilElementAppears(element);
		element.click();
		element.clear();
		element.sendKeys(value);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mdriver.navigate().back();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendKeys(WebElement element, String value) {
		waitUntilElementClickable(element);

		Actions actions = new Actions(wdriver);
		actions.moveToElement(element);
		actions.click();
		actions.sendKeys(Keys.chord(Keys.CONTROL, "a"), value);
		actions.build().perform();
	}

	/**
	 * To send keys in text box and hide keyboard on mobile
	 * 
	 * @param MobileElement,
	 *            value
	 */

	public void clickElement(WebElement element) {

		// .fa-remove:before
		String script = "return window.getComputedStyle(document.querySelector('.fa-close'),'::before')";
		JavascriptExecutor js = (JavascriptExecutor) wdriver;
		js.executeScript("arguments[0].click(); ", script);

		Actions build = new Actions(wdriver);
		build.moveToElement(element).contextClick().build().perform();
	}

	/**
	 * swipe vertical till element visible in mobile
	 * 
	 * @param yStart,
	 *            yEnd, MobileElement
	 */
	public void swipeVerticalUntilElementIsVisible(double yStart, double yEnd, MobileElement element) {

		mdriver.context("NATIVE_APP");
		Dimension size = mdriver.manage().window().getSize();
		// System.out.println("width : " + (size.width * .85) + " yStart : " +
		// (int) (size.height * yStart) + " yEnd : "
		// + (int) (size.height * yEnd));
		for (int i = 1; i <= 10; i++) {
			try {
				// mdriver.swipe(600, yStart, 600, yEnd, 1);
				mdriver.swipe((int) (size.width * .85), (int) (size.height * yStart), (int) (size.width * .85),
						(int) (size.height * yEnd), 2000);
				element.isDisplayed();
				break;
			} catch (Exception e) {
				mdriver.swipe((int) (size.width * .85), (int) (size.height * yStart), (int) (size.width * .85),
						(int) (size.height * yEnd), 2000);
			}
		}
	}

	/**
	 * Swipe vertical in mobile
	 * 
	 * @param yStart,
	 *            yEnd, totalSwipes
	 */
	public void swipeVertical(double yStart, double yEnd, int totalSwipes) {

		mdriver.context("NATIVE_APP");
		Dimension size = mdriver.manage().window().getSize();
		for (int i = 1; i <= totalSwipes; i++) {
			try {
				mdriver.swipe((int) (size.width * .85), (int) (size.height * yStart), (int) (size.width * .85),
						(int) (size.height * yEnd), 2000);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @return boolean, tap on Mpin
	 * @param mpin
	 */
	public Boolean tapMPINBtn(String mpins) {
		MobileElement submitMPINBtn = mdriver.findElement(By.id("in.novopay.merchant:id/btnDone"));
		char[] mpin = mpins.toCharArray();
		try {
			for (char pin : mpin) {
				String mpinBtn = "in.novopay.merchant:id/btnNum" + pin;
				mdriver.findElement(By.id(mpinBtn)).click();
			}
			submitMPINBtn.click();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * Takes screenshot both web and mobile
	 */
	public void takeScreenShot(Map<String, String> workFlowDataMap) {
		String destDir = "./ScreenShots";
		File scrFile;
		if (workFlowDataMap.get("TESTON").equalsIgnoreCase("WEB")) {
			scrFile = ((TakesScreenshot) wdriver).getScreenshotAs(OutputType.FILE);
		} else {
			scrFile = ((TakesScreenshot) mdriver).getScreenshotAs(OutputType.FILE);
		}
		new File(destDir).mkdirs();
		destFile = workFlowDataMap.get("TCID") + ".png";

		try {

			FileUtils.copyFile(scrFile, new File(destDir + "/" + destFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Wait till the element load on web
	 */
	public void waitingForTheElementToLoad(WebElement element) {
		WebDriverWait wait = new WebDriverWait(wdriver, 60);
		wait.until(ExpectedConditions.visibilityOf(element));
	}

	public void selectDropDown(WebElement ele, String value) {
		WebElement mySelectElement = ele;
		Select dropdown = new Select(mySelectElement);
		List<WebElement> options = dropdown.getOptions();
		for (WebElement option : options) {
			if (option.getText().equalsIgnoreCase(value)) {
				dropdown.selectByVisibleText(value);
				System.err.println(value);
			}

		}

	}

	// select drop down as per cbs 3.0
	public void selectDropDown(WebElement dropDown, WebElement searchField, WebElement result, String searchString) {
		boolean flag = true;

		do {
			try {
				waitUntilElementClickable(dropDown);
				dropDown.click();
				sendKeys(searchField, searchString);
				waitUntilElementClickable(result);
				result.click();
				flag = false;
			} catch (StaleElementReferenceException e) {
				Reporter.log("Not able to click the drop down, element is getting updated", true);
			}
		} while (flag);

	}

	public void selectDD(WebElement ele, String value) {
		WebElement mySelectElement = ele;
		Select oSelect = new Select(mySelectElement);
		List<WebElement> elementCount = oSelect.getOptions();
		int iSize = elementCount.size();

		for (int i = 0; i < iSize; i++) {

			String sValue = elementCount.get(i).getText();
			System.out.println(sValue);

		}
	}

	/**
	 * To press on tab
	 */
	public void keyTab() throws AWTException {
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ESCAPE);

	}

	/**
	 * To page up
	 */
	public void pageUP() throws AWTException {
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_HOME);
	}

	/**
	 * Click on in visible web element for web
	 */
	public void clickInvisibleElement(WebElement webElement) {
		JavascriptExecutor executor = (JavascriptExecutor) wdriver;
		executor.executeScript("arguments[0].click();", webElement);
	}

	/**
	 * Swipe Horizontal in mobile
	 */
	public void swipeHorizantal(double xStart, double xEnd, int totalSwipes) {
		mdriver.context("NATIVE_APP");
		Dimension size = mdriver.manage().window().getSize();
		for (int i = 1; i <= totalSwipes; i++) {
			try {
				mdriver.swipe((int) (size.width * xStart), (int) (size.height * .30), (int) (size.width * xEnd),
						(int) (size.height * .30), 800);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Launch web browser though passing web application url as a parameter
	 */
	public WebDriver launchBrowser(String url) {

		// local machine
		String browser = JavaUtils.configProperties.get("browser");

		if (browser.equalsIgnoreCase("Firefox")) {
			// System.setProperty("webdriver.gecko.marionette",
			// "./drivers/geckodriver.exe");

			wdriver = new FirefoxDriver();

		} else if (browser.equalsIgnoreCase("Chrome")) {

			if (JavaUtils.configProperties.get("OperatingSystem").equalsIgnoreCase("WINDOWS")) {

				System.setProperty("webdriver.chrome.driver", "./drivers/chromedriver.exe");
			} else if (JavaUtils.configProperties.get("OperatingSystem").equalsIgnoreCase("MAC")) {
				System.setProperty("webdriver.chrome.driver", "/Applications/chromedriver1");
			}

			wdriver = new ChromeDriver();
		}
		wdriver.get(url);
		// wdriver.manage().window().maximize();
		return wdriver;
	}

	public WebElement findAndScrollToElement(String xpath) {
		WebElement element = wdriver.findElement(By.xpath(xpath));
		try {
			scrollToWebElement(element);
			return element;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Scroll till web element visible only for Web
	 */
	public void scrollToElement(WebElement webElement) {
		JavascriptExecutor executor = (JavascriptExecutor) wdriver;
		executor.executeScript("arguments[0].scrollIntoView(true);", webElement);
	}

	/**
	 * To hide soft keyboard in mobile
	 */
	public static void hideKeyboard(Context ctx) {
		InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View v = ((Activity) ctx).getCurrentFocus();
		if (v == null)
			return;

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	/**
	 * To verify element in mobile
	 */
	public boolean findElement(String name) {
		try {
			if ((mdriver.findElementByName(name)) != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	/**
	 * This method will Capture screenshot on failed test script, save in
	 * Screenshots folder
	 * 
	 * @param result,
	 *            Tcid
	 */
	public void captureScreenshotOnFailedTest(ITestResult result, String Tcid) {
		if (ITestResult.FAILURE == result.getStatus()) {
			try {
				Log.info("Taking screenshot on failed test");
				File source = ((TakesScreenshot) wdriver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(source, new File("./Screenshots/" + Tcid + ".png"));
				Log.info("Screenshot taken");
			} catch (Exception e) {
				System.out.println("Exception while taking screenshot " + e.getMessage());
			}
		}
	}

	public void waitUntilBlockUI() {
		try {
			WebElement loadingScreen = wdriver
					.findElement(By.xpath(".//div[@class='scroll-table']//div[@class='ng-busy-default-sign']"));
			if (loadingScreen.isDisplayed()) {
				while (loadingScreen.isDisplayed()) {
					Log.info("waiting for the page to load..");

					Thread.sleep(2000);

				}
			}
		} catch (InterruptedException | StaleElementReferenceException | NoSuchElementException e) {
			System.out.println("Loading spinner not found");
		}
	}

	public void scrollDown() throws AWTException {
		Log.info("scrollDown method is in progress");
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_PAGE_DOWN);
		robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
		Log.info("scrollDown method has been executed..");
	}

	public void pressEscape() throws AWTException {
		Log.info("pressEscape method is in progress");
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ESCAPE);
		robot.keyRelease(KeyEvent.VK_ESCAPE);
		Log.info("pressEscape method has been executed..");
	}

	public void pressTab() throws AWTException {
		Log.info("pressTab method is in progress");
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
		Log.info("pressTab method has been executed..");
	}

	public void pressEnter() throws AWTException {
		Log.info("pressTab method is in progress");
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		Log.info("pressTab method has been executed..");
	}

	public void scrollUp() throws AWTException {
		Log.info("scrollUp method is in progress");
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_PAGE_UP);
		robot.keyRelease(KeyEvent.VK_PAGE_UP);
		Log.info("scrollUp method has been executed..");
	}

	public String[] splitStringValueTwice(String excelCellData, int number) {
		try {
			if (excelCellData.toLowerCase().equalsIgnoreCase("df") == false
					&& excelCellData.toLowerCase().equalsIgnoreCase("df") == false) {
				Log.info("getting a String array from Excel cell");
				String[] set = excelCellData.split("-");
				int numberOfDataSet = set.length;
				String[] individualValues = set[number].split(",");
				return individualValues;
			}
		} catch (NullPointerException n) {
			n.printStackTrace();
		}
		return null;

	}

	public String[] splitStringValue(String excelCellData) {
		if (excelCellData.toLowerCase().equalsIgnoreCase("df") == false
				&& excelCellData.toLowerCase().equalsIgnoreCase("na") == false) {
			Log.info("getting comma seperated values");
			String[] individualValues = excelCellData.split(",");
			return individualValues;
		}
		return null;
	}

	public void multipleFieldFill(List<WebElement> listOfElements, int number, String value) {
		if (value.toLowerCase().equalsIgnoreCase("df") == false && value.toLowerCase().equalsIgnoreCase("na") == false
				&& listOfElements.get(number).isEnabled()) {
			Log.info("filling the field - " + listOfElements.get(number));
			listOfElements.get(number).clear();
			listOfElements.get(number).sendKeys(value);
		}
	}

	public void multipleDropdownSelect(List<WebElement> listOfElements, int number, String value) {
		if (value.toLowerCase().equalsIgnoreCase("df") == false && value.toLowerCase().equalsIgnoreCase("na") == false
				&& listOfElements.get(number).isEnabled()) {
			Log.info("selecting the option from gthe dropdown - " + listOfElements.get(number));
			WebElement element = listOfElements.get(number);
			Select selectItem = new Select(element);
			if (element.isEnabled()) {
				selectItem.selectByVisibleText(value);
			} else {
				System.out.println("Element need not be selected");
			}
		}
	}

	public void moveToElement(WebElement element) {
		Actions act = new Actions(wdriver);
		act.moveToElement(element).perform();
	}

	public void conditionalFieldFill(WebElement element, String text) {
		if (text.toLowerCase().equalsIgnoreCase("df")) {
			System.out.println("moving on..");
			System.out.println(element);
		} else if (text.toLowerCase().equalsIgnoreCase("na")) {
			System.out.println("moving on..");
			System.out.println(element);
		} else {
			element.clear();
			element.sendKeys(text);
		}
	}

	public void conditionalTickCheckbox(WebElement element, String text) {
		if (text.toLowerCase().equalsIgnoreCase("yes") && element.isSelected() == false
				&& text.toLowerCase().equalsIgnoreCase("df") == false) {
			Log.info("ticking the checkbox" + element);
			element.click();
		} else if (text.toLowerCase().equalsIgnoreCase("no")) {
			System.out.println("option has been chosen not to be checked");
		}
	}

	public String randomGSTNumber() {
		Log.info("getting a random GST Number");
		int min = 1000;
		int max = 9999;
		long number = ThreadLocalRandom.current().nextLong(min, max + 1);
		String GSTNumber = "11AKLPG" + number + "L1Z1";
		return GSTNumber;
	}

	public String todaysDate() {
		Log.info("filling Today's date");
		System.out.println("filling Today's date");
		DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
		Calendar cal = Calendar.getInstance();
		String reportDate = df.format(cal.getTime());
		return reportDate;
	}

	public void scrollToWebElement(WebElement element) throws InterruptedException {
		((JavascriptExecutor) wdriver).executeScript("arguments[0].scrollIntoView(true);", element);
		Thread.sleep(500);

	}

	public void scrollUpToWebElement() {
		try {
			((JavascriptExecutor) wdriver).executeScript("window.scrollBy(0,-250);", "");
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String randomMobileNumber() {
		Log.info("getting a random mobile number");
		long min = 9000000000L;
		long max = 9999999997L;
		long number = ThreadLocalRandom.current().nextLong(min, max + 1);
		String mobileNumber = "" + number + "";
		return mobileNumber;
	}

	public void selectDropdownByVisibleText(WebElement element, String value) {
		if (value.toLowerCase().equalsIgnoreCase("df") == false
				&& value.toLowerCase().equalsIgnoreCase("na") == false) {
			Log.info("selecting the option from dropdown by name - " + element);
			waitingForTheElementToLoad(element);
			Select dropdown = new Select(element);
			if (element.isEnabled()) {
				dropdown.selectByVisibleText(value);
			}
		}
	}

	public void clickElementMultipleTimes(WebElement element, int number) {
		Log.info("clicking the element" + element + number + " number of times");
		System.out.println("clicking the element" + element + number + " number of times");
		if (number > 0) {
			for (int i = 0; i <= number - 1; i++) {
				element.click();
			}
		} else if (number == 0) {
			Log.info("Element need not be clicked");
			System.out.println("Element need not be clicked");
		}
	}

	public String randomString() {
		Log.info("getting a random string");
		final String randomString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		final java.util.Random rand = new java.util.Random();
		// consider using a Map<String,Boolean> to say whether the identifier is being
		// used or not
		final Set<String> identifiers = new HashSet<String>();

		StringBuilder builder = new StringBuilder();
		while (builder.toString().length() == 0) {
			int length = rand.nextInt(5) + 5;
			for (int i = 0; i < length; i++) {
				builder.append(randomString.charAt(rand.nextInt(randomString.length())));
			}
			if (identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString();
	}

	public String randomAadharNumber() {
		long min = 100000000000L;
		long max = 999999999997L;
		long number = ThreadLocalRandom.current().nextLong(min, max + 1);
		String aadharNumber = "" + number + "";
		return aadharNumber;
	}

	public String nextMonthSameDay() {
		Log.info("filling next Month Same Day");
		System.out.println("filling next Month Same Day");
		DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 30);
		String reportDate = df.format(cal.getTime());
		return reportDate;
	}

	public String firstRepaymentDateConditional() throws InterruptedException {
		String ff = nextMonthSameDay();
		String[] today = ff.split(" ");
		int date = Integer.parseInt(today[0]);
		DateFormat df = new SimpleDateFormat("dd MMMM yyyy");
		Calendar cal = Calendar.getInstance();

		if (date >= 21 && date <= 29) {
			cal.add(Calendar.DATE, 40);
			String reportDate = df.format(cal.getTime());
			System.out.println("fetching conditional date");
			return reportDate;
		} else if (date >= 30 && date <= 31) {
			cal.add(Calendar.DATE, 35);
			String reportDate = df.format(cal.getTime());
			System.out.println("fetching conditional date");
			return reportDate;
		} else if (date > 0 && date < 10) {
			cal.add(Calendar.DATE, 30);
			System.out.println("filling same date next month");
			String reportDate = df.format(cal.getTime());
			return reportDate;
		} else if (date >= 10 && date < 19) {
			cal.add(Calendar.DATE, 21);
			System.out.println("filling same date next month");
			String reportDate = df.format(cal.getTime());
			return reportDate;
		} else if (date >= 19 && date < 21) {
			cal.add(Calendar.DATE, 15);
			System.out.println("filling same date next month");
			String reportDate = df.format(cal.getTime());
			return reportDate;
		}
		return null;
	}

	public static String randomDate() {
		Random random = new Random();
		int minDay = (int) LocalDate.of(1960, 1, 1).toEpochDay();
		int maxDay = (int) LocalDate.of(1990, 1, 1).toEpochDay();
		long randomDay = minDay + random.nextInt(maxDay - minDay);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);

		return randomBirthDate.format(formatter);
		// System.out.println(randomBirthDate);

	}

	public String randomPincode() {
		long min = 560000L;
		long max = 560150L;
		long number = ThreadLocalRandom.current().nextLong(min, max + 1);
		String pincode = "" + number + "";
		return pincode;
	}

	public void closeOpenedTab() throws AWTException {
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_W);
	}
}
