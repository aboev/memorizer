package memorizer.freecoders.com.flashcards.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Alex on 2014-11-22.
 */
public class FileUtils {
    private static String LOG_TAG = "FileUtils";

    public final static Boolean copyFileFromUri(File srcFile, File dstFile) {
        if (srcFile.getAbsolutePath().equals(dstFile.getAbsolutePath())) {
            Log.d(LOG_TAG, "Skipping file copy, src = dest");
            return true;
        }
        try {
            InputStream in = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(dstFile);
            byte[] buf = new byte[1024];
            int len;
            int size = 0;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                size += len;
            }
            in.close();
            out.close();
            Log.d(LOG_TAG, "File copied from " + srcFile.getAbsolutePath() +
                    " to " + dstFile.getAbsolutePath() + " size = " + size);
            return true;
        } catch (java.io.IOException e) {
            Log.d(LOG_TAG, "File access error: " + e.getLocalizedMessage());
            return false;
        }
    }

    public final static String getRealPathFromURI(Context context, Uri contentURI) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}