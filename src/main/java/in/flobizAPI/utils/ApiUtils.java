package in.flobizAPI.utils;

import static com.jayway.restassured.RestAssured.given;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;
import org.testng.Assert;
import org.testng.Reporter;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

import in.Headers;
public class ApiUtils extends JavaUtils {

	String stri;
	String fileName;

	Properties velocityProps;

	public HashMap<String, String> getHeaders() {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("x-apikey", "5da6fb5d3cbe87164d4bb35d");
		headers.put("Host", "flobizhiring-57e6.restdb.io");
		headers.put("User-Agent", "PostmanRuntime/7.19.0");
		System.out.println(headers);
		return headers;
	}

	

	public String getRequest(String apiName, Map<String, String> headers) {

		String server = configProperties.get("serverHostName");
		String path = "";

		path = configProperties.get("serverPath");

		String serverURL = server + path + apiName;
		System.out.println(serverURL);
		double startTime = System.currentTimeMillis();

		Response post = given().relaxedHTTPSValidation().headers(headers).when().get(serverURL);

		double stopTime = System.currentTimeMillis();

		String response = post.asString();
		Reporter.log("\nServer URL : " + serverURL, true);

		Reporter.log("\nTotal time taken for the get request is : " + ((stopTime - startTime) / 1000) + " Seconds ...",
				true);

		Reporter.log("\nResponse Obtained : " + response, true);
		return response;
	}
	
	

	

	public String postRequest(String apiName, String requestInStringFormat, Map<String, String> headers) {

		String server = configProperties.get("serverHostName");
		String path = "";

		path = configProperties.get("serverPath");

		String serverURL = server + path + apiName;
	
		System.out.println(serverURL);
		double startTime = System.currentTimeMillis();

		Response post = given().relaxedHTTPSValidation().headers(headers).body(requestInStringFormat).when()
				.post(serverURL);

		double stopTime = System.currentTimeMillis();

		String response = post.asString();
		Reporter.log("\nServer URL : " + serverURL, true);
		String timeTaken = String.valueOf((stopTime - startTime) / 1000);
		setTimeTaken(timeTaken);
		Reporter.log("\nTotal time taken for the post request is : " + getTimeTaken() + " Seconds ...", true);

		Reporter.log("\nRequest sent is \n" + requestInStringFormat, true);

		Reporter.log("\nResponse Obtained : " + response, true);
		return response;
	}
	
	public String createPostRequest( String requestInStringFormat, Map<String, String> headers) {

		String server = configProperties.get("serverHostName1");


		String serverURL = server;
	
		System.out.println(serverURL);
		double startTime = System.currentTimeMillis();

		Response post = given().relaxedHTTPSValidation().headers(headers).body(requestInStringFormat).when()
				.post(serverURL);

		double stopTime = System.currentTimeMillis();

		String response = post.asString();
		Reporter.log("\nServer URL : " + serverURL, true);
		String timeTaken = String.valueOf((stopTime - startTime) / 1000);
		setTimeTaken(timeTaken);
		Reporter.log("\nTotal time taken for the post request is : " + getTimeTaken() + " Seconds ...", true);

		Reporter.log("\nRequest sent is \n" + requestInStringFormat, true);

		Reporter.log("\nResponse Obtained : " + response, true);
		return response;
	}
	
	
	
	public String putRequest(String apiName, String requestInStringFormat, Map<String, String> headers) {

		String server = configProperties.get("serverHostName");
		String path = "";

		path = configProperties.get("serverPath");

		String serverURL = server + path + apiName;
	
		System.out.println(serverURL);
		double startTime = System.currentTimeMillis();

		Response put = given().relaxedHTTPSValidation().headers(headers).body(requestInStringFormat).when()
				.put(serverURL);

		double stopTime = System.currentTimeMillis();

		String response = put.asString();
		Reporter.log("\nServer URL : " + serverURL, true);
		String timeTaken = String.valueOf((stopTime - startTime) / 1000);
		setTimeTaken(timeTaken);
		Reporter.log("\nTotal time taken for the post request is : " + getTimeTaken() + " Seconds ...", true);

		Reporter.log("\nRequest sent is \n" + requestInStringFormat, true);

		Reporter.log("\nResponse Obtained : " + response, true);
		return response;
	}

	public void assertAPIResponseStatus(String api, String response) {

		int flag = 0;

		String expectedStatus = "SUCCESS";

		JsonPath jsonPath = new JsonPath(response);

		String actualStatus = jsonPath.get("response_status.status");
		StringBuilder msg = new StringBuilder();
		if (actualStatus.equalsIgnoreCase("SUCCESS")) {

		
			try {
				Assert.assertEquals(actualStatus, expectedStatus,
						"FAILURE..! Assertion for " + api + " response status failed..!");
				Reporter.log("\nResponse status assertion successful..!", true);
			} catch (AssertionError ae) {
				msg.append("Response status assertion failed. Expected [" + expectedStatus + "], Actual ["
						+ actualStatus + "]\n");
				Reporter.log(ae.getMessage(), true);
				flag = 1;

			}
		} else {
		

			try {
				Assert.assertEquals(actualStatus, expectedStatus,
						"FAILURE..! Assertion for " + api + " response status failed..!");
				Reporter.log("\nResponse status assertion successful..!", true);
			} catch (AssertionError ae) {
				msg.append("Response status assertion failed. Expected [" + expectedStatus + "], Actual ["
						+ actualStatus + "]\n");
				Reporter.log(ae.getMessage(), true);
				flag = 1;
			}

		}

		if (flag == 1) {
			setFailureReason(msg.toString());
			Assert.fail("TEST FAILED!!! Assertion error..!");
		}

	}

	

	
	//Bulk approval using same request with variable id. 
	/**
	 * 
	 * @return
	 */

	public HashMap<String, String> getRandomNameAndImages() {
		URL obj;
		try {
			obj = new URL("https://randomuser.me/api/?nat=us");

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			HashMap<String, String> nameImage = new HashMap<String, String>();
			JsonPath path = new JsonPath(response.toString());
			List<HashMap<String, HashMap<String, String>>> result = path.get("results");
			nameImage.put("NAME", result.get(0).get("name").get("title") + " " + result.get(0).get("name").get("first")
					+ " " + result.get(0).get("name").get("last"));
			nameImage.put("IMAGE", getImageInBase64Encoded(result.get(0).get("picture").get("medium")));
			nameImage.put("EMAIL", String.valueOf(result.get(0).get("email")));
			nameImage.put("USERNAME", result.get(0).get("login").get("username"));
			nameImage.put("SALUTATION", result.get(0).get("name").get("title"));
			nameImage.put("FIRSTNAME", result.get(0).get("name").get("first"));
			nameImage.put("LASTNAME", result.get(0).get("name").get("last"));
			return nameImage;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getDate(int count) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, count);
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
		long stamp = calendar.getTimeInMillis();
		return stamp;
	}
	public String formatVersion(String version){
	
		String formatVersion =String.format("%04d", Integer.parseInt(version));
		System.out.println(formatVersion);
		return formatVersion;
	}

	public String getImageInBase64Encoded(String url) throws IOException {
		URL obj;
		obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");

		// return
		// Base64.encodeBase64String((IOUtils.toByteArray(con.getInputStream())));
		byte[] buffer = new byte[con.getInputStream().available()];
		File outputImage = new File(configProperties.get("imageFolder") + "image.png");
		OutputStream outStream = new FileOutputStream(outputImage);
		outStream.write(buffer);
		outStream.close();
		return configProperties.get("imageFolder") + "image.png";
	}
}