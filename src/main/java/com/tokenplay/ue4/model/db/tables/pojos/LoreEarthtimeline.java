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
public class LoreEarthtimeline implements Serializable {

    private static final long serialVersionUID = 1308220493;

    private String learthId;
    private String learthEra;
    private String learthDates;
    private String learthDescrip;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public LoreEarthtimeline() {
    }

    public LoreEarthtimeline(LoreEarthtimeline value) {
        this.learthId = value.learthId;
        this.learthEra = value.learthEra;
        this.learthDates = value.learthDates;
        this.learthDescrip = value.learthDescrip;
        this.createdAt = value.createdAt;
        this.updatedAt = value.updatedAt;
    }

    public LoreEarthtimeline(String learthId, String learthEra, String learthDates, String learthDescrip, Timestamp createdAt, Timestamp updatedAt) {
        this.learthId = learthId;
        this.learthEra = learthEra;
        this.learthDates = learthDates;
        this.learthDescrip = learthDescrip;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @NotNull
    @Size(max = 32)
    public String getLearthId() {
        return this.learthId;
    }

    public void setLearthId(String learthId) {
        this.learthId = learthId;
    }

    @NotNull
    @Size(max = 255)
    public String getLearthEra() {
        return this.learthEra;
    }

    public void setLearthEra(String learthEra) {
        this.learthEra = learthEra;
    }

    @NotNull
    @Size(max = 255)
    public String getLearthDates() {
        return this.learthDates;
    }

    public void setLearthDates(String learthDates) {
        this.learthDates = learthDates;
    }

    @NotNull
    @Size(max = 255)
    public String getLearthDescrip() {
        return this.learthDescrip;
    }

    public void setLearthDescrip(String learthDescrip) {
        this.learthDescrip = learthDescrip;
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
        StringBuilder sb = new StringBuilder("LoreEarthtimeline (");

        sb.append(learthId);
        sb.append(", ").append(learthEra);
        sb.append(", ").append(learthDates);
        sb.append(", ").append(learthDescrip);
        sb.append(", ").append(createdAt);
        sb.append(", ").append(updatedAt);

        sb.append(")");
        return sb.toString();
    }
}
