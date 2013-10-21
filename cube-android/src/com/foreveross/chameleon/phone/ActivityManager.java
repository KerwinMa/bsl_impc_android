package com.foreveross.chameleon.phone;

import java.util.Stack;

import org.apache.cordova.DroidGap;

import android.app.Activity;

public class ActivityManager { 
	private static Stack<DroidGap> webStack  = new Stack<DroidGap>(); 

    private static Stack<Activity> activityStack  = new Stack<Activity>(); ; 
    private static ActivityManager instance; 
    private ActivityManager() { 
    } 
    public static ActivityManager getScreenManager() { 
        if (instance == null) { 
            instance = new ActivityManager(); 
        } 
        return instance; 
    } 
    //退出栈顶Activity 
    public void popActivity(Activity activity) { 
        if (activity != null) { 
           //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作 
            activity.finish(); 
            activityStack.remove(activity); 
            activity = null; 
        } 
    } 
    //获得当前栈顶Activity 
    public Activity currentActivity() { 
        Activity activity = null; 
       if(!activityStack.empty()) 
         activity= activityStack.lastElement(); 
        return activity; 
    } 
    //将当前Activity推入栈中 
    public void pushActivity(Activity activity) { 
  
        activityStack.add(activity); 
    } 
    
  //退出栈中所有Activity 
    public void popAllActivity() { 
        while (true) { 
            Activity activity = currentActivity(); 
            if (activity == null) { 
                break; 
            } 
            popActivity(activity); 
        } 
    } 
    
    //退出栈中所有Activity 
    public void popAllActivityExceptOne(Class cls) { 
        while (true) { 
            Activity activity = currentActivity(); 
            if (activity == null) { 
                break; 
            } 
            if (activity.getClass().equals(cls)) { 
                break; 
            } 
            popActivity(activity); 
        } 
    } 
    
/*************-----------网页的Activity管理--------******/
    
    //将当前Activity推入栈中 
    public void pushWeb(DroidGap gap) { 
  
        webStack.add(gap); 
    } 
    
  //退出栈中所有Activity 
    public void popAllWeb() { 
        while (true) { 
            DroidGap gap = currentWeb(); 
            if (gap == null) { 
                break; 
            } 
            popWeb(gap); 
        } 
    } 
    
    //退出栈顶Activity 
    public void popWeb(DroidGap gap) { 
        if (gap != null) { 
           //在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作 
        	gap.finish(); 
            webStack.remove(gap); 
            gap = null; 
        } 
    } 
    
    
  //获得当前栈顶Activity 
    public DroidGap currentWeb() { 
        DroidGap gap = null; 
       if(!webStack.empty()) 
         gap= webStack.lastElement(); 
        return gap; 
    } 
} 