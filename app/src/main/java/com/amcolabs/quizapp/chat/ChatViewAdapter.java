package com.amcolabs.quizapp.chat;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.amcolabs.quizapp.R;
import com.amcolabs.quizapp.appcontrollers.ProfileAndChatController;
import com.amcolabs.quizapp.configuration.Config;
import com.amcolabs.quizapp.uiutils.UiUtils;
/**
 * ChatViewAdapter is a Custom class to implement custom row in ListView
 *
 */
public class ChatViewAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<Message> mMessages;
	private ProfileAndChatController controller;



	public ChatViewAdapter(Context context, ArrayList<Message> messages, ProfileAndChatController controller) {
		super();
		this.mContext = context;
		this.mMessages = messages;
		this.controller = controller;
		this.notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return mMessages.size();
	}
	@Override
	public Object getItem(int position) {		
		return mMessages.get(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Message message = (Message) this.getItem(position);

		if(position<5){

		}
			
		ViewHolder holder; 
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_row, parent, false);
			holder.chatRow = (LinearLayout) convertView.findViewById(R.id.chat_row_wrapper);
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
			holder.timestamp = (TextView) convertView.findViewById(R.id.message_time);
			holder.timestamp.setTextColor(Color.LTGRAY);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		holder.message.setText(message.getMessage());
		holder.timestamp.setText(DateUtils.getRelativeTimeSpanString((long)Config.convertToUserTimeStamp(message.getTimestamp())*1000, (long)Config.getCurrentTimeStamp()*1000, DateUtils.FORMAT_ABBREV_RELATIVE));	
		
		// TODO: Add margins to left and right respectively to differentiate views in chat
		LayoutParams lp = (LayoutParams) holder.chatRow.getLayoutParams();
		//check if it is a status message then remove background, and change text color.
		if(message.isStatusMessage()){
			holder.chatRow.setBackgroundDrawable(null);
			lp.gravity = Gravity.LEFT;
			holder.timestamp.setVisibility(View.GONE);
			holder.message.setTextColor(Color.GRAY);
		}
		else{		
			//Check whether message is mine to show green background and align to right
			if(message.isMine())
			{
				holder.chatRow.setBackgroundResource(R.drawable.bubble_right);
				holder.message.setGravity(Gravity.RIGHT);
				lp.gravity = Gravity.RIGHT;
				lp.rightMargin = 4;
				lp.leftMargin = 10;
			}
			//If not mine then it is from sender to show orange background and align to left
			else
			{
				holder.chatRow.setBackgroundResource(R.drawable.bubble_left);
				holder.message.setGravity(Gravity.LEFT);
				lp.gravity = Gravity.LEFT;
				lp.rightMargin = 10;
				lp.leftMargin = 4;
			}
			holder.timestamp.setVisibility(View.VISIBLE);
			holder.chatRow.setLayoutParams(lp);
			holder.message.setTextColor(Color.BLACK);
		}
		return convertView;
	}
	
	private static class ViewHolder
	{
		LinearLayout chatRow;
		TextView message;
		TextView timestamp;
	}

	@Override
	public long getItemId(int position) {
		//Unimplemented, because we aren't using Sqlite.
		return position;
	}

}
