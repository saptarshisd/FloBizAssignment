package in.flobiz.API;

import java.io.IOException;
import java.util.HashMap;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.path.json.JsonPath;

import in.flobiz.API.floBizAssignment.CreateIssue;
import in.flobizAPI.utils.ApiUtils;
import in.flobizAPI.utils.JavaUtils;

public class CreateIssue_Positive {
	JavaUtils javaUtils = new JavaUtils();
	ApiUtils apiUtils = new ApiUtils();
	public String APINAME = "issues";
	CreateIssue createIssue = new CreateIssue();

	@BeforeSuite
	public void readConfig() {
		javaUtils.readConfigProperties();
	}

	@Test
	public void createIssuesTest() {
		HashMap<String, String> headers = apiUtils.getHeaders();
		String requestBody = createIssue.getRequestBody();

		String response = createIssue.postRequest(APINAME,requestBody, headers);
		JsonPath jpath = new JsonPath(response);
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
