/* @Author : Divya
 * Description : This will unzip any given file
 */
package testdownload;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This utility extracts files and directories of a standard zip file to a destination directory.
 **/
public class UnZipUtility {
	
	 public void unZipIt(String zipFile, String outputFolder){

	     byte[] buffer = new byte[1024];
	    	
	     try{
	    		
	    	//create output directory is not exists
	    	/*File folder = new File(OUTPUT_FOLDER);
	    	if(!folder.exists()){
	    		folder.mkdir();
	    	}*/
	    		
	    	//get the zip file content
	    	ZipInputStream zis = 
	    		new ZipInputStream(new FileInputStream(zipFile));
	    	//get the zipped file list entry
	    	ZipEntry ze = zis.getNextEntry();
	    		
	    	while(ze!=null){
	    			
	    	   String fileName = ze.getName();
	           File newFile = new File(outputFolder + File.separator + fileName);
	                
	                
	            //create all non exists folders
	            //else you will hit FileNotFoundException for compressed folder
	            new File(newFile.getParent()).mkdirs();
	              
	            FileOutputStream fos = new FileOutputStream(newFile);             

	            int len;
	            while ((len = zis.read(buffer)) > 0) {
	       		fos.write(buffer, 0, len);
	            }
	        		
	            fos.close();   
	            ze = zis.getNextEntry();
	    	}
	    	
	        zis.closeEntry();
	    	zis.close();
	    		
//	    	System.out.println("Done");
	    		
	    }catch(IOException ex){
	       ex.printStackTrace(); 
	    }
	   }    
	
}
