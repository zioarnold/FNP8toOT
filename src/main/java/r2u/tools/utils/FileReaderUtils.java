package r2u.tools.utils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import r2u.tools.config.Configurator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileReaderUtils {
    private static final Configurator instance = Configurator.getInstance();
    private static final Logger logger = Logger.getLogger(FileReaderUtils.class.getName());

    private static List<String> returnFilesListedInCSV() {
        List<String> pathList = null;
        try {
            pathList = FileUtils.readLines(new File(instance.getCsv()), "UTF-8");
        } catch (IOException e) {
            logger.error("Unable to read the: " + instance.getCsv() + " somehow. Aborting!!!!", e);
            System.exit(-1);
        }
        return pathList;
    }

    public static List<String> returnOldFileNamesFromOriginPath() {
        ArrayList<String> oldFileNames = new ArrayList<>();
        for (int l = 1; l < returnFilesListedInCSV().size(); l++) {
            String[] originPathNewName = returnFilesListedInCSV().get(l).split(";");
            String originPath = originPathNewName[0];
            String[] originPathSplited = originPath.split("/");
            String oldFileName = originPathSplited[originPathSplited.length - 1];
            oldFileNames.add(oldFileName);
        }
        return oldFileNames;
    }

    public static List<String> returnNewFileNamesFromCSV() {
        ArrayList<String> newFileNames = new ArrayList<>();
        for (int l = 1; l < returnFilesListedInCSV().size(); l++) {
            String[] originPathNewName = returnFilesListedInCSV().get(l).split(";");
            String newName = originPathNewName[1];
            newFileNames.add(newName);
        }
        return newFileNames;
    }
}
