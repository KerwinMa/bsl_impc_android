package com.foreveross.chameleon.store.core;


import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.app.Application;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.GenericRowMapper;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseResults;
import com.j256.ormlite.table.ObjectFactory;

public class ModelFinder {

	private ModelFinder() {

	}

	private MutilDBSupport mutilDBSupport = null;

	public static ModelFinder build(Application aContext) {

		return build(aContext, null);
	}

	public static ModelFinder build(Application aContext, String dbName) {
		return build(aContext, dbName, -1);
	}

	public static ModelFinder build(Application aContext, String dbName,
			int dbVersion) {
		ModelFinder modelFinder = new ModelFinder();
		if (dbVersion != -1) {
			modelFinder.mutilDBSupport = MutilDBSupport.build(aContext, dbName,dbVersion);
		} else if (dbName != null) {
			modelFinder.mutilDBSupport = MutilDBSupport.build(aContext, dbName);
		} else {
			modelFinder.mutilDBSupport = MutilDBSupport.build(aContext);
		}
		return modelFinder;
	}


	@SuppressWarnings("unchecked")
	public <CT> CT callBatchTasks(Callable<CT> callable)
			throws Exception {
		return (CT)mutilDBSupport.getRuntimeDao().callBatchTasks(callable);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> void clearObjectCache(
			Class<T> clazz) {
		mutilDBSupport.getRuntimeDao(clazz).clearObjectCache();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> void closeLastIterator(
			Class<T> clazz) {
		mutilDBSupport.getRuntimeDao(clazz).closeLastIterator();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> CloseableIterator<T> closeableIterator(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).closeableIterator();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> void commit(
			DatabaseConnection arg0, Class<T> clazz) {
		mutilDBSupport.getRuntimeDao(clazz).commit(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> long countOf(Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).countOf();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> long countOf(
			PreparedQuery<T> arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).countOf(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> int create(T t, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).create(t);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> T createIfNotExists(T arg0,
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).createIfNotExists(arg0);
	}

	// public <ID,T extends BaseDaoEnabled<T, ID>> CreateOrUpdateStatus
	// createOrUpdate(T arg0)
	// {
	// return mutilDBSupport.getDao(BaseDaoEnabled.class).createOrUpdate(arg0);
	// }

	public <ID,T extends BaseDaoEnabled<T, ID>> int delete(Collection<T> arg0,
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).delete(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> int delete(
			PreparedDelete<T> arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).delete(arg0);
	}

	@SuppressWarnings("unchecked")
	public <ID,T extends BaseDaoEnabled<T, ID>> int delete(T arg0) {
		return mutilDBSupport.getRuntimeDao(arg0.getClass()).delete(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> DeleteBuilder<T, ID> deleteBuilder(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).deleteBuilder();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> int deleteById(ID arg0,
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).deleteById(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> int deleteIds(
			Collection<ID> arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).deleteIds(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> void endThreadConnection(
			DatabaseConnection arg0, Class<T> clazz) {
		mutilDBSupport.getRuntimeDao(clazz).endThreadConnection(arg0);

	}

	public <ID,T extends BaseDaoEnabled<T, ID>> int executeRaw(String arg0,
			Class<T> clazz, String... arg1) {
		return mutilDBSupport.getRuntimeDao(clazz).executeRaw(arg0, arg1);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> int executeRawNoArgs(
			String arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).executeRawNoArgs(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> ID extractId(T arg0,
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).extractId(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> FieldType findForeignFieldType(
			Class<?> arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).findForeignFieldType(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> ConnectionSource getConnectionSource(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).getConnectionSource();
	}

	public <FT, T> ForeignCollection<FT> getEmptyForeignCollection(String arg0,
			Class<T> clazz) throws SQLException {
		return mutilDBSupport.getDao(clazz).getEmptyForeignCollection(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> ObjectCache getObjectCache(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).getObjectCache();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> RawRowMapper<T> getRawRowMapper(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).getRawRowMapper();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> GenericRowMapper<T> getSelectStarRowMapper(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).getSelectStarRowMapper();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> CloseableWrappedIterable<T> getWrappedIterable(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).getWrappedIterable();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> CloseableWrappedIterable<T> getWrappedIterable(
			PreparedQuery<T> arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).getWrappedIterable(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> boolean idExists(ID arg0,
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).idExists(arg0);
	}

	@Deprecated
	public <ID,T extends BaseDaoEnabled<T, ID>> boolean isAutoCommit(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).isAutoCommit();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> boolean isAutoCommit(
			DatabaseConnection arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).isAutoCommit(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> boolean isTableExists(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).isTableExists();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> boolean isUpdatable(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).isUpdatable();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> CloseableIterator<T> iterator(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).iterator();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> CloseableIterator<T> iterator(
			int arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).iterator(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> CloseableIterator<T> iterator(
			PreparedQuery<T> arg0, int arg1, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).iterator(arg0, arg1);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> CloseableIterator<T> iterator(
			PreparedQuery<T> arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).iterator(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> T mapSelectStarRow(
			DatabaseResults arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).mapSelectStarRow(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> String objectToString(T arg0,
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).objectToString(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> boolean objectsEqual(T arg0,
			T arg1, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).objectsEqual(arg0, arg1);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> List<T> query(
			PreparedQuery<T> pq, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).query(pq);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> QueryBuilder<T, ID> queryBuilder(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).queryBuilder();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> List<T> queryForAll(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).queryForAll();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> List<T> queryForEq(String arg0,
			Object arg1, Class<T> clazz) {

		return mutilDBSupport.getRuntimeDao(clazz).queryForEq(arg0, arg1);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> List<T> queryForFieldValues(
			Map<String, Object> arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).queryForFieldValues(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> List<T> queryForFieldValuesArgs(
			Map<String, Object> arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz)
				.queryForFieldValuesArgs(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> T queryForFirst(
			PreparedQuery<T> arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).queryForFirst(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> T queryForId(ID arg0,
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).queryForId(arg0);
	}

	@SuppressWarnings("unchecked")
	public <ID,T extends BaseDaoEnabled<T, ID>> List<T> queryForMatching(T arg0) {
		return mutilDBSupport.getRuntimeDao(arg0.getClass()).queryForMatching(arg0);
	}

	@SuppressWarnings("unchecked")
	public <ID,T extends BaseDaoEnabled<T, ID>> List<T> queryForMatchingArgs(
			T arg0) {
		return mutilDBSupport.getRuntimeDao(arg0.getClass()).queryForMatchingArgs(arg0);
	}

	@SuppressWarnings("unchecked")
	public <ID,T extends BaseDaoEnabled<T, ID>> T queryForSameId(T arg0) {
			return (T) mutilDBSupport.getRuntimeDao(arg0.getClass()).queryForSameId(arg0);
	}

	@SuppressWarnings("unchecked")
	public <ID,T extends BaseDaoEnabled<T, ID>> GenericRawResults<Object[]> queryRaw(
			String arg0,  DataType[] arg1, String... arg2) {
		return mutilDBSupport.getRuntimeDao().queryRaw(arg0, arg1, arg2);
	}

	@SuppressWarnings("unchecked")
	public <ID,T extends BaseDaoEnabled<T, ID>> GenericRawResults<T> queryRaw(
			String arg0, RawRowMapper<T> arg1, String... arg2) {
		return mutilDBSupport.getRuntimeDao().queryRaw(arg0, arg1, arg2);
	}

	@SuppressWarnings("unchecked")
	public <ID,T extends BaseDaoEnabled<T, ID>> GenericRawResults<String[]> queryRaw(String arg0, String... arg1) {
		return mutilDBSupport.getRuntimeDao().queryRaw(arg0, arg1);
	}

	public long queryRawValue(
			 String arg0, String... arg1) {
		return mutilDBSupport.getRuntimeDao().queryRawValue(arg0, arg1);
	}
	public <ID,T extends BaseDaoEnabled<T, ID>> long queryRawValue(Class<T> clazz,
			 String arg0, String... arg1) {
		return mutilDBSupport.getRuntimeDao(clazz).queryRawValue(arg0, arg1);
	}

//	public <T extends BaseDaoEnabled<?, Long>> int refresh(T arg0) {
//		try {
//			return mutilDBSupport.getDao().refresh(arg0);
//		} catch (SQLException e) {
//		
//			e.printStackTrace();
//		}
//		return 0;
//	}

	public <ID,T extends BaseDaoEnabled<T, ID>> void rollBack(
			DatabaseConnection arg0, Class<T> clazz) {
		mutilDBSupport.getRuntimeDao(clazz).rollBack(arg0);
	}

	@Deprecated
	public <ID,T extends BaseDaoEnabled<T, ID>> void setAutoCommit(boolean arg0,
			Class<T> clazz) {
		mutilDBSupport.getRuntimeDao(clazz).setAutoCommit(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> void setAutoCommit(
			DatabaseConnection arg0, boolean arg1, Class<T> clazz) {
		mutilDBSupport.getRuntimeDao(clazz).setAutoCommit(arg0, arg1);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> void setObjectCache(
			boolean arg0, Class<T> clazz) {
		mutilDBSupport.getRuntimeDao(clazz).setObjectCache(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> void setObjectCache(
			ObjectCache arg0, Class<T> clazz) {
		mutilDBSupport.getRuntimeDao(clazz).setObjectCache(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> void setObjectFactory(
			ObjectFactory<T> arg0, Class<T> clazz) {
		mutilDBSupport.getRuntimeDao(clazz).setObjectFactory(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> DatabaseConnection startThreadConnection(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).startThreadConnection();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> int update(
			PreparedUpdate<T> arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).update(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> int update(T arg0, Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).update(arg0);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> UpdateBuilder<T, ID> updateBuilder(
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).updateBuilder();
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> int updateId(T arg0, ID arg1,
			Class<T> clazz) {
		return mutilDBSupport.getRuntimeDao(clazz).updateId(arg0, arg1);
	}

	public <ID,T extends BaseDaoEnabled<T, ID>> int updateRaw(Class<T> clazz,
			String arg0, String... arg1) {
		return mutilDBSupport.getRuntimeDao(clazz).updateRaw(arg0, arg1);
	}
	@SuppressWarnings("unchecked")
	public synchronized <ID,T extends BaseDaoEnabled<T, ID>>  CreateOrUpdateStatus    createOrUpdate(T data){
		return mutilDBSupport.getRuntimeDao(data.getClass()).createOrUpdate(data);
	}
}
