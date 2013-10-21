package common.extras.plugins;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DocPlugin extends CordovaPlugin{
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
			throws JSONException {
		System.out.println("action is "+action);
        if (action.equals("loadFile")) {
        	String fileType = args.getString(0);
            String path = args.getString(1); 
            if(path.subSequence(0, 8).equals("file:///"))
            	//把地址前缀的file:///去掉，不然无法打开文件
            	path=path.substring(8);
            openfile(fileType,path);
            return true;
        }
        return false;
    }
    
	public void openfile(String fileType, String path) {
		// 获取id，跳转activity。
		Log.i("chencao", "openFile type=" + fileType + " path=" + path);

		Intent intent = null;
		if (FileIntent.FILE_PDF.equals(fileType)) {
			intent = FileIntent.getPdfFileIntent(path);
		}else if (FileIntent.FILE_CHM.equals(fileType)) {
			intent = FileIntent.getChmFileIntent(path);
		}else if (FileIntent.FILE_TEXT_HTML.equals(fileType)) {
			intent = FileIntent.getHtmlFileIntent(path);
		}else if (FileIntent.FILE_WORD.equals(fileType)) {
			intent = FileIntent.getWordFileIntent(path);
		} else if (FileIntent.FILE_EXCEL.equals(fileType)) {
			intent = FileIntent.getExcelFileIntent(path);
		} else if (FileIntent.FILE_PPT.equals(fileType)) {
			intent = FileIntent.getPptFileIntent(path);
		} else if (FileIntent.FILE_CHM.equals(fileType)) {
			intent = FileIntent.getChmFileIntent(path);
		} else {
			// do nothing...
		}

		if (intent != null) {
			try {
				cordova.getActivity().startActivity(intent);
			} catch (Exception ex) {
				Log.w("chencao", "打开文件出错，没有合适的程序。");
				Toast.makeText(cordova.getActivity(), "打开文件出错，没有合适的程序。",
						Toast.LENGTH_LONG).show();
			}
		}
	}
    

}