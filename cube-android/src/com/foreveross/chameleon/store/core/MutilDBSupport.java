package com.foreveross.chameleon.store.core;

import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;

import android.app.Application;

import com.foreveross.chameleon.CubeConstants;
import com.foreveross.chameleon.push.mina.library.util.PropertiesUtil;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.misc.BaseDaoEnabled;

public class MutilDBSupport {
	private Application mContext;
	private WeakHashMap<String, OrmliteDatabaseHelper> mHelperMap = new WeakHashMap<String, OrmliteDatabaseHelper>();
	@SuppressWarnings("rawtypes")
	private WeakHashMap mDaoMap = new WeakHashMap();
	private String dbName;
	private int dbVersion;

	public static MutilDBSupport build(Application aContext) {
		String dbName = PropertiesUtil.readProperties(aContext, CubeConstants.CUBE_CONFIG)
				.getString("STORE_DB_NAME", "CUBE");
		return build(aContext, dbName);

	}

	public static MutilDBSupport build(Application aContext, String dbName) {
		int dbVersion = PropertiesUtil.readProperties(aContext, CubeConstants.CUBE_CONFIG)
				.getInteger("STORE_DB_VERSION", 1);
		return build(aContext, dbName, dbVersion);
	}

	public static MutilDBSupport build(Application aContext, String dbName,
			int dbVersion) {
		MutilDBSupport mutilDBSupport = new MutilDBSupport();
		mutilDBSupport.mContext = aContext;
		mutilDBSupport.dbName = dbName;
		mutilDBSupport.dbVersion = dbVersion;

		return mutilDBSupport;
	}

	public <ID, T extends BaseDaoEnabled<T, ID>> RuntimeExceptionDao<T, ID> getRuntimeDao(
			Class<T> clazz) {
		try {
			Dao<T, ID> dao = getOrmliteDatabaseHelper().getDao(clazz);
			return new RuntimeExceptionDao<T, ID>(dao);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RuntimeExceptionDao getRuntimeDao() {
		try {
			Dao dao = getOrmliteDatabaseHelper().getDao(BaseModel.class);

			return new RuntimeExceptionDao(dao);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public <ID, T> Dao<T, ID> getDao(final Class<T> clazz) {
		checkContext();
		String key = dbName + "_" + clazz.getName();
		@SuppressWarnings("unchecked")
		Dao<T, ID> dao = getFromMap(mDaoMap, key,
				new IModelGenerator<Dao<T, ID>>() {

					@Override
					public Dao<T, ID> generate() {
						try {
							return getOrmliteDatabaseHelper().getDao(clazz);
						} catch (SQLException e) {
							e.printStackTrace();
						}
						return null;
					}
				});
		return dao;
	}

	public OrmliteDatabaseHelper getOrmliteDatabaseHelper() {
		checkContext();
		OrmliteDatabaseHelper ormliteDatabaseHelper = getFromMap(mHelperMap,
				dbName, new IModelGenerator<OrmliteDatabaseHelper>() {
					@Override
					public OrmliteDatabaseHelper generate() {
						return new OrmliteDatabaseHelper(mContext, dbName,
								dbVersion);
					}
				});

		return ormliteDatabaseHelper;
	}

	public <T> T getFromMap(Map<String, T> map, String key,
			IModelGenerator<T> generator) {
		T value = map.get(key);
		if (value == null) {
			map.put(key, value = generator.generate());
		}
		return value;
	}

	private void checkContext() {
		if (mContext == null)
			throw new IllegalStateException("Application not register!");
	}

}
