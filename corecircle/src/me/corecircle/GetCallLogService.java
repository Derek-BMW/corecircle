package me.corecircle;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;

/*
 * GetCallLogServie���ڼ�⵽�绰�Ҷ�״̬����CustomPhoneStateListener�к������á�
 * ��ȡ���һ�ε�ͨ����¼���ж��Ƿ�Ϊ������ϵ�˵ĺ��룬����������Ӧ�����ͨ����Ϣ���¡�
 * ������ִ�к�stopself()
 */
public class GetCallLogService extends Service {
	// �����ӳ�ʱ��Ϊ10s������ϵͳ�����¼ͨ����¼
	static final int DELAY = 10000;

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

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				// ��ȡ���һ��ͨ����¼����Ϣ��NUMBER,DURATION,TYPE,CACHED_NAME,DATE
				Cursor cursor = getContentResolver().query(
						CallLog.Calls.CONTENT_URI,
						new String[] { CallLog.Calls.CACHED_NAME,
								CallLog.Calls.NUMBER, CallLog.Calls.DURATION,
								CallLog.Calls.TYPE, CallLog.Calls.DATE }, null,
						null, CallLog.Calls.DEFAULT_SORT_ORDER);

				if (cursor.moveToFirst()) {
					int nameidx = cursor
							.getColumnIndex(CallLog.Calls.CACHED_NAME);
					int numberidx = cursor.getColumnIndex(CallLog.Calls.NUMBER);
					int durationidx = cursor
							.getColumnIndex(CallLog.Calls.DURATION);
					int typeidx = cursor.getColumnIndex(CallLog.Calls.TYPE);
					int dateidx = cursor.getColumnIndex(CallLog.Calls.DATE);

					String name = cursor.getString(nameidx);
					String number = cursor.getString(numberidx);
					long duration = cursor.getLong(durationidx);
					int calltype = cursor.getInt(typeidx);
					long date = cursor.getLong(dateidx);

					cursor.close();

					if (name != null) {// İ�����룬ֱ�Ӻ��ӣ����뱨������

						CallLogHelper dbHelper = new CallLogHelper(
								getBaseContext());
						SQLiteDatabase db = dbHelper.getWritableDatabase();

						// ʹ����������ѯMainTable����ȡ�������ϴ�ͨ��ʱ�䡣
						Cursor cursor2 = db.query(CallLogHelper.MAIN_TABLE,
								null, CallLogHelper.CACHED_NAME + "=?",
								new String[] { name }, null, null, null);

						if (cursor2.moveToFirst()) {

							/*
							 * ���嵥���ֵȨֵ������������ �����߼�Ȩֵ���Ǿ�������ó�������ֻ���г������
							 * InitialValue = 33; LowerBound = 27; UpperBound
							 * =41; CallTypeWeightͨ������Ȩֵ: INCOMING_TYPE��5��
							 * OUTGOING_TYPE��5�� MISSED_TYPE��3��
							 * DurationWeightͨ��ʱ��Ȩֵ: 0<t<1*60s:1 1*60<t<5*60:2
							 * 5*60<10*60:3 >10*60:4
							 */
							float UpperBound = 41;
							float CallTypeWeight = 0;
							float DurationWeight = 0;

							// �ϴ�ͨ�����ڣ����ڼ���ͨ�����
							long lastcalldate = cursor2
									.getLong(cursor2
											.getColumnIndex(CallLogHelper.LASTCALLDATE));
							// �ϴεķ���
							float lastscore = cursor2.getFloat(cursor2
									.getColumnIndex(CallLogHelper.SCORE));
							// ����ͨ�����ϴ�ͨ���ļ��������������һ�����Ϊһ�졣
							int interval = (int) ((date - lastcalldate)
									/ (24 * 60 * 60 * 1000) + 1);
							float newscore = 0;

							switch (calltype) {
							case CallLog.Calls.INCOMING_TYPE:
							case CallLog.Calls.OUTGOING_TYPE:
								CallTypeWeight = 5;
								break;
							case CallLog.Calls.MISSED_TYPE:
								CallTypeWeight = 3;
								break;
							default:
								break;
							}

							if (duration < 1 * 60)
								DurationWeight = 1;
							else if (duration < 5 * 60)
								DurationWeight = 2;
							else if (duration < 10 * 60)
								DurationWeight = 3;
							else
								DurationWeight = 4;
							// �����µķ���
							newscore = (float) (lastscore + 1
									/ (Math.sqrt(interval)) * CallTypeWeight
									* DurationWeight);
							if (newscore > UpperBound)
								newscore = UpperBound;

							ContentValues values = new ContentValues();
							values.put(CallLogHelper.LASTCALLDATE, date);
							values.put(CallLogHelper.SCORE, newscore);
							db.update(CallLogHelper.MAIN_TABLE, values,
									CallLogHelper.CACHED_NAME + " = ?",
									new String[] { name });
						}
						cursor2.close();
						db.close();
					}// name!=null
				}// cursor.moveToFirst
				cursor.close();
			}
		}, DELAY);

		// let the service leave the big big world!
		stopSelf();
		return START_STICKY;
	}// onStartCommand

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}