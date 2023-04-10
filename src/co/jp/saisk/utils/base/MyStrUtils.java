package co.jp.saisk.utils.base;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import co.jp.saisk.utils.MyConst;
import co.jp.saisk.utils.file.MyFileConst;



public class MyStrUtils {
	public static final String NUMBER_INTEGER = "^[0-9]*.[0]*$";
	public static int getBitesLength(String str) {
		int ret = 0;

		if (isNotEmpty(str)) {
			try {
				ret = str.getBytes(MyFileConst.ENCODE_SHIFT_JIS).length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof String) {
			return  obj.toString().length() == 0;
		} else if (obj instanceof List) {
			return ((List)obj).size()==0;
		} else if (obj instanceof Map) {
			return ((Map)obj).size()==0;
		} else if (obj instanceof Set) {
			return ((Set)obj).size()==0;
		} else if (obj instanceof Object[]) {
			return ((Object[])obj).length==0;
		}
		return false;
	}
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}

	public static boolean isEqual(Object oldObj,Object newObj) {
		boolean retVal = false;
		if (oldObj==null ||newObj==null) {
			return retVal;
		}
		if (oldObj instanceof String && newObj instanceof String) {
			if (String.valueOf(oldObj).equals(newObj)) {
				retVal = true;
			}
		}
		return retVal;
	}

	public static boolean isNotEqual(Object oldObj,Object newObj) {
		return !isEqual(oldObj,newObj);
	}
	
	/**
	 * @param str
	 * @param strA
	 * @param strB
	 * @return String
	 */
	public static String fromAtoBByTrim(String str, String strA, String strB) {
		String ret = "";

		if (MyStrUtils.isNotEmpty(str)) {
			ret = fromAtoB(str, strA, strB);
			ret = lrTrimSpace(ret);
		}

		return ret;

	}
	public static String escapeNull(String str) {
		if (MyStrUtils.isEmpty(str)) {
			return "";
		} else {
			return str;
		}
	}
	/**
	 * @param ret
	 * @return String
	 */

	public static String lrTrimSpace(String str) {

		if (isEmpty(str)) {
			return "";
		}

		str = str.trim();
		while (str.startsWith(" ")
				|| str.startsWith(MyConst.Z_SIGN_SPACE_1)) {
			str = str.substring(1, str.length());
		}
		while (str.endsWith(" ")
				|| str.endsWith(MyConst.Z_SIGN_SPACE_1)) {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	/**
	 * @param str
	 * @param strA
	 * @param strB
	 * @return String
	 */
	public static String fromAtoB(String str, String strA, String strB) {

		if (null == strA) {
			strA = "";
		}
		if (null == strB) {
			strB = "";
		}
		String temp = "";
		int n = 0;
		if ("".equals(strA)) {
			if ("".equals(strB)) {
				return str;
			}
			if (str.contains(strB)) {
				return str.substring(0, str.indexOf(strB));
			}
			return str;
		}

		if ("".equals(strB)) {
			if (!"".equals(strA)) {
				if (str.contains(strA)) {
					n = str.indexOf(strA) + strA.length();
					if (n < str.length()) {
						return str.substring(n);
					} else if (n == str.length()) {
						return "";
					}
					return str;
				}
				return str;
			}
		}

		if (str.contains(strA)) {
			n = str.indexOf(strA) + strA.length();
			if (n < str.length()) {
				temp = str.substring(n);
			}

			if (temp.contains(strB)) {
				return temp.substring(0, temp.indexOf(strB));
			}
			return temp;
		} else {
			if (str.contains(strB)) {
				return str.substring(0, str.indexOf(strB));
			}
			return str;
		}
	}

	public static int getNumByte(long num) {
		int cnt = 0;
		if (num == 0) {
			return 1;
		}
		while (num != 0) {
			num = num / 10;
			cnt++;
		}
		return cnt;
	}

	public static String trimLeftChar(String retVal, String trimCh) {
		if (isEmpty(retVal)) {
			return "";
		}
		while (retVal.startsWith(trimCh)) {
			retVal = retVal.substring(1);
		}
		return retVal;
	}
	public static String getNumberByTrimDot0(String str) {
		if (MyStrUtils.isNotEmpty(str)) {
			if (str.matches(NUMBER_INTEGER)) {
				str = String.valueOf(getIntVal(str));
			}
		}
		return str;
	}
	public static int getIntVal(String cellContents) {

		return (int) getLongVal(cellContents);
	}
	public static long getLongVal(String cellContents) {
		return (long) Double.parseDouble(cellContents);
	}
}
