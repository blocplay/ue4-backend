/*
 * This file is generated by jOOQ.
 */
package com.tokenplay.ue4.model.db.tables.pojos;


import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


/**
 * This class is generated by jOOQ.
 */
@Generated(value = {
    "http://www.jooq.org", "jOOQ version:3.10.6"}, comments = "This class is generated by jOOQ")
@SuppressWarnings({
    "all", "unchecked", "rawtypes"})
public class LoreHolidays implements Serializable {

    private static final long serialVersionUID = -1080997795;

    private String lholId;
    private String lholName;
    private String lholTheme;
    private String lholTnDate;
    private String lholWhere;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public LoreHolidays() {
    }

    public LoreHolidays(LoreHolidays value) {
        this.lholId = value.lholId;
        this.lholName = value.lholName;
        this.lholTheme = value.lholTheme;
        this.lholTnDate = value.lholTnDate;
        this.lholWhere = value.lholWhere;
        this.createdAt = value.createdAt;
        this.updatedAt = value.updatedAt;
    }

    public LoreHolidays(String lholId, String lholName, String lholTheme, String lholTnDate, String lholWhere, Timestamp createdAt,
        Timestamp updatedAt) {
        this.lholId = lholId;
        this.lholName = lholName;
        this.lholTheme = lholTheme;
        this.lholTnDate = lholTnDate;
        this.lholWhere = lholWhere;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @NotNull
    @Size(max = 32)
    public String getLholId() {
        return this.lholId;
    }

    public void setLholId(String lholId) {
        this.lholId = lholId;
    }

    @NotNull
    @Size(max = 255)
    public String getLholName() {
        return this.lholName;
    }

    public void setLholName(String lholName) {
        this.lholName = lholName;
    }

    @NotNull
    @Size(max = 255)
    public String getLholTheme() {
        return this.lholTheme;
    }

    public void setLholTheme(String lholTheme) {
        this.lholTheme = lholTheme;
    }

    @NotNull
    @Size(max = 255)
    public String getLholTnDate() {
        return this.lholTnDate;
    }

    public void setLholTnDate(String lholTnDate) {
        this.lholTnDate = lholTnDate;
    }

    @NotNull
    @Size(max = 255)
    public String getLholWhere() {
        return this.lholWhere;
    }

    public void setLholWhere(String lholWhere) {
        this.lholWhere = lholWhere;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("LoreHolidays (");

        sb.append(lholId);
        sb.append(", ").append(lholName);
        sb.append(", ").append(lholTheme);
        sb.append(", ").append(lholTnDate);
        sb.append(", ").append(lholWhere);
        sb.append(", ").append(createdAt);
        sb.append(", ").append(updatedAt);

        sb.append(")");
        return sb.toString();
    }
}
