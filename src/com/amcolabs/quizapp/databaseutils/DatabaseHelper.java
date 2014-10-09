package com.amcolabs.quizapp.databaseutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
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
	private static final int DATABASE_VERSION = 17;
	private static String DATABASE_PATH = "/data/data/com.amcolabs.quizapp/databases/";

	// the DAO object we use to access the Category table
	private Dao<Category, Integer> categoriesDao = null;
	private Dao<Quiz, Integer> quizDao = null;
	private Dao<QuizPlaySummary, Integer> quizSummaryDao = null;
	private Dao<Badge, Integer> badgesDao = null;
	private Dao<ChatList,Integer> chatListDao = null;
	private Dao<UserPreferences, Integer> userPreferencesDao = null;
	private Dao<User, Integer> usersInfo = null;
	private Dao<OfflineChallenge, Integer> offlineChallengeDao = null;
	private Dao<LocalQuizHistory, Integer> quizHistoryDao = null;
	private Dao<GameEvents, Integer> gameEventsDao = null;

	private RuntimeExceptionDao<Category, Integer> categoriesRuntimeExceptionDao = null;
	private RuntimeExceptionDao<Quiz, Integer> quizRuntimeExceptionDao = null;
	private RuntimeExceptionDao<QuizPlaySummary, Integer> quizSummaryRuntimeExceptionDao = null;
	private RuntimeExceptionDao<Badge, Integer> badgesExceptionDao = null;
	private RuntimeExceptionDao<UserPreferences, Integer> userPreferencesRuntimeDao;
	private RuntimeExceptionDao<OfflineChallenge, Integer> offlineChallengeRuntimeExDao = null;
	private RuntimeExceptionDao<LocalQuizHistory, Integer> quizHistoryExceptionDao = null;
	private RuntimeExceptionDao<GameEvents, Integer> gameEventsExceptionDao = null;
	
	/* for each updater
	 * upgrade() , getter , setters for runtime ex and dao s , create tables 
	 */

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
				return getCategoryDao().queryForAll();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return new ArrayList<Category>();
    }
    HashMap<String, Quiz> cachedQuizzes = new HashMap<String, Quiz>();
    public Quiz getQuizById(String quizId){
		try {
			if(cachedQuizzes.containsKey(quizId)){
				return cachedQuizzes.get(quizId);
			}
			
			Quiz quiz = getQuizDao().queryBuilder().where().eq("quizId", quizId).queryForFirst();
			cachedQuizzes.put(quizId , quiz);
			return quiz;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    public List<QuizPlaySummary> getAllQuizSummary(){
		try {
			return getQuizSummaryDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public List<Quiz> getAllQuizzes(long q , double fromTimeStamp){
		try {
			if(fromTimeStamp>0){
				return getQuizDao().queryBuilder().orderBy("modifiedTimestamp", false).limit(q).where().gt("modifiedTimestamp", fromTimeStamp).query();
			}
			return getQuizDao().queryBuilder().orderBy("modifiedTimestamp", false).limit(q).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
	
	/**
	 * Comparator to get quizzes Ordered by XP in Descending order
	 * @author vinay
	 *
	 */
	public class QuizComparator implements Comparator<Quiz>{

		@Override
		public int compare(Quiz lhs, Quiz rhs) {
			if(quizApp.getUser().getPoints(lhs.quizId) > quizApp.getUser().getPoints(rhs.quizId)){
				return -1;
			}
			else{
				return 1;
			}
		}
		
	}
	
	/**
	 * Method to sort QuizList by XP in Descending order
	 * @param quizList
	 * @return sorted list
	 */
	private List<Quiz> orderByXP(List<Quiz> quizList){
		Collections.sort(quizList,new QuizComparator());
		return quizList;
	}
    
    /**
     * Get all Quizzes ordered by given Field. Results will be in descending order.
     * @param orderBy Field by which the result has to be ordered. If null is passed quizzes are ordered by userXp.
     * @return
     */
    public List<Quiz> getAllQuizzes(String orderBy){
    	if(orderBy==null){
    		orderBy = "modifiedTimestamp";
    	}
		try {
			return getQuizDao().queryBuilder().orderBy(orderBy, false).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public List<Quiz> getAllQuizzesOrderedByXP(){
    	return orderByXP(getAllQuizzes(null));
    }
    
    public List<Quiz> getAllQuizzesOrderedByXP(int n){
    	return orderByXP(getAllQuizzes(n,-1));
    }
    
    /**
     * not using currently
     *  TODO: not proper; get top quizzes Ids from userHistory and get corresponding quizzes from db
     * @param n
     * @return
     */
    public List<Quiz> getTopQuizzes(long n){
		try {
			return getQuizDao().queryBuilder().orderBy("modifiedTimestamp", false).limit(n).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public List<Badge> getAllBadges(){
		try {
				return getBadgesDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public List<Badge> getAllPendingBadges(){
		try {
			return getBadgesDao().queryBuilder().where().eq("isPending", true).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public boolean removePendingState(ArrayList<Badge> badges){
    	for(int i=0;i<badges.size();i++){
			try {
				badges.get(i).setPending(false);
				getBadgesDao().createOrUpdate(badges.get(i));
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
    	}
    	return true;
    }
    
    public boolean setPendingState(ArrayList<Badge> unlockedBadges){
    	for(int i=0;i<unlockedBadges.size();i++){
			try {
				unlockedBadges.get(i).setPending(true);
				getBadgesDao().createOrUpdate(unlockedBadges.get(i));
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
    	}
		return true;
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
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, ChatList.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, User.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, Badge.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, QuizPlaySummary.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, OfflineChallenge.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, LocalQuizHistory.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTableIfNotExists(connectionSource, GameEvents.class);
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
			TableUtils.dropTable(connectionSource, ChatList.class, true);
			TableUtils.dropTable(connectionSource, User.class, true);
			TableUtils.dropTable(connectionSource, Badge.class, true);
			TableUtils.dropTable(connectionSource, QuizPlaySummary.class, true);
			TableUtils.dropTable(connectionSource, OfflineChallenge.class, true);
			TableUtils.dropTable(connectionSource, LocalQuizHistory.class, true);
			TableUtils.dropTable(connectionSource, GameEvents.class, true);
			
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
	public Dao<LocalQuizHistory, Integer> getQuizHistoryDao() throws SQLException {
		if (quizHistoryDao == null) {
			quizHistoryDao = getDao(LocalQuizHistory.class);
		}
		return quizHistoryDao;
	}
	
	public Dao<Quiz, Integer> getQuizDao() throws SQLException {
		if (quizDao == null) {
			quizDao = getDao(Quiz.class);
		}
		return quizDao;
	}
	
	public Dao<QuizPlaySummary, Integer> getQuizSummaryDao() throws SQLException {
		if (quizSummaryDao == null) {
			quizSummaryDao = getDao(QuizPlaySummary.class);
		}
		return quizSummaryDao;
	}
	
	public Dao<Badge, Integer> getBadgesDao() throws SQLException {
		if (badgesDao == null) {
			badgesDao = getDao(Badge.class); 
		}
		return badgesDao;
	}

	public Dao<ChatList, Integer> getChatListDao() throws SQLException {
		if (chatListDao == null) {
			chatListDao = getDao(ChatList.class); 
		}
		return chatListDao;
	}
	
	public Dao<User, Integer> getUsersInfoDao() throws SQLException {
		if (usersInfo == null) {
			usersInfo = getDao(User.class); 
		}
		return usersInfo;
	}

	public Dao<GameEvents, Integer> getGameEventsDao() throws SQLException {
		if (gameEventsDao == null) {
			gameEventsDao = getDao(GameEvents.class); 
		}
		return gameEventsDao;
	}
	public Dao<OfflineChallenge, Integer> getOfflineChallengesDao() throws SQLException {
		if (offlineChallengeDao == null) {
			offlineChallengeDao = getDao(OfflineChallenge.class); 
		}
		return offlineChallengeDao;
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
	
	public RuntimeExceptionDao<LocalQuizHistory, Integer> getQuizHistoryExceptionDao() {
		if (quizHistoryExceptionDao == null) {
			quizHistoryExceptionDao = getRuntimeExceptionDao(LocalQuizHistory.class);
		}
		return quizHistoryExceptionDao;
	}
	
	public RuntimeExceptionDao<Quiz, Integer> getQuizDataExceptionDao() {
		if (quizRuntimeExceptionDao == null) {
			quizRuntimeExceptionDao = getRuntimeExceptionDao(Quiz.class);
		}
		return quizRuntimeExceptionDao;
	}
	
	public RuntimeExceptionDao<QuizPlaySummary, Integer> getQuizSummaryDataExceptionDao() {
		if (quizSummaryRuntimeExceptionDao == null) {
			quizSummaryRuntimeExceptionDao = getRuntimeExceptionDao(QuizPlaySummary.class);
		}
		return quizSummaryRuntimeExceptionDao;
	}

	public RuntimeExceptionDao<GameEvents, Integer> getGameEventsExceptionDao() {
		if (gameEventsExceptionDao == null) {
			gameEventsExceptionDao = getRuntimeExceptionDao(GameEvents.class);
		}
		return gameEventsExceptionDao;
	}

	public RuntimeExceptionDao<Badge, Integer> getBadgesExceptionDao() {
		if (badgesExceptionDao == null) {
			badgesExceptionDao = getRuntimeExceptionDao(Badge.class);
		}
		return badgesExceptionDao;
	}
	
	public RuntimeExceptionDao<OfflineChallenge, Integer> getOfflineChallengesExceptionDao() {
		if (offlineChallengeRuntimeExDao == null) {
			offlineChallengeRuntimeExDao = getRuntimeExceptionDao(OfflineChallenge.class);
		}
		return offlineChallengeRuntimeExDao;
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
			max = (Double) getQuizDao().queryRaw("select max(modifiedTimestamp) from Quiz",new DataType[]{DataType.DOUBLE}).closeableIterator().next()[0];
		} catch (SQLException e){
			e.printStackTrace();
		}
		return max;
    }
	
    public double getMaxTimeStampBadges(){
    	double max = -1;
		try {
			max = (Double) getBadgesDao().queryRaw("select max(modifiedTimestamp) from Badge",new DataType[]{DataType.DOUBLE}).closeableIterator().next()[0];
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
			max = (Double) getCategoryDao().queryRaw("select max(modifiedTimestamp) from Category",new DataType[]{DataType.DOUBLE}).closeableIterator().next()[0];
		} catch (SQLException e){
			e.printStackTrace();
		}
		return max;
	}
    
    /**
     * To Create or update a offlineChallengeDao by its ID
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
    
    public QuizPlaySummary getQuizSummaryByQuizId(String quizId){
		try {
			return getQuizSummaryDao().queryBuilder().where().eq("quizId", quizId).queryForFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}
		return null;
    }
    
    public boolean createOrUpdateQuizSummary(QuizPlaySummary qHistory){
    	CreateOrUpdateStatus tmp=null;
    	try {
			tmp = getQuizSummaryDao().createOrUpdate(qHistory);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tmp!=null?true:false;
    }
    
    public void setRecentChat(String uid, String message , boolean unseen){
    	try {
			getChatListDao().createOrUpdate(new ChatList(uid, message,Config.getCurrentTimeStamp() , unseen?0:1)); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public List<ChatList> getUnseenChatList(String uid, String message , boolean unseen){
    	try {
			return getChatListDao().queryBuilder().where().eq("unseenMessagesFlag", 1).query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    public List<ChatList> getAllChatList(){
    	try {
			return getChatListDao().queryBuilder().query();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    
    public void saveUser(User user){
    	try {
			getUsersInfoDao().createOrUpdate(user);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public User getUserByUid(String uid){
		try {
			User u = getUsersInfoDao().queryBuilder().where().eq("uid",uid).queryForFirst();
			return u;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

    }
    public HashMap<String , User> getAllUsersByUid(List<String> uids , final DataInputListener<Boolean> usersListener){
    	ArrayList<String> pendingList = new ArrayList<String>();
    	for(String uid : uids){
    		try {
    			User u = getUsersInfoDao().queryBuilder().where().eq("uid",uid).queryForFirst();
    			if(!quizApp.cachedUsers.containsKey(uid) && u!=null)
    				quizApp.cachedUsers.put( uid , u);
    			else if(quizApp.cachedUsers.containsKey(uid) && u==null){
    				getUsersInfoDao().createOrUpdate(u);
    			}
    			else if(!quizApp.cachedUsers.containsKey(uid)){
    				pendingList.add(uid);
    			}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				pendingList.add(uid);
			}
    	}
    	
    	if(pendingList.size()>0){//synchronized call ?	 
    		quizApp.getServerCalls().getUids(pendingList, new DataInputListener<List<User>>(){
    			@Override
    			public String onData(List<User> s) {
    				for(User user :s){
    					try {
							getUsersInfoDao().createOrUpdate(user);
	        				quizApp.cachedUsers.put(user.uid , user);
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    				}
    				usersListener.onData(true);
    				return null;
    			}
    		}, true); //pending users listener
    	}
    	else{
    		usersListener.onData(true);
    	}
    	return quizApp.cachedUsers;
    }

	public Category getCategoryById(String id) {
		try {
			List<Category> tmp = getCategoryDao().queryBuilder().where().eq("categoryId", id).query();
			if(tmp.size()>0)
				tmp.get(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<OfflineChallenge> getPendingRecentOfflineChallenges(long limit) {
		try {
			if(limit>0)
				return getOfflineChallengesDao().queryBuilder().limit(limit).where().eq("isCompleted", false).query();
			else
				return getOfflineChallengesDao().queryBuilder().where().eq("isCompleted", false).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<OfflineChallenge> getCompleteRecentOfflineChallenges(long limit) {
		try {
			if(limit>0)
				return getOfflineChallengesDao().queryBuilder().limit(limit).where().eq("isCompleted", true).query();
			else
				return getOfflineChallengesDao().queryBuilder().where().eq("isCompleted", true).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

//	public void updateOfflineChallenge(String id, boolean b, boolean hasWon) {
//		try {
//			 List<OfflineChallenge> offlineChallenges = getOfflineChallengesDao().queryBuilder().where().eq("offlineChallengeId", id).query();
//			 if(offlineChallenges!=null && offlineChallenges.size()>0){
//				 OfflineChallenge offlineChallenge = offlineChallenges.get(0);
//				 offlineChallenge.hasWon = hasWon;
//				 offlineChallenge.isCompleted = true;
//				getOfflineChallengesDao().createOrUpdate(offlineChallenge);
//			 }
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public int getLastChallengeIndex() {
		try {
			OfflineChallenge offlineChallenge = getOfflineChallengesDao().queryBuilder().orderBy("toUid_userChallengeIndex", false).queryForFirst();
			if(offlineChallenge!=null){
				return Integer.parseInt(offlineChallenge.toUid_userChallengeIndex.split("_")[1]);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void updateOfflineChallenge(OfflineChallenge offlineChallenge) {
		try {
			offlineChallenge.isCompleted();
			getOfflineChallengesDao().createOrUpdate(offlineChallenge);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public OfflineChallenge getOfflineChallengeByChallengeId(String id) {
		try {
			return getOfflineChallengesDao().queryBuilder().where().eq("offlineChallengeId", id).queryForFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void addGameEvents(GameEvents evt){
		try {
			evt.timestamp = Config.getCurrentTimeStamp();
			getGameEventsDao().create(evt);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<GameEvents> getAllGameEvents(double timestamp){
		try {
			return getGameEventsDao().queryBuilder().limit((long)50).orderBy("timestamp", false).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;		
	}

	public void addToQuizHistory(LocalQuizHistory q) {
		try {
			getQuizHistoryDao().create(q);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Badge getBadgeById(String badgeId) {
		try {
			return getBadgesDao().queryBuilder().where().eq("badgeId", badgeId).queryForFirst();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

