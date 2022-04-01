
package com.szip.blewatch.base.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Util.MathUtil;


/**
 * This class will receive and process all new SMS.
 */
public class SmsService extends BroadcastReceiver {
    // Debugging
    private static final String TAG = "SmsServiceDATA******";

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    // public static final String SMS_ACTION = "SenderSMSFromeFP";
    private static String preID = null;

    // Received parameters
    private Context mContext = null;

    public SmsService() {
        Log.i(TAG, "SmsReceiver(), SmsReceiver created!");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive()");

        mContext = context;
        if (intent.getAction().equals(SMS_RECEIVED)) {
            sendSms();
        }
    }

    void sendSms() {
        String msgbody;
        String address;
        String id;

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(Uri.parse("content://sms/inbox"), null,
                    null, null, "_id desc");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    msgbody = cursor.getString(cursor.getColumnIndex("body"));
                    address = cursor.getString(cursor.getColumnIndex("address"));
                    id = cursor.getString(cursor.getColumnIndex("_id"));
                    if (id.equals(preID)) {
                        break;
                    } else {
                        preID = id;
                        if ((msgbody != null) && (address != null)) {
                            Log.i(TAG, "SmsReceiver(),sendSmsMessage, msgbody = " + msgbody
                                    + ", address = " + address);
                            Intent intent = new Intent(BroadcastConst.SEND_BLE_DATA);
                            intent.putExtra("command","sendNotify");
                            intent.putExtra("title",msgbody);
                            intent.putExtra("label", address);
                            intent.putExtra("id", 0);
                            mContext.sendBroadcast(intent);
                            break;
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }
    }

}
