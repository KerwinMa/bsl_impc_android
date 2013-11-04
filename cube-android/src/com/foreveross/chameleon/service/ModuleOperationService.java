package com.foreveross.chameleon.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.foreveross.chameleon.Application;
import com.foreveross.chameleon.BroadcastConstans;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.URL;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.event.ModuleChangedEvent;
import com.foreveross.chameleon.phone.modules.CubeApplication;
import com.foreveross.chameleon.phone.modules.CubeModule;
import com.foreveross.chameleon.phone.modules.CubeModuleManager;
import com.foreveross.chameleon.phone.modules.task.CheckDependsTask;
import com.foreveross.chameleon.phone.modules.task.DownloadFileAsyncTask;
import com.foreveross.chameleon.phone.modules.task.ThreadPlatformUtils;
import com.foreveross.chameleon.phone.modules.task.UnZipTask;
import com.foreveross.chameleon.util.FileCopeTool;
import com.foreveross.chameleon.util.FileIntent;
import com.squareup.otto.ThreadEnforcer;

public class ModuleOperationService extends Service {

	private ExecutorService pool = Executors.newFixedThreadPool(3);
	private ExecutorService zippool = Executors.newFixedThreadPool(5);

	public class ModuleOperationServiceBinder extends Binder {
		public ModuleOperationService getService() {
			return ModuleOperationService.this;
		}
	}
  
	@Override
	public IBinder onBind(Intent intent) {
		return new ModuleOperationServiceBinder();
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	public static Intent getIntent(Context context) {
		return new Intent(context, ModuleOperationService.class);
	}

	public void install(final CubeModule module) {
		final CubeApplication cubeApplication = com.foreveross.chameleon.Application.class
				.cast(this.getApplicationContext()).getCubeApplication();
		
		if (!CubeModuleManager.getInstance().getAll_map().get(module.getCategory())
				.contains(module)) {
			throw new UnsupportedOperationException("你传入的对象不在管理器中");
		}

		DownloadFileAsyncTask task = new DownloadFileAsyncTask(this) {

			private int preProgress = -1;

			@Override
			public void doPreExecuteWithoutDialog() {
				super.doPreExecuteWithoutDialog();
				module.setModuleType(CubeModule.INSTALLING);
				//if (!module.isHidden()) {
					CubeModuleManager.getInstance().add2Main(module);
				//}
				sendModuleBroadcast(module);
				sendProcessBroadcast(module, -1);
				ThreadPlatformUtils.addAutodownLoadTaskCout();
				System.out.println("这是下载模块doPreExecuteWithoutDialog方法调用");
				sentModuleDownloadCount();
			}

			int flag = 0;

			@Override
			protected void onProgressUpdate(Integer... progress) {
				super.onProgressUpdate(progress);
				int p = progress[0];
				module.setProgress(p);
				System.out.println("当前下载的模块是:"+module+"进度为:"+p);
				// Log.v("downloadTask", " module "+module.getIdentifier()
				// +" is download :" +p);
				if (p == 0 || p == 100 || (p - preProgress) >= 5) {
					sendModuleBroadcast(module);
					sendProcessBroadcast(module, p);
					preProgress = p;
				}
				if ((flag + p) == -1) {
					flag++;
					sendModuleBroadcast(module);
					sendProcessBroadcast(module, p);
				}
			}

			@Override
			protected void doPostExecute(String result) {
				super.doPostExecute(result);
				final CubeApplication cubeApplication = com.foreveross.chameleon.Application.class
						.cast(context.getApplicationContext())
						.getCubeApplication();
				
				//sentModuleDownloadCount();
				System.out.println("下载模块:  "+module.getName()+"  成功 ,未下载模块数为: "+ThreadPlatformUtils.getAutodownLoadTaskCout());
				UnZipTask unZipTask = new UnZipTask(
						ModuleOperationService.this, cubeApplication, module) {

					@Override
					protected void onPreExecute() {
						super.onPreExecute();
					}

					@Override
					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						if (result) {
							if(module.isHidden()) {
								CubeModuleManager.getInstance().removeFormMain(module);
							}	
							module.setModuleType(CubeModule.INSTALLED);
							if(isExist(module,"icon.img")) {
								module.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.img"));
							}else if(isExist(module,"icon.png")) {
								module.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
							}
							
							CubeModuleManager.getInstance().removeFormUninstalled(module);
							CubeModuleManager.getInstance().add2Installed(module);
							Set<CubeModule> storedSet = Application.class
									.cast(getApplication())
									.getCubeApplication().getModules();
							storedSet.add(module);
							sendModuleBroadcast(module);
							sendProcessBroadcast(module, 101);
							sendWebBroadCast(module, "install");
							cubeApplication.save(cubeApplication);
							Log.v("Depends", "安装成功，开始检查依赖");
							
							ThreadPlatformUtils.SecreaseAutodownLoadTaskCout();//
						//	System.out.println("下载模块:  "+module.getName()+"  成功 ,未下载模块数为: "+ThreadPlatformUtils.getAutodownLoadTaskCout());
							sentModuleDownloadCount();
							if(ThreadPlatformUtils.getAutodownLoadTaskCout()==0) {
								Intent i = new Intent(BroadcastConstans.RefreshMainPage);
								i.putExtra("identifier", module.getIdentifier());
								i.putExtra("type", "main");
								sendBroadcast(i);
								System.out.println("已完成自动下载模块");
								
								ThreadPlatformUtils.setAutodownLoadallcount(0);
							}
							
							
							CheckDependsTask task = new CheckDependsTask() {
								@Override
								protected void onPostExecute(
										ArrayList<CubeModule> result) {
									if (null == result) {
										return;
									} else if (result.size() == 0) {
										Log.v("Depands", "没有依赖模块下载");
										Intent intent = new Intent(BroadcastConstans.CANCEELDIOLOG);
										sendBroadcast(intent);
									} else {
										for (CubeModule m : result) {
											int type = m.getModuleType();
											if (m.getPrivileges() != null) {
												if (type == CubeModule.UPGRADABLE
														&& type != CubeModule.UPGRADING
														&& type != CubeModule.DELETING) {
													Application.class.cast(
															getApplication())
															.upgrade(m);
												} else if (type == CubeModule.UNINSTALL
														&& type != CubeModule.INSTALLING
														&& type != CubeModule.DELETING) {
													Application.class.cast(
															getApplication())
															.install(m);
												}
											}
										}
									}
									super.onPostExecute(result);
								}
							};
							String checkPath = Environment
									.getExternalStorageDirectory().getPath()
									+ "/" + getPackageName() + "/www/";
							task.execute(module.getIdentifier(), checkPath);
						} else {
							if(module !=null) {
								module.setModuleType(CubeModule.UNINSTALL);
								CubeModuleManager.getInstance().removeFormMain(module);
								ThreadPlatformUtils.SecreaseAutodownLoadTaskCout();//减一
								System.out.println("下载模块:  "+module.getName()+"  失败 ,未下载模块数为: "+ThreadPlatformUtils.getAutodownLoadTaskCout());
								sentModuleDownloadCount();
								if(ThreadPlatformUtils.getAutodownLoadTaskCout()==0) {
									Intent i = new Intent(BroadcastConstans.RefreshMainPage);
									i.putExtra("identifier", module.getIdentifier());
									i.putExtra("type", "main");
									sendBroadcast(i);
									System.out.println("已下载完成全部模块");
									ThreadPlatformUtils.setAutodownLoadallcount(0);
								}
								Toast.makeText(context, "网络状态不稳定!", Toast.LENGTH_SHORT)
										.show();
								sendModuleBroadcast(module);
								sendProcessBroadcast(module, 101);
							}
						}
					};
				};
				unZipTask.execute();
				
				//ThreadPlatformUtils.executeByPalform(unZipTask, zippool,new String[] {});
			}

			@Override
			protected void doHttpFail(Exception e) {
				super.doHttpFail(e);
				if (module!=null) {
					module.setModuleType(CubeModule.UNINSTALL);
					CubeModuleManager.getInstance().removeFormMain(module);
					ThreadPlatformUtils.SecreaseAutodownLoadTaskCout();//减一
					System.out.println("下载模块:  "+module.getName()+"  失败 ,未下载模块数为: "+ThreadPlatformUtils.getAutodownLoadTaskCout());
					sentModuleDownloadCount();
					if(ThreadPlatformUtils.getAutodownLoadTaskCout()==0) {
						Intent i = new Intent(BroadcastConstans.RefreshMainPage);
						i.putExtra("identifier", module.getIdentifier());
						i.putExtra("type", "main");
						sendBroadcast(i);
						System.out.println("已下载完成全部模块");
						ThreadPlatformUtils.setAutodownLoadallcount(0);
					}
					Toast.makeText(context, "网络状态不稳定!", Toast.LENGTH_SHORT).show();
					sendModuleBroadcast(module);
					sendProcessBroadcast(module, 101);
				}

			}

		};

		task.setShowProgressDialog(false);
		task.setNeedProgressDialog(false);
		ThreadPlatformUtils.executeByPalform(task, pool,
				new String[] { module.getDownloadUrl(),
						module.getIdentifier() + ".zip",
						DownloadFileAsyncTask.SDCARD, getPackageName() });
	}

	public void sendProcessBroadcast(CubeModule module, int progress) {
		Intent it = new Intent(BroadcastConstans.MODULE_PROGRESS);
		it.putExtra("identifier", module.getIdentifier());
		it.putExtra("progress", progress);
		sendBroadcast(it);
	}
	public boolean isExist(CubeModule cubeModule,String name) {
		String path = URL.getSdPath(getApplicationContext(), cubeModule.getIdentifier())+"/"+name;
		File f= new File(path);
		if (f.exists()) {
			return true;
		} else {
			return false;
		}
	}

	public void uninstall(final CubeModule cubeModule) {
		if (!CubeModuleManager.getInstance().getAll_map().get(cubeModule.getCategory())
				.contains(cubeModule)) {
			throw new UnsupportedOperationException("你传入的对象不在管理器中");
		}
		cubeModule.setModuleType(CubeModule.DELETING);
		sendModuleBroadcast(cubeModule);
		final CubeApplication app = Application.class.cast(
				getApplicationContext()).getCubeApplication();

		AsyncTask<String, Integer, Boolean> task = new AsyncTask<String, Integer, Boolean>() {
			private int preProgress = -1;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				ThreadPlatformUtils.addTask2List(this);
				sendProcessBroadcast(cubeModule, -1);
			}

			@Override
			protected Boolean doInBackground(String... params) {
				publishProgress(-1);
				String basePath = Environment.getExternalStorageDirectory()
						.getPath() + "/" + app.getPackageName();
				StringBuilder folderPath = new StringBuilder();
				StringBuilder zipPath = new StringBuilder();
				folderPath.append(basePath).append("/www/")
						.append(cubeModule.getIdentifier());
				zipPath.append(basePath + "/")
						.append(cubeModule.getIdentifier()).append(".zip");
				app.tool.deleteFolder(folderPath.toString());
				app.tool.deleteFile(zipPath.toString());
				return true;
			}

			int flag = 0;

			@Override
			protected void onProgressUpdate(Integer... progress) {
				super.onProgressUpdate(progress);
				int p = progress[0];
				cubeModule.setProgress(p);
				if (p == 0 || p == 100 || (p - preProgress) >= 5) {
					sendModuleBroadcast(cubeModule);
					sendProcessBroadcast(cubeModule, p);
					preProgress = p;
				}

				if ((flag + p) == -1) {
					flag++;
					sendModuleBroadcast(cubeModule);
					sendProcessBroadcast(cubeModule, p);
				}
			}

			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				ThreadPlatformUtils.finishTask(this);
				if (result) {
					cubeModule.setModuleType(CubeModule.UNINSTALL);
					cubeModule.setIcon(cubeModule.getInstallIcon());
					CubeModuleManager.getInstance().removeFormInstalled(cubeModule);
					if (!cubeModule.isHidden()) {
						CubeModuleManager.getInstance().removeFormMain(cubeModule);
					}
					CubeModule newVersion = CubeModuleManager.getInstance()
							.getIdentifier_new_version_map().get(
									cubeModule.getIdentifier());
					if (newVersion != null) {
						newVersion.setModuleType(CubeModule.UNINSTALL);
						newVersion.setUpdatable(false);
						CubeModuleManager.getInstance().removeFormUpdatable(newVersion);
						CubeModuleManager.getInstance().removeFormInstalled(cubeModule);
						CubeModuleManager.getInstance().add2Uninstalled(newVersion);
						CubeModuleManager.getInstance().getIdentifier_new_version_map()
								.remove(newVersion);
						CubeModuleManager.getInstance().getIdentifier_old_version_map()
								.remove(cubeModule);
						CubeModuleManager.getInstance().getAllSet().remove(newVersion);
						app.getOldUpdateModules().remove(
								cubeModule.getIdentifier());
						app.getNewUpdateModules().remove(
								newVersion.getIdentifier());
					} else {
						CubeModuleManager.getInstance().add2Uninstalled(cubeModule);
					}
					sendWebBroadCast(cubeModule, "uninstall");
					Set<CubeModule> storedSet = Application.class
							.cast(getApplication()).getCubeApplication()
							.getModules();
					storedSet.remove(cubeModule);
					app.save(app);
					sendModuleBroadcast(cubeModule);
					sendProcessBroadcast(cubeModule, 101);
				} else {
					Toast.makeText(ModuleOperationService.this, "删除失败!",
							Toast.LENGTH_SHORT).show();
					sendProcessBroadcast(cubeModule, 101);
				}

			};

		};

		ThreadPlatformUtils.executeByPalform(task, pool, new String[] {});
	}

	/**
	 * 安装assets文件夹下的预装模块
	 */
	public void installConfigModule(String identifier) {

	}

	public void reset() {
	
		AsyncTask<String, Integer, Boolean> resetTask = new AsyncTask<String, Integer, Boolean>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				sendBroadcast(new Intent("com.csair.reset").putExtra(
						"resetStatus", "start"));
			}

			@Override
			protected Boolean doInBackground(String... params) {
				String path = Environment.getExternalStorageDirectory()
						.getPath()
						+ "/"
						+ Application.class.cast(getApplication())
								.getApplicationContext().getPackageName();
				CubeApplication app = Application.class.cast(getApplication())
						.getCubeApplication();
				try {
					app.tool.deleteFolder(path);
					app.install();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				super.onPostExecute(result);
				if (result == true) {
					CubeApplication app = CubeApplication
							.resetInstance(getApplicationContext());
					app.loadApplication();
					Application.class.cast(getApplication())
							.setCubeApplication(app);
					sendBroadcast(new Intent("com.csair.reset").putExtra(
							"resetStatus", "finish"));
				} else {
					sendBroadcast(new Intent("com.csair.reset").putExtra(
							"resetStatus", "failed"));

				}
			}

		};

		ThreadPlatformUtils.executeByPalform(resetTask, pool, new String[] {});

	}

	public void upgrade(final CubeModule module) {
		final CubeApplication cubeApplication = Application.class.cast(
				this.getApplicationContext()).getCubeApplication();
		if (!CubeModuleManager.getInstance().getAll_map().get(module.getCategory())
				.contains(module)) {
			throw new UnsupportedOperationException("你传入的对象不在管理器中");
		}

		DownloadFileAsyncTask task = new DownloadFileAsyncTask(this) {

			private int preProgress = -1;

			@Override
			public void doPreExecuteWithoutDialog() {
				super.doPreExecuteWithoutDialog();
				module.setModuleType(CubeModule.UPGRADING);
				if (!module.isHidden()) {
					CubeModule oldCubeModule = CubeModuleManager.getInstance()
							.getIdentifier_old_version_map().get(
									module.getIdentifier());
					CubeModuleManager.getInstance().removeFormMain(oldCubeModule);
					CubeModuleManager.getInstance().add2Main(module);
				}
				System.out.println("这是更新模块doPreExecuteWithoutDialog方法调用");
				ThreadPlatformUtils.addAutodownLoadTaskCout();
				sentModuleDownloadCount();
				sendModuleBroadcast(module);
			}

			int flag = 0;

			@Override
			protected void onProgressUpdate(Integer... progress) {
				int p = progress[0];
				module.setProgress(p);
				System.out.println("当前更新的模块是:"+module+"进度为:"+p);
				Log.v("downloadTask", " module " + module.getIdentifier()
						+ " is download :" + p);
				
				if (p == 0 || p == 100 || (p - preProgress) >= 5) {
					sendModuleBroadcast(module);
					sendProcessBroadcast(module, p);
					preProgress = p;
				}
				if ((flag + p) == -1) {
					flag++;
					sendModuleBroadcast(module);
					sendProcessBroadcast(module, p);
				}

			}

			@Override
			protected void doPostExecute(String result) {
				super.doPostExecute(result);
				final CubeApplication cubeApplication = com.foreveross.chameleon.Application.class
						.cast(context.getApplicationContext())
						.getCubeApplication();
				
				sentModuleDownloadCount();
				System.out.println("更新模块:  "+module.getName()+"  成功 ,更新模块数为: "+ThreadPlatformUtils.getAutodownLoadTaskCout());
				UnZipTask unZipTask = new UnZipTask(
						ModuleOperationService.this, cubeApplication, module) {

					@Override
					protected Boolean doInBackground(String... params) {
						// 先删除旧文件
						String basePath = Environment
								.getExternalStorageDirectory().getPath()
								+ "/"
								+ context.getPackageName();
						StringBuilder sb2 = new StringBuilder();
						sb2.append(basePath).append("/www/")
								.append(module.getIdentifier());
						cubeApplication.tool.deleteFolder(sb2.toString());
						return super.doInBackground(params);
					}

					protected void onPostExecute(Boolean result) {
						super.onPostExecute(result);
						CubeModule oldModule = CubeModuleManager.getInstance()
								.getIdentifier_old_version_map().get(
										module.getIdentifier());
						if (null == oldModule) {
							return;
						}
						if (result) {
							module.setModuleType(CubeModule.INSTALLED);
							module.setUpdatable(false);
							if(isExist(module,"icon.img")) {
								module.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.img"));
							}else if(isExist(module,"icon.png")) {
								module.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
							}
							oldModule.setModuleType(CubeModule.INSTALLED);
							if(isExist(module,"icon.img")) {
								oldModule.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.img"));
							}else if(isExist(module,"icon.png")) {
								oldModule.setIcon(URL.getSdPath(context, module.getIdentifier()+"/icon.png"));
							}
							oldModule.setUpdatable(false);
							CubeModuleManager.getInstance().removeFormUpdatable(module);
							CubeModuleManager.getInstance().removeFormInstalled(oldModule);
							CubeModuleManager.getInstance().add2Installed(module);
							CubeModuleManager.getInstance().getIdentifier_new_version_map()
									.remove(module.getIdentifier());
							CubeModuleManager.getInstance().getIdentifier_old_version_map()
									.remove(oldModule.getIdentifier());
							CubeModuleManager.getInstance().getAllSet().remove(oldModule);

							Set<CubeModule> storedSet = cubeApplication
									.getModules();
							cubeApplication.getOldUpdateModules().remove(
									oldModule.getIdentifier());
							storedSet.remove(oldModule);
							storedSet.add(module);
							cubeApplication.save(cubeApplication);
							sendWebBroadCast(module, "upgrade");
							sendModuleBroadcast(module);
							sendProcessBroadcast(module, 101);
							Log.v("Depends", "升级成功，开始检查依赖");
							ThreadPlatformUtils.SecreaseAutodownLoadTaskCout();//
							sentModuleDownloadCount();
							if(ThreadPlatformUtils.getAutodownLoadTaskCout()==0) {
								Intent i = new Intent(BroadcastConstans.RefreshMainPage);
								i.putExtra("identifier", module.getIdentifier());
								i.putExtra("type", "main");
								sendBroadcast(i);
								ThreadPlatformUtils.setAutodownLoadallcount(0);
							}
							
							CheckDependsTask task = new CheckDependsTask() {
								@Override
								protected void onPostExecute(
										ArrayList<CubeModule> result) {
									if (null == result) {
										return;
									} else if (result.size() == 0) {
										Log.v("Depands", "没有依赖模块下载");
										Intent intent = new Intent(BroadcastConstans.CANCEELDIOLOG);
										sendBroadcast(intent);
									} else {
										for (CubeModule m : result) {
											int type = m.getModuleType();
											if (m.getPrivileges() != null) {
												if (type == CubeModule.UPGRADABLE
														&& type != CubeModule.UPGRADING
														&& type != CubeModule.DELETING) {
													Application.class.cast(
															getApplication())
															.upgrade(m);
												} else if (type == CubeModule.UNINSTALL
														&& type != CubeModule.INSTALLING
														&& type != CubeModule.DELETING) {
													Application.class.cast(
															getApplication())
															.install(m);
												}
											}
										}
									}
									super.onPostExecute(result);
								}
							};
							String checkPath = Environment
									.getExternalStorageDirectory().getPath()
									+ "/" + getPackageName() + "/www/";
							task.execute(module.getIdentifier(), checkPath);
						} else {
							module.setModuleType(CubeModule.UPGRADABLE);
							module.setUpdatable(true);
							oldModule.setModuleType(CubeModule.INSTALLED);
							oldModule.setUpdatable(true);
							if (!module.isHidden()) {
								CubeModuleManager.getInstance().removeFormMain(module);
								CubeModuleManager.getInstance().add2Main(oldModule);
							}
							ThreadPlatformUtils.SecreaseAutodownLoadTaskCout();//减一
							System.out.println("更新模块:  "+module.getName()+"  失败 ,未更新模块数为: "+ThreadPlatformUtils.getAutodownLoadTaskCout());
							sentModuleDownloadCount();
							if(ThreadPlatformUtils.getAutodownLoadTaskCout()==0) {
								Intent i = new Intent(BroadcastConstans.RefreshMainPage);
								i.putExtra("identifier", module.getIdentifier());
								i.putExtra("type", "main");
								sendBroadcast(i);
								System.out.println("已下载完成全部模块");
								ThreadPlatformUtils.setAutodownLoadallcount(0);
								Intent intent = new Intent(BroadcastConstans.CANCEELDIOLOG);
								sendBroadcast(intent);
							}
							Toast.makeText(context, "网络状态不稳定!", Toast.LENGTH_SHORT)
									.show();
						}
						sendModuleBroadcast(module);
						sendProcessBroadcast(module, 101);
					};
				};
				unZipTask.execute();
//				ThreadPlatformUtils.executeByPalform(unZipTask, pool,
//						new String[] {});
			}

			@Override
			protected void doHttpFail(Exception e) {
				super.doHttpFail(e);
				CubeModule oldModule = CubeModuleManager.getInstance()
						.getIdentifier_old_version_map().get(
								module.getIdentifier());
				if (oldModule == null) {
					return;
				}
				module.setModuleType(CubeModule.UPGRADABLE);
				module.setUpdatable(true);
				oldModule.setModuleType(CubeModule.INSTALLED);
				oldModule.setUpdatable(true);
				if (!module.isHidden()) {
					CubeModuleManager.getInstance().removeFormMain(module);
					CubeModuleManager.getInstance().add2Main(oldModule);
				}
				ThreadPlatformUtils.SecreaseAutodownLoadTaskCout();
				System.out.println("更新模块:  "+module.getName()+"  失败 ,未更新模块数为: "+ThreadPlatformUtils.getAutodownLoadTaskCout());
				if(ThreadPlatformUtils.getAutodownLoadTaskCout()==0) {
					Intent i = new Intent(BroadcastConstans.RefreshMainPage);
					i.putExtra("identifier", module.getIdentifier());
					i.putExtra("type", "main");
					sendBroadcast(i);
					System.out.println("已下载完成全部模块");
					ThreadPlatformUtils.setAutodownLoadallcount(0);
				}
				Toast.makeText(context, "网络状态不稳定!", Toast.LENGTH_SHORT).show();
				sendModuleBroadcast(module);
				sendProcessBroadcast(module, 101);
			}

		};

		task.setShowProgressDialog(false);
		task.setNeedProgressDialog(false);
		ThreadPlatformUtils.executeByPalform(task, pool,
				new String[] { module.getDownloadUrl(),
						module.getIdentifier() + ".zip",
						DownloadFileAsyncTask.SDCARD, getPackageName() });

	}
	//刷新自动下载进度
	public void sentModuleDownloadCount() {
		Intent it = new Intent(BroadcastConstans.MODULE_AUTODOWNLOAD_PROGERSS);
		sendBroadcast(it);
	}

	@Override
	public void onDestroy() {
		pool.shutdownNow();
	}

	public void sendModuleBroadcast(CubeModule module) {
		EventBus.getEventBus(TmpConstants.EVENTBUS_MODULE_CHANGED,
				ThreadEnforcer.MAIN).post(
				new ModuleChangedEvent(module.getIdentifier(), module
						.getCategory()));

	}

	public void sendWebBroadCast(CubeModule module, String type) {
		Intent it = new Intent(BroadcastConstans.MODULE_WEB);
		it.putExtra("identifier", module.getIdentifier());
		it.putExtra("type", type);
		sendBroadcast(it);
	}
	
	public void sendMainBroadCast(CubeModule module, String type) {
		Intent it = new Intent(BroadcastConstans.RefreshMainPage);
		it.putExtra("identifier", module.getIdentifier());
		it.putExtra("type", type);
		sendBroadcast(it);
	}
	public ArrayList<CubeModule> checkDepends(String identifier) {
		String path = Environment.getExternalStorageDirectory().getPath() + "/"
				+ getPackageName() + "/www/";
		ArrayList<CubeModule> result = null;
		CheckDependsTask task = new CheckDependsTask() {

			@Override
			protected void onPostExecute(ArrayList<CubeModule> result) {
				super.onPostExecute(result);
				this.cancel(true);
				Log.e("check_tag", "task canceled");
			}

		};
		task.execute(identifier, path);
		try {
			result = task.get();
		} catch (InterruptedException e) {
			Log.e("checkDepends", "InterruptedException:检查依赖失败");
			try {
				result = task.get();
				return result;
			} catch (InterruptedException e1) {
				Log.e("checkDepends", "InterruptedException:检查依赖失败");
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				Log.e("checkDepends", "ExecutionException:检查依赖失败");
				e1.printStackTrace();
			}
		} catch (ExecutionException e) {
			Log.e("checkDepends", "ExecutionException:检查依赖失败");
			e.printStackTrace();
		}
		return result;
	}

	public void downloadAttachMent(final String attach) {

		new Thread() {
			public void run() {
				downloadAttachMentFile(attach);
			};
		}.start();
	}

	private void downloadAttachMentFile(String attach) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet post = new HttpGet(URL.getDownloadUrl(
					ModuleOperationService.this, attach));
			HttpResponse response;

			response = client.execute(post);
			HttpEntity entity = response.getEntity();
			String fileName = response.getFirstHeader("Content-Disposition")
					.getValue();
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {

				fileName = attach
						+ fileName.substring(fileName.indexOf("=") + 1);
				FileCopeTool copeTool = new FileCopeTool(this);
				Application.class.cast(getApplication()).getCubeApplication()
						.getPackageName();
				copeTool.createFile(Application.class.cast(getApplication())
						.getCubeApplication().getPackageName()
						+ "/www/com.foss.announcement");
				String dirpath = Environment.getExternalStorageDirectory()
						+ "/"
						+ Application.class.cast(getApplication())
								.getCubeApplication().getPackageName()
						+ "/www/com.foss.announcement";

				fileOutputStream = new FileOutputStream(new File(dirpath,
						fileName));
				// 开始下载
				Log.i("Environment", dirpath + "/" + fileName);
				byte[] buf = new byte[1024 * 256];
				int ch = -1;

				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
				}
			}
			Log.d("cube", "下载完成");

			if (is != null) {
				is.close();
			}
			
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.e("cube", "下载失败");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("cube", "下载失败");
		}
	}

	public void openAttachment(String fileType, String path) {
		Log.i("chencao", "openFile type=" + fileType + " path=" + path);

		Intent intent = null;
		if (FileIntent.FILE_PDF.equals(fileType)) {
			intent = FileIntent.getPdfFileIntent(path);
		} else if (FileIntent.FILE_CHM.equals(fileType)) {
			intent = FileIntent.getChmFileIntent(path);
		} else if (FileIntent.FILE_TEXT_HTML.equals(fileType)) {
			intent = FileIntent.getHtmlFileIntent(path);
		} else if (FileIntent.FILE_WORD.equals(fileType)) {
			intent = FileIntent.getWordFileIntent(path);
		} else if (FileIntent.FILE_EXCEL.equals(fileType)) {
			intent = FileIntent.getExcelFileIntent(path);
		} else if (FileIntent.FILE_PPT.equals(fileType)) {
			intent = FileIntent.getPptFileIntent(path);
		} else if ("txt".equals(fileType)) {
			intent = FileIntent.getTextFileIntent(path, false);

		} else {
			// do nothing...
		}

		if (intent != null) {
			try {
				startActivity(intent);
			} catch (Exception ex) {
				Log.w("chencao", "打开文件出错，没有合适的程序。");
				Toast.makeText(this, "打开文件出错，没有合适的程序。", Toast.LENGTH_LONG)
						.show();
			}
		}

	}
}
