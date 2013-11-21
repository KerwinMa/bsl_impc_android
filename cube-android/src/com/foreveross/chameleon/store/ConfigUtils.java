package com.foreveross.chameleon.store;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

public class ConfigUtils extends OrmLiteConfigUtil {
	public static void main(String[] args) {
		try {
			writeConfigFile("model_config.txt");

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
