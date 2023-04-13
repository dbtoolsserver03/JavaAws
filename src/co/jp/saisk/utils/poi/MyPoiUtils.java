package co.jp.saisk.utils.poi;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import co.jp.saisk.utils.MyConst;
import co.jp.saisk.utils.base.MyDateUtil;
import co.jp.saisk.utils.base.MyStrUtils;

public class MyPoiUtils {

	public static void main(String[] args) {
		
	}

	public static Workbook getWorkBook(String filePath) throws Throwable {
		File f = new File(filePath);
		FileInputStream fileIn = new FileInputStream(f);
		Workbook wb;

		// 拡張子を取得
		String endStr = getFileExtension(f.getName());

		if (endStr.length() == 4) {
			wb = new HSSFWorkbook(fileIn);
		} else {
			wb = new XSSFWorkbook(fileIn);
		}
		return wb;
	}

	public static String getCellContent(Cell cell, boolean checkStrikeOut) throws Throwable {

		String retStr = "";
			if (null == cell) {
				return retStr;
			}
			if (checkStrikeOut) {
				Font font;
				if (cell instanceof HSSFCell) {
					HSSFCell c = (HSSFCell)cell;
					font = c.getCellStyle().getFont(c.getRow().getSheet().getWorkbook());
					if (font.getStrikeout()) {
						return retStr;
					}
				} else if (cell instanceof XSSFCell) {
					XSSFCell c = (XSSFCell)cell;
					font = c.getCellStyle().getFont();
					if (font.getStrikeout()) {
						return retStr;
					} 
				}
			}
			retStr = getCellStr(cell);
		
		return retStr;
	}

	public static MyCellInfo getCellInfo(Cell cell) throws Throwable {

		if (null == cell) {
			return null;
		}
		MyCellInfo retVal = null;
			retVal = new MyCellInfo(getCellStr(cell),cell.getRowIndex(),cell.getColumnIndex());
			Font font = null;
			if (cell instanceof HSSFCell) {
				HSSFCell c = (HSSFCell)cell;
				font = c.getCellStyle().getFont(c.getRow().getSheet().getWorkbook());
			} else {
				XSSFCell c = (XSSFCell)cell;
				font = c.getCellStyle().getFont();
			}
			
			CellStyle cs = cell.getCellStyle();

			retVal.setCs(cs);
			retVal.setFont(font);
		return retVal;
	}
	/**
	 * @param cell
	 * @param retStr
	 * @return
	 * @throws Throwable 
	 */
	private static String getCellStr(Cell cell) throws Throwable {
		
		String retStr = "";
			switch (cell.getCellType()) {
			case STRING:
				retStr = cell.getRichStringCellValue().getString();
				break;
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					retStr = MyDateUtil.getFormatDateTime(cell.getDateCellValue(), MyConst.YYYY_MM_DD_SLASH);
				} else {
					retStr = String.valueOf(cell.getNumericCellValue());
				}
				break;
			case BOOLEAN:
				retStr = String.valueOf(cell.getBooleanCellValue());
				break;
			case FORMULA:
				try {
					if (DateUtil.isCellDateFormatted(cell)) {
						retStr = MyDateUtil.getFormatDateTime(cell.getDateCellValue(), MyConst.YYYY_MM_DD_SLASH);
					} else {
						retStr = String.valueOf(cell.getNumericCellValue());
						retStr = MyStrUtils.getNumberByTrimDot0(retStr);
					}
				} catch (Throwable e) {
					try {
						retStr = cell.getRichStringCellValue().getString();
					} catch (Throwable e1) {
						try {
							retStr = MyDateUtil.getFormatDateTime(cell.getDateCellValue(), MyConst.YYYY_MM_DD_SLASH);
						} catch (Throwable e2) {
							retStr = cell.getCellFormula();
						}
					}
				}
				break;
			default:
				retStr = String.valueOf(cell.getStringCellValue());
			}
		
		return retStr;
	}
	public static String getCellContent(Row row, int colIndex, boolean checkStrikeOut) throws Throwable {

		String retStr = "";
			if (row == null) {
				return "";
			}
	
			Cell cell = row.getCell(colIndex);
			if(cell == null) {
				return "";
			}
			retStr = getCellContent(cell, checkStrikeOut);
			
		return retStr;
	}
	
	public static String getFileExtension(String filePath) {
		// ファイルのパスを指定してファイルオブジェクトを作成する
	    File targetFile = new File(filePath);

	    String extension = "";

	    //ファイルの存在確認
	    if (targetFile != null && targetFile.exists() && targetFile.getName().contains(".")) {

	      // 拡張子を取得
	      extension = targetFile.getName().substring(targetFile.getName().lastIndexOf("."));

	    }
	    return extension;
	}

}
