# openvpn_autogenerate_and_reconnect
Regenerate an openvpn file and restart openvpn protocoll with script. Then the connection should be reconnected with new properties.

# Jar File
## Requirements:

Root rights for the jar file

At least one openvpn-config-file in the folder "/etc/openvpn/"

Java version 1.8 or higher

## Program steps (without gui).

1  Checks if the txt file exists in the folder. If yes, continues with step #3. If no, continues with step #2

2  It reads all files located in the folder and writes the file names into the file "/etc/openvpn/...txt"

3  It reads all filenames stored in the txt-file and saves them to a temporary array. Finds a random int number between -1 and count of config-files to choose a random file. Deletes the chosen filename in the array and rewrites the txt-file with the new array, so without the chosen filename.

4  It finds the openvpn-config-file in the folder ... with the chosen file-name.

5  If the user decided to change the line with "auth" info to tell openvpn protocol to read login-info from a file, it writes the properties to the line. If not, it just copies the config-file to the folder /etc/openvpn/. The original openvpn-config-file will stay original.

## Shell Script
The shell script controls protocols and timing of executing programs including a small log file to see connection infos.
