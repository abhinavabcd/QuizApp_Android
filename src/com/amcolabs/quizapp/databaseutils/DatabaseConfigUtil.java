package com.amcolabs.quizapp.databaseutils;

import java.io.IOException;
import java.sql.SQLException;

import com.amcolabs.quizapp.User;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 * DatabaseConfigUtl writes a configuration file to avoid using Annotation processing in runtime. This gains a
 * noticable performance improvement. configuration file is written to /res/raw/ by default.
 * More info at: http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Config-Optimization
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

	private static final Class<?>[] classes = new Class[] {
	    Question.class,
	    Quiz.class,
	    Category.class,
	    UserPreferences.class,
	    UserQuizStats.class,
	    Badge.class,
	    ChatList.class,
	    User.class,
	    OfflineChallenge.class
	    
	  };
	
	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile("ormlite_config.txt",classes);	
		System.out.print("DataBases config rewritten..");
	}
}
