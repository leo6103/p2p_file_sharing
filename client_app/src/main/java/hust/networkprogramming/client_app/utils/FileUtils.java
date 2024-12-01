package hust.networkprogramming.client_app.utils;

import java.io.File;

public final class FileUtils {
    public static boolean fileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}
