package co.jp.saisk.utils.file;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import co.jp.saisk.utils.MyConst;
import co.jp.saisk.utils.base.BeanMap;
import co.jp.saisk.utils.base.MyStrUtils;
public class MyDirectory {

	public static LinkedList<File> getFilesList(String filePath, boolean isHaveSubFile) {

		LinkedList<File> retList = new LinkedList<File>();
		LinkedList<File> list = new LinkedList<File>();
		File dir = new File(filePath);
		File file[] = dir.listFiles();
		if (null == file) {
		return retList;
		}

		for (int i = 0; i < file.length; i++) {

			if (file[i].isDirectory())
				list.add(file[i]);
			else
				retList.add(file[i]);
		}

		if (isHaveSubFile) {
			File tmp;
			while (!list.isEmpty()) {
				tmp = list.removeFirst();

				if (tmp.isDirectory()) {
					file = tmp.listFiles();
					if (file == null)
						continue;
					for (int i = 0; i < file.length; i++) {
						if (file[i].isDirectory())
							list.add(file[i]);
						else {
							retList.add(file[i]);
						}
					}
				} else {
					retList.add(tmp);
				}
			}
		}

		return retList;
	}

	public static LinkedList<File> getFoldersList(String filePath) {

		LinkedList<File> list = new LinkedList<File>();
		LinkedList<File> folderlist = new LinkedList<File>();
		File dir = new File(filePath);
		File file[] = dir.listFiles();

		if (file == null) {
			return folderlist;
		}
		for (int i = 0; i < file.length; i++) {

			if (file[i].isDirectory()) {
				list.add(file[i]);
				folderlist.add(file[i]);
			}
		}

		File tmp;
		while (!list.isEmpty()) {
			tmp = list.removeFirst();

			if (tmp.isDirectory()) {
				file = tmp.listFiles();
				if (file == null)
					continue;
				for (int i = 0; i < file.length; i++) {
					if (file[i].isDirectory()) {
						folderlist.add(file[i]);

						list.add(file[i]);
					}
				}
			}
		}

		return folderlist;
	}
	
	
	public static Set<String> getFileSet(String filePath, boolean isHaveSubFile) {
		Set<String> retSet = new TreeSet<String>();
		for (File f : getFilesList(filePath, isHaveSubFile)) {
			retSet.add(f.getName());
		}
		return retSet;
	}

	public static void copyFile(String fromFilePath, String toPath) throws Throwable {

		File fromFile = new File(fromFilePath);
		File toFile=new File(toPath);
		toFile.mkdirs();
		String command = "cmd /c copy "+ fromFile.getAbsolutePath() + " " + toFile.getAbsolutePath() + File.separator;
		Runtime.getRuntime().exec(command).waitFor();
	}
	public static void main(String[] args) throws Throwable {
		
		Map<String,String> repFolderMap= new LinkedHashMap<>();
		repFolderMap.put(MyConst.YYYYMMDD,MyConst.REG_YYYYMMDD );
		repFolderMap.put(MyConst.YYYYMM,MyConst.REG_YYYYMM);
			
		System.out.println(getFolderSetLike("C:\\soft\\pleiades-2023-03-java-win-64bit-jre_20230326\\workspace0331Boot\\JavaAws\\tmp\\aws_test\\bucketsaisk01\\YYYYMM\\YYYYMMDD",repFolderMap));
	}

	
	public static Map<String,BeanMap> getFolderSetLike(String foldPattern, Map<String,String> regFolderMap) throws Throwable {
		Map<String,BeanMap> retMap = new TreeMap<>();
		if (!MyStrUtils.isHasReg(foldPattern,regFolderMap)) {
			retMap.put(foldPattern, new BeanMap());
			return retMap;
		}

		//foldPattern = MyStrUtils.getStrByXXXUpperReg(foldPattern, regFolderMap);
		foldPattern = MyStrUtils.getStrByXXXUpper(foldPattern, regFolderMap);

		String regEscape =MyStrUtils.funReplace(foldPattern, File.separator, MyConst.SIGN);
		String regex =MyStrUtils.getRegexStrByRegMap(regEscape, regFolderMap);
		
		Set<String> strSet = new LinkedHashSet<>();

		for (String key : regFolderMap.keySet()) {
			strSet.add(key);
		}
		strSet.add(File.separator);
		
		String folderPath = MyStrUtils.lrTrimStartEndBySet(foldPattern,strSet,false);
		for (File f : getFoldersList(folderPath)) {
			String str = f.getAbsolutePath();
			
			System.out.println(MyStrUtils.funReplace(str, File.separator, MyConst.SIGN).matches(regex) + "-->" + str + ":" + regex);
			if (MyStrUtils.funReplace(str, File.separator, MyConst.SIGN).matches(regex)) {
				retMap.put(str, MyStrUtils.getBeanMap(str,foldPattern,regFolderMap));
			}
		}
		return retMap;
	}


}
