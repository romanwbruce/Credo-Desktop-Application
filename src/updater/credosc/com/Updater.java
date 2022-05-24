package updater.credosc.com;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
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
	
	public static String workingDirectory = "";
	
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
				
		File test = new File(workingDirectory+"/app.properties");
		
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
		
		File dir = new File(workingDirectory+"/releases");
		
		this.createDirectoryIfNotExists(workingDirectory+"/releases");
		
		String currentReleaseFile = "";
		if(dir != null) {
			if(dir.listFiles()!=null) {
				for(File f : dir.listFiles()) {
					currentReleaseFile = f.getName();
				}
			}
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
		
		if(currentReleaseFile == null || currentReleaseFile == "") {
			logger.write("We don't have a release file.. downloading...");
			downloadLatest();
			open();
		}
		
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
		if(directoryToBeDeleted.listFiles() != null) {
		        for (File file : directoryToBeDeleted.listFiles()) {
		            deleteDirectory(file);
		        }
		    return directoryToBeDeleted.delete();
		}
		return true;
	}
	
	void downloadLatest() {
		
		this.downloadReleaseFile();
		
		logger.write("Downloading latest version from "+releaseRepo);
		logger.write("Saving as... "+saveFileName);
		
		deleteDirectory(new File(workingDirectory+"/releases"));
		createDirectoryIfNotExists(workingDirectory+"/releases");
		
		try {
			InputStream in = new URL(latestSource).openStream();
			Files.copy(in, Paths.get(workingDirectory+"/releases/"+saveFileName), StandardCopyOption.REPLACE_EXISTING);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		logger.write("Writing download data.");
		
		try { 

		FileOutputStream out = new FileOutputStream(workingDirectory+"/"+configFile);
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

		try {
		 InputStream in = getClass().getResourceAsStream("app.properties");
		 //File file = new File(workingDirectory+"/app.properties");
         //FileReader reader = new FileReader(inputStream);
		 props = new Properties();		 
		 props.load(in);
		 
		 
		 app = props.getProperty("app");
		 releaseRepo  = props.getProperty("release_repo");
		 releaseDirectory = "releases";
		 currentRelease = props.getProperty("downloaded_version");
		 runnableName = props.getProperty("runnable_name");
		
		 JOptionPane.showMessageDialog(null, app+", "+releaseRepo+", "+releaseDirectory+", "+currentRelease+", "+runnableName);
		 
		 logger.write("Detected app: "+releaseRepo);
		}catch(Exception e) {
			String tek = " ";
			for(StackTraceElement el : e.getStackTrace()) {
				tek += el.getMethodName()+" @ line "+el.getLineNumber();
			}
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage()+", "+tek);
		}
	}
	
	void downloadReleaseFile() {
		try {
			
			JOptionPane.showInternalMessageDialog(null, "Download "+app+"?");
			download("https://raw.githubusercontent.com/romanwbruce/DummyReleases/main/"+app+"/"+app+".properties", "updates.properties");
			 
			 
			 
			 File file = new File(workingDirectory+"/updates.properties");

			 Properties props2 = new Properties();
			 props2.load(new FileInputStream(file));
			 
			 this.latestRelease = props2.getProperty("current_version");
			 this.latestSource = props2.getProperty("exec_download");
			 this.saveFileName = props2.getProperty("runnable_name");
			 
		}catch(Exception e) {
			
		}
	}
	
	void download(String uri, String outputName) {
		URL url = null;
		try {
			url = new URL(uri);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream is = null;
		OutputStream os = null;
		try {
		    os = new FileOutputStream(new File(workingDirectory+"/"+outputName));
		    
		    is = url.openStream();
		    copy(is, os);
		    
		} catch (IOException exp) {
		    exp.printStackTrace();
		} finally {
		    try {
		        is.close();
		    } catch (Exception exp) {
		    }
		    try {
		        os.close();
		    } catch (Exception exp) {
		    }
		}

	}
	
	void copy(InputStream source, OutputStream target) throws IOException {
	    byte[] buf = new byte[8192];
	    int length;
	    while ((length = source.read(buf)) > 0) {
	        target.write(buf, 0, length);
	    }
	}
	
	void open() {
		logger.write("Opening: "+runnableName);
		Desktop desktop = Desktop.getDesktop();
	    try {
			desktop.open(new File(workingDirectory+"/releases/"+saveFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	    System.exit(0);
	}
	
	void createDirectoryIfNotExists(String dir) {
	    String directoryName = dir;

	    File directory = new File(directoryName);
	    if (! directory.exists()){
	        directory.mkdir();
	    }
	}

	public static void main(String[] args) {
		
		workingDirectory = System.getProperty("user.dir");
		System.out.println("Using "+workingDirectory);
		JOptionPane.showMessageDialog(null, "Working dir: "+workingDirectory);
		
		 String parent = null;
		try {
			parent = new File(Updater.class.getProtectionDomain().getCodeSource().getLocation()
					    .toURI()).getParentFile().getPath();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		workingDirectory=parent;
		 
	JOptionPane.showMessageDialog(null, "Parent dir: "+parent);
		try {
			new Updater();
		}catch(Exception e) {
			String tek = " ";
			for(StackTraceElement el : e.getStackTrace()) {
				tek += el.getMethodName()+" @ line "+el.getLineNumber();
			}
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage()+", "+tek);
		}
	}

}