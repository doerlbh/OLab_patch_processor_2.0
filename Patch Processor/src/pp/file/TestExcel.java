//package pp.file;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//
//public class TestExcel {
//
//	public static void main(String[] args) throws IOException {
//		ExcelFileDealer exl = new ExcelFileDealer();
//		ArrayList<String> alN = new ArrayList<String>();
//		alN.add("sdfas");
//		alN.add("KDK");
//		alN.add("opape");
//		exl.addData("test name", alN);
//
//		ArrayList<Double> al = new ArrayList<Double>();
//		al.add(12.0);
//		al.add(3.4);
//		al.add(5.22);
//		exl.addSubData("test data", al);
//
//			FileOutputStream fos = new FileOutputStream(new File("test.xls")); 
//			exl.exportExcel(fos);
//		exl.outputExcel("test.xls");
//
//	}
//
//}
