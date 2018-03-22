package test.qun.com.weishi.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.R;
import test.qun.com.weishi.bean.ProcessBean;

/**
 * Created by Administrator on 2018/3/22 0022.
 */

public class ProcessEngine {
    public int obtainProcessCount(Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2,获取正在运行进程的集合
//        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3,返回集合的总数
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        return processes.size();
    }

    public long obtainAvailableMemory(Context context) {
        //1,获取activityManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2,构建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //3,给memoryInfo对象赋(可用内存)值
        am.getMemoryInfo(memoryInfo);
        //4,获取memoryInfo中相应可用内存大小
        return memoryInfo.availMem;
    }

    public long obtainTotalMemory(Context context) {
        //1,获取activityManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2,构建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //3,给memoryInfo对象赋(可用内存)值
        am.getMemoryInfo(memoryInfo);
        //4,获取memoryInfo中相应可用内存大小
        return memoryInfo.totalMem;
    }

    public List<ProcessBean> obtainProcessInfo(Context context) {
        List<ProcessBean> processInfoList = new ArrayList<>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        //2,获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            ProcessBean bean = new ProcessBean();
            bean.setPackageName(runningAppProcessInfo.processName);
            Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            android.os.Debug.MemoryInfo memoryInfo = memoryInfos[0];
            bean.memSize = memoryInfo.getTotalPrivateDirty() * 1024;

            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(bean.packageName, 0);
                //8,获取应用的名称
                bean.name = applicationInfo.loadLabel(pm).toString();
                //9,获取应用的图标
                bean.icon = applicationInfo.loadIcon(pm);
                //10,判断是否为系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    bean.isSystem = true;
                } else {
                    bean.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //需要处理
                bean.name = runningAppProcessInfo.processName;
                bean.icon = context.getResources().getDrawable(R.drawable.ic_launcher);
                bean.isSystem = true;
                e.printStackTrace();
            }
            processInfoList.add(bean);
        }
        return processInfoList;
    }
}
