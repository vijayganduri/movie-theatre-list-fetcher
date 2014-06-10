package com.vijayganduri.theatrescraper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ReadWriteExcelFile {

	private static final String FILE_NAME = "TheatresList.xls" ;

	private static final String WORKSHEET_TITLE = "All Theatres";

	private static final String WS_COLUMN_PAGE_NO = "pageno";
	private static final String WS_COLUMN_THEATRE = "theatre";
	private static final String WS_COLUMN_ADDRESS = "address";
	private static final String WS_COLUMN_PLACE = "place";
	private static final String WS_COLUMN_COUNTRY = "country";
	private static final String WS_COLUMN_PHONE = "phone";

	private static final String TAG = ReadWriteExcelFile.class.getName();

	public void createFile(int pageno, List<Theatre> theatres) throws Exception{
		try{

			int lastRow = 0;
			HSSFWorkbook workbook = null;
			HSSFSheet worksheet = null;

			File file = new File("out", FILE_NAME);

			if(file.exists()){
				FileInputStream fileInputStream = new FileInputStream(file);				
				workbook = new HSSFWorkbook(fileInputStream);
				worksheet = workbook.getSheet(WORKSHEET_TITLE);
				lastRow = worksheet.getLastRowNum();
				fileInputStream.close();
			}else{				
				workbook = new HSSFWorkbook();
				worksheet =  workbook.createSheet(WORKSHEET_TITLE);

				HSSFRow rowhead=   worksheet.createRow((short)0);
				rowhead.createCell((short) 0).setCellValue(WS_COLUMN_PAGE_NO);
				rowhead.createCell((short) 1).setCellValue(WS_COLUMN_THEATRE);
				rowhead.createCell((short) 2).setCellValue(WS_COLUMN_ADDRESS);
				rowhead.createCell((short) 3).setCellValue(WS_COLUMN_PLACE);
				rowhead.createCell((short) 4).setCellValue(WS_COLUMN_COUNTRY);
				rowhead.createCell((short) 5).setCellValue(WS_COLUMN_PHONE);
			}

			for(int i=0;i<theatres.size();i++){
				HSSFRow row=   worksheet.createRow((short)(lastRow+i+1));
				row.createCell((short) 0).setCellValue(String.valueOf(pageno));
				row.createCell((short) 1).setCellValue(theatres.get(i).getTheatre());
				row.createCell((short) 2).setCellValue(theatres.get(i).getAddress());
				row.createCell((short) 3).setCellValue(theatres.get(i).getPlace());
				row.createCell((short) 4).setCellValue(theatres.get(i).getCountry());
				row.createCell((short) 5).setCellValue(theatres.get(i).getPhone());
			}
			FileOutputStream fileOut =  new FileOutputStream(file);
			workbook.write(fileOut);

			fileOut.close();

		} catch ( Exception ex ) {
			errprintln(TAG+";  Write exception : Fix this.."+ex);
			throw ex;
		}
	}

	protected int getLastSavedPosition(){
		File file = new File("out", FILE_NAME);

		if(file.exists()){
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
				HSSFSheet worksheet = workbook.getSheet(WORKSHEET_TITLE);

				int lastRow = worksheet.getLastRowNum();
				if(lastRow==0){
					return 0;
				}

				HSSFRow bottomRow = worksheet.getRow(lastRow);

				fileInputStream.close();

				int lastPos = Integer.parseInt(bottomRow.getCell(0).getStringCellValue());

				return lastPos;
			} catch (Exception e) {
				errprintln(TAG+" exception getting pos : "+e);
			}
		}

		return 0;
	}

	private static void errprintln(String msg){
		System.err.println(msg);
	}

}
