/**
 * 
 */
package com.foreveross.chameleon.phone.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

/**
 * @author zhoujun
 *
 */
public class MsgExpanderListAdapter extends BaseExpandableListAdapter
{
	public static final int TYPE_TITLE= 0;
    public static final int TYPE_CONTENT= 1;
    public static final int TYPE_COUNT=TYPE_CONTENT+1;
	private Context context;

	private Map<SectionListElement, List<ContentListElement>> scMap = new HashMap<SectionListElement, List<ContentListElement>>();

    private LayoutInflater layoutInflater;
    
    private List<SectionListElement> sleList;
    
    public MsgExpanderListAdapter(Context context,
			Map<SectionListElement, List<ContentListElement>> scMap)
	{
		super();
		this.context = context;
		this.scMap = scMap;
		this.layoutInflater = (LayoutInflater) context .getSystemService("layout_inflater");
		sleList = new ArrayList<SectionListElement>();
		Iterator<SectionListElement> it = scMap.keySet().iterator();
		while(it.hasNext())
		{
			SectionListElement se = it.next();
			if(se.getText().equals("公告"))
			{
				sleList.add(0, se);
			}
			else if(se.getText().equals("系统")&&sleList.size()>0)
			{
				sleList.add(1, se);
			}
			else if(se.getText().equals("系统")&&sleList.size()==0)
			{
				sleList.add(0, se);
			}
			else
			{
				sleList.add(se);
			}
		}
		
		Collections.sort(sleList,new Comparator<SectionListElement>()
		{

			@Override
			public int compare(SectionListElement lhs, SectionListElement rhs)
			{
				int flag = 0;
				if(lhs.getText().equals("系统") && rhs.getText().equals("公告"))
				{
					flag = 1;
				}
				else if(lhs.getText().equals("公告") && rhs.getText().equals("系统"))
				{
					flag = -1;
				}
				return flag;
			}
		});
	}

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		return scMap.get(sleList.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		return scMap.get(sleList.get(groupPosition)).get(childPosition).getViewForListElement(layoutInflater, context, convertView, parent, TYPE_CONTENT);
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return scMap.get(sleList.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return sleList.get(groupPosition);
	}

	@Override
	public int getGroupCount()
	{
		return sleList.size();
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		return sleList.get(groupPosition).getViewForListElement(layoutInflater, context, convertView, parent, TYPE_TITLE);
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}

	/**
	 * @return the scMap
	 */
	public Map<SectionListElement, List<ContentListElement>> getScMap()
	{
		return scMap;
	}

	/**
	 * @param scMap the scMap to set
	 */
	public void setScMap(Map<SectionListElement, List<ContentListElement>> scMap)
	{
		this.scMap = scMap;
		sleList = new ArrayList<SectionListElement>();
		Iterator<SectionListElement> it = scMap.keySet().iterator();
		while(it.hasNext())
		{
			SectionListElement se = it.next();
			if(se.getText().equals("公告"))
			{
				sleList.add(0, se);
			}
			else if(se.getText().equals("系统")&&sleList.size()>0)
			{
				sleList.add(1, se);
			}
			else if(se.getText().equals("系统")&&sleList.size()==0)
			{
				sleList.add(0, se);
			}
			else
			{
				sleList.add(se);
			}
		}
		
		Collections.sort(sleList,new Comparator<SectionListElement>()
		{

			@Override
			public int compare(SectionListElement lhs, SectionListElement rhs)
			{
				int flag = 0;
				if(lhs.getText().equals("系统") && rhs.getText().equals("公告"))
				{
					flag = 1;
				}
				else if(lhs.getText().equals("公告") && rhs.getText().equals("系统"))
				{
					flag = -1;
				}
				return flag;
			}
		});
	}
	
	
	
//	public boolean isEnabled(int position) {
//		return this.resultList.get(position).isClickable();
//	}

}
