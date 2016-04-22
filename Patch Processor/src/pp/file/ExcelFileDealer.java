package pp.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class ExcelFileDealer {

	private HSSFWorkbook workBook;
	private HSSFSheet sheet;
	private int totalColumn;

	// construct a excelfiledealer with workbook and sheet created
	public ExcelFileDealer() {
		totalColumn = 0;
		workBook = new HSSFWorkbook();
		sheet = workBook.createSheet();
	}

	// add a column of strings to the sheet
	public void addData(String fieldName, ArrayList<String> fileList) {
		workBook = addDataP(fieldName, fileList);
	}

	// add a column of strings to the sheet
	private HSSFWorkbook addDataP(String fieldName, ArrayList<String> fileList) {
		int rowN = fileList.size();
		HSSFRow headRow = (sheet.getRow(0) == null ? sheet.createRow(0) : sheet.getRow(0));
		HSSFCell cell = headRow.createCell(totalColumn);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		sheet.setColumnWidth(totalColumn, 6000);			
		HSSFCellStyle style = workBook.createCellStyle();
		HSSFFont font = workBook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		short color = HSSFColor.RED.index;
		font.setColor(color);
		style.setFont(font);
		cell.setCellStyle(style);
		cell.setCellValue(new HSSFRichTextString(fieldName));

		for (int i = 0; i < rowN; i++) {
			HSSFRow row = (sheet.getRow(i + 1) == null ? sheet.createRow(i + 1) : sheet.getRow(i + 1));
			HSSFCell cellData = row.createCell(totalColumn);
			cellData.setCellValue(new HSSFRichTextString(fileList.get(i)));
		}
		totalColumn++;
		return workBook;

	}  

	// add a column of data to the chart
	public void addSubData(String fieldName, ArrayList<Double> data) {
		workBook = addSubDataP(fieldName, data);
	}

	// add a column of data to the chart
	private HSSFWorkbook addSubDataP(String fieldName, ArrayList<Double> data) {
		int rowN = data.size();
		HSSFRow headRow = (sheet.getRow(0) == null ? sheet.createRow(0) : sheet.getRow(0));
		HSSFCell cell = headRow.createCell(totalColumn);
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		sheet.setColumnWidth(totalColumn, 6000);			
		HSSFCellStyle style = workBook.createCellStyle();
		HSSFFont font = workBook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		short color = HSSFColor.RED.index;
		font.setColor(color);
		style.setFont(font);
		cell.setCellStyle(style);
		cell.setCellValue(new HSSFRichTextString(fieldName));

		for (int i = 0; i < rowN; i++) {
			HSSFRow row = (sheet.getRow(i + 1) == null ? sheet.createRow(i + 1) : sheet.getRow(i + 1));
			HSSFCell cellData = row.createCell(totalColumn);
			cellData.setCellValue(new HSSFRichTextString(data.get(i).toString()));
		}
		totalColumn++;
		return workBook;
	}

	// deserted method
	public void outputExcel(String fileName) {  
		FileOutputStream fos = null;  
		try {  
			fos = new FileOutputStream(fileName);  
			workBook.write(fos);  
			fos.flush();
			fos.close();  
		} catch (FileNotFoundException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
	}

	// deserted method...
	public void exportExcel(OutputStream os) throws IOException {
		workBook.write(os);
		os.flush();
		os.close();
	}

	// output the excel file
	public void outputExcel(String fileName, String dir) throws IOException {
		File f = new File(dir, fileName);
		f.createNewFile();
		FileOutputStream fos = null;  
		fos = new FileOutputStream(dir + "//" + fileName);  
		workBook.write(fos);  
		fos.flush();
		fos.close();    
	}

}