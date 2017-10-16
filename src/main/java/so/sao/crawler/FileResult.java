package so.sao.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 * @author guangpu.yan
 * @create 2017-10-02 23:21
 **/
public class FileResult {
    static String path = "d:/";
    public static void append(String fileName, byte[] contentInBytes) {
        FileOutputStream fop = null;
        File file;

        try {
            file = new File(path+fileName+".txt");
            fop = new FileOutputStream(file, true);

            if (!file.exists()) {
                file.createNewFile();
            }

            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}