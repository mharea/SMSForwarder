package com.mobile.smsforwarder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.mobile.smsforwarder.model.Relation;
import com.mobile.smsforwarder.util.DatabaseHelper;

import java.sql.SQLException;
import java.util.Date;

public class AddRelationActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_relation);
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
        EditText relationNameEditText = findViewById(R.id.relationNameEditText);
        Relation relation = new Relation(relationNameEditText.getText().toString(), new Date().toString());
        try {
            Dao<Relation, Long> relationDao = getHelper().getRelationDao();
            relationDao.create(relation);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        relationNameEditText.getText().clear();

    }


}
