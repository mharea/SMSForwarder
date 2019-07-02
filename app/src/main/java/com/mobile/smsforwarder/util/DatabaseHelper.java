package com.mobile.smsforwarder.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mobile.smsforwarder.R;
import com.mobile.smsforwarder.model.Mail;
import com.mobile.smsforwarder.model.Number;
import com.mobile.smsforwarder.model.Relation;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    //information of database
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "smsforwarder_db.db";

    private Dao<Mail, Long> mailDao;
    private Dao<Number, Long> numberDao;
    private Dao<Relation, Long> relationDao;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        // Create Table with given table name with columnName
        try {
            TableUtils.createTable(connectionSource, Relation.class);
            TableUtils.createTable(connectionSource, Mail.class);
            TableUtils.createTable(connectionSource, Number.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Mail.class, true);
            TableUtils.dropTable(connectionSource, Number.class, true);
            TableUtils.dropTable(connectionSource, Relation.class, true);
            onCreate(database, connectionSource);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<Relation, Long> getRelationDao() throws SQLException {
        if (relationDao == null) {
            relationDao = getDao(Relation.class);
        }
        return relationDao;
    }

    public Dao<Mail, Long> getMailDao() throws SQLException {
        if (mailDao == null) {
            mailDao = getDao(Mail.class);
        }
        return mailDao;
    }


    public Dao<Number, Long> getNumberDao() throws SQLException {
        if (numberDao == null) {
            numberDao = getDao(Number.class);
        }
        return numberDao;
    }

}
