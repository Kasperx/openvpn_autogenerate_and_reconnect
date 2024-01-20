
package main.java.org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Tools extends DAO
{
	static List<String> getFilenameFromConfigfile(String fileNameWithAllConfigs)
    {
    	return loadLinesFromConfigFile(fileNameWithAllConfigs);
    }
    static List<File> convertToFileList(List<String> list) {
    	List<File> files = new ArrayList<File>();
    	for(String name: list) {
    		files.add(new File(name));
    	}
    	return files;
    }
    static List<String> convertToTextList(List<File> list) {
    	List<String> files = new ArrayList<>();
    	for(File name: list) {
    		files.add(name.getAbsolutePath());
    	}
    	return files;
    }
    public static List<String> loadLinesFromConfigFile(String fpath)
    {
        try {
            return new ArrayList<>(
                    Files.readAllLines(
                            Paths.get(fpath),
                            StandardCharsets.UTF_8)
                    );

        } catch(Exception e) {
            Logger logger = LogManager.getLogger(Tools.class.getName());
            logger.error(e);
            return new ArrayList<String>();
        }
    }
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
            getLogger().error(e);
        }
    }
    private static Logger getLogger () {
        return LogManager.getLogger(Tools.class);
    }
    public static void writeEmptyFile(String file) {
    	try {
			new PrintWriter(file).close();
		} catch (FileNotFoundException e) {
			getLogger().error(e);
		}
    }
    public static void writeFile(String file, List<String> content) {
    	String text = "";
    	for(String name: content) {
    		text += name + "\n";
    	}
    	writeFile(file, text);
    }
    public static void writeFile(String file, String content)
    {
        try {
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close();
            fw.close();
        } catch(Exception e) {
            getLogger().error(e);
        }
    }

    public static boolean isNextPositionEndOfArray(String[] args, int position)
    {
        if(args.length - 1 >= position + 1) {
            return false;
        } else {
            return true;
        }
    }
}
