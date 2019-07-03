package com.mobile.smsforwarder;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.mobile.smsforwarder.model.Mail;
import com.mobile.smsforwarder.model.Number;
import com.mobile.smsforwarder.model.Relation;
import com.mobile.smsforwarder.util.ActivityRequestCode;
import com.mobile.smsforwarder.util.DatabaseHelper;
import com.mobile.smsforwarder.util.NumberType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddRelationActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper = null;

    private int activeTabItem;
    private NumberType numberType = NumberType.FROM_NUMBER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_relation);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        numberType = NumberType.FROM_NUMBER;
                        break;
                    case 1:
                        numberType = NumberType.TO_NUMBER;
                        break;
                    case 2:
                        numberType = null;
                    default:
                        numberType = null;
                }
                initData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public void initData() {
        ListView relationItemListView = findViewById(R.id.relationItemListView);

        if (numberType != null) {
            List<Number> numbers = null;
            try {
                numbers = getHelper().getNumberDao().queryBuilder().where().eq("type", numberType).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ArrayList<String> numberNames = new ArrayList<>();
            for (Number n : numbers)
                numberNames.add(n.toString());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddRelationActivity.this, android.R.layout.simple_list_item_1, numberNames);
            relationItemListView.setAdapter(adapter);

        } else {
            List<Mail> mails = null;
            try {
                mails = getHelper().getMailDao().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ArrayList<String> mailsName = new ArrayList<>();
            for (Mail m : mails)
                mailsName.add(m.toString());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddRelationActivity.this, android.R.layout.simple_list_item_1, mailsName);
            relationItemListView.setAdapter(adapter);
        }


//
//        // set onClick event for every item of list
//        relationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                showPopup(((TextView) view).getText().toString());
//            }
//        });
    }


    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void onClick_saveButton(View v) {
        EditText relationNameEditText = findViewById(R.id.relationNameEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Relation relation = new Relation(relationNameEditText.getText().toString(), new Date().toString());
        try {
            Dao<Relation, Long> relationDao = getHelper().getRelationDao();
            relationDao.create(relation);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        relationNameEditText.setEnabled(false);
        saveButton.setEnabled(false);

    }

    public void onClick_addButton(View v) {
        if ( numberType != null) {
            Intent pickNumberIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            //addRelationIntent.putExtra(NumberType.class.getName(), chosenNumberType.toString());
            startActivityForResult(pickNumberIntent, ActivityRequestCode.PICK_NUMBER_ACTIVITY_CODE);
        }
        else{
            showPopup();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityRequestCode.PICK_NUMBER_ACTIVITY_CODE) {

            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                if (cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String digits = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    //this way we exclude - ( ) and spaces from digits string
                    digits = digits.replaceAll("(?:(?:(?:-)|(?:(?:\\ ))|(?:(?:\\())|(?:(?:\\)))))", "");
                    saveNumber(name, digits);
                    initData();

                }

            }

        }
    }

    public void saveNumber(String name, String digits) {

        Relation relation = null;
        try {
            relation = getHelper().getRelationDao().queryForId(Long.valueOf(6));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Number number = new Number(name, digits, numberType, new Date().toString(), relation);
        try {
            getHelper().getNumberDao().create(number);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void showPopup() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_add_mail);
        TextView popupTextView = dialog.findViewById(R.id.infoTextView);
        EditText mailNameEditText = dialog.findViewById(R.id.mailNameEditText);
        EditText mailAddressEditText = dialog.findViewById(R.id.mailAddressEdixText);

        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button saveButton = dialog.findViewById(R.id.saveButton);


        popupTextView.setText("Should add some text here");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MainActivity", "Cancel button on popup_relation was clicked");
                dialog.dismiss();
            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Relation relation = null;
                    relation = getHelper().getRelationDao().queryForId(Long.valueOf(9));
                    Mail mail = new Mail(mailNameEditText.getText().toString(), mailAddressEditText.getText().toString(), new Date().toString(), relation);
                    getHelper().getMailDao().create(mail);

                } catch (SQLException e) {
                    e.printStackTrace();
                }


                /*
                Log.i("MainActivity", "deletedNumber=[" + number + "], numberType=[" + NumberType.FROM_NUMBER.toString() + "]");
                Toast.makeText(getBaseContext(),"Number [" + number + "] of type ["
                        + chosenNumberType.toString() + "] was deleted.",Toast.LENGTH_LONG).show();

                */
                dialog.dismiss();

                initData();
            }
        });

        dialog.show();
    }


}
