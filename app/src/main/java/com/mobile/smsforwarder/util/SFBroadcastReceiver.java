package com.mobile.smsforwarder.util;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.mobile.smsforwarder.model.Mail;
import com.mobile.smsforwarder.model.Number;
import com.mobile.smsforwarder.model.Relation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class SFBroadcastReceiver extends BroadcastReceiver {
    private DatabaseHelper databaseHelper = null;
    final String username = "inthersmsforwarder@gmail.com";
    final String password = "inther2019!";

    String fromNumberDigits;
    String mailSubject;
    String mailText;
    String emailStringList;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("SFBroadcastReceiver", "enter onReceive() with intentAction=[" + intent.getAction() + "]");

        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {

                List<Number> fromNumbers = null;
                String from = smsMessage.getOriginatingAddress();
                Log.i("SFBroadcastReceiver", "received message=[" + smsMessage.getMessageBody() + "], from=[" + from + "]");

                try {
                    fromNumbers = getHelper().getNumberDao().queryBuilder().
                            where().eq("digits", from).
                            and().eq("type", NumberType.FROM_NUMBER).query();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (fromNumbers.size() > 0) {
                    Log.i("SFBroadcastReceiver", "we have registered [" + from + "], number");
                    String message ="Message: " + smsMessage.getMessageBody() + "\nRedirected from: " + fromNumbers.get(0).getName() + " (" + fromNumbers.get(0).getDigits() + ")";
                    for (Number fromNumber : fromNumbers) {
                        sendMessage(fromNumber, message);
                        if(isOnline()){
                            sendEmail(fromNumber, message);
                        }
                    }
                } else {
                    Log.i("SFBroadcastReceiver", "!!! we don't have registered [" + from + "], number");

                }
            }
        }

        destroyHelper();

    }

    private void sendMessage(Number fromNumber, String message) {
        Log.i("SFBroadcastReceiver", "********** sending received message **********");
        List<Relation> relations = new ArrayList<>();

        try {
            relations = getHelper().getRelationDao().queryBuilder().where().eq("id", fromNumber.getRelation().getId()).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Relation relation : relations) {
            Log.i("###SFBroadcastReceiver", "Sending message to all toNumbers in Relation [" + relation + "]");
            List<Number> toNumbers = new ArrayList<>();

            try {
                toNumbers = getHelper().getNumberDao().queryBuilder().
                        where().eq("relation_id", relation.getId())
                        .and().eq("type", NumberType.TO_NUMBER).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            for (Number toNumber : toNumbers) {

                SmsManager sms = SmsManager.getDefault();
                ArrayList<String> parts = sms.divideMessage(message);
                sms.sendMultipartTextMessage(toNumber.getDigits(), null, parts, null, null);

                Log.i("SFBroadcastReceiver", "send message=[" + message + "], to contact: " + toNumber.getName() + " (" + toNumber.getDigits() + ")");
            }

        }
    }

    private void sendEmail(Number fromNumber, String message) {

        Log.i("SFBroadcastReceiver", "********** sending received message by email **********");
        List<Relation> relations = new ArrayList<>();

        try {
            relations = getHelper().getRelationDao().queryBuilder().where().eq("id", fromNumber.getRelation().getId()).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Relation relation : relations) {
            Log.i("###SFBroadcastReceiver", "Sending message to all toNumbers in Relation [" + relation + "]");
            List<Mail> toEmails = new ArrayList<>();

            try {
                toEmails = getHelper().getMailDao().queryBuilder().
                        where().eq("relation_id", relation.getId()).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            StringBuilder emailStringBuilderList = new StringBuilder();
            int numberOfEmails = toEmails.size();
            int currentIndex = 0;
            for (Mail toEmail : toEmails) {
                 currentIndex++;
                 emailStringBuilderList.append(toEmail.getAddress());
                 if(currentIndex < numberOfEmails){
                     emailStringBuilderList.append(",");
                 }
            }

            if(emailStringBuilderList.length()>0){
                emailStringList = emailStringBuilderList.toString();
                mailSubject = "Message redirected from number: " + fromNumber.getDigits();
                mailText = message;
                //send(fromNumber, message, emailStringList);
                final SendEmailTask emailTask = new SendEmailTask();
                emailTask.execute();
            }

        }
    }

    protected void destroyHelper() {
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }


    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(ContextProvider.getAppContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) ContextProvider.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @SuppressLint("StaticFieldLeak")
    class SendEmailTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("Email sending", "sending start");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                GmailSender sender = new GmailSender(username, password);
                //subject, body, sender, to
                sender.sendMail(mailSubject,
                        mailText,
                        username,
                        emailStringList);

                Log.i("Email sending", "send");
            } catch (Exception e) {
                Log.i("Email sending", "cannot send");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}
