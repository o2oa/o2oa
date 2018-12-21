package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckYear {
	public static void main(String[] args) {
		String dateStr = "1017";
		//String eL = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$";  
		String eL = "[1-9]{1}[0-9]{3}"; 
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(dateStr);
		boolean dateFlag = m.matches();
		
		System.out.println(dateFlag);
	}
}
