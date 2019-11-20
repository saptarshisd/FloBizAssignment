package in.flobizAPI.utils;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ReporterUtils implements ITestListener,ISuiteListener{

	private ExtentReports extent;
	private ExtentTest test;

	String dir = System.getProperty("user.dir");

	public void onStart(ISuite arg0) {
		
		extent = new ExtentReports("./reports/ApiTestReport.html", true);
	}
	
	public void onFinish(ISuite arg0) {
		
		extent.flush();
		extent.close();
	}
	
	public void onStart(ITestContext arg0) {
		
	}
	
	public void onFinish(ITestContext arg0) {
		
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		
//		String[] clsParts = result.getInstanceName().split("\\.");
//		String clsName = clsParts[(clsParts.length)-1];
//		test.log(LogStatus.FAIL, clsName+" started..!");
//		extent.endTest(test);
	}
	
	public void onTestStart(ITestResult result) {
		
		String[] clsParts = result.getInstanceName().split("\\.");
		String clsName = clsParts[(clsParts.length)-1];
		
		test = extent.startTest(clsName);
		test.log(LogStatus.INFO, clsName+" started..!");
	}

	public void onTestFailure(ITestResult result) {
		
		String[] clsParts = result.getInstanceName().split("\\.");
		String clsName = clsParts[(clsParts.length)-1];
		test.log(LogStatus.PASS, clsName+" failure..!");
		extent.endTest(test);
	}

	public void onTestSkipped(ITestResult result) {
		
		String[] clsParts = result.getInstanceName().split("\\.");
		String clsName = clsParts[(clsParts.length)-1];
		test.log(LogStatus.SKIP, clsName+" skipped..!");
		extent.endTest(test);
	}

	public void onTestSuccess(ITestResult result) {
		
		String[] clsParts = result.getInstanceName().split("\\.");
		String clsName = clsParts[(clsParts.length)-1];
		test.log(LogStatus.PASS, clsName+" Passed..!");
		extent.endTest(test);
	}
	
//	public void onStart(ITestContext result) {
//	}
//
//	public void onFinish(ITestContext result) {
//		
//	}
//
//	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
//	}
//
//	public void onTestStart(ITestResult result){
//		
//		extent = new ExtentReports("./reports/Extent.html", false);
//		String[] clsParts = result.getInstanceName().split("\\.");
//		String clsName = clsParts[(clsParts.length)-1];
//		
//		test = extent.startTest(clsName);
//		test.log(LogStatus.INFO, clsName+" started..!");
//	}
//
//	public void onTestSuccess(ITestResult result) {
//		
////		String[] clsParts = result.getInstanceName().split("\\.");
////		String clsName = clsParts[(clsParts.length)-1];
////		
////		M_BasePage bp=new M_BasePage(null);
////		Row ids=bp.readExcel("sheet1", clsName, 1);
////		
////		test.log(LogStatus.PASS, "Test with ID's : "+ids.getCell(2).getStringCellValue() +" passed..!");
//		extent.endTest(test);
//	}
//	
//	public void onTestFailure(ITestResult result) 
//	{
////		String[] clsParts = result.getInstanceName().split("\\.");
////		String clsName = clsParts[(clsParts.length)-1];
////		
////		M_BasePage bp=new M_BasePage(null);
////		Row ids=bp.readExcel("sheet1", clsName, 1);
////
////		test.log(LogStatus.FAIL, "Test with ID's : "+ids.getCell(2).getStringCellValue() +" failed..!");
////		
////		String filePath = dir+"/screenshots/"+clsName+".png";
////		File file = new File(filePath);
////		byte imageData[] = new byte[(int) file.length()];
////		try {
////			FileInputStream imageInFile = new FileInputStream(file);
////			imageInFile.read(imageData);
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
////
////		// Converting Image byte array into Base64 String
////		String imageDataString = Base64.encodeBase64URLSafeString(imageData);
////		test.log(LogStatus.FAIL, result.getThrowable());
////		test.log(LogStatus.INFO, "HTML", "<img alt=\"Screenshot\" data-featherlight=\"data:image/png;base64,"+imageDataString+"\" width=\"500\" height=\"600\" src=\"data:image/png;base64,"+imageDataString+"\" width=\"500\" height=\"600\"/>");
//		extent.endTest(test);
//	}
//
//	public void onTestSkipped(ITestResult result) {
//		
//		String[] clsParts = result.getInstanceName().split("\\.");
//		String clsName = clsParts[(clsParts.length)-1];
//		test.log(LogStatus.SKIP, clsName);
//		extent.endTest(test);
//	}
//
//
//	@SuppressWarnings("unused")
//	private String getTestMethodName(ITestResult result)
//	{
//		return result.getMethod().getConstructorOrMethod().getName();
//
//	}
//
//	public void onConfigurationFailure(ITestResult result) {
//		
////		String[] clsParts = result.getInstanceName().split("\\.");
////		String clsName = clsParts[(clsParts.length)-1];
////		test.log(LogStatus.FAIL, "Failed "+clsName+" because of configuration failure..!");
//	}
//
//	public void onConfigurationSkip(ITestResult result) {
//		
//		
//	}
//
//	public void onConfigurationSuccess(ITestResult result) {
//		// TODO Auto-generated method stub
////		System.out.println("onConfigurationSuccess");
//	}
//
//	public void onExecutionFinish() {
//		
//		System.out.println("Test Execution Has Been Completed..!");
//	}
//
//	public void onExecutionStart() {
//		
//		System.out.println("Test Execution Has Begun..!");
//	}

}
