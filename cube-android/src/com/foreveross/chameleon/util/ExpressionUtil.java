package com.foreveross.chameleon.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;

import com.csair.impc.R;

public class ExpressionUtil {
	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * 
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static ExpressionUtil singleton;
	private int page;
	private int length;

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public static ExpressionUtil getInstance() {
		if (singleton == null) {
			singleton = new ExpressionUtil();
		}
		return singleton;
	}

	private ExpressionUtil() {
	}

	HashMap<String, String> expressionMap;
	List<ExpressionElement> expressionList;// = new ArrayList<Map<String, Integer>>();
	
	public void dealExpression(Context context,
			SpannableString spannableString, Pattern patten, int start)
			throws SecurityException, NoSuchFieldException,
			NumberFormatException, IllegalArgumentException,
			IllegalAccessException {
		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			if (matcher.start() < start) {
				continue;
			}
			if (expressionMap == null) {
				return;
			}

			String value = expressionMap.get(key);
			if (TextUtils.isEmpty(value)) {
				continue;
			}
			Field field = R.drawable.class.getDeclaredField(value);
			int resId = Integer.parseInt(field.get(null).toString()); // 通过上面匹配得到的字符串来生成图片资源id
			if (resId != 0) {
				Bitmap bitmap = BitmapFactory.decodeResource(
						context.getResources(), resId);
				ImageSpan imageSpan = new ImageSpan(bitmap); // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
				int end = matcher.start() + key.length(); // 计算该图片名字的长度，也就是要替换的字符串的长度
				spannableString.setSpan(imageSpan, matcher.start(), end,
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE); // 将该图片替换字符串中规定的位置中
				if (end < spannableString.length()) { // 如果整个字符串还未验证完，则继续。。
					dealExpression(context, spannableString, patten, end);
				}
				break;
			}
		}
	}

	/**
	 * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	public SpannableString getExpressionString(Context context, String str,
			String zhengze) {
		
		SpannableString spannableString = new SpannableString(str);
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE); // 通过传入的正则表达式来生成一个pattern
		try {
			dealExpression(context, spannableString, sinaPatten, 0);
		} catch (Exception e) {
			Log.e("dealExpression", e.getMessage());
		}
		return spannableString;
	}

	public List<Map<String, Object>> getImageIdList(int current) {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		try {
			List<ExpressionElement> currenList = expressionList.subList(current*length, (current*length+length<expressionList.size())?current*length+length:current*length+expressionList.size()-current*length);
			for(ExpressionElement em:currenList) {
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("image", em.getId());
				listItems.add(listItem);
			}
			
				if (currenList.size() < length) {
					for (int i = currenList.size(); i < length; i++) {
						listItems.add(null);
					}
				}
					Map<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("image", R.drawable.del_icon);
					
					listItems.add(listItem);
				
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listItems;

	}

	public int getPage() {
		return page;
	}
	public ExpressionElement getExpressionElement(int index){
		if(index<expressionList.size()){
			return expressionList.get(index);
		}
		return null;
	}
	/**
	 * 初始化读取表情配置文件 解析出来的数据放到expressionMap中
	 * 
	 * @param context
	 * @param len一页表情多少个
	 */
	public void ParseExpressionFileData(Context context, int len) {
		List<String> data;
		setLength(len);

		expressionMap = new HashMap<String, String>();
		expressionList = new ArrayList<ExpressionElement>();
		try {
			data = new ArrayList<String>();
			InputStream in = context.getResources().getAssets()
					.open("expression");
			BufferedReader br = new BufferedReader(new InputStreamReader(in,
					"UTF-8"));
			String read = null;
			while ((read = br.readLine()) != null) {
				data.add(read);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			for (String str : data) {
				
				String[] text = str.split(",");
				String fileName = text[0]
						.substring(0, text[0].lastIndexOf("."));
				expressionMap.put(text[1], fileName);
				Field field;
				field = R.drawable.class
						.getDeclaredField(fileName);
				int resourceId = Integer.parseInt(field.get(null).toString());
				ExpressionElement em = new ExpressionElement();
				em.setCode(text[1]);
				em.setId(resourceId);
				expressionList.add(em);
			}
			if (expressionMap.size() % len > 0) {
				page = expressionMap.size() / len + 1;
			} else {
				page = expressionMap.size() / len;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public class ExpressionElement{
		int id;
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		String code;
	}
}