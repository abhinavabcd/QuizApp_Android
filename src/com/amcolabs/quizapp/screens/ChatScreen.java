package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.Random;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.chat.ChatViewAdapter;
import com.amcolabs.quizapp.chat.Message;

/**
 * ChatScreen is a screen which show chat log and enables chatting
 * 
 * @author Vinay Ainavolu
 *
 */

public class ChatScreen extends Screen {

	ArrayList<Message> messages;
	ChatViewAdapter adapter;
	ListView chatView;
	EditText text;
	static Random rand = new Random();	
	static String sender;
	
	public ChatScreen(AppController controller) {
		super(controller);
		View chatLayout = LayoutInflater.from(controller.getContext()).inflate(R.layout.chat_main, null);
		
		text = (EditText) chatLayout.findViewById(R.id.text);
		text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage(v);
			}
		});
		
//		this.setTitle(sender);
		messages = new ArrayList<Message>();

		messages.add(new Message("Hello", -1,false));
		messages.add(new Message("Hi!", -1,true));
		messages.add(new Message("Wassup??", -1,false));
		messages.add(new Message("nothing much, working on speech bubbles.", -1, true));
		messages.add(new Message("you say!", -1, true));
		messages.add(new Message("oh thats great. how are you showing them", -1, false));

		adapter = new ChatViewAdapter(controller.getContext(), messages);
		chatView = (ListView) chatLayout.findViewById(R.id.chat_list_view);
		chatView.setAdapter(adapter);
		chatView.setEmptyView(chatLayout.findViewById(R.id.empty));
//		setListAdapter(adapter);
		addNewMessage(new Message("This is a long long long text message to test how the borders and margins getting aligned with the parent view. I hope this will server the purpose. If not I had to increase this messag length and rerun the code :|", -1, false));
		addNewMessage(new Message("So how di your long message go ? This is a long long long text message to test how the borders and margins getting aligned with the parent view. I hope this will server the purpose. If not I had to increase this messag length and rerun the code :|", -1, true));
		addNewMessage(new Message("mmm, well, using 9 patches png to show them.", -1, true));
		addView(chatLayout);
	}
	
	public void setUsers(User user1 , User user){
		
	}
	
	
	public void sendMessage(View v){
		String newMessage = text.getText().toString().trim(); 
		if(newMessage.length() > 0)
		{
			text.setText("");
			addNewMessage(new Message(newMessage, -1, true));
			new SendMessage().execute();
		}
	}
	private class SendMessage extends AsyncTask<Void, String, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(2000); //simulate a network call
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			this.publishProgress(String.format("%s started writing", sender));
			try {
				Thread.sleep(2000); //simulate a network call
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.publishProgress(String.format("%s has entered text", sender));
			try {
				Thread.sleep(3000);//simulate a network call
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			
			return "";
		}
		@Override
		public void onProgressUpdate(String... v) {
			
			if(messages.get(messages.size()-1).isStatusMessage)//check whether we have already added a status message
			{
				messages.get(messages.size()-1).setMessage(v[0]); //update the status for that
				adapter.notifyDataSetChanged(); 
//				getListView().setSelection(messages.size()-1);
			}
			else{
				addNewMessage(new Message(true,v[0])); //add new message, if there is no existing status message
			}
		}
		@Override
		protected void onPostExecute(String text) {
			if(messages.get(messages.size()-1).isStatusMessage)//check if there is any status message, now remove it.
			{
				messages.remove(messages.size()-1);
			}
			
			addNewMessage(new Message(text,-1, false)); // add the original message from server.
		}
		

	}
	
	void addNewMessage(Message m)
	{
		messages.add(m);
		adapter.notifyDataSetChanged();
//		getListView().setSelection(messages.size()-1);
	}
}