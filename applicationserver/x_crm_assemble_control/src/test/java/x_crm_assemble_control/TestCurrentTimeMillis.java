package x_crm_assemble_control;

import java.util.Random;

import org.apache.commons.lang3.*;

public class TestCurrentTimeMillis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int i = 0;
		while (i < 20) {
			System.out.println(getFixLenthString(12));
			//getFixLenthString(9);
			i++;
		}
	}

	private static String getFixLenthString(int strLength) {

		Random rm = new Random();

		// 获得随机数  
		double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

		// 将获得的获得随机数转化为字符串  
		String fixLenthString = String.valueOf(pross);
		//System.out.println(fixLenthString);
		// 返回固定的长度的随机数  
		return fixLenthString.substring(2, strLength + 2);
	}
}
