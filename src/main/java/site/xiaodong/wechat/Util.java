package site.xiaodong.wechat;

public class Util {
	// 日期正则，已考虑平闰年、月份日期
	public static String dateRegex = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
	// wxid_sdfsdfsdf:\n 6.7
	// 整数部分小于三位数
	public static String messageRegex = "^(\\w*:\\n)?\\s*[0-9]{1,3}(\\.\\d*)?\\s*$";
	public static boolean isBlank(String s) {
		if (s == null || s.equals(""))
			return true;
		return false;
	}
}
