package co.jp.saisk.exe.aws;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;

import co.jp.saisk.utils.aws.AwsS3Utils;
import co.jp.saisk.utils.poi.MyPoiUtils;

public class CompareFileLst {

	public static final String REP_STR="xxxx";
	
	public static final String REG_YYYYMMDD = "\\\\d{8}";
	
	public static void main(String[] args) {
		try {
			run();
			
			//test01();
			 

		} catch (Throwable e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	public static void run() throws Throwable {
		Workbook wb = MyPoiUtils.getWorkBook(null);

		Sheet st = wb.getSheet("Sheet1");

		for (Row row : st) {
			String pathA = MyPoiUtils.getCellContent(row, 1, false);
			String fileA = MyPoiUtils.getCellContent(row, 2, false);

			// 指定されたバケット配下のキーのオブジェクト（ファイル）リストを取得する。
			ObjectListing aObjectListing = AwsS3Utils.s3client.listObjects(new ListObjectsRequest()
					.withBucketName(pathA).withDelimiter(fileA));
			Set<String> fileASet = AwsS3Utils.getFileNm(aObjectListing, false);

			Map<String, String> aMapFilter = getSetFilter(fileASet, fileA);

			String pathB = MyPoiUtils.getCellContent(row, 3, false);
			String fileB = MyPoiUtils.getCellContent(row, 4, false);

			// 指定されたバケット配下のキーのオブジェクト（ファイル）リストを取得する。
			ObjectListing bObjectListing = AwsS3Utils.s3client.listObjects(new ListObjectsRequest()
					.withBucketName(pathB));
			Set<String> fileBSet = AwsS3Utils.getFileNm(bObjectListing, false);
			Map<String, String> bMapFilter = getSetFilter(fileBSet, fileB);
			
			
			Set<String> noExistSet = new TreeSet<String>();
			for (Entry<String,String> entry : aMapFilter.entrySet()) {
				if (!bMapFilter.containsKey(entry.getKey())) {
					noExistSet.add(entry.getKey());
				}
			}
		}
	}
	public static Map<String,String> getSetFilter(Set<String> fileASet, String fileA) {
		
		Map<String,String> retMap = new TreeMap<String,String>();
		
		String[] strAArr = fileA.split(REP_STR);
		
		String fileApre = strAArr[0];
		String fileAaft = strAArr[1];
		
		String regex=fileA.replaceAll("YYYYMMDD", REG_YYYYMMDD)
				.replaceAll(REP_STR, "\\\\d{"+REP_STR.length()+"}")
				.replaceAll("yyyymmdd", REG_YYYYMMDD);
		
		int posXXX = fileA.indexOf(REP_STR);
		for (String str : fileASet) {
			
			System.out.println(str.matches(regex)+ "->" + str + ":" + regex);
			if (str.matches(regex)) {
				retMap.put(str.substring(posXXX, posXXX+REP_STR.length()), str);
			}
		}
		return retMap;
	}
	
	
	private static void test01() {
		System.out.println("a01000120230101_20230201.xa.csv".matches("a01\\d{4}\\d{8}_\\d{8}.xa.csv"));
		
		Set<String> fileASet = new HashSet<String>();
		fileASet.add("a01000120230101_20230201.xa.csv");
		fileASet.add("a01000220230201_20230301.xa.csv.bk");
		fileASet.add("a01000320230301_20230401.xa.csv");
		fileASet.add("a02000420230401_20230501.xa.csv");
		
		 Map<String,String> map = getSetFilter(fileASet, "a01xxxxYYYYMMDD_yyyymmdd.xa.csv");
		 System.out.println(map);
	}
}
