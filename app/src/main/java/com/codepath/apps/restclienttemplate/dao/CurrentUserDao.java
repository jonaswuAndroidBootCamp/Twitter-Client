package com.codepath.apps.restclienttemplate.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.codepath.apps.restclienttemplate.dao.CurrentUser;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CURRENT_USER.
*/
public class CurrentUserDao extends AbstractDao<CurrentUser, Long> {

    public static final String TABLENAME = "CURRENT_USER";

    /**
     * Properties of entity CurrentUser.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property InternalId = new Property(0, Long.class, "internalId", true, "INTERNAL_ID");
        public final static Property Id = new Property(1, Long.class, "id", false, "ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Screen_name = new Property(3, String.class, "screen_name", false, "SCREEN_NAME");
        public final static Property Profile_image_url = new Property(4, String.class, "profile_image_url", false, "PROFILE_IMAGE_URL");
        public final static Property Location = new Property(5, String.class, "location", false, "LOCATION");
    };


    public CurrentUserDao(DaoConfig config) {
        super(config);
    }
    
    public CurrentUserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CURRENT_USER' (" + //
                "'INTERNAL_ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: internalId
                "'ID' INTEGER," + // 1: id
                "'NAME' TEXT," + // 2: name
                "'SCREEN_NAME' TEXT," + // 3: screen_name
                "'PROFILE_IMAGE_URL' TEXT," + // 4: profile_image_url
                "'LOCATION' TEXT);"); // 5: location
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CURRENT_USER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CurrentUser entity) {
        stmt.clearBindings();
 
        Long internalId = entity.getInternalId();
        if (internalId != null) {
            stmt.bindLong(1, internalId);
        }
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(2, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        String screen_name = entity.getScreen_name();
        if (screen_name != null) {
            stmt.bindString(4, screen_name);
        }
 
        String profile_image_url = entity.getProfile_image_url();
        if (profile_image_url != null) {
            stmt.bindString(5, profile_image_url);
        }
 
        String location = entity.getLocation();
        if (location != null) {
            stmt.bindString(6, location);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public CurrentUser readEntity(Cursor cursor, int offset) {
        CurrentUser entity = new CurrentUser( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // internalId
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // screen_name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // profile_image_url
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // location
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CurrentUser entity, int offset) {
        entity.setInternalId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setScreen_name(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setProfile_image_url(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLocation(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(CurrentUser entity, long rowId) {
        entity.setInternalId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(CurrentUser entity) {
        if(entity != null) {
            return entity.getInternalId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
