
package main.java.com.openvpn_autogenerate_and_reconnect;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;

public class DAO {

	public String fileNameWithAllConfigs = "/etc/openvpn/fileWithAllConfigs.txt";
    public String openvpnConfigFile = "/etc/openvpn/openvpn.conf";
    public List<String> list_configNames;
//    public List<String> list_filesInFolder;

    public String pathToConfigFiles = "/etc/openvpn/configs";
    public boolean replaceLoginString = false;
    public boolean deactivate_ciphers = false;
    
//    static final Logger logger = Logger.getLogger(RegenerateOVPNConfigFile.class.getName());
    public Logger logger;
    boolean test = false;
    boolean consoleOut = false;
}
