package test;

public class Test1 {
	public static void main(String[] args) {
		//double cellvalue = 1.0;
		double cellvalue = Double.valueOf("2.01");
		cellvalue = Math.floor(cellvalue);
		System.out.println("test:"+(int)cellvalue);
	}
}
