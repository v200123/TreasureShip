package com.jzz.treasureship.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author user
 *
 */
public class CrashHandler4SaveLog2File implements UncaughtExceptionHandler {
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // CrashHandler实例
    private static CrashHandler4SaveLog2File INSTANCE = new CrashHandler4SaveLog2File();
    // 程序的Context对象
    private Context mContext;
    // 手机IMEI号
    private String mImeiNum = "";
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat(
            CrashLogConfig.DATE_FORMAT);
    private int maxLogFileCount = CrashLogConfig.MAX_LOG_FILE_COUNT;

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler4SaveLog2File() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler4SaveLog2File getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        // 检测异常记录配置文件catch.config是否存在，若存在则取配置文件中参数，若不存在则取默认参数
        try {
            getCatchConfig();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mImeiNum = getIMEI(context);
    }

    private static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "";
        try {
            //imei = telephonyManager.getDeviceId();
            imei = "sdsssss";
        } catch (Exception e) {
            // TODO: handle exception
        } catch (Error e) {
            // TODO: handle exception
        }
        return imei;
    }

    private void getCatchConfig() throws IOException, JSONException {
        String configPath = Environment.getExternalStorageDirectory()
                + CrashLogConfig.CONFIG_FILE_NAME;
        File configFile = new File(configPath);
        if (configFile.exists()) {
            String jasonRead = readLocalFile(configPath);
            JSONObject addJasonRead = new JSONObject(jasonRead);
            maxLogFileCount = Integer.parseInt(addJasonRead
                    .getString("MaxLogFileCount"));
        }
    }

    private String readLocalFile(String filePath) throws IOException {
        File file = new File(filePath);
        InputStreamReader isr = new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String str = "";
        String mimeTypeLine = null;
        while ((mimeTypeLine = br.readLine()) != null) {
            str = str + mimeTypeLine;
        }
        br.close();
        return str;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(CrashLogConfig.SLEEP_TIME);
            } catch (InterruptedException e) {
                Log.e(CrashLogConfig.TAG, CrashLogConfig.THREAD_SLEEP_ERR,
                        e);
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast来显示异常信息
        // new Thread() {
        // @Override
        // public void run() {
        // Looper.prepare();
        // Toast.makeText(mContext, CrashLogConfig.ERR_INFOR,
        // Toast.LENGTH_SHORT).show();
        // Looper.loop();
        // }
        // }.start();
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 检查日志文件总数是否超限
        //checkLogFileCount();
        // 保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? CrashLogConfig.NULL_TAG
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put(CrashLogConfig.VERSION_NAME, versionName);
                infos.put(CrashLogConfig.VERSION_CODE, versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(CrashLogConfig.TAG, CrashLogConfig.COLLECT_ERR, e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(CrashLogConfig.TAG,
                        field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(CrashLogConfig.TAG, CrashLogConfig.COLLECT_ERR, e);
            }
        }
    }

    class TxtFileFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            // TODO Auto-generated method stub
            return pathname.getName().endsWith(".txt");
        }

    }

    private void checkLogFileCount() {
        String logItemPath = Environment.getExternalStorageDirectory()
                + CrashLogConfig.FILE_PATH_SUFFIX;
        File logFileItem = new File(logItemPath);
        if (logFileItem.exists() == false) {
            logFileItem.mkdirs();
        }
        File[] logFileList = logFileItem.listFiles(new TxtFileFilter());
        if (logFileList.length >= maxLogFileCount) {
            // 根据文件最后更新时间进行冒泡排序
            for (int i = 0; i < logFileList.length; i++) {
                for (int j = i + 1; j < logFileList.length; j++) {
                    if (logFileList[i].lastModified() > logFileList[j]
                            .lastModified()) {
                        File temFile = logFileList[i];
                        logFileList[i] = logFileList[j];
                        logFileList[j] = temFile;
                    }
                }
            }

            // 删除最后更新时间最早的log文件，为新文件释放存储空间
            int index = 0;
            int listLen = logFileList.length;
            while (listLen >= maxLogFileCount) {
                logFileList[index++].delete();
                listLen--;
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        sb.append(CrashLogConfig.BLOCK_TAG);
        sb.append(CrashLogConfig.DEVICE_INFO);
        sb.append(CrashLogConfig.BLOCK_TAG);
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(CrashLogConfig.BLOCK_TAG);
        sb.append(CrashLogConfig.EXCEPTION_INFO);
        sb.append(CrashLogConfig.BLOCK_TAG);
        sb.append(result);
        sb.append(CrashLogConfig.BLOCK_TAG);
        sb.append(CrashLogConfig.END_TAG);
        sb.append(CrashLogConfig.BLOCK_TAG);
        Log.e("MDMErr", "errInfo is \n" + sb.toString());
        try {
            String timestamp = Long.toString(System.currentTimeMillis());
            timestamp = timestamp.substring(timestamp.length() - 6,
                    timestamp.length() - 1);
            String time = formatter.format(new Date());
            String fileName = CrashLogConfig.IMEI_NUM + mImeiNum
                    + CrashLogConfig.FILE_NAME_PREFIX + time + "-"
                    + timestamp + CrashLogConfig.FILE_NAME_SUFFIX;
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory()
                        + CrashLogConfig.FILE_PATH_SUFFIX;
                File dir = new File(path);
                if (!dir.exists()) {
                    boolean ret = dir.mkdir();
                    if(!ret){
                        dir.mkdirs();
                    }
                }
                File file = new File(path, fileName);
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(CrashLogConfig.TAG, CrashLogConfig.WRITE_ERR, e);
        }
        return null;
    }

}