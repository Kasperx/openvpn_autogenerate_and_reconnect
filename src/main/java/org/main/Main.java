
package main.java.org.main;

import java.io.File;
import org.apache.logging.log4j.LogManager;

public class Main extends Tools
{
	static {
		if(new File(System.getProperty("user.dir")+"/config/log4j.xml").exists()) {
			System.setProperty("log4j.configurationFile", System.getProperty("user.dir")+"/config/log4j.xml");
		}
	}
    private void showHelp ()
    {
    	logger.info("This program can find ovpn-config-files from a specific folder, read a random file from it and regenerate a new ovpn-config-file to the ovpn-working-dir.");
    	logger.info("Syntax: Main [-h|-?|-help] [--path] [--replace] [--memory-file] [--config-file] [-replace] [-deactivate-ciphers]");
    	logger.info("Options:");
    	logger.info("\t[-h|-help|-?]\t\t 	show this help and exit");
    	logger.info("\t[--path]\t\t     	specific memory file with config-file-names. Default = '"
    			+ this.pathToConfigFiles + "'");
    	logger.info("\t[--memory-file]     	specific directory of config file with names. Default = '"
    			+ this.fileNameWithAllConfigs + "'");
    	logger.info("\t[--config-file]     	openvpn config file. Default = '"
    			+ this.openvpnConfigFile + "'");
    	logger.info("\t[-replace]\t\t\t  	replace login string like 'auth <loginname>' in configfile. Default = '"
    			+ this.replaceLoginString + "'");
    	logger.info("\t[-deactivate-ciphers]  	deactivate line with cipher info like 'data-ciphers AES-256-GCM' in configfile. Default = '"
    			+ this.deactivate_ciphers1 + "'");
    	logger.info("Exit");
		System.exit(0);
    }
    public static void main(String[] args)
    {
        // telling java to really run headless, otherwise an exception is thrown
        System.setProperty("java.awt.headless", "true");
        Main main = new Main();
		main.logger = LogManager.getLogger(main.getClass());
        String pathToConfigFiles = null;
        String pathToMemoryFile = null;
        String pathToConfigFile = null;
        boolean replaceLoginString = false;
        boolean deactivate_ciphers = false;
		boolean consoleLog = false;
        ///////////////////////////////////////////////////////////////////
        // find parameter -v
    	for(int i = 0; i < args.length; i++) {
    		if(args[i].equalsIgnoreCase("-v")) {
    			DAO.consoleOut = true;
    		}
    	}
    	///////////////////////////////////////////////////////////////////
    	// find parameter test
    	for(int i = 0; i < args.length; i++) {
    		if(args[i].equalsIgnoreCase("test") || args[i].equalsIgnoreCase("-test")) {
    			DAO.test = true;
    		}
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
		new Program().runWithInfo(pathToConfigFiles, replaceLoginString, deactivate_ciphers, pathToConfigFile, pathToMemoryFile);
    }
}
