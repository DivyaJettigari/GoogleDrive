package testdownload;
/* @Author : Divya
 * Description : This will  downloads the Contents from Folder in Google Drive
 */

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class sampleclass {

	private static String filePath = System.getProperty("user.dir")+"\\";
	static boolean isMettlScriptFailed=false;
	static boolean isGoogleScriptFailed=false;
	static boolean isScriptFailed=false;
	public static void main(String[] args) {
		WebDriver driver = null;
		try{
			FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("browser.download.folderList", 2);
			profile.setPreference("browser.download.manager.showWhenStarting",false);
			profile.setPreference("browser.download.dir",filePath);
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk","application/excel,application/x-excel,application/vnd.ms-excel,application/zip,application/msword, application/csv, application/ris, text/csv, image/png, application/pdf, text/html, text/plain, application/zip, application/x-zip, application/x-zip-compressed, application/download, application/octet-stream");
			profile.setPreference("browser.helperApps.alwaysAsk.force", false);
			profile.setPreference("browser.download.manager.showWhenStarting",false);
			
			//System.setProperty("webdriver.gecko.driver","drivers//geckodriver.exe");
			System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
			System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");
			
			driver = new FirefoxDriver();
		}catch(Exception e)
		{
			e.printStackTrace();
			return;	
		}
		
		try{
			
		String credFilePath = System.getProperty("user.dir")+"\\"+"Credentials.txt";
		String[] credentials = new String[10];
		
		//Reading the file for Credentials and File names
		try(BufferedReader br = new BufferedReader(new FileReader(credFilePath))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    credentials = sb.toString().split(System.lineSeparator());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Parsing the date read from the file
		for( int i=0; i<credentials.length; i++ ){
			credentials[i] = credentials[i].split(":")[1].trim();
		}
		driver.manage().window().maximize();
		//driver.manage().window().setPosition(new org.openqa.selenium.Point(-2000, 0));
		
		//Login and download given folder
		googleDrive(driver, credentials);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Method to login to google drive and download sample folder
	//download all contains
	//unzip it
	//open them
	public static void googleDrive(WebDriver driver, String[] credentials){
		System.out.println("Executing for Google Drive..");
		WebDriverWait wait = new WebDriverWait(driver, 60);
		Actions act = new Actions(driver);
		try{
			if( !credentials[0].equals("") && !credentials[1].equals("") && !credentials[2].equals(""))
			{
				//creating object of Unzip Utility class
				UnZipUtility unZip = new UnZipUtility();
				
				boolean sharedWithMe = false;
				
				String downloadFile = credentials[2];
				downloadFile = downloadFile.replaceAll(" ", "_");
				//navigate to google drive
				driver.get("https://accounts.google.com/signin/v2/identifier?service=wise&passive=true&continue=http%3A%2F%2Fdrive.google.com%2F%3Futm_source%3Den_US&utm_medium=button&utm_campaign=web&utm_content=gotodrive&usp=gtd&ltmpl=drive&flowName=GlifWebSignIn&flowEntry=ServiceLogin");
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("identifierId")));
				
				//Login to google drive
				//put username
				driver.findElement(By.id("identifierId")).sendKeys(credentials[0]);
				driver.findElement(By.id("identifierNext")).click();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@type,'password')]")));
				//put password
				driver.findElement(By.xpath("//input[contains(@type,'password')]")).sendKeys(credentials[1]);
				driver.findElement(By.id("passwordNext")).click();
				
				try{
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'"+credentials[2]+"')]")));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'"+credentials[2]+"')]")));
				}catch (Exception e) {
					sharedWithMe = true;
				}
				
				if( sharedWithMe ){
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Shared with me')]")));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'Shared with me')]")));
					driver.findElement(By.xpath("//span[contains(text(),'Shared with me')]")).click();
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'"+credentials[3]+"')]")));
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[contains(text(),'"+credentials[3]+"')]")));
				}
			
				driver.findElement(By.xpath("//span[contains(text(),'"+credentials[3]+"')]")).click();
				//right click and click on download option  
				act.contextClick(driver.findElement(By.xpath("//span[contains(text(),'"+credentials[3]+"')]"))).build().perform();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Download')]")));
				List<File> filelist = new ArrayList<File>();
				File dir = new File(filePath);
				filelist = Arrays.asList(dir.listFiles());
				int inititalfilecount = filelist.size();
				driver.findElement(By.xpath("//div[contains(text(),'Download')]")).click();
				do
				{
					System.out.println("Waiting for Google Drive file download to start");
					Thread.sleep(4000);
					filelist = Arrays.asList(dir.listFiles());
				}
				while(filelist.size() == inititalfilecount);
				
				
				 String files = null;
				 int p=1;
				 A: while( p==1)
				 {
					 System.out.println("Waiting for Google Drive file download to complete");
					 File folder = new File(filePath);
					 File[] listOfFiles = folder.listFiles(); 
					 for (int i = 0; i < listOfFiles.length; i++) 
					 {
						 if (listOfFiles[i].isFile()) 
						 {
							 files = listOfFiles[i].getName();
							 if (files.endsWith(".rar") || files.endsWith(".zip"))
							 {
								 System.out.println(files);
								 break A;
							 }
						 }
					 }
					 Thread.sleep(4000);
				 }
				 System.out.println("Google Drive file download completed");
				 String unZipFile = filePath+files;
				 unZip.unZipIt(unZipFile, filePath);
				
				
				 File directory = new File(filePath+File.separator+credentials[5]);
				 //get all the files from a directory
				 File[] fList = directory.listFiles();
		        
		        
				 for (File file : fList){
		                
					 if(file.isFile()){
						 openFile(file);
					 }
				 }
			}
			driver.findElement(By.xpath("//a[contains(@aria-label,'"+credentials[0]+"')]")).click();
			driver.findElement(By.xpath("//a[contains(@aria-label,'"+credentials[0]+"')]")).sendKeys(Keys.ENTER);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Sign out')]")));
			driver.findElement(By.xpath("//a[contains(text(),'Sign out')]")).click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@type,'password')]")));
		}catch (Exception e) {
				e.printStackTrace();
				isGoogleScriptFailed=true;
				driver.findElement(By.xpath("//a[contains(@aria-label,'"+credentials[0]+"')]")).click();
				driver.findElement(By.xpath("//a[contains(@aria-label,'"+credentials[0]+"')]")).sendKeys(Keys.ENTER);
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Sign out')]")));
				driver.findElement(By.xpath("//a[contains(text(),'Sign out')]")).click();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[contains(@type,'password')]")));
		}
	}
	

	public static void openFile(File file){
		try{
			Desktop d = Desktop.getDesktop();
			d.open(file);
			System.out.println("File Opened");
			/*//close and delete a open file
			Thread.sleep(30000);
	       Runtime.getRuntime().exec(
	           "cmd /c taskkill /f /im soffice.bin");*/
			// Handle code is on hold,for this need admin credentials,Once we get confirmation about admin credentials will be working on this code.
		/*	try 
			{ 
			Process p=Runtime.getRuntime().exec(System.getProperty("user.dir")+"\\handle HRC-IT_Security-April_2016.xls"); 
			p.waitFor(); 
			BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String[] credentials = new String[6];
			String line=reader.readLine(); 
			while(line != null ) 
			{ 
				System.out.println(line);
				line = reader.readLine();
				try
				{
				if(line != null && line.startsWith("soffice.bin"))
				{
				sb.append(line);
			    sb.append(System.lineSeparator());
				}
				
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} 
			System.out.println("buffer = " + sb);
			credentials=sb.toString().split(System.lineSeparator());
			//credentials=sb.toString().split(System.);
			String ln=credentials[0];

			String Pid = ln.substring(ln.indexOf("pid: ")+4, ln.indexOf("type:")).trim();
			System.out.println("Pid="+Pid);
			String handleId = ln.substring(ln.indexOf("type: File")+10, ln.indexOf(System.getProperty("user.dir"))-2).trim();
			System.out.println("handleId="+handleId);
			Process pr=Runtime.getRuntime().exec(
			        "cmd /c handle -c 824 -y -p 4840");
			BufferedReader reader1=new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line1=reader1.readLine(); 
			while(line1 != null ) 
			{ 
				System.out.println(line1);
				line1 = reader1.readLine();
			} 
			}
			catch(InterruptedException e2) {} 

			System.out.println("Done"); */
			
		}catch (Exception e) {
			e.printStackTrace();
					}
	}
	
	

}
