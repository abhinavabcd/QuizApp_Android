package com.amcolabs.quizapp.screens;

import java.util.ArrayList;
import java.util.Random;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.amcolabs.quizapp.AppController;
import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.Screen;
import com.amcolabs.quizapp.User;
import com.amcolabs.quizapp.chat.ChatViewAdapter;
import com.amcolabs.quizapp.chat.Message;
import com.amcolabs.quizapp.widgets.GothamTextView;
import com.squareup.picasso.Picasso;

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
	private GothamTextView user1Name;
	private GothamTextView user1Status;
	private GothamTextView user2Name;
	private GothamTextView user2Status;
	private ImageView user1Image;
	private ImageView user2Image;
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
		
		
		user1Name = (GothamTextView)chatLayout.findViewById(R.id.user_name);
		user1Status = (GothamTextView)chatLayout.findViewById(R.id.user_status_msg);
		user1Image = (ImageView)chatLayout.findViewById(R.id.user1);
		
		user2Name = (GothamTextView)chatLayout.findViewById(R.id.user_name_2);
		user2Status = (GothamTextView)chatLayout.findViewById(R.id.user_status_msg_2);
		user2Image = (ImageView)chatLayout.findViewById(R.id.user2);
		
		
		
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
	
	public void showUsers(User user2 , User user){
		user1Status.setText(user.status);
		user1Name.setText(user.name);
		if(user.pictureUrl!=null){
			Picasso.with(getApp().getContext()).load(user.pictureUrl).into(user1Image);
		}
		
		user2Status.setText(user2.status);
		user2Name.setText(user2.name);
		if(user2.pictureUrl!=null){
			Picasso.with(getApp().getContext()).load(user2.pictureUrl).into(user2Image);
		}
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