package me.corecircle;

import android.telephony.PhoneStateListener;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


public class CustomPhoneStateListener extends PhoneStateListener {
	Context context;

	public CustomPhoneStateListener(Context context) {
		super();
		this.context = context;
	}

	int phoneState = TelephonyManager.CALL_STATE_IDLE;

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		super.onCallStateChanged(state, incomingNumber);

		switch (state) {
		case TelephonyManager.CALL_STATE_IDLE:
			if (phoneState != TelephonyManager.CALL_STATE_IDLE) {
				// ��ȡ���µ�ͨ����¼������¼�����ݿ���
				Intent intent = new Intent(context, GetCallLogService.class);
				/* �趨��TASK�ķ�ʽ */
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				/* ��startService��������Intent */
				context.startService(intent);
			}
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			break;
		default:
			break;
		}
		phoneState = state;
	}

};
