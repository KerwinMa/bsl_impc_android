package com.foreveross.chameleon.phone.chat.camera;

import android.os.Build;
import android.os.Build.VERSION;
import java.lang.reflect.Field;

public class SdkUtil
{
  private static int SDK_INT = 0;
  static final String TAG = "FileUtils";
  private static final int V2X2 = 8;
  private static final int V2X3 = 9;
  private static final int V4X0 = 14;

  static
  {
    try
    {
      SDK_INT = Build.VERSION.class.getField("SDK_INT").getInt(null);
    
    }
    catch (Exception localException1)
    {
      try
      {
        SDK_INT = Integer.parseInt((String)Build.VERSION.class.getField("SDK").get(null));
       
      }
      catch (Exception localException2)
      {
        SDK_INT = 2;
      }
    }
  }

  public static boolean is2x2()
  {
    return SDK_INT >= 8;
  }

  public static boolean is2x3()
  {
    return SDK_INT >= 9;
  }

  public static boolean is4x0()
  {
    return SDK_INT >= 14;
  }
}

/* Location:           E:\DevTool\jd-gui-0.3.5.windows (1)\classes_dex2jar.jar
 * Qualified Name:     vStudio.Android.Camera360.base.utils.SdkUtil
 * JD-Core Version:    0.6.2
 */