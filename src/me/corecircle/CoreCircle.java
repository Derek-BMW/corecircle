package me.corecircle;

import java.util.Random;
import me.corecircle.R;
import me.corecircle.ShakeDetector.OnShakeListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.app.PendingIntent;
import android.app.AlarmManager;

public class CoreCircle extends Activity {
	/** Called when the activity is first created. */
	private int RunTime = 0;
	private static long StartTime = 0, RepeatTime = 24 * 60 * 60 * 1000;// ���Ѽ������
	private static int MODE = MODE_PRIVATE;// ��д�����ļ�ģʽ
	private static final String PREFERENCE_NAME = "SaveSetting";// ��д�����ļ��ļ���

	private int TotalMember = 0;
	private int RecomMember = -5;// ������ĳ�Ա����
	private String CoreMember[];
	private int CoreMemberScore[];
	private int NeedToReplaceMember = -1;
	private boolean NeedToCreateMember = false;// �����Ƿ���д�������
	private static String PhoneType[];
	private static String PhoneNumber[];
	private static String PhoneMessage[];
	private static final int PICK_CONTACT_SUBACTIVITY = 2;// *Value to describe
															// ContactsPiker
	private static final int PICK_CORE_MEMBER = 3;
	private static final int INIT_LOG = 1;
	private static final int INIT_HELP = 4;
	private Button mb1;
	private ShakeDetector shake;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);// ��ȥ���⣨Ӧ�õ�����)
		// ���趨����Ҫд��setContentView֮ǰ����������쳣��
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		InitSP(0);

		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new CustomPhoneStateListener(this),
				PhoneStateListener.LISTEN_CALL_STATE);

		Intent wait = new Intent();
		wait.setClass(CoreCircle.this, SplashScreen.class);
		startActivityForResult(wait, INIT_LOG);

	}// end OnCreate

	@Override
	public void onStart() {
		super.onStart();

		if (RunTime != 0) {

			setContentView(R.layout.layout_main);
			InitMainActivity();

			ViewMovement();

			mb1 = (Button) findViewById(R.id.lma_button1);
			mb1.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					// ˢ��һ����Activity����
					InitMainActivity();
					ViewMovementRadom();

					mb1.setEnabled(false);
					for (; MovementView.ReturnWait() == 0;) {
						// wait
					}

					Handler mHandler = new Handler();
					Runnable mRunnable = new Runnable() {
						public void run() {
							mb1.setEnabled(true);
							InitPhoneAlertDialog(CoreMember[RecomMember]);
							ViewMovement();
						}
					};
					mHandler.postDelayed(mRunnable, 1000 * 2);
				}
			});

			shake = new ShakeDetector(this);
			shake.registerOnShakeListener(new OnShakeListener() {
				public void onShake() {
					ViewMovement();
				}
			});
			shake.start();
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (RunTime != 0) {
			shake.stop();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float xtouch = event.getX();
		float ytouch = event.getY();

		try {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				String temp = MovementView.CheckInOut((int) xtouch,
						(int) ytouch);

				if (temp == "IN") {

					ViewMovementStop();
				} else {
					InitPhoneAlertDialog(temp);
				}
				// ViewMovement();
				break;
			case MotionEvent.ACTION_MOVE:

				break;
			case MotionEvent.ACTION_UP:
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PICK_CONTACT_SUBACTIVITY: // contactspickerģ��(����CoreMemberReplace)
		{
			if (data != null) {
				final Uri uriRet = data.getData();

				try {

					Cursor t_cursor;
					int Continue = 1;
					String ReplaceMember = null;

					t_cursor = managedQuery(uriRet, null, null, null, null);
					t_cursor.moveToFirst();
					ReplaceMember = t_cursor
							.getString(t_cursor
									.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
					if (NeedToReplaceMember != -1 && ReplaceMember != null) {
						for (int i = 0; i < CoreMember.length; i++)
							if (ReplaceMember.contentEquals(CoreMember[i]))
								Continue = 0;
						if (Continue == 1) {
							Replace(CoreMember[NeedToReplaceMember],
									ReplaceMember);// �������ݿ�
							CoreMember[NeedToReplaceMember] = ReplaceMember;// ������Activity

						} else {

						}
						NeedToReplaceMember = -1;
						ReplacePhoneAlertDialog();

					}
					if (NeedToCreateMember == true && ReplaceMember != null) {
						for (int i = 0; i < CoreMember.length; i++)
							if (ReplaceMember.contentEquals(CoreMember[i]))
								Continue = 0;
						if (Continue == 1) {
							String tempcoremember[] = new String[TotalMember + 1];
							for (int i = 0; i < TotalMember; i++)
								tempcoremember[i] = CoreMember[i];
							tempcoremember[TotalMember] = ReplaceMember;
							InitContact(ReplaceMember);
							TotalMember++;
							CoreMember = tempcoremember;

						} else {

						}
						NeedToCreateMember = false;
					}// end if
					InitMainActivity();

				} catch (Exception e) {

				}
			}// end if
		}
			break; // end PICK_CONTACT_SUBACTIVITY:

		case PICK_CORE_MEMBER: {// CoreSecect��Ϣ����
			if (data != null)// ��Activity��ȷ����
			{
				if (RunTime == 0) {
					RunTime++;
					InitSP(1);
				} else // �����ǵ�һ�����У�����ʱ��������ݿ�
				{
					for (int i = 0; i < TotalMember; i++) {
						DeleteContact(CoreMember[i]);
					}
					RunTime++;
				}
				Bundle bundle = data.getExtras();
				CoreMember = bundle.getStringArray("CoreMember");
				TotalMember = CoreMember.length;

				for (int i = 0; i < TotalMember; i++) {
					InitContact(CoreMember[i]);
				}
				InitMainActivity();// ��ʼ��CoreMember ���¼������ܶ�
				// setContentView(R.layout.layout_main);
				if (RunTime == 1) {// ע��BroadcastReceiver&AlarmManger
					StartTime = SystemClock.elapsedRealtime();
					Intent intent = new Intent(CoreCircle.this,
							NotificationServiceReceiver.class);
					PendingIntent sender = PendingIntent.getBroadcast(
							CoreCircle.this, 0, intent, 0);
					AlarmManager am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
					am.setRepeating(AlarmManager.RTC, StartTime, RepeatTime,
							sender);
				}
			} else {
				if (RunTime == 0)
					CautionForCoreSelectError();
			}

		}// end PICK_CORE_MEMBER
			break;

		case INIT_LOG:
			if (RunTime == 0) {
				Intent help = new Intent();
				help.setClass(CoreCircle.this, AppHelp.class);
				startActivityForResult(help, INIT_HELP);

			}// end if
			break;

		case INIT_HELP:
			if (RunTime == 0) {
				Intent select = new Intent();
				select.setClass(CoreCircle.this, MyCoreSelect.class);
				startActivityForResult(select, PICK_CORE_MEMBER);

			}
			break;
		}// end switch
		super.onActivityResult(requestCode, resultCode, data);

	}// end onActivityResult

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 4, 0, R.string.app_insert);
		menu.add(0, 0, 1, R.string.app_replace);
		menu.add(0, 1, 2, R.string.app_reset);
		menu.add(0, 5, 3, R.string.app_help);
		menu.add(0, 2, 4, R.string.app_about);
		menu.add(0, 3, 5, R.string.app_exit);

		return super.onCreateOptionsMenu(menu);

	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 0: {
			// ��ʼ����ContactsPickerģ��
			ReplacePhoneAlertDialog();
		}
			break;
		case 1: {
			// ��ʼ����CoreSelect���ȴ��䷵��ֵ
			Intent temp = new Intent();
			temp.setClass(CoreCircle.this, MyCoreSelect.class);
			startActivityForResult(temp, PICK_CORE_MEMBER);
		}
			break;
		case 2:
			AboutAlertDialog();
			break;
		case 3:
			finish();
			break;
		case 4: {
			if (TotalMember >= 6)
				CreatePhoneAlertDialog();
			else {
				NeedToCreateMember = true;// �жϱ�־��ΪTrue ������ContactsPickerģ��
				startActivityForResult(
						new Intent(
								Intent.ACTION_PICK,
								android.provider.ContactsContract.Contacts.CONTENT_URI),
						PICK_CONTACT_SUBACTIVITY);
			}
		}
			break;
		case 5: {
			Intent help = new Intent();
			help.setClass(CoreCircle.this, AppHelp.class);
			startActivity(help);
		}
			break;
		}
		return true;
	}

	// ���ڲ�����ϵ��
	public void InitContact(String contact) {

		String name = contact;
		long date = java.lang.System.currentTimeMillis();
		float InitialValue = 33;

		CallLogHelper dbHelper = new CallLogHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(CallLogHelper.CACHED_NAME, name);
		values.put(CallLogHelper.LASTCALLDATE, date);
		values.put(CallLogHelper.SCORE, InitialValue);
		db.insert(CallLogHelper.MAIN_TABLE, null, values);

		db.close();
	}

	// ����ɾ����ϵ��
	public void DeleteContact(String contact) {

		CallLogHelper dbHelper = new CallLogHelper(this);
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		db.delete(CallLogHelper.MAIN_TABLE, CallLogHelper.CACHED_NAME + "=?",
				new String[] { contact });
		db.close();
	}

	// �滻��ϵ�ˣ���Ҫ������Ϣ
	public void Replace(String oldcontact, String newcontact) {
		DeleteContact(oldcontact);
		InitContact(newcontact);
	}

	private void SPWrite(String key, int value)// д�������ļ�����
	{
		SharedPreferences sharedPreferences = getSharedPreferences(
				PREFERENCE_NAME, MODE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	private void SPRead(String key, int value)// д�������ļ�����
	{

		SharedPreferences sharedPreferences = getSharedPreferences(
				PREFERENCE_NAME, MODE);
		value = sharedPreferences.getInt(key, value);
		RunTime = value;
	}

	private void InitSP(int control) {
		if (control == 1) {
			SPWrite("runtime", RunTime);
		} else
			SPRead("runtime", 0);
	}

	private void ViewMovement() {
		// InitMainActivity();
		MovementView.RefreshMove(TotalMember, CoreMemberScore, CoreMember);
	}

	private void ViewMovementStop() {
		MovementView.RefreshStop();

	}

	private void ViewMovementRadom() {

		MovementView.RefreshStop();
		MovementView.RefreshRadom(TotalMember, RecomMember);

	}

	private void InitMainActivity() // Including Reading Database to get
									// CoreMember and Init The Score
	{
		CallLogHelper dbHelper = new CallLogHelper(getBaseContext());
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = db.query(CallLogHelper.MAIN_TABLE, null, null, null,
				null, null, null);
		int num = cursor.getCount();
		InitSP(0);
		// �ּ������飬���ķ�ΧΪ1~8,��ϵ���ܶ�
		int grade[] = new int[num];
		String name[] = new String[num];
		while (cursor.moveToNext()) {
			name[cursor.getPosition()] = cursor.getString(cursor
					.getColumnIndex(CallLogHelper.CACHED_NAME));
			int lastscore = cursor.getInt(cursor
					.getColumnIndex(CallLogHelper.SCORE));
			switch (lastscore) {
			case 27:
				grade[cursor.getPosition()] = 1;
				break;
			case 28:
			case 29:
				grade[cursor.getPosition()] = 2;
				break;
			case 30:
			case 31:
				grade[cursor.getPosition()] = 3;
				break;
			case 32:
			case 33:
				grade[cursor.getPosition()] = 4;
				break;
			case 34:
			case 35:
				grade[cursor.getPosition()] = 5;
				break;
			case 36:
			case 37:
				grade[cursor.getPosition()] = 6;
				break;
			case 38:
			case 39:
				grade[cursor.getPosition()] = 7;
				break;
			case 40:
			case 41:
				grade[cursor.getPosition()] = 8;
				break;
			default:
				break;
			}
		}

		// ����ϵ�������ת������Ҫ��ϵ�ĳ̶�
		int reversegradesum = 0;
		int scale = 9;
		int[] reversegrade = new int[num];
		for (int i = 0; i < num; i++) {
			reversegrade[i] = scale - grade[i];
			reversegradesum += reversegrade[i];
		}
		Random random = new Random();// ����random����
		int randNumber = random.nextInt(reversegradesum);
		int range = 0;
		RecomMember = 0;
		for (int i = 0; i < num; i++) {
			range += reversegrade[i];
			if (randNumber <= range) {
				RecomMember = i;
				break;
			}
		}

		cursor.close();
		db.close();
		// ������Activity
		CoreMember = name;
		CoreMemberScore = grade;
		TotalMember = num;
	}// end InitMainActivity

	private void InitPhoneMessage() {
		if (PhoneType != null) {
			PhoneMessage = new String[PhoneType.length];
			for (int i = 0; i < PhoneType.length; i++)
				PhoneMessage[i] = "[" + PhoneType[i] + "]:" + PhoneNumber[i];
		}
	}

	// �õ�����һ����ϵ�˵ĵ绰��Ϣ
	private void GetCoreMemberMessage(String name) {
		try {
			Cursor t_cursor, t_cphones;

			int t_contactId, t_typePhone, t_resType;// ��־�绰�������ͱ���
			String t_phonetype;// �绰����������ʱ����

			t_cursor = managedQuery(ContactsContract.Contacts.CONTENT_URI,
					null, null, null, null);
			t_cursor.moveToFirst();
			for (int j = 0; j < t_cursor.getCount(); j++) {
				if (name.contentEquals(t_cursor.getString(t_cursor
						.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)))) {
					t_contactId = t_cursor.getInt(t_cursor
							.getColumnIndex(ContactsContract.Contacts._ID));
					t_cphones = managedQuery(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + t_contactId, null, null);

					if (t_cphones.getCount() > 0) {
						t_cphones.moveToFirst();
						PhoneType = new String[t_cphones.getCount()];
						PhoneNumber = new String[t_cphones.getCount()];
						for (int i = 0; i < t_cphones.getCount(); i++) {
							t_typePhone = t_cphones
									.getInt(t_cphones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
							t_resType = ContactsContract.CommonDataKinds.Phone
									.getTypeLabelResource(t_typePhone);
							t_phonetype = getString(t_resType);

							PhoneType[i] = t_phonetype;
							PhoneNumber[i] = t_cphones
									.getString(t_cphones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							t_cphones.moveToNext();

						}
						t_cphones.close();

					}// end if
					else {

					}// end else
					break;
				}

				t_cursor.moveToNext();
			}// end for

		}// end try
		catch (Exception e) {

		}
	}// end GetCoreMemberMessage

	private void InitPhoneAlertDialog(String name) {// The Fuction to Show the
													// list catain phonenumber
													// and phonetype
		if (name != null) {
			GetCoreMemberMessage(name);
			InitPhoneMessage();
			new AlertDialog.Builder(CoreCircle.this)
					.setTitle("��" + name + "����绰")
					.setItems(PhoneMessage,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										final int seclectitem) {
									new AlertDialog.Builder(CoreCircle.this)
											.setTitle("���ٴ�ȷ�ϲ���")
											.setNeutralButton(
													"ȷ��",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialoginterface,
																int wichbutton) {
															PhoneCall(PhoneNumber[seclectitem]);

														}
													})// end .setNeutralButton
														// floor-2
											.setNegativeButton(
													"ȡ��",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialoginterface,
																int wichbutton) {
															dialoginterface
																	.dismiss();

														}
													}).show();// end
																// .setNegativeButton
																// floor-2

								}// end onClick
							})// end .setitemsfloor -1
					.show();

		}// end if
	}// end InitPhoneAlertDialog

	private void ReplacePhoneAlertDialog() {// The Fuction to Show the list
											// catain phonenumber and phonetype
		if (CoreMember != null) {
			new AlertDialog.Builder(CoreCircle.this)
					.setTitle("����Ҫ�滻˭��")
					.setItems(CoreMember,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										final int seclectitem) {
									new AlertDialog.Builder(CoreCircle.this)
											.setTitle("��ȷ���滻TA��")
											.setNeutralButton(
													"ȷ��",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialoginterface,
																int wichbutton) {
															NeedToReplaceMember = seclectitem;
															// ��ʼ����ContactsPickerģ��
															startActivityForResult(
																	new Intent(
																			Intent.ACTION_PICK,
																			android.provider.ContactsContract.Contacts.CONTENT_URI),
																	PICK_CONTACT_SUBACTIVITY);

														}
													})// end .setNeutralButton
														// floor-2
											.setNegativeButton(
													"ȡ��",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialoginterface,
																int wichbutton) {
															ReplacePhoneAlertDialog();
														}
													}).show();// end
																// .setNegativeButton
																// floor-2

								}// end onClick
							})// end .setitems
					.setNegativeButton("����",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										int wichbutton) {
									dialoginterface.dismiss();
								}
							})// end .setNegativeButton floor-1
					.show();

		}// end if
	}// end InitPhoneAlertDialog

	private void CreatePhoneAlertDialog() {// The Fuction to Show the list
											// catain phonenumber and phonetype

		new AlertDialog.Builder(CoreCircle.this).setTitle("Hi")
				.setMessage("����Ȧ����\n��ѡ���滻Ȧ�ڳ�Ա")
				.setNeutralButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface,
							int wichbutton) {
						dialoginterface.dismiss();
					}
				}).show();

	}// end CreatePhoneAlertDialog()

	private void AboutAlertDialog() {// The Fuction to Show the list catain
										// phonenumber and phonetype

		new AlertDialog.Builder(CoreCircle.this).setTitle("��������")
				.setMessage(R.string.app_abouttxt)
				.setNeutralButton("����", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface,
							int wichbutton) {
						dialoginterface.dismiss();

					}
				}).show();

	}// end AboutAlertDialog()

	private void CautionForCoreSelectError() {// The Fuction to Show the list
												// catain phonenumber and
												// phonetype

		new AlertDialog.Builder(CoreCircle.this).setTitle("��ʾ")
				.setMessage("���Ȱ����򵼹�����ĺ���Ȧ~")
				.setNeutralButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface,
							int wichbutton) {
						Intent temp = new Intent();
						temp.setClass(CoreCircle.this, MyCoreSelect.class);
						startActivityForResult(temp, PICK_CORE_MEMBER);
					}
				})

				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface,
							int wichbutton) {
						finish();
					}
				}).show();

	}// end CautionForNotification()

	private void PhoneCall(String strInput) {
		try {
			Intent mycall = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + strInput));
			startActivity(mycall);
		} catch (Exception e) {

		}
	}
}// end Activity