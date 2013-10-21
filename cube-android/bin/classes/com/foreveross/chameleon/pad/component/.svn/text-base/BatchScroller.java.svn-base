/**
 * 
 */
package com.foreveross.chameleon.pad.component;

import java.util.List;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.foreveross.chameleon.store.model.IDObject;
import com.foreveross.chameleon.util.Pool;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [CubeAndroid, 2013-9-3]
 */
public abstract class BatchScroller<ID, T extends IDObject<ID>> implements
		OnScrollListener {

	/**
	 * [上或下页是可视范围的多少倍?]
	 */
	private int visible_count_times = 2;
	private FixedLinkedDeque<T> datas;;
	private NotifyListener notifyListener;

	public interface NotifyListener {
		public void notifyChanged();
	}

	public BatchScroller(List<T> wrappedData,int fixedSize,
			NotifyListener notifyListener) {
		this.datas = new FixedLinkedDeque<T>(wrappedData, fixedSize);
		this.notifyListener = notifyListener;
	}

	public BatchScroller(List<T> wrappedData,int fixedSize, int visible_count_times,
			NotifyListener notifyListener) {
		this.datas = new FixedLinkedDeque<T>(wrappedData, fixedSize);
		this.visible_count_times = visible_count_times;
		this.notifyListener = notifyListener;
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param view
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 * @param totalItemCount
	 *            2013-9-3 下午7:19:39
	 */

	private int previousFirstVisibleItem = 0;

	@Override
	public void onScroll(final AbsListView view, int firstVisibleItem,
			final int visibleItemCount, final int totalItemCount) {
		if (firstVisibleItem > previousFirstVisibleItem) {
			if (canloadNext(firstVisibleItem, visibleItemCount)) {
				Pool.getPool().execute(new Runnable() {

					@Override
					public void run() {
						T t = datas.get(totalItemCount - 1);
						datas.addAll(loadNext(t.getMyId(), visible_count_times
								* visibleItemCount));
						view.post(new Runnable() {

							@Override
							public void run() {
								notifyListener.notifyChanged();
							}
						});
					}
				});

			}
		} else {
			if (canloadPrevious(firstVisibleItem, visibleItemCount)) {
				Pool.getPool().execute(new Runnable() {

					@Override
					public void run() {
						T t = datas.get(0);
						datas.addAll(loadPrevious(t.getMyId(),
								visible_count_times * visibleItemCount));
						view.post(new Runnable() {

							@Override
							public void run() {
								notifyListener.notifyChanged();
							}
						});
					}
				});

			}
		}
		previousFirstVisibleItem = firstVisibleItem;

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param view
	 * @param scrollState
	 *            2013-9-3 下午7:19:39
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	private boolean canloadPrevious(int firstVisibleItem, int visibleItemCount) {
		return firstVisibleItem == visible_count_times * visibleItemCount;
	}

	private boolean canloadNext(int firstVisibleItem, int visibleItemCount) {
		return firstVisibleItem == (visible_count_times + 1) * visibleItemCount;
	}

	public abstract List<T> loadNext(ID topID, int howmany2load);

	public abstract List<T> loadPrevious(ID bottomID, int howmany2load);

}
