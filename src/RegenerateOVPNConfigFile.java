
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegenerateOVPNConfigFile
{
    private static String fileNameWithAllConfigs = "/etc/openvpn/fileWithAllConfigs.txt";
    private static String newConfig = "/etc/openvpn/openvpn.conf";
    private static List<String> list_configName;
    private static List<File> list_filesInFolder;

    private static String pathToConfigs;
    private static boolean replaceLoginString = false;
    
//    static final Logger logger = Logger.getLogger(RegenerateOVPNConfigFile.class.getName());
    static final Logger logger = LogManager.getLogger(RegenerateOVPNConfigFile.class);

    public RegenerateOVPNConfigFile(){}
    
    private void run()
    {
        readConfigFiles();

        /*
         * if info file is empty (so all files were used already) -> read config files from folder and save to file 
         */
        if(list_configName == null
             || list_configName.size() == 0) {
            readConfigFilesFromFolder();
        }
        // if "fileNameWithAllConfigs" still exists and has only one name, delete it. Needed config file is already loaded
        else if (list_configName.size() == 1) {
        	File file = new File(fileNameWithAllConfigs);
        	if (file.exists()) {
        		file.delete();
        	}
        }
        createNewOVPNFile();
    }

    private void readConfigFiles()
    {

        logger.info("Start. Make sure to have at least one ovpn-file in folder...");
        File file = new File(fileNameWithAllConfigs);

        ///////////////////////////////////////////////////////////////////
        // Reading config file, saving names to list
        if (file.exists()) {
        	
            long lastModified_ = file.lastModified();
            long now_ = Calendar.getInstance().getTimeInMillis();
            long diffInSecs = (now_ - lastModified_) / 1000;
            int hours = (int) (diffInSecs / 3600);

            list_configName = new ArrayList<String>();
            list_configName = loadFile(fileNameWithAllConfigs);
            logger.info("Found information file: " + fileNameWithAllConfigs+ " with " + list_configName.size() + " files.");

            if(hours > 59) {
                logger.info("Last modification: " + (float) hours / 24 + " days ago");
            } else {
                logger.info("Last modification: " + hours + " hours ago");
            }
            
        } else {
        	
            try {
                if(!file.createNewFile()) {
                    logger.info("no information file created. Exit");
                    System.exit(0);
                } else {
                    logger.info("Creating file: " + fileNameWithAllConfigs);
                }
                
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void readConfigFilesFromFolder()
    {
        logger.info("Reading config files from folder: '" + pathToConfigs + "'");

        // Source:
        // https://stackoverflow.com/questions/1844688/how-to-read-all-files-in-a-folder-from-java
        try {
            list_filesInFolder = new ArrayList<File>();
            list_configName = new ArrayList<String>();
            list_filesInFolder = 
                    Files
                    	.walk(Paths.get(pathToConfigs))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
            logger.info("Found " + list_filesInFolder.size() + " files");
            // store fileNameWithAllConfigss to info file
            String allConfigs = "";
            for(File configName : list_filesInFolder)
            {
                allConfigs += configName.toString() + "\n";
                list_configName.add(configName.getAbsolutePath());
            }
            writeFile(fileNameWithAllConfigs, allConfigs);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String getRandomFile()
    {
        String newConfigName = "";

        try
        {
            // random number in between 0 (inclusive) and x.size (exclusive)
            int random = 0;
            String allConfigs = "";
            File file = new File(fileNameWithAllConfigs);
            
//            do
//            {
//                if (list_configName != null
//                    && list_configName.size() > 0)
//                    random = new Random().nextInt(list_configName.size() - 1);
//            }while(random < 0 || random >= list_configName.size());

            if (list_configName != null) {
//                random = new Random().nextInt(list_configName.size() - 1);
//                return (int) ((Math.random() * (max - min)) + min);
                random = (int) ((Math.random() * (
                		(list_configName.size() - 1) - 0)
                		) + 0);
            }
            
            // delete info file if emtpy
            if (list_configName == null
                || list_configName.size() == 0)
            {
                logger.info("All loaded config files used, Deleting info file. Will be recreated on next runtime");
                if (file.exists()) {
                	if(file.delete()) {
                		logger.info("File deleted.");
                	} else {
                		logger.info("File not deleted.");
                	}
                } else {
                	logger.info("File does not exist.");
                }
            }
            logger.info("Get a random config (index: " + random + ") ... -> File: " + list_configName.get(random));
            newConfigName = list_configName.get(random);
            list_configName.remove(random);
            // write new file names to information file, but with one file (=name) less than before
            for(String temp : list_configName) {
                allConfigs += temp.toString() + "\n";
            }
            writeFile(fileNameWithAllConfigs, allConfigs);
            
        } catch(Exception e) {
            e.printStackTrace();
            return list_configName.get(0);
        }
        return newConfigName;
    }

    private void createNewOVPNFile()
    {
        String configName = getRandomFile();

        try
        {
            // write new config file
            Files.copy(
            		Paths.get(configName),
            		Paths.get(newConfig),
            		StandardCopyOption.REPLACE_EXISTING);

            // Some config files dont need the login info, its already stored in key, so no need to replace
            if(replaceLoginString) {
                // reading config file
                List<String> fileContent = new ArrayList<>(
                		Files.readAllLines(
        				Paths.get(newConfig),
        				StandardCharsets.UTF_8));
    
                    // Replacing line with login-data-txt-file
                    logger.info("Replacing line with login info file");
                    for(int i = 0; i < fileContent.size(); i++) {
                        if(fileContent.get(i).equals("auth-user-pass")) {
                            fileContent.set(i,"auth-user-pass /etc/openvpn/user.txt");
                            break;
                        }
                    }
                
    
                // Write new config file
                Files.write(
                        Paths.get(newConfig), // new filename
                        fileContent, // new file content
                        StandardCharsets.UTF_8 // options
                );
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadFile(String fpath)
    {
        try {
            return new ArrayList<>(
                    Files.readAllLines(
                            Paths.get(fpath),
                            StandardCharsets.UTF_8)
                    );

        } catch(Exception e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }

    // public static Vector loadFileByLines(String fpath)
    // {
    // Vector lines = new Vector();
    // String line;
    // FileReader finr = null;
    // BufferedReader finb = null;
    //
    // try
    // {
    // finr = new FileReader(fpath);
    // finb = new BufferedReader(finr);
    //
    // while((line = finb.readLine()) != null)
    // lines.addElement(line);
    // closeData(fpath, finr, finb);
    // finb = null; finr = null;
    // }
    // catch (IOException e)
    // {
    // logger.info("#### Datei " + fpath + " kann nicht geöffnet werden:
    // " + e);
    // closeData(fpath, finr, finb);
    // lines.removeAllElements();
    // finb = null; finr = null;
    // }
    // return(lines);
    // }

    public static void closeData(String fname, FileReader finr, BufferedReader finb)
    {
        try {
            if(finb != null) {
                finb.close();
            }
            if(finr != null)
            {
                finr.close();
            }
        } catch(IOException e) {
//            logger.info("#### File " + fname + " can not be closed: " + e);
            e.printStackTrace();
        }
    }

    private static void writeFile(String file, String content)
    {
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close();
            fw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isNextPositionEndOfArray(String[] args, int position)
    {
        if(args.length - 1 >= position + 1) {
            return false;
        } else {
            return true;
        }
    }

    private String showHelp ()
    {
        return "Description\n"
        		+ "\tThis program can find ovpn-config-files from a specific folder,\n"
        		+ "\tread a random file from it and regenerate a new ovpn-config-file to the ovpn-working-dir.\n"
        		+ "Options:\n"
                + "\t[-h|-help|-?] 	for help\n"
                + "\t[-path]     	for specific directory of config files\n"
                + "\t[-replace]  	for replace login string like 'auth <loginname>' in configfile\n"
                + "Exit";
    }
    
    public static void main(String[] args)
    {
        ///////////////////////////////////////////////////////////////////
        // telling java to really run headless, otherwise an exception is thrown
        System.setProperty("java.awt.headless", "true");
        RegenerateOVPNConfigFile myobj = new RegenerateOVPNConfigFile();
        
//    	try {
//			LogManager.getLogManager().readConfiguration(new FileInputStream(
//					System.getProperty("user.dir")+File.separator+"config"+File.separator+"log4j.properties"));
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
        String log4jConfigFile = System.getProperty("user.dir")+File.separator+"config"+File.separator+"log4j.xml";
//        ConfigurationSource source = null;
//		try {
//			source = new ConfigurationSource(new FileInputStream(log4jConfigFile));
//			Configurator.initialize(null, source);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//        System.setProperty("log4j.configurationFile", log4jConfigFile);
    	if(new File(log4jConfigFile).exists()) {
    		logger.info("File found");
    	} else {
    		logger.info("File not found");
    	}
        
        // args = new String [1];
        // args[0] = "-h";

        ///////////////////////////////////////////////////////////////////
        // read all data from input
        if(args.length == 0) {
            logger.info("No input found. Exit...");
            System.exit(0);
        }
        for(int i = 0; i < args.length; i++) {
            if(args.length == 1 && args[i].startsWith("-") && (
                    args[i].toLowerCase().contains("h")
                    || args[i].toLowerCase().contains("help")
                    || args[i].toLowerCase().contains("?")
                    )
                    )
            {
                logger.info(myobj.showHelp());
                System.exit(0);
            }
            if(args[i].equals("-path") && !isNextPositionEndOfArray(args, i)) {
                pathToConfigs = args[i + 1];
            }
            if(args[i].equals("-replace")) {
                replaceLoginString = true;
            }
        }

        try {
	        // Check input params
	        if(!new File(pathToConfigs).isDirectory()) {
	            logger.info("Error: Found input: " + pathToConfigs + ", but its not a directory. Exit");
	            System.exit(1);
	        } else {
	            logger.info("Found input: " + pathToConfigs);
	        }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        if(!replaceLoginString) {
            logger.info("NOT replacing login string in config");
        } else {
            logger.info("Replacing login string in config");
        }
        myobj.run();
    }
}
