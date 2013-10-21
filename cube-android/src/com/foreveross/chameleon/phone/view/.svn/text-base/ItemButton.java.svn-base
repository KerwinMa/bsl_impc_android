package com.foreveross.chameleon.phone.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.ModuleType;
import com.foreveross.chameleon.phone.modules.task.ThreadPlatformUtils;
import com.foreveross.chameleon.util.CheckNetworkUtil;

public class ItemButton extends Button {

	public ItemButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void initModel(Context context, CubeModule cubeModule) {
		if(cubeModule.getLocal()!=null){
			this.setVisibility(View.GONE);
		}else if(cubeModule.getModuleType()==CubeModule.INSTALLED){
//        	this.setBackgroundResource(R.drawable.app_delete);
			this.setText("删除");
            this.setOnClickListener(new myClickListener(context, cubeModule,ModuleType.UNINSTALLED));
       
        }else if(cubeModule.getModuleType()==CubeModule.INSTALLING){
        	
//			this.setBackgroundResource(R.drawable.app_install_ing);
			this.setText("正在安装");
			this.setClickable(false);
        	
        } else if(cubeModule.getModuleType()==CubeModule.UPGRADABLE){
        	
//        	this.setBackgroundResource(R.drawable.app_update);
        	this.setText("更新");
			this.setOnClickListener(new myClickListener(context, cubeModule,ModuleType.UPDATABLE));
        	
        }else if(cubeModule.getModuleType()==CubeModule.UPGRADING){
        	
//			this.setBackgroundResource(R.drawable.app_update_ing);
        	this.setText("正在更新");
			this.setClickable(false);
        	
        }else if(cubeModule.getModuleType()==CubeModule.DELETING){
        	
//        	this.setBackgroundResource(R.drawable.app_delete_ing);
        	this.setText("正在删除");
			this.setClickable(false);
        	
        }else if(cubeModule.getModuleType()==CubeModule.UNINSTALL){
//			this.setBackgroundResource(R.drawable.app_install);
        	this.setText("安装");
			this.setOnClickListener(new myClickListener(context, cubeModule,ModuleType.INSTALLED));
        }
	}

	class myClickListener implements OnClickListener {
		private Context context;
		private CubeModule cubeModule;
		private ModuleType moduleType;

		public myClickListener(Context context, CubeModule cubeModule,
				ModuleType moduleType) {
			this.context = context;
			this.cubeModule = cubeModule;
			this.moduleType = moduleType;
		}

		@Override
		public void onClick(final View v) {
			
			int loginType = Application.class.cast(context.getApplicationContext()).getLoginType();
			
			if(loginType==TmpConstants.LOGIN_OUTLINE){
				return;
			}
			switch (moduleType) {
			case INSTALLED: {
				if(CheckNetworkUtil.checkNetWork(context)){
					if(ThreadPlatformUtils.downloadTaskCount<=2){
						Application.class.cast(context.getApplicationContext()).install(cubeModule);
					}
					else {
						Toast.makeText(context, "下载任务过多，请稍后下载", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(context, "网络故障，无法下载", Toast.LENGTH_SHORT).show();
				}
			}

				break;
			case UNINSTALLED:
			{
			    if(!TextUtils.isEmpty(cubeModule.getIdentifier())){
			    	if(cubeModule.getLocal()!=null){
			    		Dialog dialog = new AlertDialog.Builder(context).setTitle("提示")
			    				.setMessage("此模块为捆绑模块，不能被删除")
			    				.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			    					@Override
			    					public void onClick(DialogInterface dialog, int which) {
			    						
			    						dialog.dismiss();
			    					}
			    				}).create();
			    		dialog.show();
			    	}else {
			    		Dialog dialog = new AlertDialog.Builder(context).setTitle("提示")
			    				.setMessage("确定删除此模块？")
			    				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			    					@Override
			    					public void onClick(DialogInterface dialog, int which) {
			    						
			    						dialog.dismiss();
			    					}
			    				})
			    				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			    					
			    					@Override
			    					public void onClick(DialogInterface dialog, int which) {
			    						Application.class.cast(context.getApplicationContext()).uninstall(cubeModule);
			    					}
			    				}).create();
			    		dialog.show();
			    		
			    	}
			    }
			
		
			}
				break;
			case UPDATABLE:
			{	
				if(CheckNetworkUtil.checkNetWork(context)){
					if(ThreadPlatformUtils.downloadTaskCount<=2){
						Application.class.cast(context.getApplicationContext()).upgrade(cubeModule);
					}
					else {
						Toast.makeText(context, "下载任务过多，请稍后", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(context, "网络故障，无法更新", Toast.LENGTH_SHORT).show();
				}
			}
				break;
			default:
				break;

			}
		}

	}
}
