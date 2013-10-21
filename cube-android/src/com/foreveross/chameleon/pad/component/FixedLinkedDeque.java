package com.foreveross.chameleon.pad.component;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 
 */

/**
 * [非线程安全]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [Testj, 2013-9-3]
 */
public final class FixedLinkedDeque<T>  {
	private int fixedSize = 0;
	private List<T> datas;

	public FixedLinkedDeque(List<T> datas, int fixedSize) {
		this.datas = datas;
		if(datas.size()>fixedSize){
			datas.retainAll(datas.subList(0, fixedSize-1));
		}
		this.fixedSize = fixedSize;
	}


	public boolean isEmpty() {
		return datas.isEmpty();
	}

	
	public boolean contains(Object o) {
		return datas.contains(o);
	}

	public int size() {
		return datas.size();
	}

	public Iterator<T> iterator() {
		return datas.iterator();
	}

	public boolean containsAll(Collection<?> c) {
		return datas.containsAll(c);
	}

	public ListIterator<T> listIterator() {
		return datas.listIterator();
	}

	public void clear() {
		datas.clear();
	}

	public T get(int index) {
		return datas.get(index);
	}

	public T set(int index, T element) {
		return datas.set(index, element);
	}

	public ListIterator<T> listIterator(int index) {
		return datas.listIterator(index);
	}

	

	public Object[] toArray() {
		return datas.toArray();
	}


	public int lastIndexOf(Object o) {
		return datas.lastIndexOf(o);
	}



	public boolean equals(Object o) {
		return datas.equals(o);
	}


	public int hashCode() {
		return datas.hashCode();
	}

	
	public int indexOf(Object o) {
		return datas.indexOf(o);
	}

	public String toString() {
		return datas.toString();
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return datas.subList(fromIndex, toIndex);
	}

	public T remove(int index) {
		return datas.remove(index);
	}

	public boolean remove(Object o) {
		return datas.remove(o);
	}

	

	public boolean removeAll(Collection<?> c) {
		return datas.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return datas.retainAll(c);
	}

	/*****************************************************************************
	 * 
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-9-3 下午7:01:45
	 *******************************************************************************/


	public boolean add(T e) {
		if (datas.size() == fixedSize) {
			datas.remove(0);
		}
		return datas.add(e);
	}

	public void add(int index, T element) {
		if (datas.size() == fixedSize) {
			if (index > fixedSize / 2) {
				datas.remove(0);
			} else {
				datas.remove(datas.size()-1);
			}
			datas.remove(0);
		}
		datas.add(index, element);
	}

	
	public boolean addAll(Collection<? extends T> c) {
		int deleteSize = datas.size() + c.size() - fixedSize;
		if (deleteSize > 0) {
			for (int i = 0; i < deleteSize; i++) {
				datas.remove(datas.size()-1);
			}
		}
		return datas.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends T> c) {
		int deleteSize = datas.size() + c.size() - fixedSize;
		if (deleteSize > 0) {
			if (index > fixedSize / 2) {
				for (int i = 0; i < deleteSize; i++) {
					datas.remove(0);
				}
			} else {
				for (int i = 0; i < deleteSize; i++) {
					datas.remove(datas.size()-1);
				}
			}
		}
		return datas.addAll(index, c);
	}

}
