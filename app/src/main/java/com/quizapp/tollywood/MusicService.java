package com.quizapp.tollywood;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.quizapp.tollywood.serverutils.ServerCalls;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

public class MusicService {
	private static final String TAG = "MusicService";

	public static  MusicFiles MUSIC_PREVIOUS = MusicFiles.NONE;

	private static HashMap<MusicFiles, MediaPlayer> players = new HashMap();
	private static MusicFiles currentMusic = MusicFiles.NONE;
	private static MusicFiles previousMusic = MusicFiles.NONE;

	private static float musicVolume = 100;
	private static float maxVolume = 100;
	public static float getMusicVolume(Context context) {
			return musicVolume;
	}

	public enum MusicFiles{
		QUIZ_MUSIC("music/quiz_play.mp3"),
		GAME_MUSIC("music/app_music.mp3"),
		NONE(null);

		String path = null;
		MusicFiles(String path){
			this.path = path;
		}

		public Task<Uri> cache(){
			return Task.callInBackground(new Callable<Uri>() {
				@Override
				public Uri call() throws Exception {
					if(path==null) return null;
					File f = new File(path);
					String directory = f.getParent();
					String fileName = f.getName();
					String root = Environment.getExternalStorageDirectory().getPath();
					File myDir = new File(root + "/"+directory);
					File audioFile = new File(myDir, fileName);
					if(!audioFile.exists()){
						myDir.mkdirs();
						//download and save
						URL url = new URL(ServerCalls.CDN_PATH+path);
						URLConnection connection = url.openConnection();
						InputStream inputStream = connection.getInputStream();
						try { // Make sure the Pictures directory exists.path.mkdirs() ; URL url = new URL ( "http: / /androidsaveitem .appspot.com/download.jpg") ; URLConnection ucon = url.openConnection ( ) ;
							FileOutputStream outputStream = new FileOutputStream(audioFile);

							byte buffer[] = new byte[1024];
							int dataSize;
							while ((dataSize = inputStream.read(buffer)) != -1) {
								outputStream.write(buffer, 0, dataSize);
							}
							outputStream.close();
						}
						catch (Exception ex){
							return null;
						}
						finally {
							inputStream.close();
						}
					}
					return Uri.parse(audioFile.toString());
				};
			});

		}
		public Uri getUri(){
			if(path==null) return null;
			String root = Environment.getExternalStorageDirectory().getPath();
			File f = new File(path);
			String directory = f.getParent();
			String fileName = f.getName();

			File myDir = new File(root + "/"+directory);
			File audioFile = new File(myDir, fileName);
			return Uri.parse(audioFile.toString());

		}

	}

    public static void start(final Context context, final MusicFiles music) {
        start(context, music, false);
    }

    public static void start(final Context context, final MusicFiles music, final boolean force) {
		music.cache().onSuccess(new Continuation<Uri, Void>() {
			@Override
			public Void then(Task<Uri> task) throws Exception {
				startMusic(context, music, force);
				return null;
			}
		}, Task.UI_THREAD_EXECUTOR);

	}

	private static void startMusic(Context context, MusicFiles music, boolean force) {
		if (!force && currentMusic!=MusicFiles.NONE) {
// already playing some music and not forced to change
			return;
		}
		if (music == MUSIC_PREVIOUS) {
			Log.d(TAG, "Using previous music [" + previousMusic + "]");
			music = previousMusic;
		}
		if (currentMusic == music) {
// already playing this music
			return;
		}
		if (currentMusic != MusicFiles.NONE) {
			previousMusic = currentMusic;
			Log.d(TAG, "Previous music was [" + previousMusic + "]");
// playing some other music, pause it and change
			pause();
		}
		currentMusic = music;
		Log.d(TAG, "Current music is now [" + currentMusic + "]");
		MediaPlayer mp = players.get(music);
		if (mp != null) {
			if (!mp.isPlaying()) {
				mp.start();
			}
		} else {
			switch(music){

				case QUIZ_MUSIC:
					mp = MediaPlayer.create(context, music.getUri());
					break;
				case GAME_MUSIC:
					mp = MediaPlayer.create(context, music.getUri());
					break;
				case NONE:
					break;
			}
			players.put(music, mp);
			float volume = getMusicVolume(context);
			Log.d(TAG, "Setting music volume to " + volume);
			mp.setVolume(volume, volume);
			try {
				mp.setLooping(true);
				mp.start();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	public static void pause() {
		Collection<MediaPlayer> mps = players.values();
		for (MediaPlayer p : mps) {
			if (p.isPlaying()) {
				p.pause();
			}
		}
// previousMusic should always be something valid
		if (currentMusic != MusicFiles.NONE) {
			previousMusic = currentMusic;
			Log.d(TAG, "Previous music was [" + previousMusic + "]");
		}
		currentMusic = MusicFiles.NONE;
		Log.d(TAG, "Current music is now [" + currentMusic + "]");
	}

	public static void release() {
		Log.d(TAG, "Releasing media players");
		Collection<MediaPlayer> mps = players.values();
		for (MediaPlayer mp : mps) {
			try {
				if (mp != null) {
					if (mp.isPlaying()) {
						mp.stop();
					}
					mp.release();
				}
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		mps.clear();
		if (currentMusic != MusicFiles.NONE) {
			previousMusic = currentMusic;
			Log.d(TAG, "Previous music was [" + previousMusic + "]");
		}
		currentMusic = MusicFiles.NONE;
		Log.d(TAG, "Current music is now [" + currentMusic + "]");
	}

	public static void setMusicVolume(float volume) {
		musicVolume = (float) (1 - (Math.log(maxVolume - volume) / Math.log(maxVolume)));;
		if(players.get(currentMusic)!=null)
			players.get(currentMusic).setVolume(volume, volume);
	}

	public static void setApplicationMusicVolume(float volume) {
		Log.d(TAG, "Setting volume : "+volume);
		musicVolume = (float) (1 - (Math.log(maxVolume - volume) / Math.log(maxVolume)));
		Collection<MediaPlayer> mps = players.values();
		for (MediaPlayer mp : mps) {
			try {
				if (mp != null)
					mp.setVolume(musicVolume, musicVolume);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	public static void changeMusic(Context context  , MusicFiles music) {
		pause();
		start(context, music, true);
	}
}