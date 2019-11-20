package in.flobizAPI.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
/*import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;*/
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.testng.Reporter;
import org.testng.SkipException;

public class JavaUtils {
	String stri;
	String fileName;
	static String failureReason;
	public static HashMap<String, String> configProperties = new HashMap<String, String>();
	public static String assertionMessage, timeTaken;
	Properties velocityProps;
	public static HashMap<byte[], String> imageByte = new HashMap<byte[], String>();

	/* Read the properties file and returns a 'Value' for a particular 'Key' */
	public HashMap<String, String> readConfigProperties() {
		String sectionName = null;
		Set<Entry<String, String>> dataSet;
		Ini ini;
		try {
			ini = new Ini(new File("./config.ini"));

			Ini.Section section = ini.get("Common");
			dataSet = section.entrySet();

			sectionName = section.get("configName");
			section = ini.get(sectionName);

			dataSet.addAll(section.entrySet());
			for (Map.Entry<String, String> set : dataSet) {
				configProperties.put(set.getKey().toString(), set.getValue().toString());
			}
			return configProperties;
		} catch (InvalidFileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void addConfigToIni(String key, String value) {
		try {
			Ini ini = new Ini(new File("./config.ini"));
			ini.put("Common", key, value);
			ini.store();
		} catch (InvalidFileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addTestDataIni(String key, String value) {
		try {
			File file = new File("./testData.ini");
			if (!(file.exists())) {
				file.createNewFile();
				Ini ini = new Ini(file);
			}
			Ini ini = new Ini(file);
			ini.put("Common", key, value);
			ini.store();
		} catch (InvalidFileFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int randBetween(int start, int end) {
		return start + (int) Math.round(Math.random() * (end - start));
	}
	
	
	
	
	public String randomString(int length) {
		//Log.info("getting a random string");
		final String randomString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		final java.util.Random rand = new java.util.Random();
		// consider using a Map<String,Boolean> to say whether the identifier is
		// being used or not
		final Set<String> identifiers = new HashSet<String>();

		StringBuilder builder = new StringBuilder();
		while (builder.toString().length() == 0) {
			for (int i = 0; i < length; i++) {
				builder.append(randomString.charAt(rand.nextInt(randomString.length())));
			}
			if (identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString();
	}
	
	
	public String[] getrandomDate(String pattern) {
		/*
		 * String before = "31September1991"; Format formatter = new
		 * SimpleDateFormat("dd MMMM yyyy"); String dobj = formatter.format(new
		 * Date()); System.out.println(dobj);
		 */

		GregorianCalendar gc = new GregorianCalendar();
		int year = randBetween(1970, 1990);

		gc.set(gc.YEAR, year);

		int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));

		gc.set(gc.DAY_OF_YEAR, dayOfYear);

		// String dob= (gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" +
		// gc.get(gc.DAY_OF_MONTH));
		Format formatter = null;
		String dob = new SimpleDateFormat("yyyy-MM-dd").format(gc.getTime());
		/*if (pattern.equals("dd MMMM yyyy")) {
			formatter = new SimpleDateFormat(pattern);
		} else if (pattern.equals("dd/MM/yyyy")) {
			formatter = new SimpleDateFormat(pattern);
		}*/
		formatter = new SimpleDateFormat(pattern);

		String date = formatter.format(gc.getTime());

		return new String[] { dob, date };

	}
	
	public static String getvalueFromIni(String name) {
		Ini ini;
		try {
			ini = new Ini(new File("./config.ini"));
			return ini.get("Common", name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getvalueFromTestDataIni(String name) {
		Ini ini;
		try {
			ini = new Ini(new File("./testData.ini"));
			return ini.get("Common", name);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void addMobConfigToIni(String sheetName, String sectionName)

			throws EncryptedDocumentException, InvalidFormatException, IOException {
		Ini ini = new Ini(new File("./data.ini"));
		FileInputStream file = new FileInputStream(configProperties.get("testData"));
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheet(sheetName);
		Iterator<Row> it = sheet.rowIterator();
		Row Headers = it.next();
		while (it.hasNext()) {
			Row record = it.next();
			if (record.getCell(2).toString().equalsIgnoreCase("RANDOM")) {
				ini.put(sectionName, record.getCell(0).toString(), generateRandomNo(10));

			} else {

				record.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
				ini.put(sectionName, record.getCell(0).toString(), record.getCell(2).toString());

			}
		}
		ini.store();
	}

	public String checkExecutionStatus(String workbook, String sheetName, String testCaseID) {

		HashMap<String, String> testRow = readExcelData(workbook, sheetName, testCaseID);

		/*
		 * Checks the execution status of the current testCaseID which is set in
		 * the Excel - TestData sheet if marked 'Yes' testCase would execute ,
		 * else testCase would skip
		 */
		if (testRow.get("Execution Status").toLowerCase().equalsIgnoreCase("no")) {
			throw new SkipException(
					"Skipping the test flow with ID " + testCaseID + " as it marked 'NO' in the Execution Excel Sheet");
		}

		Reporter.log("\nExecuting the " + testRow.get("Test Description") + " : " + testCaseID, true);
		return testCaseID;
	}

	/* Returns the values in column1 of the TestData in an ArrayList */
	public ArrayList<String> returnRowsUniqueValueBasedOnClassName(String sheetName, Class<?> className) {

		String[] clsParts = className.getName().split("\\.");
		String clsName = clsParts[(clsParts.length) - 1];
		// String[] allValues = null;
		ArrayList<String> allValues = new ArrayList<String>();
		try {
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();

			Row headers = it.next();
			int i = 1;
			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(1).toString() + "".trim();
				if (cellValue.equalsIgnoreCase(clsName)) {
					allValues.add(record.getCell(0).toString() + "".trim());
				}
			}
			return allValues;
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (InvalidFormatException e) {
			throw new NullPointerException("Failed due to InvalidFormatException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}

	/*
	 * return List of HashMap with data read from excel sheet
	 */
	public List<HashMap<String, String>> returnRowsUniqueValueBasedOnTestTypeList(String workbookName, String sheetName,
			String testType) {

		HashMap<String, String> dataMap = new HashMap<String, String>();
		List<HashMap<String, String>> allValues = new ArrayList<HashMap<String, String>>();
		try {
			FileInputStream file = new FileInputStream(configProperties.get(workbookName));
			if (file != null) {
				System.out.println();
			}
			System.out.println(configProperties.get(workbookName));
			String key, value;
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			boolean flag = false;
			Iterator<Row> it = sheet.rowIterator();
			int i = 0;
			Row headers = it.next();
			while (it.hasNext()) {

				Row record = it.next();

				// if ((record.getCell(3).toString().trim() +
				// "").equalsIgnoreCase("yes")) {
				if (testType.equalsIgnoreCase("no-check")) {
					flag = true;
				} else if ((record.getCell(1).toString().trim() + "").equalsIgnoreCase(testType)) {
					flag = true;
				}

				// }
				if (flag == true) {
					for (i = 0; i < headers.getLastCellNum(); i++) {
						if ((null != record.getCell(i))
								&& (record.getCell(i).getCellType() == record.getCell(i).CELL_TYPE_NUMERIC)) {
							if (HSSFDateUtil.isCellDateFormatted(record.getCell(i))) {

								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

								value = dateFormat.format(record.getCell(i).getDateCellValue()).trim();

							} else {
								record.getCell(i).setCellType(Cell.CELL_TYPE_STRING);

								value = record.getCell(i).toString().trim();
							}
							key = headers.getCell(i).toString().trim();

						} else {

							key = (headers.getCell(i) + "".toString()).trim() + "";
							value = (null != record.getCell(i)) ? (record.getCell(i) + "".toString()).trim() + "" : "";
						}
						dataMap.put(key, value);
					}
					allValues.add(dataMap);
					dataMap = new HashMap<String, String>();
				}
				flag = false;
			}

			return allValues;

		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (InvalidFormatException e) {
			throw new NullPointerException("Failed due to InvalidFormatException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}

	}

	//

	public HashMap<Integer, String[]> returnRowsUniqueValueBasedOnClassNameList(String sheetName, Class<?> className) {

		String[] clsParts = className.getName().split("\\.");
		String clsName = clsParts[(clsParts.length) - 1];
		// String[] allValues = null;

		HashMap<Integer, String[]> allValues = new HashMap<Integer, String[]>();
		try {
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();
			int i = 0;
			Row headers = it.next();
			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(1).toString() + "";
				if (cellValue.equalsIgnoreCase(clsName)) {
					allValues.put(i, new String[] { record.getCell(0).toString(), record.getCell(5).toString(),
							record.getCell(6).toString(), record.getCell(7).toString() });
					i++;
				}
			}
			return allValues;
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (InvalidFormatException e) {
			throw new NullPointerException("Failed due to InvalidFormatException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}
	//

	/*
	 * Returns the ArrayList to Two-Dimensional Object array for dataProvider
	 * Iteration
	 */
	public Object[][] returnAllUniqueValues(String sheetName, Class<?> className) {

		ArrayList<String> listValues = returnRowsUniqueValueBasedOnClassName(sheetName, className);

		Object[][] allValues = new Object[listValues.size()][1];
		for (int i = 0; i < listValues.size(); i++) {
			allValues[i][0] = listValues.get(i);
		}
		return allValues;
	}

	public Object[][] returnAllUniqueValuesInArray(String sheetName, Class<?> className) {

		HashMap<Integer, String[]> listValues = returnRowsUniqueValueBasedOnClassNameList(sheetName, className);

		Object[][] allValues = new Object[listValues.size()][];

		for (int i = 0; i < listValues.size(); i++) {
			allValues[i] = new Object[listValues.get(i).length];
			allValues[i] = listValues.get(i);
		}

		return allValues;
	}

	public Object[][] returnAllUniqueValuesInMap(String workbookName, String sheetName, String testType) {

		List<HashMap<String, String>> listValues = returnRowsUniqueValueBasedOnTestTypeList(workbookName, sheetName,
				testType);

		Object[][] allValues = new Object[listValues.size()][1];

		for (int i = 0; i < listValues.size(); i++) {
			allValues[i][0] = listValues.get(i);

		}
		return allValues;
	}

	/*
	 * Puts all the excels rows from startRowValue to endRowValue and returns
	 * Two-Dimensional Object array for dataProvider Iteration
	 */
	public Object[][] returnRowsUniqueValueInArray(String sheetName, String startRowValue, String endRowValue) {

		Object[][] values = new String[3][1];
		try {
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetName);
			Iterator<Row> it = sheet.rowIterator();

			Row headers = it.next();
			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(0).toString();
				if (cellValue.equalsIgnoreCase(startRowValue)) {
					int j = 0;

					while (!(record.getCell(0).toString().equalsIgnoreCase(endRowValue))) {
						values[j][0] = record.getCell(0).toString();
						j++;
						record = it.next();
					}
					values[j][0] = record.getCell(0).toString();
					break;
				}
				break;
			}
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (InvalidFormatException e) {
			throw new NullPointerException("Failed due to InvalidFormatException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}

		return values;
	}

	public HashMap<String, String> readExcelData(String workbook, String sheetname, String uniqueValue) {
		try {
			String key, value;
			FileInputStream file = new FileInputStream(configProperties.get(workbook));
			HashMap<String, String> dataMap = new HashMap<String, String>();
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetname);
			Iterator<Row> it = sheet.rowIterator();

			Row headers = it.next();
			while (it.hasNext()) {

				Row record = it.next();
				String cellValue = record.getCell(0).toString().trim();
				if (cellValue.equalsIgnoreCase(uniqueValue)) {

					for (int i = 0; i < record.getLastCellNum(); i++) {
						if (record.getCell(i).getCellType() == record.getCell(i).CELL_TYPE_NUMERIC) {
							if (HSSFDateUtil.isCellDateFormatted(record.getCell(i))) {

								DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

								value = dateFormat.format(record.getCell(i).getDateCellValue()).trim();

							} else {
								record.getCell(i).setCellType(Cell.CELL_TYPE_STRING);

								value = record.getCell(i).toString().trim();
							}
							key = headers.getCell(i).toString().trim();

						} else {

							key = headers.getCell(i).toString().trim();
							value = record.getCell(i).toString().trim();
						}
						dataMap.put(key, value);
					}
					break;
				}
			}
			return dataMap;
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (InvalidFormatException e) {
			throw new NullPointerException("Failed due to InvalidFormatException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}

	public HashMap<String, String> readExcelDataHeaders(String sheetname) {
		try {
			String key, value;
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");
			HashMap<String, String> dataMap = new HashMap<String, String>();
			Workbook wb = WorkbookFactory.create(file);
			Sheet sheet = wb.getSheet(sheetname);
			Iterator<Row> it = sheet.rowIterator();

			while (it.hasNext()) {

				Row headers = it.next();
				for (int i = 0; i < headers.getLastCellNum(); i++) {
					key = headers.getCell(i).toString();
					value = headers.getCell(i).toString();
					dataMap.put(key, value);
				}
				break;
			}
			return dataMap;
		} catch (NullPointerException e) {
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (InvalidFormatException e) {
			throw new NullPointerException("Failed due to InvalidFormatException" + e);
		} catch (IOException e) {
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}

	public String getTodaysDate(String format) {

		Format formatter = new SimpleDateFormat(format);
		String todaysDate = formatter.format(new Date());
		return todaysDate;
	}

	public String generateRandomNumber(int number) {

		Random ran = new Random();
		int x = ran.nextInt(number);
		String randomNo = "1528900" + String.valueOf(x);
		return randomNo;
	}

	public String getTodaysDateAndTime() {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar cal = Calendar.getInstance();
		Date tdy = cal.getTime();
		String today = df.format(tdy);

		return today;
	}

	public String getRequiredDateandTime(int daysToAdd) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar cal1 = Calendar.getInstance();
		cal1.add(Calendar.DATE, daysToAdd);
		Date day = cal1.getTime();
		String reqDate = df.format(day);

		return reqDate;
	}

	public void printHeaders(Map<String, String> headers) {

		Reporter.log("\nHeaders used are : ", true);
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			Reporter.log(entry.getKey() + " : " + entry.getValue(), true);
		}
	}

	public Map<String, String> readHeadersFromExcel(String sheetname, String headersToCall) {

		Workbook wb;
		try {

			HashMap<String, String> headers = new HashMap<String, String>();

			String key, value;
			FileInputStream file = new FileInputStream("./test-data/TestData.xlsx");

			wb = WorkbookFactory.create(file);

			Sheet sheet = wb.getSheet(sheetname);

			for (Row currentRow : sheet) {
				if (currentRow.getCell(0).getStringCellValue().toLowerCase().equals(headersToCall)) {
					Row headerKeyRow = sheet.getRow(currentRow.getRowNum() + 1);
					Row headerValueRow = sheet.getRow(currentRow.getRowNum() + 2);
					for (int i = 0; i < (headerKeyRow.getLastCellNum() - headerKeyRow.getFirstCellNum()); i++) {
						key = headerKeyRow.getCell(i).getStringCellValue();
						if (headerValueRow.getCell(i).getCellType() == headerValueRow.getCell(i).CELL_TYPE_NUMERIC) {
							headerValueRow.getCell(i).setCellType(Cell.CELL_TYPE_STRING);
						}
						value = headerValueRow.getCell(i).getStringCellValue();
						headers.put(key, value);
					}
					return headers;
				}
			}
			return headers;

		} catch (NullPointerException e) {
			Reporter.log("Unable to load headers from the excelsheet..!");
			throw new NullPointerException("Failed due to NullPointerException" + e);
		} catch (EncryptedDocumentException e) {
			Reporter.log("Unable to load headers from the excelsheet..!");
			throw new EncryptedDocumentException("Failed due to EncryptedDocumentException" + e);
		} catch (InvalidFormatException e) {
			Reporter.log("Unable to load headers from the excelsheet..!");
			throw new NullPointerException("Failed due to InvalidFormatException" + e);
		} catch (IOException e) {
			Reporter.log("Unable to load headers from the excelsheet..!");
			throw new NullPointerException("Failed due to IOException" + e);
		}
	}

	/*
	 * Returns a random number for mobile number using utils from apache commons
	 */
	public String generateRandomStan() {

		return RandomStringUtils.randomAlphanumeric(5);

	}

	/*
	 * Returns a random number for stan using utils from apache commons
	 */
	public String generateRandomClientRefNumber() {

		return RandomStringUtils.randomNumeric(12);

	}

	public String generateRandomAlphaString(int count) {
		return RandomStringUtils.randomAlphabetic(count);
	}
	public String generateRandomAlphaNumericString(int count){
		String s=RandomStringUtils.randomAlphanumeric(10);
		String alphaNum=s.toUpperCase();
			return alphaNum;
	}

	/*
	 * Returns a random number for stan
	 */
	public String generateRandomNo(int count) {
		return "8" + RandomStringUtils.randomNumeric(count - 1);
	}

	/*
	 * Returns a random number for stan
	 */
	public String returnRandomNumber() {

		Random rand = new Random();
		BigInteger upperLimit = new BigInteger("999999999999999");
		BigInteger result;
		do {
			result = new BigInteger(upperLimit.bitLength(), rand); // (2^4-1) =
																	// 15 is the
																	// maximum
																	// value
		} while (result.compareTo(upperLimit) >= 0); // exclusive of 13

		return result.toString();
	}

	/*
	 * Writes the API Execution details by creating new sheet for every run to
	 * Excel Report File, Iterates through the cells for a particular testcaseID
	 * and populates the data
	 */
	public void writeExecutionStatusToExcel(String[] APIExecutionDetails) throws InvalidFormatException, IOException {

		try {
			int rowToUpdate = 0;
			File file = new File(configProperties.get("testReport"));
			if (!(file.exists())) {
				file.createNewFile();
				Workbook workbook = new HSSFWorkbook();
				Sheet worksheet = workbook.createSheet(configProperties.get("reportSheetName"));
				Row headers = worksheet.createRow(0);

				headers.createCell(0).setCellValue("BUILD NUMBER");
				headers.createCell(1).setCellValue("API NAME");
				headers.createCell(2).setCellValue("TCID");
				headers.createCell(3).setCellValue("TEST DESCRIPTION");
				headers.createCell(4).setCellValue("RESULT");
				headers.createCell(5).setCellValue("(WARNING) REASON OF FAILURE");
				headers.createCell(6).setCellValue("RESPONSE TIME");
				FileOutputStream fileOut = new FileOutputStream(file);
				workbook.write(fileOut);
				workbook.close();
				fileOut.close();
			}
			FileInputStream fileIn = new FileInputStream(file);
			Workbook workbook = WorkbookFactory.create(fileIn);
			Sheet worksheet = workbook.getSheet(configProperties.get("reportSheetName"));
			rowToUpdate = worksheet.getLastRowNum() + 1;
			int i;
			Row record = worksheet.createRow(rowToUpdate);
			Cell cell = null;
			for (i = 0; i <=APIExecutionDetails.length; i++) {
				cell = record.createCell(i);
				if (i == 6) {
					cell.setCellValue(getTimeTaken());
				} else {
					cell.setCellValue(APIExecutionDetails[i]);
				}
				}
			FileOutputStream fileOut = new FileOutputStream(new File(configProperties.get("testReport")));
			workbook.write(fileOut);
			workbook.close();
			fileOut.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Returns the test case execution status based on its execution status code
	 */
	public String getExecutionResultStatus(int statusCode) {

		String testStatus = null;
		if (statusCode == 1) {
			testStatus = "PASS";
		} else if (statusCode == 2) {
			testStatus = "FAIL";
		} else if (statusCode == 3) {
			testStatus = "SKIP";
		}

		return testStatus;
	}

	/*
	 * Returns the set of all API's executed, as per its excel data
	 */
	public Set<String> returnAllAPINames(String excelFileName, String sheetName)
			throws EncryptedDocumentException, InvalidFormatException, IOException {

		Set<String> allAPI = new HashSet<String>();

		FileInputStream file = new FileInputStream(excelFileName);
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheet(sheetName);
		Iterator<Row> it = sheet.rowIterator();

		Row headers = it.next();
		while (it.hasNext()) {

			Row record = it.next();
			String cellValue = record.getCell(2).toString();
			allAPI.add(cellValue);
		}
		return allAPI;
	}

	/*
	 * Returns the total, passed, failed and skipped tests for a particular API
	 * from its Excel data
	 */
	public int[] returnTestCountPerAPI(String excelFileName, String sheetName, String API)
			throws EncryptedDocumentException, InvalidFormatException, IOException {

		FileInputStream file = new FileInputStream(excelFileName);
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheet(sheetName);
		Iterator<Row> it = sheet.rowIterator();
		int total = 0, pass = 0, fail = 0, skip = 0;

		Row headers = it.next();
		while (it.hasNext()) {

			Row record = it.next();
			String cellValue = record.getCell(2).toString().trim();
			if (cellValue.equalsIgnoreCase(API)) {
				String status = record.getCell(4).toString().trim();
				if (status.equalsIgnoreCase("PASS")) {
					pass++;
					total++;
				} else if (status.equalsIgnoreCase("FAIL")) {
					fail++;
					total++;
				} else if (status.equalsIgnoreCase("SKIP")) {
					skip++;
					total++;
				}
			}
		}
		return new int[] { total, pass, fail, skip };
	}

	/*
	 * Puts the values to the velocity template by iterating through all the
	 * values in the variable array
	 */
	/*
	 * public String putValuesToVelocityFile(String fileName, String[]
	 * variablesToInsert) {
	 * 
	 * initializeVelocityTemplate(); VelocityContext velocityContext = new
	 * VelocityContext(); for (int i = 0; i < variablesToInsert.length; i++) {
	 * 
	 * String currValue = variablesToInsert[i]; if (currValue.length() != 0) {
	 * velocityContext.put("variable" + i, variablesToInsert[i]); } else {
	 * velocityContext.put("variable" + i, ""); } }
	 * 
	 * Template template = null; StringWriter writer = null; try { template =
	 * Velocity.getTemplate(fileName); writer = new StringWriter();
	 * template.merge(velocityContext, writer); } catch
	 * (ResourceNotFoundException e) { throw new
	 * NullPointerException("Failed due to ResourceNotFoundException" + e); }
	 * catch (ParseErrorException e) { throw new
	 * NullPointerException("Failed due to ParseErrorException" + e); } catch
	 * (Exception e) { throw new NullPointerException("Failed due to Exception"
	 * + e); }
	 * 
	 * stri = writer.toString(); String requestInStringFormat =
	 * JsonOutput.prettyPrint(stri); return requestInStringFormat; }
	 * 
	 * /* Initializes the velocity templates so as it input the values to it
	 */
	/*
	 * public void initializeVelocityTemplate() {
	 * 
	 * velocityProps = new Properties();
	 * velocityProps.setProperty("resource.loader", "file");
	 * velocityProps.setProperty("file.resource.loader.class",
	 * "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
	 * velocityProps.setProperty("file.resource.loader.path",
	 * System.getProperty("user.dir") + "/test-data/jsons");
	 * velocityProps.setProperty("file.resource.loader.cache", "true");
	 * velocityProps.setProperty(
	 * "file.resource.loader.modificationCheckInterval", "2");
	 * 
	 * try { Velocity.init(velocityProps); } catch (Exception e) {
	 * System.out.println("Initializing the velocity template failed..!"); throw
	 * new NullPointerException("Failed due to Exception" + e); } }
	 */

	public String report() throws EncryptedDocumentException, InvalidFormatException, IOException {
		StringBuilder form = new StringBuilder();
		HashMap<String, int[]> result = consolidatedReport();
		int totalTestExecuted = 0, totalPassed = 0, totalFailed = 0;
		form.append(
				"<html>" + "<table style='border-spacing: 0px; padding:5px; font-family: monospace; font-size: 1em;'>"
						+ "<tr style='background-color:#a3a3f5;font-weight: bold;font-family: monospace;font-size: 1.1em;'> "
						+ "<td style='border:1px solid;padding:5px'>DATE OF EXECUTION</td>"
						+ "<td style='border:1px solid;padding:5px'>API NAME</td>"
						+ "<td style='border:1px solid;padding:5px'>SPRINT</td>"
						+ "<td style='border:1px solid;padding:5px'>TOTAL TEST EXECUTED</td>"
						+ "<td style='border:1px solid;padding:5px'>TOTAL PASSED</td>"
						+ "<td style='border:1px solid;padding:5px'>TOTAL FAILED</td>" + "</tr>");
		for (Map.Entry<String, int[]> data : result.entrySet()) {
			form.append("<tr style='font-family: monospace;font-size: 1em'>"
					+ "<td style='border:1px solid;text-align: center;padding:5px'>" + getTodaysDate("dd-MM-yyyy")
					+ "</td>" + "<td style='border:1px solid;padding:5px'>" + data.getKey() + "</td>"
					+ "<td style='border:1px solid;text-align: center;padding:5px'>"
					+ configProperties.get("buildNumber") + "</td>"
					+ "<td style='border:1px solid;text-align: center;padding:5px'>" + data.getValue()[2] + "</td>"
					+ "<td style='border:1px solid;text-align: center;padding:5px'>" + data.getValue()[0] + "</td>"
					+ "<td style='border:1px solid;text-align: center;padding:5px'>" + data.getValue()[1] + "</td>"
					+ "</tr>");
			totalTestExecuted += data.getValue()[2];
			totalPassed += data.getValue()[0];
			totalFailed += data.getValue()[1];

		}
		form.append("<tr style='font-family: monospace;font-size: 1em'>"
				+ "<td style='border:1px solid;text-align: center;padding:5px'></td>"
				+ "<td style='border:1px solid;padding:5px'></td>"
				+ "<td style='border:1px solid;text-align: center;padding:5px'></td>"
				+ "<td style='border:1px solid;text-align: center;padding:5px'>" + totalTestExecuted + "</td>"
				+ "<td style='border:1px solid;text-align: center;padding:5px'>"
				+ (totalPassed * 100 / totalTestExecuted) + " %</td>"
				+ "<td style='border:1px solid;text-align: center;padding:5px'>"
				+ (totalFailed * 100 / totalTestExecuted) + " %</td>" + "</tr>");
		form.append("</table></html>");
		return form.toString();
	}

	public HashMap<String, int[]> consolidatedReport()
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		String sheetname = configProperties.get("reportSheetName");
		FileInputStream file = new FileInputStream(configProperties.get("testReport"));
		HashMap<String, int[]> executionResult = new HashMap<String, int[]>();
		Workbook wb = WorkbookFactory.create(file);
		Sheet sheet = wb.getSheet(sheetname);
		Iterator<Row> it = sheet.rowIterator();
		Row headers = it.next();
		while (it.hasNext()) {
			Row record = it.next();
			String api = record.getCell(1).toString();
			String result = record.getCell(4).toString();
			if (null != executionResult.get(api)) {
				if (result.equalsIgnoreCase("PASS")) {
					++executionResult.get(api)[0];
				} else if (result.equalsIgnoreCase("FAIL")) {
					++executionResult.get(api)[1];
				}
				++executionResult.get(api)[2];
			} else {
				if (result.equalsIgnoreCase("PASS")) {
					executionResult.put(api, new int[] { 1, 0, 1 });
				} else if (result.equalsIgnoreCase("FAIL")) {
					executionResult.put(api, new int[] { 0, 1, 1 });
				}

			}

		}

		/*
		 * for(Map.Entry<String, int[]> value : executionResult.entrySet()){
		 * String key = value.getKey(); int [] arr = value.getValue();
		 * System.out.println(key+"  " +Arrays.toString(arr)); }
		 */

		return executionResult;
	}

	public String getFailureReason() {
		return JavaUtils.failureReason;
	}

	public void setFailureReason(String msg) {
		JavaUtils.failureReason = msg;
	}

	public String getTimeTaken() {
		return JavaUtils.timeTaken;
	}

	public void setTimeTaken(String tt) {
		JavaUtils.timeTaken = tt;
	}

	public List<HashMap<String, String>> getListofHashMap(HashMap<String, String> usrData, String[] keys) {
		List<HashMap<String, String>> out = new ArrayList<HashMap<String, String>>();
		HashMap<String, ArrayList<String>> innerMap = new HashMap<String, ArrayList<String>>();
		HashMap<String, String> data = new HashMap<String, String>();
		for (String k : keys) {
			Set<String> key = new TreeSet<>(usrData.keySet().stream()
					.filter(s -> s.toLowerCase().startsWith(k.toLowerCase())).collect(Collectors.toSet()));
			innerMap.put(k, new ArrayList<String>(key));
		}
		/*
		 * for (int i = 0; i < keys.length; i++) { for (int j = 0; j <
		 * innerMap.get(keys[0]).size(); j++) { data.put(keys[i],
		 * innerMap.get(keys[i]).get(j)); } out.add(data); data = new
		 * HashMap<String, String>(); }
		 */
		for (int j = 0; j < innerMap.get(keys[0]).size(); j++) {
			for (int i = 0; i < keys.length; i++) {
				data.put(keys[i], innerMap.get(keys[i]).get(j));
			}

			out.add(data);
			data = new HashMap<String, String>();
		}
		return out;
	}

}
