package com.sgu.findyourfriend.mgr;

import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.widget.Toast;

import com.sgu.findyourfriend.model.Friend;
import com.sgu.findyourfriend.model.Message;
import com.sgu.findyourfriend.utils.Controller;
import com.sgu.findyourfriend.utils.MessagesDataSource;

public class MessageManager {

	private static MessageManager instance;

	private Controller aController;
	public List<Message> messages;

	IMessage iMessage;

	// access database
	private MessagesDataSource dataSource;
	private Context context;

	public void init(Context context) {
		this.context = context;

		aController = (Controller) context;
		this.context.registerReceiver(mHandleMessageReceiver, new IntentFilter(
				Config.DISPLAY_MESSAGE_ACTION));

		// setup database
		dataSource = new MessagesDataSource(context);
		dataSource.open();
		messages = dataSource.getAllMessages();
	}

	public synchronized static MessageManager getInstance() {
		if (instance == null) {
			instance = new MessageManager();
		}
		return instance;
	}
	
	public void setMessageListener(IMessage iMessage) {
		this.iMessage = iMessage;
	}

	public List<Message> getAllMessage() {
		return messages;
	}

	public List<Message> filterMessage() {

		return messages;
	}

	public void sendMessage(String msg, List<String> addrs) {

		for (String addr : addrs) {
			Message sms = new Message(msg, true, MyProfileManager.getInstance().mine.getGcmId(),
					addr, new Date(
							System.currentTimeMillis()));
			sms = dataSource.createMessage(sms);
			iMessage.addNewMessage(sms);
			new SendMessage().execute(msg);
		}
	}

	public Message sendMessage(String msg, List<Friend> friendsId, int k) {
		Message sms = new Message(msg, true, MyProfileManager.getInstance().mine.getGcmId(),
				MyProfileManager.getInstance().mine.getGcmId(), new Date(
						System.currentTimeMillis()));
		sms = dataSource.createMessage(sms);
		iMessage.addNewMessage(sms);
		new SendMessage().execute(msg);
		return sms;
	}

	private class SendMessage extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... params) {
			String regIdFrom = MyProfileManager.getInstance().mine.getGcmId();
			String regIdTo = MyProfileManager.getInstance().mine.getGcmId();
			String message = params[0];

			aController.sendMessage(context, regIdFrom, regIdTo, message);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// addNewMessage(new Message(text.getText().toString(), false));
			// text.setText("");
		}
	}

	// Create a broadcast receiver to get message and show on screen
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String newMessage = intent.getExtras().getString(
					Config.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			aController.acquireWakeLock(context);

			Message sms = new Message(newMessage, true, "server",
					MyProfileManager.getInstance().mine.getGcmId(), new Date(System.currentTimeMillis()));
			// save to database
			sms = dataSource.createMessage(sms);

			// Display message on the screen
			iMessage.addNewMessage(sms);

			Toast.makeText(context, "Got Message: " + newMessage,
					Toast.LENGTH_LONG).show();

			// Releasing wake lock
			aController.releaseWakeLock();
		}
	};

	public void deleteMessage(Message message) {
		dataSource.deleteMessage(message);
	}

	public Message createMessage(Message msg) {
		return dataSource.createMessage(msg);
	}

	public void destroy() {
		context.unregisterReceiver(mHandleMessageReceiver);
	}

}