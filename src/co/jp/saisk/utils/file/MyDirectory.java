package co.jp.saisk.utils.file;

import java.io.File;
import java.util.LinkedList;
public class MyDirectory {

	public static LinkedList<File> getFilesList(String filePath,
			boolean isHaveSubFile) {

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

	public static void copyFile(String fromFilePath, String toPath) throws Throwable {

		File fromFile = new File(fromFilePath);
		File toFile=new File(toPath);
		toFile.mkdirs();
		String command = "cmd /c copy "+ fromFile.getAbsolutePath() + " " + toFile.getAbsolutePath() + File.separator;
		Runtime.getRuntime().exec(command).waitFor();
	}


}
