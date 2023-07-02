
package main.java.com.openvpn_autogenerate_and_reconnect;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;

public class DAO {

	public String fileNameWithAllConfigs = "/etc/openvpn/fileWithAllConfigs.txt";
    public String newConfig = "/etc/openvpn/openvpn.conf";
    public List<String> list_configName;
    public List<File> list_filesInFolder;

    public String pathToConfigs;
    public boolean replaceLoginString = false;
    
//    static final Logger logger = Logger.getLogger(RegenerateOVPNConfigFile.class.getName());
    public Logger logger;
}
