package com.amcolabs.quizapp;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.widget.MediaController;
import android.widget.Toast;

//---------------------------------------------------------------------------------------
public class MusicService extends Service 
	implements
		MediaPlayer.OnErrorListener,
		
		// Interface used by the visual representation of the player controls.
		MediaController.MediaPlayerControl,
		
		// implemented to possibly upgrade the media player interface.
		MediaPlayer.OnBufferingUpdateListener
	
	{
	// Connect service who to call onBind
    private final IBinder mBinder = new ServiceBinder();
    
    // Instance of media player
    MediaPlayer mPlayer;
    private static int FADE_IN_DURATION = 3000;
    // Saves the data buffer of the media player (used by MediaController).
    private int mBuffer = 0;

	private int musicId = R.raw.app_music;
    
    public MusicService() { }
    
    // Called by the interface ServiceConnected when calling the service
    public class ServiceBinder extends Binder {
     	 MusicService getService()
    	 {
    		return MusicService.this;
    	 }
    }

    @Override
    public IBinder onBind(Intent context)
    {
    	return mBinder;
    }

    @Override
    public void onCreate () {
    	super.onCreate();
    	
    	create();
	}

    @Override
	public int onStartCommand (Intent intent, int flags, int startId) {
         return START_STICKY;
	}

    public void destroy(){
		if(mPlayer != null)
		{
			try{
				 mPlayer.stop();
				 mPlayer.release();
			}finally {
				mPlayer = null;
			}
		}

    }
	@Override
	public void onDestroy () {
		super.onDestroy();
		destroy();
	}

	public boolean onError(MediaPlayer mp, int what, int extra) {
		Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
		
		if(mPlayer != null)
		{
			try{
				mPlayer.stop();
				mPlayer.release();
			}finally {
				mPlayer = null;
			}
		}
		return false;
	}
	
	public void create(){
    	mPlayer = MediaPlayer.create(this, musicId);
    	mPlayer.setLooping(true);

    	mPlayer.setOnErrorListener(this);

    	mPlayer.setOnErrorListener(new OnErrorListener() {
    		public boolean onError(MediaPlayer mp, int what, int extra) {
    			onError(mPlayer, what, extra);
    			return true;
    		}
    	});
	}
	
	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		return mBuffer;
	}

	@Override
	public int getCurrentPosition() {
		return mPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		return mPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		return mPlayer.isPlaying();
	}

	@Override
	public void pause() {
		if(mPlayer != null && isPlaying())
		{
			mPlayer.pause();
		}
	}

	@Override
	public void seekTo(int pos) {
		mPlayer.seekTo(pos);
	}
	
	public void resume()
	{
		if(isPlaying() == false)
		{
			seekTo(getCurrentPosition());
			play(FADE_IN_DURATION);
		}
	}
	
	@Override
	public void start() {
		if (mPlayer == null) create();
		
		Thread th = new Thread(new Runnable() {
			@Override
			public void run()
			{
				mPlayer.start();
			}
		});
		th.start();
	}
	
	public void stop() {
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		mBuffer = percent;
	}

	public void playAnother(int musicId) {
		if(musicId< 0 || musicId==this.musicId){
			if(!mPlayer.isPlaying()) play(FADE_IN_DURATION);
			return;
		}
		destroy();
		this.musicId = musicId;
		create();
		// start new 
		Thread th = new Thread(new Runnable() {
			@Override
			public void run(){
				play(FADE_IN_DURATION);
			}
		});
		th.start();
	}
	
	private int iVolume;

	private final static int INT_VOLUME_MAX = 100;
	private final static int INT_VOLUME_MIN = 0;
	private final static float FLOAT_VOLUME_MAX = 1;
	private final static float FLOAT_VOLUME_MIN = 0;
	
	public void play(int fadeDuration){
	    //Set current volume, depending on fade or not
	    if (fadeDuration > 0) 
	        iVolume = INT_VOLUME_MIN;
	    else 
	        iVolume = INT_VOLUME_MAX;

	    updateVolume(0);

	    //Play music
	    if(!mPlayer.isPlaying()) mPlayer.start();

	    //Start increasing volume in increments
	    if(fadeDuration > 0)
	    {
	        final Timer timer = new Timer(true);
	        TimerTask timerTask = new TimerTask() 
	        {
	            @Override
	            public void run() 
	            {
	                updateVolume(1);
	                
	                if (iVolume >= (musicId == R.raw.app_music ? INT_VOLUME_MAX:(INT_VOLUME_MAX*2)/3))
	                {
	                    timer.cancel();
	                    timer.purge();
	                }
	            }
	        };

	        // calculate delay, cannot be zero, set to 1 if zero
	        int delay = fadeDuration/INT_VOLUME_MAX;
	        if (delay == 0) delay = 1;

	        timer.schedule(timerTask, delay, delay);
	    }
	}

	public void pause(int fadeDuration)
	{
	    //Set current volume, depending on fade or not
	    if (fadeDuration > 0) 
	        iVolume = INT_VOLUME_MAX;
	    else 
	        iVolume = INT_VOLUME_MIN;

	    updateVolume(0);

	    //Start increasing volume in increments
	    if(fadeDuration > 0)
	    {
	        final Timer timer = new Timer(true);
	        TimerTask timerTask = new TimerTask() 
	        {
	            @Override
	            public void run() 
	            {   
	                updateVolume(-1);
	                if (iVolume == INT_VOLUME_MIN)
	                {
	                    //Pause music
	                    if (mPlayer.isPlaying()) mPlayer.pause();
	                    timer.cancel();
	                    timer.purge();
	                }
	            }
	        };

	        // calculate delay, cannot be zero, set to 1 if zero
	        int delay = fadeDuration/INT_VOLUME_MAX;
	        if (delay == 0) delay = 1;

	        timer.schedule(timerTask, delay, delay);
	    }           
	}

	private void updateVolume(int change)
	{
	    //increment or decrement depending on type of fade
	    iVolume = iVolume + change;

	    //ensure iVolume within boundaries
	    if (iVolume < INT_VOLUME_MIN)
	        iVolume = INT_VOLUME_MIN;
	    else if (iVolume > INT_VOLUME_MAX)
	        iVolume = INT_VOLUME_MAX;

	    //convert to float value
	    float fVolume = 1 - ((float) Math.log(INT_VOLUME_MAX - iVolume) / (float) Math.log(INT_VOLUME_MAX));

	    //ensure fVolume within boundaries
	    if (fVolume < FLOAT_VOLUME_MIN)
	        fVolume = FLOAT_VOLUME_MIN;
	    else if (fVolume > FLOAT_VOLUME_MAX)
	        fVolume = FLOAT_VOLUME_MAX;     

	    mPlayer.setVolume(fVolume, fVolume);
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}