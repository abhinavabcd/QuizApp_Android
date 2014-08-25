package com.amcolabs.quizapp.serverutils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.amcolabs.quizapp.QuizApp;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.appcontrollers.ProgressiveQuizController;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.databaseutils.Category;
import com.amcolabs.quizapp.databaseutils.Quiz;
import com.amcolabs.quizapp.databaseutils.UserChallenege;
import com.amcolabs.quizapp.databaseutils.UserFeed;
import com.amcolabs.quizapp.databaseutils.UserInboxMessage;
import com.amcolabs.quizapp.datalisteners.DataInputListener;
import com.amcolabs.quizapp.popups.StaticPopupDialogBoxes;
import com.amcolabs.quizapp.serverutils.ServerResponse.MessageType;
import com.amcolabs.quizapp.uiutils.UiUtils.UiText;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketException;

public class ServerCalls {

	public static final String SERVER_URL = Config.IS_TEST_BUILD? "http://192.168.0.10:8084/func":"http://quizapp-main.amcolabs.com/func";
	private static final String GET_ENCODEDKEY_URL = SERVER_URL+"?task=getEncodedKey";
	private static final String SET_GCM_KEY_URL = SERVER_URL+"?task=setGCMRegistrationId";
	private static final String GET_USER_INFO =  SERVER_URL+"?task=getUserInfo";
	private static final String GET_ALL_UPDATES =  SERVER_URL+"?task=getAllUpdates";

	private static final String GET_RATING_URL = SERVER_URL+"?task=updateUserRating";

	private static final String GET_LOGIN_WITH_GOOGLPLUS = SERVER_URL+"?task=registerWithGoogle";

	private static final String GET_LOGIN_WITH_FACEBOOK= SERVER_URL+"?task=registerWithFacebook";
	
	
	public String QUIZ_WEBSOCKET_IP_PORT = "ws://192.168.0.10:8084"; 
	
	private int serverErrorMsgShownCount =0;
	//encodedKey=YWJjZGVmZ2h8YWJoaW5hdmFiY2RAZ21haWwuY29t|1393389556|37287ef4a1261b927e8a98d639035d81f0e7eb2c
	public AsyncHttpClient client = null;
	private  SyncHttpClient sClinet;

	private QuizApp quizApp;
	//public static DatabaseHelper dbhelper = Config.getDbhelper();
	public ServerCalls(QuizApp quizApp){
		this.quizApp = quizApp;
		client  = new AsyncHttpClient();
		client.setMaxRetriesAndTimeout(3, 10);
		sClinet = new SyncHttpClient();
	}
	
	
	public  HashMap<String,String> decodeConfigVariables(ServerResponse response){
		HashMap<String,String> map = decodeConfigVariables(response.payload1);
		if(map.containsKey(Config.PREF_SERVER_TIME)){
			quizApp.getConfig().setServerTime(Double.parseDouble(map.get(Config.PREF_SERVER_TIME)) , response.getResponseTime());
		}
		return map;
	}

	
	public HashMap<String, String> decodeConfigVariables(String fromRawJson){
		HashMap<String,String> map = null;
		if(fromRawJson==null || fromRawJson.equalsIgnoreCase(""))
			return null;
		try{ 
			map = quizApp.getConfig().getGson().fromJson(fromRawJson , new TypeToken<HashMap<String,String>>(){}.getType());
		}
		catch(IllegalStateException e){
			e.printStackTrace();
			return null;
		}
		if(map.containsKey(Config.FORCE_APP_VERSION)){ 
			try{
				Context context = quizApp.getContext();
				PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				int versionCode = pInfo.versionCode;
				if(versionCode < Integer.parseInt(map.get(Config.FORCE_APP_VERSION))){
					quizApp.addUiBlock("Please Upgrade immutable");
				}
			}
			catch(Exception e){
				
			}
		}

		return map;
	}

	
	 
	public void handleResponseCodes(MessageType code, ServerResponse response){
		if(code==null){
			return;
		}
		switch(code){
			case FAILED:
				if(serverErrorMsgShownCount++%4==0)
					StaticPopupDialogBoxes.alertPrompt(quizApp.getFragmentManager(), UiText.SERVER_ERROR.getValue(), null);
				break;
		default:
			break;
		}
	}
	public void makeServerCall(final String url,final ServerNotifier serverNotifier,final boolean blockUi){
	      makeServerCall(url,serverNotifier,blockUi , false);
	}
	public void makeServerCall(final String url,final ServerNotifier serverNotifier,final boolean blockUi , boolean sync){
				final long nano1 = Config.getCurrentNanos();
				AsyncHttpClient c = sync ? sClinet : client;
				c.get(url, new AsyncHttpResponseHandler() {
						int retryCount = 0;
							@Override
						 public void onStart() {
							if(blockUi){
								quizApp.addUiBlock();
									//open a loadingDialogue
								}
						}
						@Override
						public void onSuccess(int arg0, Header[] arg1, byte[] responseBytes) {
							String response = new String(responseBytes);
						    ServerResponse serverResponse= quizApp.getConfig().getGson().fromJson(response, ServerResponse.class);
						    serverResponse.setResponseTime(Config.getCurrentNanos() - nano1);
						    MessageType messageType = serverResponse.getStatusCode();
						    handleResponseCodes(messageType, serverResponse);
						    serverNotifier.onServerResponse(messageType , serverResponse);
						}
						public void  onFailure(int messageType, org.apache.http.Header[] headers, byte[] responseBody, Throwable error){
							
							serverNotifier.onServerResponse(MessageType.FAILED , null);
							if(this.retryCount++<Config.RETRY_URL_COUNT)
								client.get(url , this); // retry Once More
							else
								handleResponseCodes(MessageType.FAILED,null);
						} 
						 @Override
						public void onFinish() {
						    	if(blockUi){ 
						    		//loadingDialogue.dismiss();
						    		quizApp.getUiUtils().removeUiBlock();
						    	}
						} 
			});
	}

	public  void makeGeneralServerPostCall(String url, Map<String,String> postData ,
			final HashMap<String,String> headers,
			final DataInputListener<String> listener,final boolean blockUi){
			SyncHttpClient client = new SyncHttpClient();
			if(headers!=null || headers.size()>0){
				for(String key: headers.keySet())
					client.addHeader(key, headers.get(key));
			}
			client.post(url, new RequestParams(postData), new AsyncHttpResponseHandler() {
				@Override
				 public void onStart() {
					if(blockUi){
						quizApp.addUiBlock();
						
						}
				}
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] responseBytes) {
					String response = new String(responseBytes);					
				    listener.onData(response);
				}
				public void  onFailure(int messageType, org.apache.http.Header[] headers, byte[] responseBody, Throwable error){
					listener.onData(null);
				}
				 @Override
				public void onFinish() {
				    	if(blockUi){
				    		quizApp.removeUiBlock();							
				    	}
				}  
				}
		);
	}
		
	public  void makeServerPostCall(String url, Map<String,String> postData ,final ServerNotifier serverNotifier,final boolean blockUi){

		final long nano1 = Config.getCurrentNanos();
		client.post(url, new RequestParams(postData), new AsyncHttpResponseHandler() {
				@Override
				 public void onStart() {
					if(blockUi){
						quizApp.addUiBlock();
						
						}
				}
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] responseBytes) {
					String response = new String(responseBytes);					
				    ServerResponse serverResponse= quizApp.getConfig().getGson().fromJson(response, ServerResponse.class);
				    MessageType messageType = serverResponse.getStatusCode();
				    serverResponse.setResponseTime(Config.getCurrentNanos() - nano1);
				    
				    handleResponseCodes(messageType , serverResponse);
				    serverNotifier.onServerResponse(messageType , serverResponse);
				}
				public void  onFailure(int messageType, org.apache.http.Header[] headers, byte[] responseBody, Throwable error){
					serverNotifier.onServerResponse(MessageType.FAILED , null);
				    handleResponseCodes(MessageType.FAILED,null);

				}
				 @Override
				public void onFinish() {
				    	if(blockUi){
				    		quizApp.removeUiBlock();							
				    	}
				}  
				}
		);
	}

	public static String mapToJson(Map <String,String> map){
		String ret ="{";
		for(Object k : map.keySet()){
			ret+=(k+":"+map.get(k)+",");
		}
		return ret.substring(0,ret.length()-1)+"}";
	}
	
	
	public void makeServerCall(String url, ServerNotifier serverNotifier){
		makeServerCall(url,serverNotifier,false);
	}	
	
	public void makeServerPostCall(String url, HashMap<String, String> postData , ServerNotifier serverNotifier){
		makeServerPostCall(url,postData,serverNotifier,false);
	}
	
	public void getEncodedKey(final String deviceId, final String phoneNumber, ServerNotifier serverNotifier) {
		String url = GET_ENCODEDKEY_URL;
		url+="&deviceId="+deviceId+"&phoneNumber="+phoneNumber;
		makeServerCall(url,serverNotifier,true);
	}
	


	public void setUserGCMKey(final Context context, String registrationId, final DataInputListener<Boolean> dataInputListener) {
		String url = SET_GCM_KEY_URL;
		url+="&encodedKey="+quizApp.getUserDeviceManager().getEncodedKey()+"&regId="+registrationId;
		makeServerCall(url,new ServerNotifier() {
			
			@Override
			public void onServerResponse(MessageType messageType, ServerResponse response) {
				switch(messageType){
					case REG_SAVED:
						if(dataInputListener!=null){
							dataInputListener.onData(true);
							GCMRegistrar.setRegisteredOnServer(context, true);
						}
						break;
					case FAILED:
						if(dataInputListener!=null){
							dataInputListener.onData(false);
							//GCMRegistrar.setRegisteredOnServer(context, false);
						}
						break;
				}
			}
		},false); 

		
	}

	public void unsetUserGCMKey(Context context, String registrationId) {
	}
 
	public void getUserInfo(final DataInputListener<String> dataInputListener) {
			String url = GET_USER_INFO;
			url+="&encodedKey="+quizApp.getUserDeviceManager().getEncodedKey();
			makeServerCall(url,new ServerNotifier() {			
			@Override
			public void onServerResponse(MessageType messageType, ServerResponse response) {
				switch(messageType){
					case FAILED: 
						if(dataInputListener!=null){
							dataInputListener.onData(null);
						}
						break;
				}
			}
		},true); 
	}		

	public void updateUserRating(final float rating, final DataInputListener<Boolean> dataInputListener) {
		String url = GET_RATING_URL;
		url+="&rating="+rating;
		url+="&encodedKey="+quizApp.getUserDeviceManager().getEncodedKey();
		makeServerCall(url,new ServerNotifier() {
			@Override
			public void onServerResponse(MessageType messageType,ServerResponse response) {
				switch(messageType){
					case RATING_OK:
						quizApp.getUserDeviceManager().setDoublePreference(Config.PREF_APP_RATING, rating);
						dataInputListener.onData(true);
						break;
				}
			}
			
		});
	}
	

	
	public static void clearAllStaticVariables() {
	}


	public void getAllUpdates(double maxQuizTimestamp ,final DataInputListener<Boolean> onFinishListener) {
		String url = GET_ALL_UPDATES;
		url+="&encodedKey="+quizApp.getUserDeviceManager().getEncodedKey();
		url+="&maxQuizTimestamp="+maxQuizTimestamp;
		makeServerCall(url, new ServerNotifier() {
			@Override
			public void onServerResponse(MessageType messageType, ServerResponse response) {
				switch(messageType){
					case OK_UPDATES:
						List<Category> categories = quizApp.getConfig().getGson().fromJson(response.payload1, new TypeToken<List<Category>>(){}.getType());
						List<Quiz> quizzes = quizApp.getConfig().getGson().fromJson(response.payload, new TypeToken<List<Quiz>>(){}.getType());
						
						for(Quiz q : quizzes){
							try {
								quizApp.getDataBaseHelper().getQuizDao().createOrUpdate(q);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						
						for(Category c : categories){
							try {
								quizApp.getDataBaseHelper().getCategoryDao().createOrUpdate(c);
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						List<UserFeed> userFeeds = quizApp.getConfig().getGson().fromJson(response.payload2, new TypeToken<List<UserFeed>>(){}.getType());
						List<UserInboxMessage> inboxMessages = quizApp.getConfig().getGson().fromJson(response.payload3, new TypeToken<List<UserInboxMessage>>(){}.getType());
						List<UserChallenege> userChalleneges = quizApp.getConfig().getGson().fromJson(response.payload4, new TypeToken<List<UserChallenege>>(){}.getType());
						onFinishListener.onData(true);
						break; 
					default:
						onFinishListener.onData(false);
						break;
				}
			}
		});
	}


	public void checkVerificationStatus(DataInputListener<String> dataInputListener) {
		// TODO Auto-generated method stub
		
	}


	public void doGooglePlusLogin(final User user,final DataInputListener<User> loginListener) {
		String url = GET_LOGIN_WITH_GOOGLPLUS;
		Map<String,String > params = new HashMap<String, String>();
		params.put("userJson",quizApp.getConfig().getGson().toJson(user));
		makeServerPostCall(url, params, new ServerNotifier() {
			@Override
			public void onServerResponse(MessageType messageType, ServerResponse response) {
//				User user = null;
				switch(messageType){
					case GPLUS_USER_SAVED:
//						user = quizApp.getConfig().getGson().fromJson(response.payload,User.class);
						quizApp.getUserDeviceManager().setPreference(Config.PREF_ENCODED_KEY, response.payload);
						loginListener.onData(user);
						break;
					default:
//						 user = quizApp.getConfig().getGson().fromJson(response.payload,User.class);
						loginListener.onData(null);
						break;
				}
			}
		},false);
	}


	public void doFacebookLogin(final User user, final DataInputListener<User> loginListener) {
		String url = GET_LOGIN_WITH_FACEBOOK;
		Map<String,String > params = new HashMap<String, String>();
		params.put("userJson",quizApp.getConfig().getGson().toJson(user));
		makeServerPostCall(url, params, new ServerNotifier() {
			@Override
			public void onServerResponse(MessageType messageType, ServerResponse response) {
				//User user = null;
				switch(messageType){
					case FACEBOOK_USER_SAVED:
						//user = quizApp.getConfig().getGson().fromJson(response.payload,User.class);
						quizApp.getUserDeviceManager().setPreference(Config.PREF_ENCODED_KEY, response.payload);
						loginListener.onData(user);
						break;
					default:
						//user = quizApp.getConfig().getGson().fromJson(response.payload,User.class);
						loginListener.onData(null);
						break;
				}
			}
		},false);
	}
	
	 private void startProgressiveQuiz(final ProgressiveQuizController pController) {
		  final String TAG = "autoBahn::";
		  final WebSocketConnection mConnection = new WebSocketConnection();
		  final String wsuri = QUIZ_WEBSOCKET_IP_PORT +"/progressiveQuiz";

	      try {
	         mConnection.connect(wsuri, new WebSocketConnectionHandler() {

	            @Override
	            public void onOpen() {
	               Log.d(TAG, "Status: Connected to " + wsuri);
//	               mConnection.sendTextMessage("Hello, world!");
	               pController.startSocketConnection(mConnection);
	            } 

	            @Override
	            public void onTextMessage(String data) {
	            	ServerResponse s = quizApp.getConfig().getGson().fromJson(data, ServerResponse.class);
	            	MessageType messageType = s.getMessageType();
	            	pController.onMessageRecieved(messageType , s , data);
	            }

	            @Override
	            public void onClose(int code, String reason) {
	               Log.d(TAG, "Connection lost.");
	            }
	         });
	      } catch (WebSocketException e) {

	         Log.d(TAG, e.toString());
	      }
	   }
}
