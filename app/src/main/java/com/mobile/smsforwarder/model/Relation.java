package com.mobile.smsforwarder.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Relation {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField
    private String gendate;


    public Relation() {
    }

    public Relation(String name, String gendate) {
        this.name = name;
        this.gendate = gendate;
    }

    public Relation(int id, String name, String gendate) {
        this.id = id;
        this.name = name;
        this.gendate = gendate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGendate() {
        return gendate;
    }

    public void setGendate(String gendate) {
        this.gendate = gendate;
    }


    @Override
    public String toString() {
        return "Relation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", gendate='" + gendate + '\'' +
                '}';
    }
}
