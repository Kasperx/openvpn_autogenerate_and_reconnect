
package main.java.com.openvpn_autogenerate_and_reconnect;

import java.io.File;
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


import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

public class Main extends Tools
{
//    private static String fileNameWithAllConfigs = "/etc/openvpn/fileWithAllConfigs.txt";
//    private static String newConfig = "/etc/openvpn/openvpn.conf";
//    private static List<String> list_configName;
//    private static List<File> list_filesInFolder;
//
//    private static String pathToConfigs;
//    private static boolean replaceLoginString = false;
//    
////    static final Logger logger = Logger.getLogger(RegenerateOVPNConfigFile.class.getName());
	Main main;
	
    public Main(){}
    
    private void exitWithError() {
    	logger.error("Exit");
    	System.exit(1);
    }
    private void showInfo(String pathToConfigFiles, boolean replaceLoginString, String pathToConfigFile) {
		logger.info("###");
//		logger.info("Parameter:");
//		logger.info("\tPath to config file: \t'"+pathToConfigFile+"'");
//		logger.info("\tReplace login info in openvpn file: \t'"+replaceLoginString+"'");
//		logger.info("\tPath to dir with config files: \t'"+pathToConfigFiles+"'");
		String[] line = {
				pathToConfigFiles,
				pathToConfigFile,
				toString(replaceLoginString)
				};
		String[] text = {
				"Path to dir with config files",
				"Path to config file",
				"Replace login info in openvpn file"
				};
		Table parameter =
				Table
				.create("\n\tParameter")
				.addColumns(
						StringColumn.create("Parameter", text),
						StringColumn.create("Discription", line)
				);
		logger.info(parameter.print());
		logger.info("###");
    }
    private String toString(boolean value) {
    	return value ? "true" : "false";
    }
    private void run(String pathToConfigFiles, boolean replaceLoginString, String pathToConfigFile) {

    	main = this;
    	if(consoleOut) {
    		showInfo(pathToConfigFiles, replaceLoginString, pathToConfigFile);
    	}
    	boolean [] status_success = new boolean [2];
    	/**************************/
    	/* check input params */
    	/**************************/
    	/* check directory with config files*/
	    if(checkDirectoryWithConfigFiles(pathToConfigFiles) <= 0) {
	    	this.pathToConfigFiles = pathToConfigFiles;
	    	status_success[0] = true;
	    } else {
	    	status_success[0] = false;
//	    	exitWithError();
	    }
	    /**************************/
	    /* check config files*/
	    if(checkConfigFile(pathToConfigFile) <= 0) {
	    	status_success[1] = true;
	    } else {
	    	status_success[1] = false;
	    }
	    /**************************/
	    /* go on */
	    for(boolean status: status_success) {
	    	if(!status) {
	    		exitWithError();
	    	}
	    }
		this.replaceLoginString = replaceLoginString;
		if(this.replaceLoginString) {
			logger.info("(Replacing login string in config)");
		} else {
			logger.info("(NOT replacing login string in config)");
		}
		run();
    }
    private void runWithInfo(String pathToConfigFiles, boolean replaceLoginString, String pathToConfigFile) {
    	
    	main = this;
    	if(StringUtils.isEmpty(pathToConfigFiles)) {
    		pathToConfigFiles = this.pathToConfigFiles;
    	}
    	if(StringUtils.isEmpty(pathToConfigFile)) {
    		pathToConfigFile = this.fileNameWithAllConfigs;
    	}
    	String[] parameter = {
				pathToConfigFiles,
				pathToConfigFile,
				"replaceLoginString",
				"test"
				};
    	String [] value = new String [parameter.length];
    	String [] info = new String [parameter.length];
    	boolean [] status_success = new boolean [2];
    	int [] i_status_success = new int [2];
    	///////////////////////////////////////////////////////
    	/* check input params */
    	///////////////////////////////////////////////////////
    	/* check directory with config files*/
    	if((i_status_success[0] = checkDirectoryWithConfigFiles(pathToConfigFiles)) <= 0) {
    		this.pathToConfigFiles = pathToConfigFiles;
    		status_success[0] = true;
    	} else {
    		status_success[0] = false;
//	    	exitWithError();
    	}
    	switch (i_status_success[0]) {
    	case 0:
    		value[0] = "Existing";
    		break;
    	case 1:
    		value[0] = "Existing, but not a directory";
    		break;
		case 2:
			value[0] = "Not existing";
			break;
		case 3:
			value[0] = "Exception";
			break;
		case 4:
			value[0] = "Existing, but has no files in it";
			break;
		default:
			break;
		}
    	///////////////////////////////////////////////////////
    	/* check config files*/
    	if((i_status_success[1] = checkConfigFile(pathToConfigFile)) <= 0) {
    		status_success[1] = true;
    	} else {
    		status_success[1] = false;
    	}
    	switch (i_status_success[1]) {
    		case 0:
    			value[1] = "(Right now) Existing, writeable";
    			break;
    		case 1:
    			value[1] = "Not existing and program has no permission to create this file";
    			break;
    		case 2:
    			value[1] = "Program has security problems on this file";
    			break;
    		case 3:
    			value[1] = "Exception";
    			break;
    	}
    	///////////////////////////////////////////////////////
    	/* go on */
    	this.replaceLoginString = replaceLoginString;
    	value[2] = toString(replaceLoginString);
    	value[3] = toString(test);
    	///////////////////////////////////////////////////////
    	/* get last mod time of file */
    	int count = 0;
    	if((count = countFiles(pathToConfigFiles)) == 0) {
    		info[0] = "Containing no files";
    	} else if(count == 1) {
    		info[0] = "Containing 1 file";
    	} else {
    		info[0] = "Containing " + count + " files";
    	}
    	info[1] = getLastModTime(new File(pathToConfigFile));
    	info[3] = "Run program and read info, but don't modify config file '"+openvpnConfigFile+"'";
    	///////////////////////////////////////////////////////
    	Table table = Table.create("").addColumns(
						StringColumn.create("Parameter", parameter),
						StringColumn.create("Value", value),
						StringColumn.create("Discription", info)
				);
    	table.appendRow();
		logger.info(table.print());
		for(boolean status: status_success) {
    		if(!status) {
    			exitWithError();
    		}
    	}
    	run();
    }
    private int countFiles(String directory) {
    	return getConfigFilesFromFolder(directory).size();
    }
	private int checkDirectoryWithConfigFiles(String pathToConfigFiles) {
		File file = new File(pathToConfigFiles);
		logger.info("Start. Make sure to have at least one ovpn-file in directory '" + file.getAbsolutePath() + "'");
		try {
	    	if(file.exists()) {
		        if(file.isDirectory()) {
//		        	logger.info("Found directory: '" + file.getAbsolutePath() + "'.");
		        	if(hasDirFiles(file.getAbsolutePath())) {
		        		return 0;
		        	} else {
		        		return 4;
		        	}
		        } else {
//		        	logger.error("Directoryname '" + file.getAbsolutePath() + "' is not a directory.");
		        	return 1;
		        }
	    	} else {
//	    		logger.error("Directory '" + file.getAbsolutePath() + "' does not exist.");
	    		return 2;
	    	}
	    	
	    } catch (Exception e) {
	        e.printStackTrace();
	        return 3;
	    }
	}
	private boolean hasDirFiles(String dirPath) {
//		return getConfigFilesFromFolder(dirPath).size() > 0;
		return countFiles(dirPath) > 0;
	}
    private void run()
    {
//    	main = this;
//        logger = LogManager.getLogger(Main.class);
//        logger.info("Start. Make sure to have at least one ovpn-file in directory '" + pathToConfigFiles + "'");
    	list_configName = getFilenameFromConfigfile();
        /*
         * if info file is empty (so all files were used already) -> read config files from folder and save to file 
         */
        // if "fileNameWithAllConfigs" still exists and has only one name, delete it. Needed config file is already loaded
        if (list_configName.size() == 1) {
        	File file = new File(fileNameWithAllConfigs);
        	if (file.exists()) {
        		file.delete();
        	}
        } else if(list_configName.size() == 0) {
        	list_filesInFolder = getConfigFilesFromFolder(pathToConfigFiles);
        	writeConfigFileNamesToConfig(fileNameWithAllConfigs, list_filesInFolder);
        }
        String newConfigName = getRandomFile(list_filesInFolder);
        if(!test) {
        	createNewOVPNFile(newConfigName);
        }
    }

//    private int readConfigFiles()
//    {
//
//        File file = new File(fileNameWithAllConfigs);
//        ///////////////////////////////////////////////////////////////////
//        // Reading config file, saving names to list
//        if (file.exists()) {
//        	
//        	long lastModified_ = file.lastModified();
//        	long now_ = Calendar.getInstance().getTimeInMillis();
//        	long diffInSecs = (now_ - lastModified_) / 1000;
//        	int hours = (int) (diffInSecs / 3600);
//        	
////        	list_configName = new ArrayList<String>();
//        	list_configName = Tools.loadFile(fileNameWithAllConfigs);
//        	logger.info("Found information file: " + fileNameWithAllConfigs+ " with " + list_configName.size() + " files.");
//        	
//        	if(hours > 59) {
//        		logger.info("Last modification: " + (float) hours / 24 + " days ago");
//        	} else {
//        		logger.info("Last modification: " + hours + " hours ago");
//        	}
//        	return 0;
//        	
//        } else {
//        	
//        	try {
//        		if(file.createNewFile()) {
//        			logger.info("Created file: '" + fileNameWithAllConfigs + "'.");
//        		} else {
//        			logger.info("File already exists: '" + fileNameWithAllConfigs + "'.");
////                	logger.info("Exit.");
////                	logger.info("No information file created. Exit");
////        			System.exit(0);
//        			return 2;
//        		}
//        		return 0;
//        		
//        	} catch(IOException e) {
//        		logger.error("File '" + fileNameWithAllConfigs + "' does not exist and program has no permission to create this file.");
////        		logger.error("Exit.");
////        		System.exit(1);
//        		return 3;
//        	} catch(SecurityException e) {
//        		logger.error("Program has security problems on this file.");
////        		logger.error("Exit");
////        		System.exit(1);
//        		return 4;
//        	} catch(Exception e) {
//        		e.printStackTrace();
////        		System.exit(1);
//        		return 5;
//        	}
//        }
//    }
    private String getLastModTime(File file) {
//    	File file = new File(fileNameWithAllConfigs);
    	String text = null;
        ///////////////////////////////////////////////////////////////////
        // Reading config file, saving names to list
    	long lastModified_ = file.lastModified();
    	long now_ = Calendar.getInstance().getTimeInMillis();
    	long diffInSecs = (now_ - lastModified_) / 1000;
    	int hours = (int) (diffInSecs / 3600);
    	if(hours > 59) {
    		text = "Last modification: " + (float) hours / 24 + " days ago";
    	} else {
    		text = "Last modification: " + hours + " hours ago";
    	}
    	return text;
    }
    private List <String> getFilenameFromConfigfile()
    {

        File file = new File(fileNameWithAllConfigs);
        ///////////////////////////////////////////////////////////////////
        // Reading config file, saving names to list
//    	long lastModified_ = file.lastModified();
//    	long now_ = Calendar.getInstance().getTimeInMillis();
//    	long diffInSecs = (now_ - lastModified_) / 1000;
//    	int hours = (int) (diffInSecs / 3600);
//    	if(hours > 59) {
//    		logger.info("Last modification: " + (float) hours / 24 + " days ago");
//    	} else {
//    		logger.info("Last modification: " + hours + " hours ago");
//    	}
    	return Tools.loadLinesFromConfigFile(fileNameWithAllConfigs);
//    	logger.info("Found information file: " + fileNameWithAllConfigs+ " with " + list_configName.size() + " files.");
    	
    }
    private int checkConfigFile(String fileNameWithAllConfigs)
    {
    	
    	File file = new File(fileNameWithAllConfigs);
    	///////////////////////////////////////////////////////////////////
    	// Reading config file, saving names to list
    	if (file.exists()) {
//    		list_configName = Tools.loadFile(fileNameWithAllConfigs);
//    		logger.info("Found information file: " + fileNameWithAllConfigs + "'.");
    		this.fileNameWithAllConfigs = fileNameWithAllConfigs;
    		return 0;
    	} else {
    		try {
    			if(file.createNewFile()) {
//    				logger.info("Created file: '" + fileNameWithAllConfigs + "'.");
    			} else {
//    				logger.info("File already exists: '" + fileNameWithAllConfigs + "'.");
    			}
    			this.fileNameWithAllConfigs = fileNameWithAllConfigs;
    			this.fileNameWithAllConfigs = fileNameWithAllConfigs;
    			return 0;
    		} catch(IOException e) {
//    			logger.error("File '" + fileNameWithAllConfigs + "' does not exist and program has no permission to create this file.");
    			return 1;
    		} catch(SecurityException e) {
//    			logger.error("Program has security problems on this file.");
    			return 2;
    		} catch(Exception e) {
    			e.printStackTrace();
    			return 3;
    		}
    	}
    }
    private void writeConfigFileNamesToConfig(String fileNameWithAllConfigs, List<File> configFiles) {
    	String allConfigs = "";
    	for(File configName : configFiles)
        {
            allConfigs += configName.toString() + "\n";
        }
        Tools.writeFile(fileNameWithAllConfigs, allConfigs);
    }
    private List<File> getConfigFilesFromFolder(String pathToConfigFiles)
    {
//        logger.info("Reading config files from folder: '" + pathToConfigFiles + "'.");
        // Source:
        // https://stackoverflow.com/questions/1844688/how-to-read-all-files-in-a-folder-from-java
        try {
            list_filesInFolder = new ArrayList<File>();
            list_configName = new ArrayList<String>();
            list_filesInFolder = 
                    Files
                    	.walk(Paths.get(pathToConfigFiles))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
            if(list_filesInFolder.size() > 0) {
//	            logger.info("Found " + list_filesInFolder.size() + " files.");
	            // store fileNameWithAllConfigss to info file
//	            String allConfigs = "";
                List<File> list_configName = new ArrayList<>();
	            for(File configName : list_filesInFolder)
	            {
//	                allConfigs += configName.toString() + "\n";
	                list_configName.add(configName);
	            }
//	            Tools.writeFile(fileNameWithAllConfigs, allConfigs);
	            return list_configName;
            
            } else {
            	
            	logger.error("Directory '" + pathToConfigFiles + "' has no files.");
            	return new ArrayList<File>();
            	
            }
        } catch(Exception e) {
            e.getMessage();
            e.printStackTrace();
            return new ArrayList<File>();
        }
    }
    private String getRandomFile(List<File> list_filesInFolder)
    {
        String newConfigName = "";

        try
        {
            // random number in between 0 (inclusive) and x.size (exclusive)
            String allConfigs = "";
            File file = new File(fileNameWithAllConfigs);
            
//            do
//            {
//                if (list_configName != null
//                    && list_configName.size() > 0)
//                    random = new Random().nextInt(list_configName.size() - 1);
//            }while(random < 0 || random >= list_configName.size());

            if (list_configName != null && list_configName.size() > 0) {
//                random = new Random().nextInt(list_configName.size() - 1);
//                return (int) ((Math.random() * (max - min)) + min);
            	int random = (int) ((Math.random() * (
                		(list_filesInFolder.size() - 1) - 0)
                		) + 0);
                logger.info("Get a random config (index: " + random + ") ... -> File: " + list_configName.get(random));
                newConfigName = list_configName.get(random);
                list_configName.remove(random);
                // write new file names to information file, but with one file (=name) less than before
                for(String temp : list_configName) {
                    allConfigs += temp.toString() + "\n";
                }
                Tools.writeFile(fileNameWithAllConfigs, allConfigs);
            } else if (list_filesInFolder.size() == 1) {
            	newConfigName = list_filesInFolder.get(0).getAbsolutePath();
            }
            // delete info file if emtpy
//            if (list_configName == null || list_configName.size() == 0) {
            else {
                logger.info("All loaded config files used, Deleting info file '" + fileNameWithAllConfigs + "' (Will be recreated on next runtime).");
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
            
        } catch(Exception e) {
        	logger.error("list_configName == null? "+list_configName == null);
        	logger.error("list_configName count of elements: "+list_configName.size());
        	logger.error(e.getMessage());
            e.printStackTrace();
            if(list_configName == null || list_configName.size() <= 0) {
            	return null;
            } else {
            	return list_configName.get(0);
            }
        }
        return newConfigName;
    }

    private void createNewOVPNFile(String newConfig)
    {
//        String configName = getRandomFile(list_filesInFolder); 
        try
        {
            // write new config file
            Files.copy(
            		Paths.get(newConfig),
            		Paths.get(openvpnConfigFile),
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
        	logger.error("createNewOVPNFile:");
        	logger.error("newconfig: "+newConfig+ new File(newConfig).exists());
        	logger.error("openvpnConfigFile: "+openvpnConfigFile+ new File(openvpnConfigFile).exists());
        	logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

//    public static List<String> loadFile(String fpath)
//    {
//        try {
//            return new ArrayList<>(
//                    Files.readAllLines(
//                            Paths.get(fpath),
//                            StandardCharsets.UTF_8)
//                    );
//
//        } catch(Exception e) {
//            e.printStackTrace();
//            return new ArrayList<String>();
//        }
//    }

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

//    public static void closeData(String fname, FileReader finr, BufferedReader finb)
//    {
//        try {
//            if(finb != null) {
//                finb.close();
//            }
//            if(finr != null)
//            {
//                finr.close();
//            }
//        } catch(IOException e) {
////            logger.info("#### File " + fname + " can not be closed: " + e);
//            e.printStackTrace();
//        }
//    }

//    private static void writeFile(String file, String content)
//    {
//        try {
//            FileWriter fw = new FileWriter(file);
//            BufferedWriter bw = new BufferedWriter(fw);
//            bw.write(content);
//            bw.flush();
//            bw.close();
//            fw.close();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private static boolean isNextPositionEndOfArray(String[] args, int position)
//    {
//        if(args.length - 1 >= position + 1) {
//            return false;
//        } else {
//            return true;
//        }
//    }

    private void showHelp ()
    {
//        logger.info("Description");
    	logger.info("This program can find ovpn-config-files from a specific folder, read a random file from it and regenerate a new ovpn-config-file to the ovpn-working-dir.");
    	logger.info("Syntax: Main [-h|-?|-help] [-path] [-replace]");
    	logger.info("Options:");
    	logger.info("\t[-h|-help|-?] 	show this help and exit");
    	logger.info("\t[--path]     	specific directory of config files. Default = '" + this.pathToConfigFiles + "'");
    	logger.info("\t[--config-file]     	specific directory of config file with names. Default = '" + this.fileNameWithAllConfigs + "'");
    	logger.info("\t[-replace]  	replace login string like 'auth <loginname>' in configfile. Default = '" + this.replaceLoginString + "'");
    	logger.info("Exit");
		System.exit(0);
    }
    
    public static void main(String[] args)
    {
        ///////////////////////////////////////////////////////////////////
        // telling java to really run headless, otherwise an exception is thrown
        System.setProperty("java.awt.headless", "true");
        Main main = new Main();
        main.logger = LogManager.getLogger(Main.class);
        String pathToConfigFiles = null;
        String pathToConfigFile = null;
        boolean replaceLoginString = false;
        ///////////////////////////////////////////////////////////////////
        // find parameter -v
    	for(int i = 0; i < args.length; i++) {
    		if(args[i] == "-v") {
    			main.consoleOut = true;
    		}
    	}
    	///////////////////////////////////////////////////////////////////
    	// find parameter test
    	for(int i = 0; i < args.length; i++) {
    		if(args[i].equalsIgnoreCase("test") || args[i].equalsIgnoreCase("-test")) {
    			main.test = true;
    		}
    	}
        ///////////////////////////////////////////////////////////////////
        // read all data from input
        if(args.length == 0) {
        	main.logger.info("No input found. Exit...");
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
                main.showHelp();
//                System.exit(0);
            }
            if(args[i].equals("--path") && !Tools.isNextPositionEndOfArray(args, i)) {
            	pathToConfigFiles = args[i + 1];
            }
            if(args[i].equals("-replace")) {
                replaceLoginString = true;
            }
            if(args[i].equals("--config-file") && !Tools.isNextPositionEndOfArray(args, i)) {
            	pathToConfigFile = args[i + 1];
            }
        }
//        main.run(pathToConfigFiles, replaceLoginString, pathToConfigFile);
        main.runWithInfo(pathToConfigFiles, replaceLoginString, pathToConfigFile);
    }
}
