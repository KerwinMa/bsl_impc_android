package com.foreveross.chameleon.store.core;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.BaseDaoEnabled;


public class BaseModel<T,ID> extends BaseDaoEnabled<T,ID> {

	private static final int FAIL=0;
	@Override
	public int create(){
		try {
			return super.create();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return FAIL;
	}

	@Override
	public int delete(){
		try {
			return super.delete();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return FAIL;
	}

	@Override
	public ID extractId(){
		// TODO Auto-generated method stub
		try {
			return super.extractId();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Dao<T, ID> getDao() {
		return super.getDao();
	}

	@Override
	public String objectToString() {
		return super.objectToString();
	}

	@Override
	public boolean objectsEqual(T other){
		try {
			return super.objectsEqual(other);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public int refresh(){
		try {
			return super.refresh();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return FAIL;
	}

	@Override
	public void setDao(Dao<T, ID> dao) {
		super.setDao(dao);
	}

	@Override
	public int update(){
		try {
			return super.update();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int updateId(ID newId){
		try {
			return super.updateId(newId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return FAIL;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
}
