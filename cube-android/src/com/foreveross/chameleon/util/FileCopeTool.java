package com.foreveross.chameleon.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.phone.modules.CubeApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
 
/**
 * 文件操作类
 * 
 * @author Administrator tan
 */
public class FileCopeTool {
	/**
	 * 
	 */
	private  Context c;

	public FileCopeTool(Context c) {
		this.c = c;
	}

	/**
	 * 读取内存卡某路径的文件
	 * 
	 * @param path
	 * @param name
	 * @return
	 */
	public String readerFile(String path, String name) {
		String result = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File s = new File(path + "/" + name);
			try {
				FileInputStream in = new FileInputStream(s);
				// 获取文件的字节数
				int lenght = in.available();
				// 创建byte数组
				byte[] buffer = new byte[lenght];
				// 将文件中的数据读到byte数组中
				in.read(buffer);
				result = EncodingUtils.getString(buffer, "UTF-8");

			} catch (Exception e) {
			}
		}
		return result;
	}

	/**
	 * 解析appJSON
	 * 
	 * @param json
	 */
	public CubeApplication AppJSONunittool(String results) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		CubeApplication result = gson.fromJson(results, CubeApplication.class);
		System.out.println("name===" + result.getName());
		System.out.println("size===" + result.getModules().size());
		return result;
	}

	/**
	 * 写文件。
	 * 
	 * @param fileName
	 * @param path
	 * @param json
	 * @return
	 */
	public boolean writeToJsonFile(String fileName, String path, String json) {
		Boolean flag = false;
		// 获取sd卡目录
		File file = null;
		OutputStream output = null;
		try {
			file = new File(path);
			if (!file.exists()) {
				file.mkdirs(); // 创建文件夹
			}
			file = new File(path + fileName);
			output = new FileOutputStream(file);
			output.write(json.getBytes());
			output.flush();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	/**
	 * 判断SD里某路径的文件是否存在。
	 * 
	 * @param path
	 * @return
	 */
	public boolean isfileExist(String path, String name) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File s = new File(path + "/" + name);
			if (s.exists()) {
				return true;
			} else {
				return false;
			}

		}
		return false;

	}

	/**
	 * 删除SD卡某路径下的某个文件
	 * 
	 * @param path
	 * @param name
	 */
	public void deleteFile(String path) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File s = new File(path);
			s.delete();

		}
	}

	/**
	 * 删除整个文件夹
	 * 
	 * @param path
	 * @param name
	 */
	public static void deleteFolder(String path) {
		File f = new File(path);
		if (f.exists()) {
			// System.out.println("存在该文件夹");
			// 在判断它是不是一个目录
			if (f.isDirectory()) {
				// System.out.println("该文件夹是一个目录");
				// 列出该文件夹下的所有内容
				String[] fileList = f.list();
				if(fileList==null){
					return;
				}
				for (int i = 0; i < fileList.length; i++) {
					// 对每个文件名进行判断
					// 如果是文件夹 那么就循环deleteFolder
					// 如果不是，直接删除文件
					String name = path + File.separator + fileList[i];
					File ff = new File(name);
					if (ff.isDirectory()) {
						deleteFolder(name);
					} else {
						ff.delete();
					}
				}
				// 最后删除文件夹
				f.delete();
				
			} else {
				System.out.println("该文件夹不是一个目录");
			}
		} else {
			System.out.println("不存在该文件夹");
		}
	}

	/**
	 * 从assets 文件夹中获取文件并读取数据
	 * 
	 * @param fileName
	 * @return
	 */
	public String getFromAssets(String fileName) {
		String result = "";
		try {
			InputStream in = c.getResources().getAssets().open(fileName);
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组中
			in.read(buffer);
			result = EncodingUtils.getString(buffer, "ENCODING");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 判断SDCard是否存在
	private static boolean isHaveSDCard() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 在内存卡新建一个文件夹
	 * 
	 * @param path
	 */
	public void createFile(String path) {
		String s = Environment.getExternalStorageDirectory().getPath();
		if (isHaveSDCard()) {
			System.out.println("path===" + s);
			File f = new File(s + "/" + path);
			if (!f.exists()) {
				System.out.println("文件夹不存在");
				f.mkdirs();

			}
		}
	}

	// 复制文件
	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	/**
	 * 单个文件复制,dir的格式如“/sdcard/js/”+filePath
	 * 
	 * @param filePath
	 * @param dir
	 * @param fileName
	 * @throws IOException
	 */
	public void copyOneFileToSDCard(String filePath, String dir, String fileName)
			throws IOException {
		InputStream is = c.getAssets().open(filePath);
		BufferedInputStream inBuff = null;
		inBuff = new BufferedInputStream(is);

		byte[] buffer = new byte[1024];
		if (isHaveSDCard()) {
			File f = new File(dir);
			if (!f.exists()) {
				f.mkdirs();
			}

			FileOutputStream fos = new FileOutputStream(dir + fileName);
			int len = inBuff.read(buffer);
			while (len > 0) {
				fos.write(buffer, 0, len);
				len = inBuff.read(buffer);
			}
			fos.close();
			inBuff.close();

		}
	}

	/**
	 * 复制Assets 里的整个文件夹到SD卡里
	 * 
	 * @param assetDir
	 * @param dir
	 */
	public void CopyAssets(String assetDir, String dir) {
		String[] files;
		try {
			// 获得Assets一共有几多文件
			files = c.getResources().getAssets().list(assetDir);
		} catch (IOException e1) {
			return;
		}
		File mWorkingPath = new File(dir);
		// 如果文件路径不存在
		if (!mWorkingPath.exists()) {
			// 创建文件夹
			if (!mWorkingPath.mkdirs()) {
				// 文件夹创建不成功时调用
			}
		}

		for (int i = 0; i < files.length; i++) {
			try {
				// 获得每个文件的名字
				String fileName = files[i];
				// 根据路径判断是文件夹还是文件
				if (!fileName.contains(".")) {
					if (0 == assetDir.length()) {
						CopyAssets(fileName, dir + fileName + "/");
					} else {
						CopyAssets(assetDir + "/" + fileName, dir + "/"
								+ fileName + "/");
					}
					continue;
				}
				File outFile = new File(mWorkingPath, fileName);
				if (outFile.exists()) {
					outFile.delete();
				}
				InputStream in = null;
				if (0 != assetDir.length()) {
					in = c.getAssets().open(assetDir + "/" + fileName);
				} else {
					in = c.getAssets().open(fileName);
				}
				OutputStream out = new FileOutputStream(outFile);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				in.close();
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 往
	 * 
	 * @param file
	 *            ：文件流
	 * @param str
	 *            ：
	 * @param int mode :0:create;1:overwrite
	 */
	public static int writeString(String file, String str, int mode) {
		java.io.File outputFile = new java.io.File(file);
		if (outputFile.exists() && mode == 0) {
			return -1;
		}
		java.io.PrintWriter output;
		try {
			output = new java.io.PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return -2;
		}
		output.print(str);
		output.close();
		return 0;
	}

	public static String readString(String file) {
		String inputStr = "";
		java.io.File inputFile = new java.io.File(file);
		java.util.Scanner input;
		try {
			input = new java.util.Scanner(inputFile);
			while (input.hasNext()) {
				inputStr += input.next();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		input.close();
		return inputStr;
	}

	
	/** 
     * 获得指定文件的byte数组 
     */  
    public static byte[] readBytes(String filePath){  
        byte[] buffer = null;  
        try {  
            File file = new File(filePath);  
            FileInputStream fis = new FileInputStream(file);  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            buffer = bos.toByteArray();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return buffer;  
    }  
  
    /** 
     * 根据byte数组，生成文件 
     */  
    public static void writeBytes(String filePath,byte[] bfile) {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try {  
            File dir = new File(filePath);  
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在  
                dir.mkdirs();  
            }  
            file = new File(filePath);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }  
	
	
	public void encrypt(String path, String encAlgKey) {
		String inputStr = FileCopeTool.readString(path);
		byte[] outputBytes = null;
		try {
			
			outputBytes=SymEncrypt.encrypt(inputStr, encAlgKey);
			FileCopeTool.writeBytes(path, outputBytes);
			Log.v("encrypt", "加密已完成");
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("encrypt", "文件写入出错");
		}

	}

	public void decrypt(String path, String strKey) {
		byte[] inputBytes = FileCopeTool.readBytes(path);
		String outputStr = null;
		try {
			outputStr=SymEncrypt.decrypt(inputBytes,strKey);
			FileCopeTool.writeString(path, outputStr,1);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("decrypt", "文件写入出错");
			return;
		}
		if(outputStr==null){
			Log.e("decrypt", "文件写入出错");
			return;
		}
		Log.v("decrypt", "解密完成");

	}
	
	
	public String[] getAssectFilePath(String identifier){
		String[] files = null;
		try {
			files = c.getResources().getAssets().list("image/snapshot/"+identifier);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}
	
	public static void scanFilesAndDeleteAtpath(final int days)
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String dir = Environment.getExternalStorageDirectory()
						.getPath() + "/" + TmpConstants.ATTACHMENT_PATH;
				File rootDir = new File(dir);
				File[] files = rootDir.listFiles();
				if(null == files)
				{
					Thread.currentThread().interrupt();
					return;
				}
				for(File file :files)
				{
					long time = file.lastModified();
					long currentTime = System.currentTimeMillis();
					if(currentTime - time >= days*1000*60*60*24)
					{
						file.delete();
					}
				}
				
			}
		}).start();
		
		
	}
	
}
