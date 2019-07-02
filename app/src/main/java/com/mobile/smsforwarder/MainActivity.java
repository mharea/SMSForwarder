package com.mobile.smsforwarder;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.mobile.smsforwarder.model.Relation;
import com.mobile.smsforwarder.util.ActivityRequestCode;
import com.mobile.smsforwarder.util.DatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    // This is how DatabaseHelper is initialized
    private DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }


    public void initData(){
        ListView relationListView = findViewById(R.id.relationListView);

        List<Relation> relations = null;
        try {
            relations = getHelper().getRelationDao().queryForAll();
            //relations = getHelper().getRelationDao().queryBuilder().where().eq("name","ISD").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ArrayList<String> relationNames = new ArrayList<>();
        for (Relation r : relations)
            relationNames.add(r.toString());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, relationNames);
        relationListView.setAdapter(adapter);
    }


    public void onClick_saveButton(View v) {
        Relation relation = new Relation();
        relation.setName("DSV-GROUP");
        relation.setGendate("11-05-2005");

        try {
            final Dao<Relation, Long> relationDao = getHelper().getRelationDao();
            relationDao.create(relation);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        initData();
    }

    public void onClick_addButton(View v) {
        Intent addRelationIntent = new Intent(MainActivity.this, AddRelationActivity.class);
        //addRelationIntent.putExtra(NumberType.class.getName(), chosenNumberType.toString());
        startActivityForResult(addRelationIntent, ActivityRequestCode.ADD_RELATION_ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode.ADD_RELATION_ACTIVITY_CODE) {
            initData();
        }
    }

}
