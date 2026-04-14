package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class DocbuilderUtility {
	 private static Logger logger = LoggerFactory.getLogger(DocbuilderUtility.class);

	 public static boolean CreateDocbuilder(String docbuilderFilePath,String docFilePath,String docMarkFilePath,String pdfMarkFilePath,String mark) {
	    	File f = new File(docbuilderFilePath);
			try {
				 if (f.exists()) {
					 
				   } else {
				     f.createNewFile();
				   }
				 
				 if(OSInfo.isLinux()) {
		 				
		 			}else {
		 				String firstChar = docFilePath.substring(0, 1);
		 				if(firstChar.equalsIgnoreCase("/")) {
		 					docFilePath = docFilePath.substring(1, docFilePath.length());
			 			}
		 			}
				 
				BufferedWriter output = new BufferedWriter(new FileWriter(f,false));
				output.write("builder.OpenFile(\""+docFilePath+"\", \"\");");
				output.write("\r\n");
				output.write("var oDocument = Api.GetDocument();");
				output.write("\r\n");
				output.write("oDocument.InsertWatermark(\""+mark+"\",true);");
				output.write("\r\n");
				if(docMarkFilePath != null) {
					output.write("builder.SaveFile(\"docx\",\""+docMarkFilePath+"\")");
					output.write("\r\n");
				}
				if(pdfMarkFilePath != null) {
				output.write("builder.SaveFile(\"pdf\",\""+ pdfMarkFilePath+ "\")");
				output.write("\r\n");
				}
				output.write("builder.CloseFile()");
				output.write("\r\n");
				
				output.flush();   
				output.close();
				
			} catch (IOException e) {
				logger.error(e);
				return false;
			} 
	    	
			return true;
	    }
	    
	    public static boolean ExcuteDocbuilder(String docbuilderExePath,String docbuilderPath) {
	    	
	    	String[] cmd = {docbuilderExePath,docbuilderPath };
	    	String cmdLinux = "cd "+docbuilderExePath+";./docbuilder " + docbuilderPath;
	    	String[] commandArr = new String[]{"/bin/bash", "-c", cmdLinux};
			Process process = null;
			try {
				Runtime runtime = Runtime.getRuntime();
				if(OSInfo.isLinux()) {
					process = runtime.exec(commandArr);
				}else {
			    	process = runtime.exec(cmd);
				}
				process.waitFor();
				logger.info("exitValue=" + process.exitValue());
			} catch (Exception e) {
				logger.error(e);
				return false;
			} finally {
				if (process != null) {
					process.destroy();
				}
			}
			return true;
	    }
	    
	    
	    public static void main(String[] args) {
	    	/*
	    	String mark = "admin@admin@P";
			String docbuilderFilePath = "D:/ONLYOFFICE/DocumentBuilder/samples/sample9.docbuilder";
			String docFilePath= "D:/ONLYOFFICE/DocumentBuilder/TESTFILES/sample1.docx";
			String docMarkFilePath= "D:/ONLYOFFICE/DocumentBuilder/TESTFILES/sample9.docx";
			String pdfMarkFilePath = "D:/ONLYOFFICE/DocumentBuilder/TESTFILES/sample9.pdf";
			String docbuilderExePath = "D:/ONLYOFFICE/DocumentBuilder/docbuilder.exe";
			if(CreateDocbuilder(docbuilderFilePath,docFilePath,docMarkFilePath,pdfMarkFilePath,mark)) {
		      	ExcuteDocbuilder(docbuilderExePath,docbuilderFilePath);
			}*/
	    	
			String docFilePath= "/D:/ONLYOFFICE/DocumentBuilder/TESTFILES/sample1.docx";
	    	System.out.println(docFilePath.substring(0, 1));
	    	System.out.println(docFilePath.substring(1, docFilePath.length()));
		}
}
