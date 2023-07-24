
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
import org.apache.logging.log4j.core.Logger;

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
    
    private void exitWithError(String text) {
    	if(consoleOut) {
			if(StringUtils.isNotEmpty(text)) {
				logger.error(text);
				logger.error("Exit");
			} else {
				logger.error("Exit");
			}
    	}
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
	    		exitWithError(null);
	    	}
	    }
		this.replaceLoginString = replaceLoginString;
		if(this.replaceLoginString) {
			if(consoleOut) {
				logger.info("(Replacing login string in config)");
			}
		} else {
			if(consoleOut) {
				logger.info("(NOT replacing login string in config)");
			}
		}
		run();
    }
    private void runWithInfo(String pathToConfigFiles, boolean replaceLoginString, boolean deactivate_ciphers, String pathToConfigFile, String pathToMemoryFile) {
    	
    	main = this;
    	///////////////////////////////////////////////////////
    	int count = 0;
    	int pos_pathToConfigFiles = count++;
    	int pos_pathToConfigFile = count++;
    	int pos_pathToMemoryFile = count++;
    	int pos_replaceLoginString = count++;
    	int pos_test = count++;
    	int pos_deactivate_ciphers = count++;
    	///////////////////////////////////////////////////////
    	if(StringUtils.isEmpty(pathToConfigFiles)) {
    		pathToConfigFiles = this.pathToConfigFiles;
    	}
    	if(StringUtils.isEmpty(pathToMemoryFile)) {
    		pathToMemoryFile = this.fileNameWithAllConfigs;
    	}
    	if(StringUtils.isEmpty(pathToConfigFile)) {
    		pathToConfigFile = this.openvpnConfigFile;
    	}
    	String[] parameter = {
				pathToConfigFiles,
				pathToConfigFile,
				pathToMemoryFile,
				"replaceLoginString",
				"test",
				"deactivate_ciphers"
				};
    	String [] value = new String [parameter.length];
    	String [] info = new String [parameter.length];
    	boolean [] status_success = new boolean [3];
    	int [] i_status_success = new int [3];
    	///////////////////////////////////////////////////////
    	/* check input params */
    	///////////////////////////////////////////////////////
    	/* check directory with config files*/
    	if((i_status_success[pos_pathToConfigFiles] = checkDirectoryWithConfigFiles(pathToConfigFiles)) <= 0) {
    		this.pathToConfigFiles = pathToConfigFiles;
    		status_success[pos_pathToConfigFiles] = true;
    	} else {
    		status_success[pos_pathToConfigFiles] = false;
//	    	exitWithError();
    	}
    	switch (i_status_success[pos_pathToConfigFiles]) {
    	case 0:
    		value[pos_pathToConfigFiles] = "Existing";
    		break;
    	case 1:
    		value[pos_pathToConfigFiles] = "Existing, but not a directory";
    		break;
		case 2:
			value[pos_pathToConfigFiles] = "Not existing";
			break;
		case 3:
			value[pos_pathToConfigFiles] = "Exception";
			break;
		case 4:
			value[pos_pathToConfigFiles] = "Existing, but has no files in it";
			break;
		default:
			break;
		}
    	///////////////////////////////////////////////////////
    	/* check memory file */
    	if((i_status_success[pos_pathToMemoryFile] = checkConfigFile(pathToMemoryFile)) <= 0) {
    		this.fileNameWithAllConfigs = pathToMemoryFile;
    		status_success[pos_pathToMemoryFile] = true;
    	} else {
    		status_success[pos_pathToMemoryFile] = false;
    	}
    	switch (i_status_success[pos_pathToMemoryFile]) {
    		case 0:
    			value[pos_pathToMemoryFile] = "(Right now) Existing, writeable";
    			break;
    		case 1:
    			value[pos_pathToMemoryFile] = "Not existing and program has no permission to create this file";
    			break;
    		case 2:
    			value[pos_pathToMemoryFile] = "Program has security problems on this file";
    			break;
    		case 3:
    			value[pos_pathToMemoryFile] = "Exception";
    			break;
    	}
    	///////////////////////////////////////////////////////
    	/* check config file */
    	if((i_status_success[pos_pathToConfigFile] = checkConfigFile(pathToConfigFile)) <= 0) {
    		this.openvpnConfigFile = pathToConfigFile;
    		status_success[pos_pathToConfigFile] = true;
    	} else {
    		status_success[pos_pathToConfigFile] = false;
    	}
    	switch (i_status_success[pos_pathToConfigFile]) {
    	case 0:
    		value[pos_pathToConfigFile] = "(Right now) Existing, writeable";
    		break;
    	case 1:
    		value[pos_pathToConfigFile] = "Not existing and program has no permission to create this file";
    		break;
    	case 2:
    		value[pos_pathToConfigFile] = "Program has security problems on this file";
    		break;
    	case 3:
    		value[pos_pathToConfigFile] = "Exception";
    		break;
    	}
    	///////////////////////////////////////////////////////
    	/* go on */
    	this.replaceLoginString = replaceLoginString;
    	this.deactivate_ciphers1 = deactivate_ciphers;
    	this.deactivate_ciphers2 = deactivate_ciphers;
    	value[pos_replaceLoginString] = toString(replaceLoginString);
    	value[pos_test] = toString(test);
    	value[pos_deactivate_ciphers] = toString(deactivate_ciphers);
    	///////////////////////////////////////////////////////
    	/* get last mod time of file */
    	int countFiles = 0;
    	if((countFiles = countFiles(pathToConfigFiles)) == 0) {
    		info[pos_pathToConfigFiles] = "Containing no files";
    	} else if(countFiles == 1) {
    		info[pos_pathToConfigFiles] = "Containing 1 file";
    	} else {
    		info[pos_pathToConfigFiles] = "Containing " + countFiles + " files";
    	}
    	info[pos_pathToConfigFile] = getLastModTime(new File(pathToConfigFile));
    	info[pos_pathToMemoryFile] = getLastModTime(new File(pathToMemoryFile));
    	info[pos_test] = "Run program and read info, but don't modify config file '"+openvpnConfigFile+"'";
    	info[pos_replaceLoginString] = "Input user login info to config file.";
    	info[pos_deactivate_ciphers] = "Deactivate cipher info in config file like 'data-ciphers AES-256-GCM'";
    	///////////////////////////////////////////////////////
    	if(consoleOut) {
	    	Table table = Table.create("").addColumns(
							StringColumn.create("Parameter", parameter),
							StringColumn.create("Value", value),
							StringColumn.create("Discription", info)
					);
	    	table.appendRow();
			logger.info(table.print());
    	}
//		for(boolean status: status_success) {
		for(int i=0; i<status_success.length; i++) {
    		if(!status_success[i]) {
    			exitWithError(info[i]);
    		}
    	}
    	run();
    }
    private int countFiles(String directory) {
    	return getConfigFilesFromFolder(directory).size();
    }
	private int checkDirectoryWithConfigFiles(String pathToConfigFiles) {
		File file = new File(pathToConfigFiles);
		if(consoleOut) {
			logger.info("Start. Make sure to have at least one ovpn-file in directory '" + file.getAbsolutePath() + "'");
		}
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
	    	if(consoleOut) {
    			logger.error(e.getMessage());
	    	}
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
    	list_configNames = getFilenameFromConfigfile(fileNameWithAllConfigs);
    	List<String> list_filesInFolder = getConfigFilesFromFolder(pathToConfigFiles);
//    	if(consoleOut) {
//    		logger.info("Found "+list_filesInFolder.size()+" files in folder "+pathToConfigFiles);
//    	}
    	/*
    	 * if info file is empty (so all files were used already) -> read config files from folder and save to file 
    	 */
    	list_configNames = getConfigFileFromMemoryFile(list_configNames, list_filesInFolder);
//        // if "fileNameWithAllConfigs" still exists and has only one name, delete it. Needed config file is already loaded
//        if (list_configName.size() == 1) {
//        	File file = new File(fileNameWithAllConfigs);
//        	if(consoleOut) {
//				logger.info("One known config file left. Recreating memory file '"+fileNameWithAllConfigs+"'.");
//        	}
//        	if (file.exists()) {
//        		file.delete();
//        	}
//        	///////////////////////////////////////////////////////////////////
//        	// read config files, write to memory file
//        	list_filesInFolder = getConfigFilesFromFolder(pathToConfigFiles);
//        	if(consoleOut) {
//        		logger.info("Found "+list_filesInFolder.size()+" files in folder "+pathToConfigFiles);
//        	}
//        } else if(list_configName.size() == 0) {
//        	File file = new File(fileNameWithAllConfigs);
//        	if (file.exists()) {
//        		file.delete();
//        	}
//        	if(consoleOut) {
//				logger.info("Creating memory file with file names.");
//        	}
//        	///////////////////////////////////////////////////////////////////
//        	// read config files, write to memory file
//        	list_filesInFolder = getConfigFilesFromFolder(pathToConfigFiles);
//        	if(consoleOut) {
//        		logger.info("Found "+list_filesInFolder.size()+" files in folder "+pathToConfigFiles);
//        	}
//        } else {
//        	// do nothing
//        	;
//        }
//        if(!test) {
//        	writeConfigFileNamesToConfig(fileNameWithAllConfigs, list_filesInFolder);
//        }
        String newConfigName = getRandomFileAndRewriteToMemoryFile(fileNameWithAllConfigs, list_configNames, list_filesInFolder);
        if(!test) {
        	createNewOVPNFile(newConfigName);
        }
    }
    private List<String> getConfigFileFromMemoryFile(List<String> list_configNamesFromMemoryFile, List<String> list_filesInFolder) {
    	// if "fileNameWithAllConfigs" still exists and has only one name, delete it. Needed config file is already loaded
        if (list_configNamesFromMemoryFile.size() == 1) {
        	///////////////////////////////////////////////////////////////////
        	// get last name from memoryfile
//        	writeFile(fileNameWithAllConfigs, list_filesInFolder);
        } else if(list_configNamesFromMemoryFile.size() == 0) {
//        	writeFile(fileNameWithAllConfigs, "");
        	///////////////////////////////////////////////////////////////////
        	// read files from folder, write to memoryfile
        	list_configNamesFromMemoryFile = list_filesInFolder;
//        	writeFile(fileNameWithAllConfigs, list_configNamesFromMemoryFile);
//        	if(consoleOut) {
//        		logger.info("Found "+list_filesInFolder.size()+" files in folder "+pathToConfigFiles);
//        	}
        } else /* if(list_configName.size() > 1) */{
        	// do nothing
        	;
        }
//        if(!test) {
//        	writeConfigFileNamesToConfig(fileNameWithAllConfigs, list_filesInFolder);
//        }
        return list_configNamesFromMemoryFile;
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
    private int checkConfigFile(String fileNameWithAllConfigs)
    {
    	File file = new File(fileNameWithAllConfigs);
    	///////////////////////////////////////////////////////////////////
    	// Reading config file, saving names to list
    	if (file.exists()) {
//    		list_configName = Tools.loadFile(fileNameWithAllConfigs);
//    		logger.info("Found information file: " + fileNameWithAllConfigs + "'.");
//    		this.fileNameWithAllConfigs = fileNameWithAllConfigs;
    		return 0;
    	} else {
    		try {
    			if(file.createNewFile()) {
//    				logger.info("Created file: '" + fileNameWithAllConfigs + "'.");
    			} else {
//    				logger.info("File already exists: '" + fileNameWithAllConfigs + "'.");
    			}
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
            allConfigs += configName.toString() /*+ "\n"*/;
        }
    	if(consoleOut) {
    		logger.info("Writing "+configFiles.size()+" filenames to memory file '"+fileNameWithAllConfigs+"'");
    	}
        Tools.writeFile(fileNameWithAllConfigs, allConfigs);
    }
    private List<String> getConfigFilesFromFolder(String pathToConfigFiles)
    {
//        logger.info("Reading config files from folder: '" + pathToConfigFiles + "'.");
        // Source:
        // https://stackoverflow.com/questions/1844688/how-to-read-all-files-in-a-folder-from-java
        try {
        	List<File> list_filesInFolder = new ArrayList<>();
//            list_configNames = new ArrayList<String>();
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
                List<String> list_configName = new ArrayList<>();
	            for(File configName : list_filesInFolder)
	            {
	                list_configName.add(configName.getAbsolutePath() /*+"\n"*/ ) ;
	            }
//	            Tools.writeFile(fileNameWithAllConfigs, allConfigs);
	            return list_configName;
            
            } else {
            	
            	if(consoleOut) {
    				logger.error("Directory '" + pathToConfigFiles + "' has no files.");
            	}
            	return new ArrayList<String>();
            	
            }
        } catch(Exception e) {
        	if(consoleOut) {
				e.getMessage();
        	}
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }
//    private String getRandomFile(List<File> list_filesInFolder)
    private String getRandomFileAndRewriteToMemoryFile(String fileNameWithAllConfigs, List<String> list_configName, List<String> list_filesFromFolder)
    {
        String newConfigName = "";

        try
        {
            // random number in between 0 (inclusive) and x.size (exclusive)
//            File file = new File(fileNameWithAllConfigs);
//            list_configName = getFilenameFromConfigfile();
            
//            do
//            {
//                if (list_configName != null
//                    && list_configName.size() > 0)
//                    random = new Random().nextInt(list_configName.size() - 1);
//            }while(random < 0 || random >= list_configName.size());
        	///////////////////////////////////////////////////////////////////
            if (list_configName != null && list_configName.size() > 1) {
//                random = new Random().nextInt(list_configName.size() - 1);
//                return (int) ((Math.random() * (max - min)) + min);
            	int random = (int) ((Math.random() * (
                		(list_configName.size() - 1) - 0)
                		) + 0);
            	if(consoleOut) {
    				logger.info("Get a random config (index: " + random + ") ... -> File: " + list_configName.get(random));
            	}
                newConfigName = list_configName.get(random);
                list_configName.remove(random);
                // write new file names to information file, but with one file (=name) less than before
                String allConfigs = "";
                for(String temp : list_configName) {
                    allConfigs += temp.toString() + "\n";
                }
                Tools.writeEmptyFile(fileNameWithAllConfigs);
                Tools.writeFile(fileNameWithAllConfigs, allConfigs);
            ///////////////////////////////////////////////////////////////////
            } else if (list_configName.size() == 1) {
            	newConfigName = list_configName.get(0);
            	Tools.writeEmptyFile(fileNameWithAllConfigs);
            	Tools.writeFile(fileNameWithAllConfigs, list_filesFromFolder);
        	///////////////////////////////////////////////////////////////////
            }
//            else /* if (list_filesInFolder.size() == 0) */ {
//            	if(consoleOut) {
//    				logger.info("All loaded config files used, Deleting info file '" + fileNameWithAllConfigs + "' (Will be recreated on next runtime).");
//            	}
//                if (file.exists()) {
//                	if(file.delete()) {
//                		if(consoleOut) {
//            				logger.info("File deleted.");
//                		}
//                	} else {
//                		if(consoleOut) {
//            				logger.info("File not deleted.");
//                		}
//                	}
//                } else {
//                	if(consoleOut) {
//        				logger.info("File does not exist.");
//                	}
//                }
//            }
            
        } catch(Exception e) {
        	if(consoleOut) {
				logger.error("list_configName == null? "+list_configName == null);
				logger.error("list_configName count of elements: "+list_configName.size());
        		logger.error(e.getMessage());
        	}
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
            if(replaceLoginString || deactivate_ciphers1 || deactivate_ciphers2) {
            	boolean [] found = new boolean [3];
            	// reading config file
            	List<String> fileContent = new ArrayList<>(
            			Files.readAllLines(
            					Paths.get(newConfig),
            					StandardCharsets.UTF_8));
	            // Replacing line with login-data-txt-file
            	// ... Some config files dont need the login info, its already stored in key, so no need to replace ...
	            for(int i = 0; i < fileContent.size(); i++) {
	                if(replaceLoginString
                		&& fileContent.get(i).equals("auth-user-pass")) {
				        if(consoleOut) {
							logger.info("Found line with login info. Rewriting");
				        }
	                    fileContent.set(i,"auth-user-pass /etc/openvpn/user.txt");
	                    found[0] = true;
	                    if(!deactivate_ciphers1 && ! deactivate_ciphers2) {
	                    	break;
	                    } else {
	                    	if(found[1] && found[2]) {
	                    		break;	
	                    	} else {
	                    		continue;
            				}
	                    }
	                }
            	// Replacing line with login-data-txt-file
            		if(deactivate_ciphers1
        				&& fileContent.get(i).equals("data-ciphers AES-256-GCM:AES-256-CBC:AES-192-GCM:AES-192-CBC:AES-128-GCM:AES-128-CBC")) {
		            	if(consoleOut) {
		            		logger.info("Found line with cipher info 'data-ciphers AES-256-GCM'. Rewriting");
		            	}
            			fileContent.set(i,"#"+fileContent.get(i));
            			found[1] = true;
            			if(!replaceLoginString && deactivate_ciphers2 && found[2]) {
            				break;
            			} else {
	                    	if(found[0] && found[2]) {
	                    		break;	
	                    	} else {
	                    		continue;
	                    		// Do not just jump to next if filter. This position was already filtered, so program needs to go to next position -> continue
            				}
	                    }
            		}
            		if(deactivate_ciphers2
            				&& fileContent.get(i).equals("data-ciphers-fallback AES-256-CBC")) {
            			if(consoleOut) {
            				logger.info("Found line with cipher info 'data-ciphers-fallback'. Rewriting");
            			}
            			fileContent.set(i,"#"+fileContent.get(i));
            			found[2] = true;
            			if(!replaceLoginString && deactivate_ciphers1 && found[1]) {
            				break;
            			} else {
            				if(found[0] && found[1]) {
            					break;	
            				} else {
            					continue;
            					// Do not just jump to next if filter. This position was already filtered, so program needs to go to next position -> continue
            				}
	                    }
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
        	if(consoleOut) {
				logger.error("createNewOVPNFile:");
				logger.error("newconfig: '"+newConfig+"', file exists? "+new File(newConfig).exists());
        		logger.error("openvpnConfigFile: '" +openvpnConfigFile+ "', file exists? "+ new File(openvpnConfigFile).exists());
        		logger.error(e.getMessage());
        	}
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
    	logger.info("\t[--path]     	specific memory file with config-file-names. Default = '"
    			+ this.pathToConfigFiles + "'");
    	logger.info("\t[--memory-file]     	specific directory of config file with names. Default = '"
    			+ this.fileNameWithAllConfigs + "'");
    	logger.info("\t[--config-file]     	openvpn config file. Default = '"
    			+ this.openvpnConfigFile + "'");
    	logger.info("\t[-replace]  	replace login string like 'auth <loginname>' in configfile. Default = '"
    			+ this.replaceLoginString + "'");
    	logger.info("\t[-deactivate-ciphers]  	deactivate line with cipher info like 'data-ciphers AES-256-GCM' in configfile. Default = '"
    			+ this.deactivate_ciphers1 + "'");
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
        String pathToMemoryFile = null;
        String pathToConfigFile = null;
        boolean replaceLoginString = false;
        boolean deactivate_ciphers = false;
        ///////////////////////////////////////////////////////////////////
        // find parameter -v
    	for(int i = 0; i < args.length; i++) {
    		if(args[i].equalsIgnoreCase("-v")) {
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
//        ///////////////////////////////////////////////////////////////////
//        // read all data from input
//        if(args.length == 0) {
//        	main.logger.info("No input found. Exit...");
//            System.exit(0);
//        }
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
            if(args[i].equals("-deactivate-ciphers")) {
            	deactivate_ciphers = true;
            }
            if(args[i].equals("--memory-file") && !Tools.isNextPositionEndOfArray(args, i)) {
            	pathToMemoryFile = args[i + 1];
            }
            if(args[i].equals("--config-file") && !Tools.isNextPositionEndOfArray(args, i)) {
            	pathToConfigFile = args[i + 1];
            }
        }
//        main.run(pathToConfigFiles, replaceLoginString, pathToConfigFile);
        main.runWithInfo(pathToConfigFiles, replaceLoginString, deactivate_ciphers, pathToConfigFile, pathToMemoryFile);
    }
}
