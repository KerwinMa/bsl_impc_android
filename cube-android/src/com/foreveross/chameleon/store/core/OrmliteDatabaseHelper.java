package com.foreveross.chameleon.store.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.csair.impc.R;
import com.foreveross.chameleon.push.cubeparser.type.CommonModuleMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessage;
import com.foreveross.chameleon.push.cubeparser.type.NoticeModuleMessageStub;
import com.foreveross.chameleon.push.cubeparser.type.SystemMessage;
import com.foreveross.chameleon.store.model.AutoDownloadRecord;
import com.foreveross.chameleon.store.model.AutoShowViewRecord;
import com.foreveross.chameleon.store.model.ChatDataModel;
import com.foreveross.chameleon.store.model.ConversationMessage;
import com.foreveross.chameleon.store.model.MessageModule;
import com.foreveross.chameleon.store.model.MultiUserInfoModel;
import com.foreveross.chameleon.store.model.SessionModel;
import com.foreveross.chameleon.store.model.SystemInfoModel;
import com.foreveross.chameleon.store.model.UserModel;
import com.foreveross.chameleon.store.model.ViewModuleRecord;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class OrmliteDatabaseHelper extends OrmLiteSqliteOpenHelper {

	public OrmliteDatabaseHelper(Context context, String dbName, int version) {
		super(context, dbName, null, version, R.raw.model_config);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		
		try {
			Log.i(OrmliteDatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, NoticeModuleMessage.class);
			TableUtils.createTable(connectionSource,
					NoticeModuleMessageStub.class);
			TableUtils.createTable(connectionSource, CommonModuleMessage.class);
			TableUtils.createTable(connectionSource, SystemMessage.class);
			TableUtils.createTable(connectionSource, UserModel.class);
			TableUtils.createTable(connectionSource, MessageModule.class);
			TableUtils.createTable(connectionSource, ConversationMessage.class);
			TableUtils.createTable(connectionSource, SessionModel.class);
			TableUtils.createTable(connectionSource, ViewModuleRecord.class);
			TableUtils.createTable(connectionSource, AutoDownloadRecord.class);
			TableUtils.createTable(connectionSource, AutoShowViewRecord.class);
			TableUtils.createTable(connectionSource, MultiUserInfoModel.class);
			TableUtils.createTable(connectionSource, SystemInfoModel.class);
			TableUtils.createTable(connectionSource, ChatDataModel.class);
		} catch (Exception e) {
			Log.e(OrmliteDatabaseHelper.class.getName(),"Can't create database", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			Log.i(OrmliteDatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, NoticeModuleMessage.class,
					true);
			TableUtils.dropTable(connectionSource,
					NoticeModuleMessageStub.class, true);
			TableUtils.dropTable(connectionSource, CommonModuleMessage.class,
					true);
			TableUtils.dropTable(connectionSource, SystemMessage.class, true);
			TableUtils.dropTable(connectionSource, UserModel.class, true);
			TableUtils.dropTable(connectionSource, MessageModule.class, true);
			TableUtils.dropTable(connectionSource, ConversationMessage.class, true);
			TableUtils.dropTable(connectionSource, SessionModel.class, true);
			TableUtils.dropTable(connectionSource, ViewModuleRecord.class, true);
			TableUtils.dropTable(connectionSource, AutoDownloadRecord.class, true);
			TableUtils.dropTable(connectionSource, AutoShowViewRecord.class, true);
			TableUtils.dropTable(connectionSource, MultiUserInfoModel.class, true);
			TableUtils.dropTable(connectionSource, SystemInfoModel.class, true);
			TableUtils.dropTable(connectionSource, ChatDataModel.class, true);
			onCreate(db, connectionSource);
		} catch (Exception e) {
			Log.e(OrmliteDatabaseHelper.class.getName(),
					"Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

}
