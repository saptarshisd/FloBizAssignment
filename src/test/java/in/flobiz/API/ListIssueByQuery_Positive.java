package in.flobiz.API;

import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import in.flobizAPI.utils.ApiUtils;
import in.flobizAPI.utils.JavaUtils;

public class ListIssueByQuery_Positive {
	public String APINAME = "issues?fromemail=knutmt@gmail.com";
	JavaUtils javaUtils = new JavaUtils();
	ApiUtils apiUtils = new ApiUtils();

	@BeforeSuite
	public void readConfig() {
		javaUtils.readConfigProperties();
	}

	@Test
	public void listIssuesByQueryTest() {
		HashMap<String, String> headers = apiUtils.getHeaders();

		String response = apiUtils.getRequest(APINAME, headers);
	}

	@AfterMethod
	public void result(ITestResult result) throws InvalidFormatException, IOException {

		String failureReason = "";

		if (!result.isSuccess()) {
			failureReason = javaUtils.getFailureReason().isEmpty() ? result.getThrowable() + ""
					: javaUtils.getFailureReason() + "";
		}
	}

	@AfterSuite
	public void clearSessionToken() {
		JavaUtils.configProperties.remove("session");
	}

}
