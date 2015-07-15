package com.amcolabs.quizapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.amcolabs.quizapp.configuration.Config;

import java.util.Collection;
import java.util.HashMap;

public class MusicService {
	private static final String TAG = "MusicService";

	public static final int MUSIC_PREVIOUS = -1;
	public static final int MUSIC_GAME = 1;
	public static final int MUSIC_QUIZ = 2;

	private static HashMap<Integer, MediaPlayer> players = new HashMap();
	private static int currentMusic = -1;
	private static int previousMusic = -1;

	private static float musicVolume = 100;
	private static float maxVolume = 100;
	public static float getMusicVolume(Context context) {
			return musicVolume;
	}

	public static void start(Context context, int music) {
		start(context, music, false);
	}

	public static void start(Context context, int music, boolean force) {
		if (!force && currentMusic > -1) {
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
		if (currentMusic != -1) {
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
			if (music == MUSIC_GAME) {
				mp = MediaPlayer.create(context, R.raw.app_music);
			} else if (music == MUSIC_QUIZ) {
				mp = MediaPlayer.create(context, R.raw.quiz_play);
			} else {
				Log.e(TAG, "unsupported music number - " + music);
				return;
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
		if (currentMusic != -1) {
			previousMusic = currentMusic;
			Log.d(TAG, "Previous music was [" + previousMusic + "]");
		}
		currentMusic = -1;
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
		if (currentMusic != -1) {
			previousMusic = currentMusic;
			Log.d(TAG, "Previous music was [" + previousMusic + "]");
		}
		currentMusic = -1;
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

	public static void changeMusic(Context context  , int music) {
		pause();
		start(context, music, true);
	}
}