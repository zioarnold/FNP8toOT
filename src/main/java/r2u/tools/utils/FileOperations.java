package r2u.tools.utils;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import r2u.tools.config.Configurator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class FileOperations {
    private static final Configurator instance = Configurator.getInstance();
    private static final Logger logger = Logger.getLogger(FileOperations.class.getName());

    public static void renameFiles(List<Object> folderList) {
        List<String> oldFileNames = FileReaderUtils.returnOldFileNamesFromOriginPath();
        List<String> newFileNames = FileReaderUtils.returnNewFileNamesFromCSV();
        Pattern pattern = Pattern.compile(instance.getRegex());

        //Ciclo principale preso da "objectFolder" su Json
        for (Object folder : folderList) {
            logger.info("Working with directory: " + instance.getPathToStore() + folder.toString());
            //Lista dei file presenti su "pathToStore"+folderList e di conseguenza ciclo quei file uno x uno
            File[] listFiles = new File(instance.getPathToStore() + folder).listFiles();
            for (int j = 0; j < Objects.requireNonNull(listFiles).length; j++) {
                logger.info("Working with file: " + listFiles[j].getName() +
                        "\nChecking if it does contains special characters.");
                //Controllo per la presenza dei caratteri strani, indicati nel "regex" del config.json
                //Se trovo - sego quei caratteracci
                if (pattern.matcher(listFiles[j].getName()).find()) {
                    try {
                        logger.info("Special character detected! Removing it!");
                        FileUtils.moveFile(
                                new File(listFiles[j].getAbsolutePath()), //file coi caratteri speciali originali presenti su FS
                                new File(removeSpecialCharacter(listFiles[j].getAbsolutePath(), instance.getRegex())) //file senza caratteri
                        );
                    } catch (IOException e) {
                        logger.error("Unable to rename files, somehow: check in config.json pathToStore: " + instance.getPathToStore() + "\noriginalPath: " + folder, e);
                    }
                }
                //Una volta rimossi i caratteri speciali, procedo con la rinomina.
                //Quindi prendo il file da rinominare ed lo rinomino a secondo del CSV della colonna "new_name"
                //Trovato corrispondenza prima nel origin_path
                for (int k = 0; k < oldFileNames.size(); k++) {
                    if (removeSpecialCharacter(listFiles[j].getName(), instance.getRegex()).equals(removeSpecialCharacter(oldFileNames.get(k), instance.getRegex()))) {
                        logger.info("File is found, trying to rename it!");
                        try {
                            FileUtils.moveFile(
                                    new File(removeSpecialCharacter(listFiles[j].getAbsolutePath(), instance.getRegex())),
                                    new File(instance.getPathToStore() + folder + newFileNames.get(k))
                            );
                            if (Files.exists(Paths.get(instance.getPathToStore() + folder + newFileNames.get(k)))) {
                                logger.info("File " + oldFileNames.get(k) + " successfully renamed to " + newFileNames.get(k) + "\nunder path: " + instance.getPathToStore() + folder);
                            }
                        } catch (IOException e) {
                            logger.error("Unable to rename it somehow. ", e);
                        }
                    }
                }
            }
        }
    }

    private static String removeSpecialCharacter(String string, String regex) {
        return string.replaceAll(regex, "");
    }

    public static void deleteRemnantsFiles(List<Object> folderList) {
        List<String> newFileNames = FileReaderUtils.returnNewFileNamesFromCSV();
        //Ciclo principale preso da "objectFolder" su Json
        for (Object folder : folderList) {
            logger.info("Working with directory: " + instance.getPathToStore() + folder.toString());
            //Lista dei file presenti su "pathToStore"+folderList e di conseguenza ciclo quei file uno x uno
            File[] listFiles = new File(instance.getPathToStore() + folder).listFiles();
            for (int j = 0; j < Objects.requireNonNull(listFiles).length; j++) {
                logger.info("Working with file: " + listFiles[j].getName() + "\nChecking if his existence within CSV.");
                //Ciclo dei file coi fileName nuovi presenti sul csv nella colonna "new_name"
                for (String newFileName : newFileNames) {
                    //Confronto se ho i file presenti su FileSystem con quelli del csv.
                    //Se coincidono - allora tengo i file, altrimenti elimino.
                    if ((instance.getPathToStore() + folder + listFiles[j].getName()).
                            equals(instance.getPathToStore() + folder + newFileName)) {
                        logger.info("File: " + instance.getPathToStore() + folder + listFiles[j].getName() + " if found on CSV. Keeping...");
                        break;
                    } else {
                        try {
                            logger.info("File: " + instance.getPathToStore() + folder + listFiles[j].getName() + " is not found on CSV. Deleting...");
                            Files.delete(listFiles[j].getAbsoluteFile().toPath());
                            logger.info("Deleted: " + instance.getPathToStore() + folder + listFiles[j].getName() + " successfully!");
                        } catch (IOException e) {
                            logger.error("Unable to delete file somehow", e);
                        }
                    }
                }
            }
        }
    }
}
