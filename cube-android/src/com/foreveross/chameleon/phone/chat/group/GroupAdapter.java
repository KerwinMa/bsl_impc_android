package com.foreveross.chameleon.phone.chat.group;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.csair.impc.R;
import com.foreveross.chameleon.store.model.FriendGroupModel;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.store.model.UserStatus;

public class GroupAdapter extends BaseExpandableListAdapter implements
		Filterable {

	private Context context;
	private List<FriendGroupModel> groupData;
	private Filter filter;

	public GroupAdapter(Context context, List<FriendGroupModel> groupData,
			Filter filter) {
		this.context = context;
		
		PinyinSimpleComparator comparator = new PinyinSimpleComparator();
		Collections.sort(groupData, comparator);
		this.groupData = groupData;
		this.filter = filter;
	}

	// ---------------------------------------以下是child的回调函数-----------------------------------------//
	@Override
	public UserModel getChild(int groupPosition, int childPosition) {
		return (UserModel) groupData.get(groupPosition)
				.getObject(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildHolder holder = null;
		if (null == convertView) {
			holder = new ChildHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_allfriend_child, parent, false);
			holder.headIv = (ImageView) convertView
					.findViewById(R.id.item_group_head_iv);
			holder.nameTv = (TextView) convertView
					.findViewById(R.id.item_group_friend_name_tv);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		UserModel friend = getChild(groupPosition, childPosition);
		
		if (getHeadIcon(friend) != -1){
			holder.headIv.setImageResource(getHeadIcon(friend));
		}
		holder.headIv.setImageBitmap(drawPushCount(context, holder.headIv,
				friend.getUnreadMessageCount()));
		holder.nameTv.setText(friend.getName());
//		holder.collectedBox.setFocusable(false);
//		if (friend.isFavor()) {
//			holder.collectedBox.setBackgroundResource(R.drawable.collected_on);
//		} else {
//			holder.collectedBox.setBackgroundResource(R.drawable.collected_off);
//		}
//		setListener(holder, groupPosition, childPosition);
		return convertView;
	}

	class ChildHolder {
		ImageView headIv;
		TextView nameTv;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return groupData.get(groupPosition).size();
	}

	// ---------------------------------------以下是group的回调函数-----------------------------------------//

	@Override
	public FriendGroupModel getGroup(int groupPosition) {
		return groupData.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder holder;
		if (null == convertView) {
			holder = new GroupViewHolder();
			convertView = LinearLayout.inflate(context, R.layout.item_allfriend_group,
					null);
			holder.groupLayout = (RelativeLayout) convertView
					.findViewById(R.id.item_group_layout);
			holder.groupNameTv = (TextView) convertView
					.findViewById(R.id.item_group_name_tv);
			holder.expandIv = (ImageView) convertView
					.findViewById(R.id.item_group_expand_iv);
			holder.isRead = (LinearLayout) convertView
					.findViewById(R.id.item_group_isread);
			holder.background = (ImageView) convertView
					.findViewById(R.id.item_group_background);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}

		/*if (groupPosition % 2 == 0) {
			// 双数，显示白色背景
			convertView.setBackgroundColor(Color.parseColor("#F7F7F7"));
		} else {
			// 单数，显示灰色背景
			convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
		}*/
		FriendGroupModel groupItemData = groupData.get(groupPosition);
		holder.groupNameTv.setText(groupItemData.getGroupName());
		if (isExpanded) {
			holder.expandIv.setImageResource(R.drawable.arrow_dowm);
		} else {
			holder.expandIv.setImageResource(R.drawable.arrow);
		}
		drawCountWithImg(context, holder.isRead,
				groupItemData.getMessageCount());
		return convertView;
	}

	class GroupViewHolder {
		RelativeLayout groupLayout;
		/** 组名 */
		TextView groupNameTv;
		/** 分组图片 */
		ImageView expandIv;
		/** 背景图片 */
		ImageView background;
		/** 整个小组未读消息条数 */
		LinearLayout isRead;

	}

	/** 点击item是否变色 */
	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-8-26 下午8:41:13
	 */
	@Override
	public Filter getFilter() {
		return filter;
	}

	public int getHeadIcon(UserModel userModel) {
		String sex = userModel.getSex();
		String status = userModel.getStatus();
		if (sex == null || status == null){
			return -1;
		}
		if (sex.equals("female")) {
			if (UserStatus.USER_STATE_AWAY.equals(status)) {
				return R.drawable.chatroom_female_online;
			} else if (UserStatus.USER_STATE_BUSY.equals(status)) {
				return R.drawable.chatroom_female_online;
			} else if (UserStatus.USER_STATE_OFFLINE.equals(status)) {
				return R.drawable.chatroom_female_outline;
			} else if (UserStatus.USER_STATE_ONLINE.equals(status)) {
				return R.drawable.chatroom_female_online;
			}
		} else if (sex.equals("male")) {
			if (UserStatus.USER_STATE_AWAY.equals(status)) {
				return R.drawable.chatroom_male_online;
			} else if (UserStatus.USER_STATE_BUSY.equals(status)) {
				return R.drawable.chatroom_male_online;
			} else if (UserStatus.USER_STATE_OFFLINE.equals(status)) {
				return R.drawable.chatroom_male_outline;
			} else if (UserStatus.USER_STATE_ONLINE.equals(status)) {
				return R.drawable.chatroom_male_online;
			}
		} else {
			if (UserStatus.USER_STATE_AWAY.equals(status)) {
				return R.drawable.chatroom_unknow_online;
			} else if (UserStatus.USER_STATE_BUSY.equals(status)) {
				return R.drawable.chatroom_unknow_online;
			} else if (UserStatus.USER_STATE_OFFLINE.equals(status)) {
				return R.drawable.chatroom_unknow_outline;
			} else if (UserStatus.USER_STATE_ONLINE.equals(status)) {
				return R.drawable.chatroom_unknow_online;
			}
		}
		return -1;
	}

	/**
	 * [画出消息条数]<BR>
	 * [功能详细描述]
	 * 
	 * @param context
	 * @param img
	 * @param count
	 * @return 2013-8-29 下午2:43:25
	 */
	public Bitmap drawPushCount(Context context, ImageView img, int count) {

		Bitmap icon = ((BitmapDrawable) img.getDrawable()).getBitmap();
		if (count == 0) {
			return icon;
		}
		// 初始化画布
		int iconSize = (int) context.getResources().getDimension(
				android.R.dimen.app_icon_size);
		Bitmap contactIcon = Bitmap.createBitmap(iconSize, iconSize,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(contactIcon);

		// 拷贝图片
		Paint iconPaint = new Paint();
		iconPaint.setDither(true);// 防抖动
		iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理，这样，当你选择Drawable时，会有抗锯齿的效果
		Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
		Rect dst = new Rect(0, 0, iconSize, iconSize);
		canvas.drawBitmap(icon, src, dst, iconPaint);
		int drawCount = count;
		// 头像的宽度
		float x = contactIcon.getWidth();
		// 文字所在位置
		float textSite = x - 20;
		// 浮标基本长度
		int length = 28;
		// 根据数字位数动态扩展浮标宽度
		while (count >= 10) {
			length = length + 8;
			if (count != 0)
				count /= 10;
			textSite = textSite - 10;
		}

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// 画最外层黑边
		RectF outerRect = new RectF(x - length, 0, x, 28);
		paint.setARGB(255, 204, 204, 204);
		canvas.drawRoundRect(outerRect, 10f, 10f, paint);
		// 画灰白色圆环
		outerRect = new RectF(x - length + 0.5f, 0.5f, x - 0.5f, 27.5f);
		paint.setColor(Color.WHITE);
		canvas.drawRoundRect(outerRect, 10f, 10f, paint);
		// 里层红色底
		RectF innerRect = new RectF(x - length + 3, 3, x - 3, 25);
		paint.setColor(Color.RED);
		canvas.drawRoundRect(innerRect, 10f, 10f, paint);
		// 启用抗锯齿和使用设备的文本字距
		Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
				| Paint.DEV_KERN_TEXT_FLAG);
		countPaint.setColor(Color.WHITE);
		countPaint.setTextSize(20f);
		countPaint.setTypeface(Typeface.DEFAULT_BOLD);
		canvas.drawText(String.valueOf(drawCount), textSite, 20, countPaint);

		return contactIcon;
	}

	public void drawCountWithImg(Context context, LinearLayout layout, int count) {
		layout.removeAllViews();
		View v = LayoutInflater.from(context).inflate(R.layout.count, null);

		if (count == 0) {
			v.setVisibility(View.GONE);
		} else {
			ImageView countView = (ImageView) v.findViewById(R.id.count_img);
			TextView text = (TextView) v.findViewById(R.id.count_text);
			text.setText(String.valueOf(count));
			int tmpCount = 1;
			while (count >= 10) {
				count = count / 10;
				tmpCount++;
			}
			switch (tmpCount) {
			case 1:
				countView.setBackgroundResource(R.drawable.push_count_1);
				break;
			case 2:
				countView.setBackgroundResource(R.drawable.push_count_10);
				break;
			case 3:
				countView.setBackgroundResource(R.drawable.push_count_100);
				break;
			default:
				break;
			}
			layout.addView(v);
		}
	}

	public static String LongToStr(long m) {
		String dateString = null;
		SimpleDateFormat formatter = new SimpleDateFormat();
		String LONG_FORMAT = "yyyy-MM-dd HH:mm:ss";
		synchronized (formatter) {
			formatter.applyPattern(LONG_FORMAT);
			dateString = formatter.format(new Date(m));
		}
		return dateString;
	}
	
	class PinyinSimpleComparator implements Comparator<FriendGroupModel> { 
	    public int compare(FriendGroupModel o1, FriendGroupModel o2) { 
	        return Collator.getInstance(Locale.CHINESE).compare(o1.getGroupName(), o2.getGroupName()); 
	    } 
	} 

}
