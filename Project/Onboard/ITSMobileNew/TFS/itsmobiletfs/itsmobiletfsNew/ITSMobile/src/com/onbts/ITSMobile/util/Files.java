package com.onbts.ITSMobile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import com.onbts.ITSMobile.model.UserModel;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Files {

    public static void checkAppDataDirectory(Context context) {
        File dir = new File(getAppDataDirectory(context));

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static String getAppDataDirectory(Context context) {
        String state = Environment.getExternalStorageState();

        String result = Environment.getDataDirectory().getAbsolutePath() + Environment.getDataDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/db";
        /*if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media

            result = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + *//*"com.onbts.issutrax"*//*context.getPackageName();
            Log.e("getAppDataDirectory", result);
            new File(result).mkdirs();
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            result = "";
        }*/
        return result;
    }

    public static byte[] readFile(String filePatch) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(filePatch, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    public static byte[] readFileLimit(File filePatch, long limit) throws IOException {
        // Open file
        byte[] data = null;
        String name = filePatch.getName();
        int index = name.lastIndexOf(".") + 1;
        String ext = index < name.length() ? name.substring(index) : "";
        if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("")) {
            Bitmap bitmap = decodeScaledBitmapFromFile(filePatch, 1000, 1000);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
            data = outputStream.toByteArray();
            bitmap.recycle();
        } else {
            RandomAccessFile f = new RandomAccessFile(filePatch, "r");
            try {
                // Get and check length
                long longlength = f.length();
                int length = (int) longlength;
                if (length != longlength)
                    throw new IOException("File size >= 2 GB");
                // Read file and return data
                data = new byte[length];
                f.readFully(data);
            } finally {
                f.close();
            }
        }
        return data;
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap decodeScaledBitmapFromFile(File file,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int sizeIm = (width >= height) ? height : width;
        int maxScreenSize = reqHeight;
        float scaleD = (float) maxScreenSize / sizeIm;
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * scaleD),
                (int) (height * scaleD), true);
        return scaledBitmap;
    }

    public static void writeFile(File file, byte[] bytes) throws IOException {
        // Open file

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bytes);
            bos.flush();
            bos.close();
        } finally {

        }
    }

    public static void writeFile(String filePath, byte[] bytes) throws IOException {
        // Open file

        File theFile = new File(filePath);
        writeFile(theFile, bytes);
    }


    public static boolean checkFiles(Uri uri) {
        boolean check = false;
        String fileName = uri.getLastPathSegment();
        fileName = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        if (fileName.equals(".doc") || fileName.equals(".docx") || fileName.equals(".pdf")) {
            check = true;
        }
        return check;
    }

    public static boolean checkImage(String filePath) {
        boolean check = false;
        filePath = filePath.substring(filePath.lastIndexOf("."), filePath.length());
        if (filePath.equals(".jpg") || filePath.equals(".jpeg") || filePath.equals(".png") || filePath.equals(".gif")) {
            check = true;
        }
        return check;
    }

    public static boolean chechFileSize(UserModel user, String filePath) {
        boolean check = false;
        File file = new File(filePath);
        long length = file.length();
        if (user.getAttachFileLength() > length)
            check = true;
        return check;
    }

}
