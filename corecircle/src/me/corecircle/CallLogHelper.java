package me.corecircle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/*
 * Class Info��
 * DatabaseHelper��Ϊһ������SQLite�������࣬�ṩ��������Ĺ��ܣ�
 *��һ��getReadableDatabase(),getWritableDatabase()���Ի��SQLiteDatabse����ͨ���ö�����Զ����ݿ���в���
 *�ڶ����ṩ��onCreate()��onUpgrade()�����ص����������������ڴ������������ݿ�ʱ�������Լ��Ĳ���
 *
 */

public class CallLogHelper extends SQLiteOpenHelper {

	private final static String DB_NAME = "CoreCircle.db";
	private final static int DB_VERSION = 1;

	public final static String MAIN_TABLE = "CalllogTable";
	public final static String CACHED_NAME = "CACHED_NAME";// Primary key
	public final static String LASTCALLDATE = "LASTCALLDATE";
	public final static String SCORE = "SCORE";// ����ļ���,INT,float

	Context context;

	// ��SQLiteOepnHelper�����൱�У������иù��캯��
	public CallLogHelper(Context context, String name, CursorFactory factory,
			int version) {
		// ����ͨ��super���ø��൱�еĹ��캯��
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public CallLogHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	public CallLogHelper(Context context, String name) {
		this(context, name, DB_VERSION);
	}

	public CallLogHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	/*
	 * �ú������ڵ�һ�δ������ݿ��ʱ��ִ��,ʵ�������ڵ�һ�εõ�SQLiteDatabse�����ʱ�򣬲Ż�����������
	 * ����ʱ��Ҫ�ǵ�ж��������������ݿⲻ�����
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		// ����MAIN_TABLE@ע��ո�
		String sql = "create table " + MAIN_TABLE + " (" + CACHED_NAME
				+ " text primary key, " + LASTCALLDATE + " long, " + SCORE
				+ " float" + ")";
		// execSQL��������ִ��SQL���
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}
