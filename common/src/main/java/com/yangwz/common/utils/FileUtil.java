package com.yangwz.common.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by allison on 16/10/21.
 * 文件基本信息
 */
public class FileUtil {

    private final static String LOCAL_PRODUCT_PATH = "/xunai/";
    private final static String LOCAL_PRODUCT_DOWNLOAD_PATH = "/xunai/download/";
    public final static String LOCAL_DRAFT_CASE_PATH = "/xunai/draft/case_image/";
    public final static String LOCAL_DRAFT_CUSTOMIZE_PATH = "/xunai/draft/customize_image/";
    public final static String LOCAL_DRAFT_ANSWER_PATH = "/xunai/draft/answer_image/";
    public final static String LOCAL_DRAFT_QUESTION_PATH = "/xunai/draft/question_image/";
    public final static String LOCAL_DRAFT_ACTIVITY_PATH = "/xunai/draft/activity_image/";
    public final static String LOCAL_DRAFT_SUGGESTION_PATH = "/xunai/draft/suggestion_image/";

    /**
     * 获得当前应用的目录
     *
     * @return SDCard存在则返回当前应用的路径，否则返回null
     */
    public static String getLocalProductPath() {
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!isSDPresent) {
            Log.e("getLocalProductPath", "SDCard disappered!");
            return null;
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + LOCAL_PRODUCT_PATH;
        File dir = new File(path);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        // 屏蔽MediaScanning展示
//		File file = new File(path + ".nomedia");
        File file = new File(path);
        if (!file.exists()) {
            FileWriter f = null;
            try {
                f = new FileWriter(file.getAbsolutePath());
                f.close();
            } catch (IOException e) {
                Log.e("getLocalProductPath", "IO Exception in file: " + path + ".nomedia");
                return path;
            } finally {
                if (f != null) {
                    try {
                        f.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return path;
    }

    public static String getLocalProductDownloadPath() {
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!isSDPresent) {
            Log.e("getLocalProductPath", "SDCard disappered!");
            return null;
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + LOCAL_PRODUCT_DOWNLOAD_PATH;
        File dir = new File(path);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        // 屏蔽MediaScanning展示
//		File file = new File(path + ".nomedia");
        File file = new File(path);
        if (!file.exists()) {
            try {
                FileWriter f = new FileWriter(file.getAbsolutePath());
                f.close();
            } catch (IOException e) {
                Log.e("getLocalProductPath", "IO Exception in file: " + path + ".nomedia");
            }
        }

        return path;
    }

    public static String getFileNameFromUrl(String path) {
        if (!TextUtils.isEmpty(path)) {
            return path.substring(path.lastIndexOf("/") + 1, path.length());
        }
        return "";
    }


    public static boolean writeFile(String filePath, InputStream inputStream) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        FileOutputStream out = null;
        try {
            makeDirs(filePath);
            out = new FileOutputStream(filePath);
            int c;

            while ((c = inputStream.read()) != -1) {
                out.write(c);
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            try {
                inputStream.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    public static String getFolderName(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }


    public static boolean existsFile(String httpUrl) {
        ///storage/emulated/0/qiji/download/u=1276606806,2213161721&fm=26&gp=0.jpg
        String filePath = getLocalProductDownloadPath() + "/" + getFileNameFromUrl(httpUrl);
        return new File(filePath).exists();
    }

    /**
     * 读取文件夹大小
     *
     * @param file
     * @return
     */
    public static double getDirSize(File file) {
        //判断文件是否存在
        if (file != null && file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“Kb”为单位
                double size = (double) file.length() / 1024;
                return size;
            }
        } else {
            return 0.0;
        }
    }


    /**
     * 删除文件或文件夹
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return true;
        }
        return deleteFile(new File(path));
    }

    /**
     * 删除文件或文件夹
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file) {
        if (file == null) {
            return true;
        }
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    /**
     * 删除发布案例保存的草稿图片文件
     *
     * @return 是否成功删除
     */
    public static boolean deleteDraftCaseImage(Context context) {
        File file = new File(getFileDirPath(LOCAL_DRAFT_CASE_PATH));
        boolean isDelete = file.exists() && deleteDir(file);
        if (isDelete) {
            updateFileFromDatabase(context, file);
        }
        return isDelete;
    }

    /**
     * 删除发布文章保存的草稿图片文件
     *
     * @return 是否成功删除
     */
    public static boolean deleteDraftCustomizeImage(Context context) {
        File file = new File(getFileDirPath(LOCAL_DRAFT_CUSTOMIZE_PATH));
        boolean isDelete = file.exists() && deleteDir(file);
        if (isDelete) {
            updateFileFromDatabase(context, file);
        }
        return isDelete;
    }

    /**
     * 删除发布回答保存的草稿图片文件
     *
     * @return 是否成功删除
     */
    public static boolean deleteDraftAnswerImage(Context context) {
        File file = new File(getFileDirPath(LOCAL_DRAFT_ANSWER_PATH));
        boolean isDelete = file.exists() && deleteDir(file);
        if (isDelete) {
            updateFileFromDatabase(context, file);
        }
        return isDelete;
    }

    /**
     * 删除发布问题保存的草稿图片文件
     *
     * @return 是否成功删除
     */
    public static boolean deleteDraftQuestionImage(Context context) {
        File file = new File(getFileDirPath(LOCAL_DRAFT_QUESTION_PATH));
        boolean isDelete = file.exists() && deleteDir(file);
        if (isDelete) {
            updateFileFromDatabase(context, file);
        }
        return isDelete;
    }

    /**
     * 删除活动发图临时保存文件夹
     *
     * @return 是否成功删除
     */
    public static boolean deleteDraftActivityImage(Context context) {
        File file = new File(getFileDirPath(LOCAL_DRAFT_ACTIVITY_PATH));
        boolean isDelete = file.exists() && deleteDir(file);
        if (isDelete) {
            updateFileFromDatabase(context, file);
        }
        return isDelete;
    }

    /**
     * 删除意见反馈发图临时保存文件夹
     *
     * @return 是否成功删除
     */
    public static boolean deleteDraftSuggestionImage(Context context) {
        File file = new File(getFileDirPath(LOCAL_DRAFT_SUGGESTION_PATH));
        boolean isDelete = file.exists() && deleteDir(file);
        if (isDelete) {
            updateFileFromDatabase(context, file);
        }
        return isDelete;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     *
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     */
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children == null) return false;
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static String getFileDirPath(String fileDir) {
        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!isSDPresent) {
            Log.e("getLocalProductPath", "SDCard disappered!");
            return null;
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + fileDir;
        File dir = new File(path);
        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        // 屏蔽MediaScanning展示
//		File file = new File(path + ".nomedia");
        File file = new File(path);
        if (!file.exists()) {
            FileWriter f = null;
            try {
                f = new FileWriter(file.getAbsolutePath());
                f.close();
            } catch (IOException e) {
                Log.e("getLocalProductPath", "IO Exception in file: " + path + ".nomedia");
                return path;
            } finally {
                if (f != null) {
                    try {
                        f.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return path;
    }

    private static void updateFileFromDatabase(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
            MediaScannerConnection.scanFile(context, paths, null, null);
            MediaScannerConnection.scanFile(context, new String[]{
                            file.getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }
}