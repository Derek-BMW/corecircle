package me.corecircle;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

public class NotificationService extends Service {


	private NotificationManager myNotiManager;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);

		CallLogHelper dbHelper = new CallLogHelper(getBaseContext());
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		Cursor cursor = db.query(CallLogHelper.MAIN_TABLE, null, null, null,
				null, null, null);
		// ��Ҫ���ѵ�����
		int notification = 0;
		final int maxneednotification = cursor.getCount()/2;
		// ���ѷ�������
		float LowerBound = 27;
		float cutscore = 2;
		float plusscore = 2;
		boolean hasRecord = cursor.moveToFirst();
		while (hasRecord) {
			// �ϴ�ͨ������
			long lastcalldate = cursor.getLong(cursor
					.getColumnIndex(CallLogHelper.LASTCALLDATE));
			// �ϴεķ���
			float lastscore = cursor.getFloat(cursor
					.getColumnIndex(CallLogHelper.SCORE));

			// ÿ���ȥ����
			lastscore -= cutscore;
			ContentValues values = new ContentValues();

			if (lastscore < LowerBound) {
				// ����
				notification++;

				// �������һ�δ�绰����2�֣�����ʱ������Ϊǰһ������ʱ�䣨Ϊ�˲����ڷ����ӵ��ر�죩
				lastscore += plusscore;
				lastcalldate = java.lang.System.currentTimeMillis() - 24 * 60
						* 60 * 1000;
				values.put(CallLogHelper.LASTCALLDATE, lastcalldate);
			}
			values.put(CallLogHelper.SCORE, lastscore);
			db.update(CallLogHelper.MAIN_TABLE, values,
					CallLogHelper.CACHED_NAME + " = ?",
					new String[] { cursor.getString(cursor
							.getColumnIndex(CallLogHelper.CACHED_NAME)) });
			hasRecord = cursor.moveToNext();
		}
		if (notification > maxneednotification) {
			myNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			setNotiType(R.drawable.icon30, "Hi,����һ��ʱ��û��Ȧ���������ϵ�ˣ����ھ�ȥȦ�ӿ�����");
		}
		cursor.close();
		db.close();

		stopSelf();
		return START_STICKY;
	}

	/* ����Notification��method */
	private void setNotiType(int iconId, String text) {
		/*
		 * �����µ�Intent����Ϊ��ѡNotification������ʱ�� ��ִ�е�Activity
		 */
		Intent notifyIntent = new Intent(this, CoreCircle.class);
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		/* ����PendingIntent��Ϊ�趨����ִ�е�Activity */
		PendingIntent appIntent = PendingIntent.getActivity(this, 0,
				notifyIntent, 0);

		/* ����Notication�����趨��ز��� */
		Notification myNoti = new Notification();
		/* �趨statusbar��ʾ��icon */
		myNoti.icon = iconId;
		/* �趨statusbar��ʾ������ѶϢ */
		myNoti.tickerText = text;
		/* �趨notification����ʱͬʱ����Ԥ������ */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		/* �趨Notification�������Ĳ��� */
		myNoti.setLatestEventInfo(this, "CoreCircle��ܰ��ʾ", text, appIntent);
		/* �ͳ�Notification */
		myNotiManager.notify(0, myNoti);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
