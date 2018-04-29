package utils;

import java.io.File;

public class Utils {

    public static File[] getDirList(String path) {

        File dir = new File(path);
        File[] files = dir.listFiles();

        return files;

    }
    
}
