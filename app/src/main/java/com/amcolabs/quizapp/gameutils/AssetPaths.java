package com.amcolabs.quizapp.gameutils;

/**
 * Created by abhinav on 7/15/15.
 */
public enum AssetPaths {
    MUSIC_ON("game_icons/music_on.png"),
    MUSIC_OFF("game_icons/music_off.png");

    String path;
    AssetPaths(String path){
        this.path = path;
    }
    public String getAssetPath(){
        return this.path;
    }

}
