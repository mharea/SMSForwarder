package com.mobile.smsforwarder.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Mail {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String address;

    @DatabaseField
    private String gendate;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Relation relation;

    public Mail() {
    }

    public Mail(String name, String address, String gendate, Relation relation) {
        this.name = name;
        this.address = address;
        this.gendate = gendate;
        this.relation = relation;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGendate() {
        return gendate;
    }

    public void setGendate(String gendate) {
        this.gendate = gendate;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", gendate='" + gendate + '\'' +
                ", relation=" + relation.getId() +
                '}';
    }
}
