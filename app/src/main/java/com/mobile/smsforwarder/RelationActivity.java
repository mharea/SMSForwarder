package com.mobile.smsforwarder;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.mobile.smsforwarder.model.Contact;
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

public class RelationActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper = null;
    private NumberType numberType = NumberType.FROM_NUMBER;
    private Relation relation = null;
    private int mode;

    private Intent relationIntent;
    private TabLayout tabLayout;
    private ListView relationItemListView;
    private EditText relationNameEditText;
    private ImageView saveButton;
    private ImageView addButton;
    private ImageView addContactButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_relation);

        relationIntent = getIntent();
        saveButton = findViewById(R.id.saveButton);
        addButton = findViewById(R.id.addButton);
        addContactButton = findViewById(R.id.addContactButton);
        relationNameEditText = findViewById(R.id.relationNameEditText);
        relationItemListView = findViewById(R.id.relationItemListView);
        tabLayout = findViewById(R.id.tabLayout);

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
                showDataInListView();
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


    public void initData() {

        if (relationIntent.getExtras().getInt(ActivityRequestCode.class.getName()) == ActivityRequestCode.VIEW_RELATION_ACTIVITY_CODE) {
            mode = ActivityRequestCode.VIEW_RELATION_ACTIVITY_CODE;
            relationNameEditText.setText(relationIntent.getExtras().getString(Relation.class.getName()));
            switchMode(mode);
            showDataInListView();

        } else {
            mode = ActivityRequestCode.ADD_RELATION_ACTIVITY_CODE;
            switchMode(mode);
        }
    }

    public void switchMode(int mode) {

        if (mode == ActivityRequestCode.VIEW_RELATION_ACTIVITY_CODE) {
            //view
            relationNameEditText.setEnabled(false);
            saveButton.setImageResource(R.drawable.ic_edit_black_48dp);
            addButton.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            relationItemListView.setVisibility(View.VISIBLE);
        } else {
            //edit
            relationNameEditText.setEnabled(true);
            saveButton.setImageResource(R.drawable.ic_done_black_48dp);
            addButton.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            relationItemListView.setVisibility(View.GONE);
            addContactButton.setVisibility(View.GONE);
        }

    }

    public void showDataInListView() {

        if (numberType != null) {
            List<Number> numbers = null;
            try {
                String relationName = relationNameEditText.getText().toString();
                relation = getHelper().getRelationDao().queryBuilder().where().eq("name", relationName).query().get(0);
                numbers = getHelper().getNumberDao().queryBuilder().where().eq("type", numberType).and().eq("relation_id", relation.getId()).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ArrayList<String> numberNames = new ArrayList<>();
            for (Number n : numbers)
                numberNames.add("Name: " + n.getName() + "\n" + "Number: " + n.getDigits());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(RelationActivity.this, android.R.layout.simple_list_item_1, numberNames);
            relationItemListView.setAdapter(adapter);
            addContactButton.setVisibility(View.VISIBLE);

        } else {
            List<Mail> mails = null;
            try {
                mails = getHelper().getMailDao().queryBuilder().where().eq("relation_id", relation.getId()).query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ArrayList<String> mailsName = new ArrayList<>();
            for (Mail m : mails)
                mailsName.add("Name: " + m.getName() + "\n" + "Address: " + m.getAddress());

            ArrayAdapter<String> adapter = new ArrayAdapter<>(RelationActivity.this, android.R.layout.simple_list_item_1, mailsName);
            relationItemListView.setAdapter(adapter);
            addContactButton.setVisibility(View.GONE);
        }

        // set onClick event for every item of list
        relationItemListView.setOnItemClickListener(
                (parent, view, position, id) -> showNumberPopup(((TextView) view).getText().toString().split("\n")[0].replace("Name: ", ""))
        );
    }


    public void showNumberPopup(String name) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_relation_list_item);
        TextView popupTextView = dialog.findViewById(R.id.infoTextView);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button deleteButton = dialog.findViewById(R.id.deleteButton);

        popupTextView.setText(String.format("Do you really want to delete %s number?", name));

        cancelButton.setOnClickListener(v -> {
            Log.i("MainActivity", "Cancel button on popup_relation was clicked");
            dialog.dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            if (numberType != null) {

                try {
                    DeleteBuilder<Number, Long> deleteNumberBuilder = getHelper().getNumberDao().deleteBuilder();
                    deleteNumberBuilder.where().eq("relation_id", relation.getId()).and().eq("name", name).and().eq("type", numberType);
                    deleteNumberBuilder.delete();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    DeleteBuilder<Mail, Long> deleteMailBuilder = getHelper().getMailDao().deleteBuilder();
                    deleteMailBuilder.where().eq("relation_id", relation.getId()).and().eq("name", name);
                    deleteMailBuilder.delete();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            Log.i("RelationActivity", "deletedNumber=[" + name + "]");
            Toast.makeText(getBaseContext(), "Number [" + name + "]" + " was deleted.", Toast.LENGTH_LONG).show();

            dialog.dismiss();
            showDataInListView();
        });

        dialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }


    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void onClick_saveButton(View v) {
        boolean checkMode = true;

        switch (mode) {
            case ActivityRequestCode.ADD_RELATION_ACTIVITY_CODE:
                relation = new Relation(relationNameEditText.getText().toString(), new Date().toString());
                List<Relation> existingRelations = new ArrayList<>();
                try {
                    Dao<Relation, Long> relationDao = getHelper().getRelationDao();
                    existingRelations = relationDao.queryBuilder().where().eq("name", relationNameEditText.getText().toString()).query();
                    if(existingRelations.size()==0){
                        relationDao.create(relation);
                    }
                    else{
                        Log.i("RelationActivity", "relation=[" + relationNameEditText.getText().toString() + "] name already exists");
                        Toast toast = Toast.makeText(getBaseContext(), "Relation [" + relation.getName() + "]" + " name already exists.", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                        mode = ActivityRequestCode.ADD_RELATION_ACTIVITY_CODE;
                        checkMode = false;
                        relationNameEditText.setText("");
                        break;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Log.i("RelationActivity", "relation=[" + relation.getName() + "] was created");
                Toast.makeText(getBaseContext(), "Relation [" + relation.getName() + "]" + " was created.", Toast.LENGTH_LONG).show();
                break;

            case ActivityRequestCode.UPDATE_RELATION_ACTIVITY_CODE:
                UpdateBuilder<Relation, Long> relationUpdateBuilder;
                try {
                    relationUpdateBuilder = getHelper().getRelationDao().updateBuilder();
                    relationUpdateBuilder.where().eq("id", relation.getId());
                    relationUpdateBuilder.updateColumnValue("name", relationNameEditText.getText().toString());
                    relationUpdateBuilder.update();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            default:

        }

        if(checkMode) {
            mode = (mode == ActivityRequestCode.UPDATE_RELATION_ACTIVITY_CODE || mode == ActivityRequestCode.ADD_RELATION_ACTIVITY_CODE)
                    ? ActivityRequestCode.VIEW_RELATION_ACTIVITY_CODE : ActivityRequestCode.UPDATE_RELATION_ACTIVITY_CODE;
        }
        switchMode(mode);

    }

    public void onClick_addButton(View v) {
        if (numberType != null) {
            showPhoneNumberPopup();
        } else {
            showEmailPopup();
        }
    }

    public void onClick_addContactButton(View v){
        if(numberType != null){
//           Intent pickNumberIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            Intent pickNumberIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
           startActivityForResult(pickNumberIntent, ActivityRequestCode.PICK_NUMBER_ACTIVITY_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // TODO add validation for numbers, we can't have same number in FROM and TO
        // TODO add validation, we can't have the same number with same type in the same relation
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ActivityRequestCode.PICK_NUMBER_ACTIVITY_CODE) {

            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Contact contact;

                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
/*                if (cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String digits = "";
                    if(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)).length() > 1) {
                        digits = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }

                    digits = digits.replaceAll("(?:(?:(?:-)|(?:(?:\\ ))|(?:(?:\\())|(?:(?:\\)))))", "");

                    saveNumber(name, digits);
                    showDataInListView();

                }*/
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
                }
                String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME};
                Cursor cursor1 = getApplicationContext().getContentResolver().query(contactData, null, null, null, null);
                if (cursor1 != null && cursor1.moveToFirst()) {
                    int numberIndex = cursor1.getColumnIndex(ContactsContract.Contacts._ID);
                    String contactId = cursor1.getString(numberIndex);
                    contact = Contact.from(contactId);
                    int nameColumnIndex = cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    String contactName = cursor1.getString(nameColumnIndex);
                    contact.setContactName(contactName);

                    Cursor numberCursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contact.getContactId()}, null);

                    if (numberCursor != null && numberCursor.moveToFirst()) {
                        int phoneNumberIndex = numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        contact.setContactMobileNumber(numberCursor.getString(phoneNumberIndex));
                    }
                    Log.i("CONTACT_NAME", contact.getContactName());
                    Log.i("CONTACT_NUMBER", contact.getContactMobileNumber());
                    saveNumber(contact.getContactName(), contact.getContactMobileNumber().replaceAll("\\s",""));
                }
//                final ContentResolver contentResolver = getContentResolver();
//                String[] projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Contacts.HAS_PHONE_NUMBER};
//                final Cursor cursor1 = contentResolver.query(ContactsContract.Data.CONTENT_URI, projection, null, null, null);
//                if(cursor1.getInt(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) ==  1){
//                    String id = String.valueOf(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
//                    Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[] {id}, null);
//                    while (phoneCursor.moveToNext()) {
//                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        String normalizedPhoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));
//                        Log.v("myapp", "phone # - " + phoneNumber);
//                        Log.v("myapp", "normalized phone # - " + normalizedPhoneNumber);
//                        saveNumber(id, phoneNumber);
//                    }
//                }

                showDataInListView();

            }

        }
    }

    public void saveNumber(String name, String digits) {
        Number number = new Number(name, digits, numberType, new Date().toString(), relation);
        try {
            getHelper().getNumberDao().create(number);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.i("RelationActivity", "number=[" + name + " (" + digits + ")] was added.");
        Toast.makeText(getBaseContext(), "Number " + name + " (" + digits + ") was added.", Toast.LENGTH_LONG).show();
    }


    public void showEmailPopup() {
        // TODO email name should be displayed in popup
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_add_mail);
        TextView popupTextView = dialog.findViewById(R.id.infoTextView);
        EditText mailNameEditText = dialog.findViewById(R.id.mailNameEditText);
        EditText mailAddressEditText = dialog.findViewById(R.id.mailAddressEdixText);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        popupTextView.setText("Create a new email (not implemented yet)");
        cancelButton.setOnClickListener(v -> {
            Log.i("RelationActivity", "Cancel button on emailPopup was clicked");
            dialog.dismiss();
        });

        saveButton.setOnClickListener(v -> {
            try {
                Mail mail = new Mail(mailNameEditText.getText().toString(), mailAddressEditText.getText().toString(), new Date().toString(), relation);
                getHelper().getMailDao().create(mail);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

            showDataInListView();
        });

        dialog.show();
    }

    public void showPhoneNumberPopup() {
        // TODO email name should be displayed in popup
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_add_number);
        TextView popupTextView = dialog.findViewById(R.id.infoTextView);
        EditText nameEditText = dialog.findViewById(R.id.nameEditText);
        EditText numberEditText = dialog.findViewById(R.id.numberEditText);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        popupTextView.setText("Add new number");
        cancelButton.setOnClickListener(v -> {
            Log.i("RelationActivity", "Cancel button on addNumberPopup was clicked");
            dialog.dismiss();
        });

        saveButton.setOnClickListener(v -> {
            try {
                Number number = new Number(nameEditText.getText().toString(), numberEditText.getText().toString(),numberType,new Date().toString(),relation);
//                number = new Number(nameEditText.getText().toString(), numberEditText.getText().toString(),numberType,new Date().toString(),relation);
                getHelper().getNumberDao().create(number);

            } catch (SQLException e) {
                e.printStackTrace();
            }
            dialog.dismiss();

            showDataInListView();
        });

        dialog.show();
    }

}
