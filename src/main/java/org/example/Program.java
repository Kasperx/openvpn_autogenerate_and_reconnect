
package main.java.org.example;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Program extends Tools {
    public Program(){}
    private void showErrorAndExit(String text) {
    	if(consoleOut) {
			if(StringUtils.isNotEmpty(text)) {
				logger.error("Error: " + text);
				logger.error("Exit");
			} else {
				logger.error("Exit");
			}
    	}
    	System.exit(1);
    }
    private void showError(String text) {
    	if(consoleOut) {
			if(StringUtils.isNotEmpty(text)) {
				logger.error("Error: " + text);
				logger.error("Exit");
			} else {
				logger.error("Exit");
			}
    	}
    	System.exit(1);
    }
    private void showInfo(String pathToConfigFiles, boolean replaceLoginString, String pathToConfigFile) {
		logger.info("###");
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
    	return value ? "Yes" : "No";
    }
    void run(String pathToConfigFiles, boolean replaceLoginString, String pathToConfigFile) {

    	//main = this;
    	if(consoleOut) {
    		showInfo(pathToConfigFiles, replaceLoginString, pathToConfigFile);
    	}
    	/* check input params */
    	/**************************/
		List<ConfigfileStatus> listFileStatus = new ArrayList<ConfigfileStatus>(Collections.nCopies(10, null));
		listFileStatus.set(0, checkDirectoryWithConfigFiles(pathToConfigFile));
		listFileStatus.set(1, checkDirectoryWithConfigFiles(pathToConfigFiles));
		for(ConfigfileStatus configfileStatus: listFileStatus) {
			if(! configfileStatus.valid()) {
				int index = listFileStatus.indexOf(configfileStatus);
				showErrorAndExit(null);
			}
		}
		this.replaceLoginString = replaceLoginString;
		if (consoleOut) {
			if(this.replaceLoginString) {
				logger.info("(Replacing login string in config)");
			} else {
				logger.info("(NOT replacing login string in config)");
			}
		}
		run();
	}
    public void runWithInfo(String pathToConfigFiles, boolean replaceLoginString, boolean deactivate_ciphers, String pathToConfigFile, String pathToMemoryFile) {

		logger = LogManager.getLogger(this.getClass());
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
		List<ConfigfileStatus> listFileStatus = new ArrayList<ConfigfileStatus>(Collections.nCopies(count, null));
    	///////////////////////////////////////////////////////
    	/* check directory with config files*/
		listFileStatus.set(pos_pathToConfigFiles, checkDirectoryWithConfigFiles(pathToConfigFiles));
		switch (listFileStatus.get(pos_pathToConfigFiles)) {
			case EXISTING:
    			value[pos_pathToConfigFiles] = ConfigfileStatus.EXISTING.toString();
    			break;
			case EXISTING_BUT_NOT_A_DIR:
    			value[pos_pathToConfigFiles] = ConfigfileStatus.EXISTING_BUT_NOT_A_DIR.toString();
    			break;
			case NOT_EXISTING:
				value[pos_pathToConfigFiles] = ConfigfileStatus.NOT_EXISTING.toString();
				break;
			case EXCEPTION:
				value[pos_pathToConfigFiles] = ConfigfileStatus.EXCEPTION.toString();
				break;
			case EXISTING_BUT_NO_FILES_CONTAINING:
				value[pos_pathToConfigFiles] = ConfigfileStatus.EXISTING_BUT_NO_FILES_CONTAINING.toString();
				break;
			default:
				break;
		}
    	///////////////////////////////////////////////////////
    	/* check memory file */
		listFileStatus.set(pos_pathToMemoryFile, checkDirectoryWithConfigFiles(pathToMemoryFile));
    	switch (listFileStatus.get(pos_pathToMemoryFile)) {
			case EXISTING_AND_WRITABLE:
    			value[pos_pathToMemoryFile] = ConfigfileStatus.EXISTING_AND_WRITABLE.toString();
    			break;
			case NOT_EXISTING_AND_WITHOUT_PERMISSION:
    			value[pos_pathToMemoryFile] = ConfigfileStatus.NOT_EXISTING_AND_WITHOUT_PERMISSION.toString();
    			break;
			case SECURITY_PROBLEMS:
    			value[pos_pathToMemoryFile] = ConfigfileStatus.SECURITY_PROBLEMS.toString();
    			break;
			case EXCEPTION:
    			value[pos_pathToMemoryFile] = ConfigfileStatus.EXCEPTION.toString();
    			break;
			case NOT_EXISTING:
    			value[pos_pathToMemoryFile] = ConfigfileStatus.NOT_EXISTING.toString();
    			break;
    	}
    	///////////////////////////////////////////////////////
    	/* check config file */
		listFileStatus.set(pos_pathToConfigFile, checkDirectoryWithConfigFiles(pathToConfigFile));
    	switch (listFileStatus.get(pos_pathToConfigFile)) {
			case EXISTING:
    			value[pos_pathToConfigFile] = ConfigfileStatus.EXISTING.toString();
    			break;
			case NOT_EXISTING_AND_WITHOUT_PERMISSION:
    			value[pos_pathToConfigFile] = ConfigfileStatus.NOT_EXISTING_AND_WITHOUT_PERMISSION.toString();
    			break;
			case SECURITY_PROBLEMS:
    			value[pos_pathToConfigFile] = ConfigfileStatus.SECURITY_PROBLEMS.toString();
    			break;
			case EXCEPTION:
    			value[pos_pathToConfigFile] = ConfigfileStatus.EXCEPTION.toString();
	    		break;
			case NOT_EXISTING:
    			value[pos_pathToConfigFile] = ConfigfileStatus.NOT_EXISTING.toString();
	    		break;
    	}
    	///////////////////////////////////////////////////////
    	/* go on */
    	this.replaceLoginString = replaceLoginString;
    	this.deactivate_ciphers1 = deactivate_ciphers;
    	this.deactivate_ciphers2 = deactivate_ciphers;
    	this.deactivate_ciphers3 = deactivate_ciphers;
    	value[pos_replaceLoginString] = toString(replaceLoginString);
    	value[pos_test] = toString(test);
    	value[pos_deactivate_ciphers] = toString(deactivate_ciphers);
    	///////////////////////////////////////////////////////
		info[pos_pathToConfigFiles] = "Folder containing config files for vpn connection.";
		/* get last mod time of file */
		info[pos_pathToConfigFile] = getLastModTime(ConfigFile.CONFIGFILE, pathToConfigFile);
		info[pos_pathToMemoryFile] = getLastModTime(ConfigFile.MEMORRYFILE,pathToMemoryFile);
		info[pos_test] = "Run program and read info, but do not modify anything.";
    	info[pos_replaceLoginString] = "Input user login info to config file.";
    	info[pos_deactivate_ciphers] = "Deactivate cipher info in config file like 'data-ciphers AES-256-GCM'.";
    	///////////////////////////////////////////////////////
    	if(consoleOut) {
	    	Table table = Table.create("").addColumns(
							StringColumn.create("Parameter", parameter),
							StringColumn.create("Value", value),
							StringColumn.create("Discription", info)
			);
	    	logger.info("");
			logger.info(table.print());
			logger.info("");
		}
		boolean foundError = false;
		for(ConfigfileStatus configfileStatus: listFileStatus) {
			if(! configfileStatus.valid()) {
				foundError = true;
				int index = listFileStatus.indexOf(configfileStatus);
				if(consoleOut) {
					showError(parameter[index] + ": " + value[index]);
				}
			}
		}
		if(foundError){
			System.exit(1);
		}
    	run();
    }
    private int countFiles(String directory) {
    	return getConfigFilesFromFolder(directory).size();
    }
	private ConfigfileStatus checkDirectoryWithConfigFiles(String pathToConfigFiles) {
		File file = new File(pathToConfigFiles);
		try {
			if(file.exists()) {
				if(file.isDirectory()) {
//		        	logger.info("Found directory: '" + file.getAbsolutePath() + "'.");
					if(hasDirFiles(file.getAbsolutePath())) {
						return ConfigfileStatus.EXISTING;
					} else {
						return ConfigfileStatus.EXISTING_BUT_NO_FILES_CONTAINING;
					}
				} else {
					return ConfigfileStatus.EXISTING_BUT_NOT_A_DIR;
				}
			} else {
				return ConfigfileStatus.NOT_EXISTING;
			}
		} catch (Exception e) {
			if(consoleOut) {
				logger.error(e.getMessage());
			}
			logger.error(e);
			return ConfigfileStatus.EXCEPTION;
		}
	}
	private boolean hasDirFiles(String dirPath) {
		return countFiles(dirPath) > 0;
	}
    private void run()
    {
		if(consoleOut) {
			logger.info("Start. Make sure to have at least one ovpn-file in directory '" + pathToConfigFiles + "'");
		}
    	list_configNames = getFilenameFromConfigfile(fileNameWithAllConfigs);
    	List<String> list_filesInFolder = getConfigFilesFromFolder(pathToConfigFiles);
    	/*
    	 * if info file is empty (so all files were used already) -> read config files from folder and save to file 
    	 */
    	list_configNames = getConfigFileFromMemoryFile(list_configNames, list_filesInFolder);
        String newConfigName = getRandomFileAndRewriteToMemoryFile(fileNameWithAllConfigs, list_configNames, list_filesInFolder);
        if(!test) {
        	createNewOVPNFile(newConfigName);
        }
    }
    private List<String> getConfigFileFromMemoryFile(List<String> list_configNamesFromMemoryFile, List<String> list_filesInFolder) {
    	// if "fileNameWithAllConfigs" still exists and has only one name, delete it. Needed config file is already loaded
        if (list_configNamesFromMemoryFile.size() == 1) {
        	// get last name from memoryfile
        } else if(list_configNamesFromMemoryFile.size() == 0) {
        	// read files from folder, write to memoryfile
        	list_configNamesFromMemoryFile = list_filesInFolder;
        } else /* if(list_configName.size() > 1) */{
        	;
        }
        return list_configNamesFromMemoryFile;
    }
    private String getLastModTime(ConfigFile configFile, String filename) {
		// Reading config file, saving names to list
		File file = null;
		if(filename == null) {
			return null;
		} else if ((file = new File(filename)).exists()) {
			long lastModified_ = file.lastModified();
			long now_ = Calendar.getInstance().getTimeInMillis();
			long diffInSecs = (now_ - lastModified_) / 1000;
			int hours = (int) (diffInSecs / 3600);
			if (hours > 59) {
				if(configFile == ConfigFile.CONFIGFILE){
					return ConfigFile.CONFIGFILE.value + "(Last modification: " + (float) hours / 24 + " days ago)";
				} else {
					return ConfigFile.MEMORRYFILE.value + "(Last modification: " + (float) hours / 24 + " days ago)";
				}
			} else {
				if(configFile == ConfigFile.CONFIGFILE){
					return ConfigFile.CONFIGFILE.value + "(Last modification: " + hours + " hours ago)";
				} else {
					return ConfigFile.MEMORRYFILE.value + "(Last modification: " + hours + " hours ago)";
				}
			}
		} else {
			if(configFile == ConfigFile.CONFIGFILE){
				return ConfigFile.CONFIGFILE.value;
			} else {
				return ConfigFile.MEMORRYFILE.value;
			}
		}
    }
    private List<String> getConfigFilesFromFolder(String pathToConfigFiles) {
        try {
        	List<File> list_filesInFolder = new ArrayList<>();
            list_filesInFolder =
            		Files
                    	.walk(Paths.get(pathToConfigFiles))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
            if(list_filesInFolder.size() > 0) {
	            // store fileNameWithAllConfigss to info file
                List<String> list_configName = new ArrayList<>();
	            for(File configName : list_filesInFolder)
	            {
	                list_configName.add(configName.getAbsolutePath() /*+"\n"*/ ) ;
	            }
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
			logger.error(e);
            return new ArrayList<String>();
        }
    }
    private String getRandomFileAndRewriteToMemoryFile(String fileNameWithAllConfigs, List<String> list_configName, List<String> list_filesFromFolder)
    {
        String newConfigName = "";

        try
        {
            if (list_configName != null && list_configName.size() > 1) {
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
            } else if (list_configName.size() == 1) {
            	newConfigName = list_configName.get(0);
            	Tools.writeEmptyFile(fileNameWithAllConfigs);
            	Tools.writeFile(fileNameWithAllConfigs, list_filesFromFolder);
            }
        } catch(Exception e) {
        	if(consoleOut) {
				logger.error("list_configName == null? "+list_configName == null);
				logger.error("list_configName count of elements: "+list_configName.size());
        		logger.error(e.getMessage());
        	}
			logger.error(e);
            if(list_configName == null || list_configName.size() <= 0) {
            	return null;
            } else {
            	return list_configName.get(0);
            }
        }
        return newConfigName;
    }
    void checkFileContent(String newConfig) {
    	try {
			List<String> fileContent = new ArrayList<>(
					Files.readAllLines(
							Paths.get(newConfig),
							StandardCharsets.UTF_8));
			if(consoleOut) {
				logger.info("##############################");
				logger.info("##############################");
				logger.info("##############################");
			}
			// Replacing line with login-data-txt-file
			// ... Some config files don't need the login info, it's already stored in key, so no need to replace ...
			for(int i = 0; i < fileContent.size(); i++) {
				if(consoleOut) {
					logger.info(fileContent.get(i));
				}
			}
			if(consoleOut) {
				logger.info("##############################");
				logger.info("##############################");
				logger.info("##############################");
			}
		} catch (IOException e) {
			logger.error(e);
		}
}
    
    private void createNewOVPNFile(String newConfig)
    {
        try
        {
            // write new config file
            Files.copy(
            		Paths.get(newConfig),
            		Paths.get(openvpnConfigFile),
            		StandardCopyOption.REPLACE_EXISTING);

            // Some config files dont need the login info, its already stored in key, so no need to replace
            if(replaceLoginString || deactivate_ciphers1 || deactivate_ciphers2) {
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
	                    continue;
	                }
            		if(deactivate_ciphers1
        				&& fileContent.get(i).equals("data-ciphers AES-256-GCM:AES-256-CBC:AES-192-GCM:AES-192-CBC:AES-128-GCM:AES-128-CBC")) {
		            	if(consoleOut) {
		            		logger.info("Found line with cipher info 'data-ciphers AES-256-GCM ***'. Rewriting");
		            	}
            			fileContent.set(i,"#"+fileContent.get(i));
            			continue;
            		}
            		if(deactivate_ciphers2
            				&& fileContent.get(i).equals("data-ciphers-fallback AES-256-CBC")) {
            			if(consoleOut) {
            				logger.info("Found line with cipher info 'data-ciphers-fallback ***'. Rewriting");
            			}
            			fileContent.set(i,"#"+fileContent.get(i));
            			continue;
            		}
            		if(deactivate_ciphers3
            				&& fileContent.get(i).startsWith("data-ciphers")) {
            			if(consoleOut) {
            				logger.info("Found line with cipher info 'data-ciphers ***'. Rewriting");
            			}
            			fileContent.set(i,"#"+fileContent.get(i));
            			continue;
            		}
	            }
            // Write new config file
            logger.info("Writing new config file '" + openvpnConfigFile + "'.");
            Files.write(
            		Paths.get(openvpnConfigFile), // new filename
            		fileContent, // new file content
            		StandardCharsets.UTF_8 // options
            		);
            }
        } catch(Exception e) {
        	if(consoleOut) {
        		logger.error("Error:");
				logger.error("createNewOVPNFile");
				logger.error("newconfig: '"+newConfig+"', file exists? "+new File(newConfig).exists());
				if(!new File(newConfig).exists()) {
					logger.error("If there are new config files in folder -> pls remember to remove or clear file config-file '" + fileNameWithAllConfigs + "'.");
				}
        		logger.error("openvpnConfigFile: '" +openvpnConfigFile+ "', file exists? "+ new File(openvpnConfigFile).exists());
        		logger.error(e.getMessage());
        	}
			logger.error(e);
        }
    }
    private void showHelp ()
    {
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
    
}
