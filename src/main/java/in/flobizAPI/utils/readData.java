package in.flobizAPI.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class readData {
	public static HashMap<String, String> readExcelData(String sheetname, String uniqueValue) {
		try {
			String key, value;
			FileInputStream file = new FileInputStream("C://User//Saptarshi//Desktop//TestData.xlsx");
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
						System.out.println(key+" :: "+value);
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

	public static void main(String[] args){
		readExcelData("User-Data","ROLE");
	}
}
