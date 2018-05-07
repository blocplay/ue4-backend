/*
 * This file is generated by jOOQ.
 */
package com.tokenplay.ue4.model.db.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import com.tokenplay.ue4.model.db.DefaultSchema;
import com.tokenplay.ue4.model.db.Indexes;
import com.tokenplay.ue4.model.db.Keys;
import com.tokenplay.ue4.model.db.tables.records.UiThemeAttributeRecord;

import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(value = {
    "http://www.jooq.org", "jOOQ version:3.10.6"}, comments = "This class is generated by jOOQ")
@SuppressWarnings({
    "all", "unchecked", "rawtypes"})
public class Tue4UiThemeAttribute extends TableImpl<UiThemeAttributeRecord> {

    private static final long serialVersionUID = 496795880;

    /**
     * The reference instance of <code>tue4_ui_theme_attribute</code>
     */
    public static final Tue4UiThemeAttribute UI_THEME_ATTRIBUTE = new Tue4UiThemeAttribute();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UiThemeAttributeRecord> getRecordType() {
        return UiThemeAttributeRecord.class;
    }

    /**
     * The column <code>tue4_ui_theme_attribute.uta_id</code>.
     */
    public final TableField<UiThemeAttributeRecord, String> UTA_ID = createField("uta_id", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>tue4_ui_theme_attribute.uta_uth_id</code>.
     */
    public final TableField<UiThemeAttributeRecord, String> UTA_UTH_ID = createField("uta_uth_id", org.jooq.impl.SQLDataType.CLOB.nullable(false),
        this, "");

    /**
     * The column <code>tue4_ui_theme_attribute.uta_uat_id</code>.
     */
    public final TableField<UiThemeAttributeRecord, String> UTA_UAT_ID = createField("uta_uat_id", org.jooq.impl.SQLDataType.CLOB.nullable(false),
        this, "");

    /**
     * The column <code>tue4_ui_theme_attribute.uta_value</code>.
     */
    public final TableField<UiThemeAttributeRecord, String> UTA_VALUE = createField("uta_value", org.jooq.impl.SQLDataType.CLOB.nullable(false),
        this, "");

    /**
     * Create a <code>tue4_ui_theme_attribute</code> table reference
     */
    public Tue4UiThemeAttribute() {
        this(DSL.name("tue4_ui_theme_attribute"), null);
    }

    /**
     * Create an aliased <code>tue4_ui_theme_attribute</code> table reference
     */
    public Tue4UiThemeAttribute(String alias) {
        this(DSL.name(alias), UI_THEME_ATTRIBUTE);
    }

    /**
     * Create an aliased <code>tue4_ui_theme_attribute</code> table reference
     */
    public Tue4UiThemeAttribute(Name alias) {
        this(alias, UI_THEME_ATTRIBUTE);
    }

    private Tue4UiThemeAttribute(Name alias, Table<UiThemeAttributeRecord> aliased) {
        this(alias, aliased, null);
    }

    private Tue4UiThemeAttribute(Name alias, Table<UiThemeAttributeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.IDX_16815_Kue4_UTA_AUT_FK, Indexes.IDX_16815_Kue4_UTA_UTH_ID_NAME_UK, Indexes.UI_THEME_ATTRIBUTE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<UiThemeAttributeRecord> getPrimaryKey() {
        return Keys.UI_THEME_ATTRIBUTE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<UiThemeAttributeRecord>> getKeys() {
        return Arrays.<UniqueKey<UiThemeAttributeRecord>>asList(Keys.UI_THEME_ATTRIBUTE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tue4UiThemeAttribute as(String alias) {
        return new Tue4UiThemeAttribute(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tue4UiThemeAttribute as(Name alias) {
        return new Tue4UiThemeAttribute(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Tue4UiThemeAttribute rename(String name) {
        return new Tue4UiThemeAttribute(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Tue4UiThemeAttribute rename(Name name) {
        return new Tue4UiThemeAttribute(name, null);
    }
}