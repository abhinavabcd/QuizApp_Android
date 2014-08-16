package com.amcolabs.quizapp.fileandcommonutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

import com.amcolabs.quizapp.QuizApp;


public class FileHelper {

	private QuizApp quizApp;
	public FileHelper(QuizApp quizApp){
		this.quizApp = quizApp;
	}
	public boolean isFileExistsInFiles(String relativePath) {
		File f = new File(quizApp.getFilesDir() , relativePath);
		return f.exists();
	}
	
	
	public boolean isFileExists(String path) {
		File f = new File(quizApp.getFilesDir().getParentFile().getPath()+"/"+path);
		return f.exists();
	}

	public static boolean isFileExists(Context context, String folder , String filePath) {
		File f = new File(context.getFilesDir().getParentFile().getPath()+"/"+folder+"/"+filePath);
		return f.exists();
	}
	

	
	public static void createFile(Context context , String relativePath , String data){
		try{
			File dFile = new File(context.getFilesDir() ,relativePath);
			if (!dFile.exists()) {
				if(dFile.getParentFile().mkdirs()){
					System.out.println("folders created");
				}
				dFile.createNewFile();
			}
			FileWriter out = new FileWriter(dFile);
			out.write(data);
			out.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}


	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}
	public static File getFile(Context context , String path){
		File f = new File(context.getFilesDir(), path);
		return f;
	}
	public static String getStringFromFile(Context context , String fileName) {
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(new File(context.getFilesDir(), fileName)));
			while ((line = in.readLine()) != null) stringBuilder.append(line);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} 

		return stringBuilder.toString();
	}
	private static String getAbsolutePath(Context context, String filePath) {
		 //TODO Auto-generated method stub
		if(filePath.startsWith(context.getFilesDir().getAbsolutePath())){
			return filePath;
		}
		return context.getFilesDir().getAbsolutePath()+"/"+filePath;
	}

	public static boolean isFileEmpty(Context context ,String path) {
		File f = new File(context.getFilesDir() , path);
		boolean ret = false;
		try {
			if(!f.exists()){
				ret= true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return ret;

	}
	public static boolean deleteFileFromFiles(Context context, String file) {
		File f = new File(context.getFilesDir().getPath()+"/"+file); 
		return f.delete();
	}
	public static boolean deleteFile(Context context, String folder, String path) {
		File f = new File(context.getFilesDir().getParentFile().getPath()+"/"+folder+"/"+path); 
		return f.delete();
	}
}

