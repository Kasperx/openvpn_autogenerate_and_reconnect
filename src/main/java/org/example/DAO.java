
package main.java.org.example;

import java.util.List;

import org.apache.logging.log4j.Logger;

public class DAO {

	protected String fileNameWithAllConfigs = "/etc/openvpn/fileWithAllConfigs.txt";
    protected String openvpnConfigFile = "/etc/openvpn/openvpn.ovpn";
    protected List<String> list_configNames;
//    public List<String> list_filesInFolder;

    public String pathToConfigFiles = "/etc/openvpn/configs";
    protected boolean replaceLoginString = false;
    protected boolean deactivate_ciphers1 = false;
    protected boolean deactivate_ciphers2 = false;
    protected boolean deactivate_ciphers3 = false;
    
//    static final Logger logger = Logger.getLogger(RegenerateOVPNConfigFile.class.getName());
    public Logger logger;
    public static boolean test = false;
    public static boolean consoleOut = false;
    protected  enum ConfigfileStatus {
        NOT_EXISTING("Not existing"),
        NOT_EXISTING_AND_WITHOUT_PERMISSION("Not existing and program has no permission to create this file"),
        EXISTING("Existing"),
        EXISTING_AND_WRITABLE("(Right now) Existing, writeable"),
        EXISTING_BUT_NOT_WRITABLE(""),
        EXISTING_BUT_NO_FILES_CONTAINING("Existing, but has no files in it"),
        EXISTING_BUT_NOT_A_DIR("Existing, but not a directory"),
        EXCEPTION("Exception"),
        SECURITY_PROBLEMS("Program has security problems on this file");

        ConfigfileStatus(String configfileStatus) {
            value = configfileStatus;
        }
        public boolean valid(){
            if(this == ConfigfileStatus.EXISTING || this == ConfigfileStatus.EXISTING_AND_WRITABLE){
                return true;
            } else {
                return false;
            }
        }
        String value;

        @Override
        public String toString() {
            return this.value;
        }
    }
    protected enum ConfigFile {
        CONFIGFILE("(Config) File with infos for vpn connection "),
        MEMORRYFILE("File that knows last used config files to avoid using same connection again ");
        String value;
        ConfigFile (String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return this.value;
        }
    }
}
