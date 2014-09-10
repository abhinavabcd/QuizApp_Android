package com.amcolabs.quizapp.databaseutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.amcolabs.quizapp.Badge;
import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Category.CategoryType;
import com.amcolabs.quizapp.fileandcommonutils.FileHelper;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "quizApp.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 4;
	private static String DATABASE_PATH = "/data/data/com.amcolabs.quizapp/databases/";

	// the DAO object we use to access the Category table
	private Dao<Category, Integer> categoriesDao = null;
	private Dao<Quiz, Integer> quizDao = null;
	private Dao<Badge, Integer> badgesDao = null;
	
	private Dao<UserPreferences, Integer> userPreferencesDao = null;
	
	private RuntimeExceptionDao<Category, Integer> categoriesRuntimeExceptionDao = null;
	private RuntimeExceptionDao<Quiz, Integer> quizRuntimeExceptionDao = null;
	private RuntimeExceptionDao<Badge, Integer> badgesExceptionDao = null;
	private RuntimeExceptionDao<UserPreferences, Integer> userPreferencesRuntimeDao;
	

	QuizApp quizApp = null;
    public DatabaseHelper(QuizApp quizApp) {
    	super(quizApp.getContext(), DATABASE_PATH+DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    	this.quizApp = quizApp;
//    	if(Config.IS_TEST_BUILD)
//    		FileHelper.deleteFile("databases", DATABASE_NAME);//delete the existing db file
    	
        boolean dbexist = true;//FileHelper.isFileExists(quizApp.getContext() , "databases" , DATABASE_NAME); 
        if (!dbexist || (quizApp!=null && quizApp.getUserDeviceManager().hasJustInstalled)) {
            try {
                InputStream myinput = quizApp.getContext().getAssets().open(DATABASE_NAME);
                String outfilename = DATABASE_PATH + DATABASE_NAME;
                Log.i(DatabaseHelper.class.getName(), "DB Path : " + outfilename);
                File newDB = new File(DATABASE_PATH);
                if (!newDB.exists()) {
                	if(newDB.mkdirs()){
                		
                	}
                	else{
                		Log.d("Database","error making directories"); //:TODO
                	}
                }
                
                OutputStream myoutput = new FileOutputStream(DATABASE_PATH+DATABASE_NAME);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = myinput.read(buffer)) > 0) {
                    myoutput.write(buffer, 0, length);
                }
                myoutput.flush();
                myoutput.close();
                myinput.close();            
            } catch (IOException e) {
                e.printStackTrace();
            }
        }        
    }

    /*
    * Check whether or not database exist
    */	
    public SQLiteDatabase openDB() throws SQLException {

        SQLiteDatabase mDb = this.getWritableDatabase();
        return mDb;
    }
    
    public List<Category> getCategories(int quantity){
    		try {
				return getCategoryDao().queryBuilder().limit((long)quantity).orderBy("modifiedTimestamp", false).query();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return new ArrayList<Category>();
    }

    public List<Category> getAllCategories(){
    		try {
				return getCategoryDao().queryBuilder().orderBy("modifiedTimestamp", false).query();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return new ArrayList<Category>();
    }
    
    public Quiz getQuizById(String quizId){
		try {
			return getQuizDao().queryBuilder().where().eq("quizId", quizId).queryForFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    public List<Quiz> getAllQuizzes(long q , double fromTimeStamp){
		try {
			if(fromTimeStamp>0){
				return getQuizDao().queryBuilder().orderBy("userXp", false).limit(q).where().gt("modifiedTimestamp", fromTimeStamp).query();
			}
			return getQuizDao().queryBuilder().orderBy("userXp", false).limit(q).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    
    
    public double getServerTimeDiffFromDB(){
    	RuntimeExceptionDao<UserPreferences, Integer> userPreferencesTable = getUserPreferencesExceptionDao();
    	List<UserPreferences> serverTimeDiff = userPreferencesTable.queryForEq("property", Config.PREF_SERVER_TIME_DIFF);
    	return Double.parseDouble(serverTimeDiff==null||serverTimeDiff.size()==0 ? "0" :serverTimeDiff.get(0).getData());
    }
   
	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, Category.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, Quiz.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, UserPreferences.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		// Not Needed as of now - should be used in future
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Quiz.class, true);
			TableUtils.dropTable(connectionSource, Category.class, true);
			TableUtils.dropTable(connectionSource, UserPreferences.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our Category class. It will create it or just give the cached
	 * value.
	 */
	public Dao<Category, Integer> getCategoryDao() throws SQLException {
		if (categoriesDao == null) {
			categoriesDao = getDao(Category.class);
		}
		return categoriesDao;
	}
	
	public Dao<Quiz, Integer> getQuizDao() throws SQLException {
		if (quizDao == null) {
			quizDao = getDao(Quiz.class);
		}
		return quizDao;
	}
	
	public Dao<Badge, Integer> getBadgesDao() throws SQLException {
		if (badgesDao == null) {
			badgesDao = getDao(Badge.class); 
		}
		return badgesDao;
	}


	public Dao<UserPreferences, Integer> getUserPreferencesDao() throws SQLException {
		if (userPreferencesDao == null) {
			userPreferencesDao = getDao(UserPreferences.class);
		}
		return userPreferencesDao;
	}
	
	
	
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our Category class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Category, Integer> getCategoryExceptionDao() {
		if (categoriesRuntimeExceptionDao == null) {
			categoriesRuntimeExceptionDao = getRuntimeExceptionDao(Category.class);
		}
		return categoriesRuntimeExceptionDao;
	}
	
	public RuntimeExceptionDao<Quiz, Integer> getQuizDataExceptionDao() {
		if (quizRuntimeExceptionDao == null) {
			quizRuntimeExceptionDao = getRuntimeExceptionDao(Quiz.class);
		}
		return quizRuntimeExceptionDao;
	}

	public RuntimeExceptionDao<Badge, Integer> getBadgesExceptionDao() {
		if (badgesExceptionDao == null) {
			badgesExceptionDao = getRuntimeExceptionDao(Badge.class);
		}
		return badgesExceptionDao;
	}
	
	public RuntimeExceptionDao<UserPreferences, Integer> getUserPreferencesExceptionDao() {
		// TODO Auto-generated method stub
		if (userPreferencesRuntimeDao == null) {
			userPreferencesRuntimeDao = getRuntimeExceptionDao(UserPreferences.class);
		}
		return userPreferencesRuntimeDao;
	}
	

    private static final AtomicInteger usageCounter = new AtomicInteger(0);
    private static DatabaseHelper helper = null;
    
    public static synchronized DatabaseHelper getHelper(QuizApp quizApp) {
        if (helper == null) {
            helper = new DatabaseHelper(quizApp);
            usageCounter.set(0);
        }
        usageCounter.incrementAndGet();
        return helper;
    }
    
    /**
     * To get Maximum of modifiedTimeStamp values in Quiz Table 
     * Returns value if successful else -1
     */
    public double getMaxTimeStampQuiz(){
    	double max = -1;
		try {
			max = (Double) getQuizDao().queryRaw("select max(modifiedTimeStamp) from Quiz",new DataType[]{DataType.DOUBLE}).closeableIterator().next()[0];
		} catch (SQLException e){
			e.printStackTrace();
		}
		return max;
    }
	
    public double getMaxTimeStampBadges(){
    	double max = -1;
		try {
			max = (Double) getBadgesDao().queryRaw("select max(modifiedTimestamp) from badge",new DataType[]{DataType.DOUBLE}).closeableIterator().next()[0];
		} catch (SQLException e){
			e.printStackTrace();
		}
		return max;
    }
    
    /**
     * To get Maximum of modifiedTimeStamp values in Category Table 
     * Returns value if successful else -1
     */
    public double getMaxTimeStampCategory(){
    	double max = -1;
		try {
			max = (Double) getCategoryDao().queryRaw("select max(modifiedTimeStamp) from Category",new DataType[]{DataType.DOUBLE}).closeableIterator().next()[0];
		} catch (SQLException e){
			e.printStackTrace();
		}
		return max;
	}
    
    /**
     * To Create or update a category by its ID
     * Returns true successful else false
     */
    public boolean createOrUpdateCategory(Category cat){
    	CreateOrUpdateStatus tmp=null;
    	try {
			tmp = getCategoryDao().createOrUpdate(cat);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmp!=null?true:false; //tmp.isCreated()
    }
    
    /**
     * To Create or update a Quiz by its ID
     * Returns true successful else false
     */
    public boolean createOrUpdateQuiz(Quiz qz){
    	CreateOrUpdateStatus tmp=null;
    	try {
			tmp = getQuizDao().createOrUpdate(qz);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmp!=null?true:false;
    }
}