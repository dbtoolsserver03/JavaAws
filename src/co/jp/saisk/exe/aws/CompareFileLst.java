package co.jp.saisk.exe.aws;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import co.jp.saisk.utils.MyConst;
import co.jp.saisk.utils.aws.AwsS3Utils;
import co.jp.saisk.utils.base.BeanMap;
import co.jp.saisk.utils.base.MyDateUtil;
import co.jp.saisk.utils.base.MyStrUtils;
import co.jp.saisk.utils.file.MyDirectory;
import co.jp.saisk.utils.file.MyFileConst;
import co.jp.saisk.utils.file.MyFileUtils;
import co.jp.saisk.utils.log.MyLog5j;
import co.jp.saisk.utils.poi.MyPoiUtils;

public class CompareFileLst {

	public static void main(String[] args) {
		Date begin = new Date();
		System.out.println("begin at :" + MyDateUtil.getFormatDateTime(begin,MyConst.YYYYMMDDHHMMSSSSS));
		try {
			run();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			Date end = new Date();
			System.out.println("begin at :" + MyDateUtil.getFormatDateTime(begin,MyConst.YYYYMMDDHHMMSSSSS));
			System.out.println("end   at :" + MyDateUtil.getFormatDateTime(end,MyConst.YYYYMMDDHHMMSSSSS));
			System.out.println("cost time:" + MyDateUtil.getMsHour(end.getTime()-begin.getTime()));
		}
	}
	
	public static void run() throws Throwable {
		String toolPath = new File("").getCanonicalPath() + File.separator ;
		MyLog5j.init(toolPath+"log", "debug.txt", MyFileConst.ENCODE_UTF_8);
		
		if (false) {
			File inputFile = new File(toolPath  + "tmp/aws_test/a01.csv");

			List<String> strLst = MyFileUtils.getFileContent(inputFile, MyFileConst.ENCODE_UTF_8);
			
			for (int i=0; i< strLst.size(); i++) {

				if (i<1) {
					continue;
				}
				BeanInfo bean = new BeanInfo(i,strLst.get(i));
				if (!bean.isNewOk()) {
					continue;
				}
				MyLog5j.writeLine("■■■ " + String.format("%06d", i));
				MyLog5j.writeLine(bean.inputInfo());

				bean.initPathBean();
				
				for (Entry<String,String> entry : bean.realPathMap.entrySet()) {
					
					String fromPath = entry.getKey();
					String toPath = entry.getValue();
					
					bean.initFileSet(fromPath,toPath);
					bean.doFilefillter(fromPath,toPath);
				}
				bean.doNotExistInB();
				MyLog5j.writeLine(bean.outputInfo());
			}
			
			
		} else {
			Workbook wb = MyPoiUtils.getWorkBook(toolPath + "tmp/aws_test/a01.xlsx");

			Sheet st = wb.getSheet("Sheet1");

			for (Row row : st) {
				if (row.getRowNum()<2) {
					continue;
				}
				BeanInfo bean = new BeanInfo(row);
				if (!bean.isNewOk()) {
					continue;
				}
				MyLog5j.writeLine("■■■ " + String.format("%06d", row.getRowNum()-1));

				MyLog5j.writeLine(bean.inputInfo());

				bean.initPathBean();
				
				for (Entry<String,String> entry : bean.realPathMap.entrySet()) {
					
					String fromPath = entry.getKey();
					String toPath = entry.getValue();
					
					bean.initFileSet(fromPath,toPath);
					bean.doFilefillter(fromPath,toPath);
				}
				bean.doNotExistInB();
				MyLog5j.writeLine(bean.outputInfo());
			}
		}
		
		MyLog5j.close();
	}
}

class BeanInfo {

	public static Map<String,Set<String>> fileCatchMap = new HashMap<>();
	public int rowNum = -1;
	public String awsPathA="";
	public String folderPathA="";
	public String fileNmA="";
	public String awsPathB="";
	public String folderPathB="";
	public String fileNmB="";
	public boolean isAFolderAws = false;
	public boolean isBFolderAws = false;
	public Map<String,String> realPathMap;


	public boolean isNewOk() {
		boolean newOk = true;
		if (MyStrUtils.isEmpty(awsPathA+folderPathA)
				||MyStrUtils.isEmpty(fileNmA)
				||MyStrUtils.isEmpty(awsPathB+folderPathB)
				||MyStrUtils.isEmpty(fileNmB)
				) {
			newOk = false;
		} else {
			if (folderPathA.length() == 0) {
				isAFolderAws = true;	
			}
			if (folderPathB.length() == 0) {
				isBFolderAws = true;	
			}
		}
		
		return newOk;
	}
	public Set<String> fileASet;
	public Set<String> fileBSet;
	
	public Map<String,BeanMap> pathAMap;
	public Map<String,BeanMap> pathBMap;
	
	public boolean isOk=false;
	public int fileCntA;
	public int fileCntB;

	public Map<String, String> aMapFilter = new TreeMap<String, String>();
	public Map<String, String> bMapFilter = new TreeMap<String, String>();
	public Set<String> noExistSet = new TreeSet<>();
	
	public static Set<String> repSet= new LinkedHashSet<>();
	static {
		repSet.add("x");
		repSet.add("?");
		repSet.add("yyyymmddhh");
		repSet.add("yyyymmddhh_???");
		repSet.add("yyyymmdd_???");
	}
	public static Map<String,String> regMap= new LinkedHashMap<>();
	static {
		regMap.put("YYYYMMDDHH", MyConst.REG_YYYYMMDDHH);
		regMap.put("YYYYMMDD", MyConst.REG_YYYYMMDD);
		regMap.put("YYYYMM",MyConst.REG_YYYYMM);
		
	}
	public String inputInfo() {
		return "↓↓↓ inputInfo [rowNum=" + rowNum + ", awsPathA=" + awsPathA + ", folderPathA=" + folderPathA + ", fileNmA="
				+ fileNmA + ", awsPathB=" + awsPathB + ", folderPathB=" + folderPathB + ", fileNmB=" + fileNmB + "]";
	}

	public String outputInfo() {
		return "↑↑↑ outputInfo [rowNum=" + rowNum + ", isOk=" + isOk + ", aMapFilter=" + aMapFilter + ", bMapFilter="
				+ bMapFilter + ", noExistSet=" + noExistSet + "]";
	}

	public BeanInfo(Row row) throws Throwable {
		rowNum = row.getRowNum();
		awsPathA = MyPoiUtils.getCellContent(row, 1, false);
		folderPathA = MyPoiUtils.getCellContent(row, 2, false);
		fileNmA = MyPoiUtils.getCellContent(row, 3, false);
		awsPathB = MyPoiUtils.getCellContent(row, 4, false);
		folderPathB = MyPoiUtils.getCellContent(row, 5, false);
		fileNmB = MyPoiUtils.getCellContent(row, 6, false);
	}

	public BeanInfo(int i, String info) {
		rowNum = i;
		String[] strArr=info.split(MyConst.COMA);
		awsPathA = strArr[1];
		folderPathA = strArr[2];
		fileNmA = strArr[3];
		awsPathB = strArr[4];
		folderPathB = strArr[5];
		fileNmB = strArr[6];
	}

	public void doNotExistInB() {
		for (Entry<String,String> entry : aMapFilter.entrySet()) {
			if (!bMapFilter.containsKey(entry.getKey())) {
				noExistSet.add(entry.getValue());
			}
		}		
		if (noExistSet.isEmpty()) {
			isOk = true;
		}
	}

	public void doFilefillter(String fromPath, String toPath) throws Throwable {
		aMapFilter.putAll(getFileFilter(fileASet, fileNmA, fromPath));
		bMapFilter.putAll(getFileFilter(fileBSet, fileNmB, toPath));
	}

	public void initPathBean() throws Throwable {
		
		// s3://bucketsaisk01/bucketsaisk01/202301/20230101/,[YYYYMM:202301 YYYYMMDD:20230101]
		pathAMap = isAFolderAws?AwsS3Utils.getPathSetLike(awsPathA,regMap):MyDirectory.getFolderSetLike(folderPathA,regMap);
		pathBMap = isBFolderAws?AwsS3Utils.getPathSetLike(awsPathB,regMap):MyDirectory.getFolderSetLike(folderPathB,regMap);
		
		String toPath = "";
		for (String pathB : pathBMap.keySet()) {
			toPath = pathB;
		}
		
		realPathMap = new TreeMap<>();
		for (String fromPath : pathAMap.keySet()) {
			realPathMap.put(fromPath, toPath);
		}
		
		System.out.println("----------------------------------------------");
	}
	
	public void initFileSet(String fromPath, String toPath) {
		
		fileASet = fileCatchMap.get(fromPath);
		if (fileASet == null) {
			fileASet = isAFolderAws ? AwsS3Utils.getNameSetByS3Path(fromPath,false) : MyDirectory.getFileSet(fromPath, false);
			fileCatchMap.put(fromPath, fileASet);
		}
		fileBSet= fileCatchMap.get(toPath);
		if (fileBSet == null) {
			fileBSet = isBFolderAws ? AwsS3Utils.getNameSetByS3Path(toPath,false) : MyDirectory.getFileSet(toPath, false);
			fileCatchMap.put(toPath, fileBSet);
		}
	}
	
	public Map<String,String> getFileFilter(Set<String> fileSet, String filePattern, String realPath) throws Throwable {
		
		Map<String,String> retMap = new TreeMap<>();
		
		String repStr = MyStrUtils.getXXX(filePattern,repSet);
		if (repStr.length()<3) {
			repStr="";
		}
		
		String regex = MyStrUtils.getStrByXXXUpperReg(filePattern,regMap);
		if (repStr.length()>0) {
			regex = regex.replaceAll(repStr, "\\\\d{" + repStr.length() + "}");
		}
		
		int posXXX = filePattern.indexOf(repStr);
		for (String str : fileSet) {
			MyLog5j.writeLine(str.matches(regex)+ "-->" + str + ":" + regex);
			if (str.matches(regex)) {
				if (filePattern == fileNmA && MyStrUtils.isNotEmpty(pathAMap.get(realPath).map)) {
					retMap.put(pathAMap.get(realPath).map.get(MyConst.YYYYMMDD) + "_"+ str.substring(posXXX, posXXX+repStr.length()), realPath+ File.separator+str);
				} else if(filePattern == fileNmB && MyStrUtils.isNotEmpty(MyStrUtils.getBeanMap(str, filePattern, regMap).map)) {
					retMap.put(MyStrUtils.getBeanMap(str, filePattern, regMap).map.get(MyConst.YYYYMMDD)+ "_" + str.substring(posXXX, posXXX+repStr.length()), str);
				} else {
					retMap.put(str.substring(posXXX, posXXX+repStr.length()), str);
				}
			}
		}
		return retMap;
	}
}
