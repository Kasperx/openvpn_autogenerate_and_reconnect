# openvpn_autogenerate_and_reconnect
Reconnect openvpn and change remote connection.
## Discription
Regenerate an openvpn file and restart openvpn connection. Then the connection may be reconnected with an other remote.

## System requirements:

Software installed: openvpn, Java (version >= 8)

At least one openvpn-config-file in the folder "/etc/openvpn/configs"

## Optional system requirements for active firewall:

Software installed: iptables, iptables-persistant

## Java program steps (headless).

1  Checks if the txt file exists in the folder. If yes, continues with step #3. If no, continues with step #2

2  It reads all files located in the folder and writes the file names into the file "/etc/openvpn/fileWithAllConfigs.txt"

3  It reads all filenames stored in the txt-file and finds a random file to use. Then stores all others back to "/etc/openvpn/fileWithAllConfigs.txt".

4  It finds the openvpn-config-file in the folder "/etc/openvpn/configs" with the chosen file-name.

5  If the user decided to change the line with "auth" info to tell openvpn to read login-info from a file, it writes the properties to the line. If not, it just copies the config-file to "/etc/openvpn/openvpn.conf". The original openvpn-config-file is handled read-only.
