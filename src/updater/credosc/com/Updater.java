package updater.credosc.com;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import javax.swing.JOptionPane;

public class Updater {
	
	RuntimeLogger logger = new RuntimeLogger();
	File configFile = new File("app.properties");
	Properties props; 
	
	String releaseDirectory = "";
	String releaseRepo		= "";
	String app 				= "";
	String currentRelease   = "";
	String runnableName		= "";
	
	String latestRelease 	= "";
	String latestSource 	= "";
	String saveFileName 	= "";

	
	public Updater() {
		logger.write("Loading properties...");
				
		File test = new File("app.properties");
		
		if(!test.exists()) {
			logger.write("NO APP PROPERTIES DETECTED!");
			System.exit(0);
		}else {
			logger.write("Found app properties");
		}
		
		
		try {
			loadProperties();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		//logger.write("Updater version "+props.getProperty("updater_version"));
		
		
		logger.write("Downloading release file...");
		this.downloadReleaseFile();
		
		checkForUpdates();
	}
	
	void checkForUpdates() {
		logger.write("Checking for updates for "+app);
		
		File dir = new File("releases");
		
		this.createDirectoryIfNotExists("releases");
		
		String currentReleaseFile = "";
		for(File f : dir.listFiles()) {
			currentReleaseFile = f.getName();
		}
	//	if(currentReleaseFile == null || currentReleaseFile == "") {
	//		logger.write("No version detected... Downloading latest.");
	//		downloadLatest();
//		}else {
			checkIfNewest(currentReleaseFile);
//		}
	}
	
	void checkIfNewest(String currentReleaseFile) {
		logger.write("Current downloaded file: "+currentReleaseFile);
		logger.write("Current downloaded version: "+currentRelease);
		
		if(!currentRelease.equalsIgnoreCase(latestRelease)) {
			JOptionPane.showMessageDialog(null, "New version detected: "+latestRelease+"");
			logger.write("---- NEW CURRENT VERSION DETECTED: "+latestRelease +" ----");
			logger.write("Latest download: "+latestSource);
			downloadLatest();
			open();
		}else {
			logger.write("We have the latest version, running...");
			open();
		}
	}
	
	boolean deleteDirectory(File directoryToBeDeleted) {
	    File[] allContents = directoryToBeDeleted.listFiles();
	    if (allContents != null) {
	        for (File file : allContents) {
	            deleteDirectory(file);
	        }
	    }
	    return directoryToBeDeleted.delete();
	}
	
	void downloadLatest() {
		
		this.downloadReleaseFile();
		
		logger.write("Downloading latest version from "+releaseRepo);
		logger.write("Saving as... "+saveFileName);
		
		deleteDirectory(new File("releases"));
		createDirectoryIfNotExists("releases");
		
		try {
			InputStream in = new URL(latestSource).openStream();
			Files.copy(in, Paths.get("releases/"+saveFileName), StandardCopyOption.REPLACE_EXISTING);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		logger.write("Writing download data.");
		
		try { 

		FileOutputStream out = new FileOutputStream(configFile);
	    //InputStream inputStream = this.getClass().getResourceAsStream("/app.properties");
		props.setProperty("downloaded_version", latestRelease);
		props.setProperty("runnable_name", saveFileName);
		runnableName = saveFileName;
		props.store(out, null);
		out.close();
		
		}catch(Exception e) {
			
		}
	}
	
	void loadProperties() throws Exception{
        InputStream inputStream = this.getClass().getResourceAsStream("/app.properties");
        
        //FileReader reader = new FileReader(inputStream);
		 props = new Properties();		 
		 props.load(inputStream);
		 
		 
		 app = props.getProperty("app");
		 releaseRepo  = props.getProperty("release_repo");
		 releaseDirectory = "releases";
		 currentRelease = props.getProperty("downloaded_version");
		 runnableName = props.getProperty("runnable_name");
		 
		 logger.write("Detected app: "+releaseRepo);
	}
	
	void downloadReleaseFile() {
		try {
			InputStream in = new URL("https://raw.githubusercontent.com/romanwbruce/DummyReleases/main/"+app+"/"+app+".properties").openStream();
		

			
			Files.copy(in, Paths.get("./updates.properties"), StandardCopyOption.REPLACE_EXISTING);
	         InputStream inputStream = this.getClass().getResourceAsStream("/updates.properties");
			 Properties props2 = new Properties();
			 props2.load(inputStream);
			 
			 this.latestRelease = props2.getProperty("current_version");
			 this.latestSource = props2.getProperty("exec_download");
			 this.saveFileName = props2.getProperty("runnable_name");
			 
		}catch(Exception e) {
			
		}
	}
	
	void open() {
		logger.write("Opening: "+runnableName);
		Desktop desktop = Desktop.getDesktop();
	    try {
			desktop.open(new File("releases/"+saveFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void createDirectoryIfNotExists(String dir) {
	    String directoryName = dir;

	    File directory = new File(directoryName);
	    if (! directory.exists()){
	        directory.mkdir();
	    }
	}

	public static void main(String[] args) {
		new Updater();
	}

}
