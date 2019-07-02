package com.mobile.smsforwarder.model;

import com.j256.ormlite.field.DatabaseField;
import com.mobile.smsforwarder.util.NumberType;

public class Number {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String digits;

    @DatabaseField
    private NumberType type;

    @DatabaseField
    private String gendate;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private Relation relation;


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

    public String getDigits() {
        return digits;
    }

    public void setDigits(String digits) {
        this.digits = digits;
    }

    public NumberType getType() {
        return type;
    }

    public void setType(NumberType type) {
        this.type = type;
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
}
