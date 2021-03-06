package com.afap.discuz.chh.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.afap.discuz.chh.greendao.CategoryListAtom;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CATEGORY_LIST_ATOM".
*/
public class CategoryListAtomDao extends AbstractDao<CategoryListAtom, Void> {

    public static final String TABLENAME = "CATEGORY_LIST_ATOM";

    /**
     * Properties of entity CategoryListAtom.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Cat = new Property(0, String.class, "cat", false, "CAT");
        public final static Property Href = new Property(1, String.class, "href", false, "HREF");
        public final static Property Title = new Property(2, String.class, "title", false, "TITLE");
        public final static Property Content = new Property(3, String.class, "content", false, "CONTENT");
        public final static Property Time = new Property(4, String.class, "time", false, "TIME");
        public final static Property Thumb_url = new Property(5, String.class, "thumb_url", false, "THUMB_URL");
    };


    public CategoryListAtomDao(DaoConfig config) {
        super(config);
    }
    
    public CategoryListAtomDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CATEGORY_LIST_ATOM\" (" + //
                "\"CAT\" TEXT," + // 0: cat
                "\"HREF\" TEXT," + // 1: href
                "\"TITLE\" TEXT," + // 2: title
                "\"CONTENT\" TEXT," + // 3: content
                "\"TIME\" TEXT," + // 4: time
                "\"THUMB_URL\" TEXT);"); // 5: thumb_url
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CATEGORY_LIST_ATOM\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, CategoryListAtom entity) {
        stmt.clearBindings();
 
        String cat = entity.getCat();
        if (cat != null) {
            stmt.bindString(1, cat);
        }
 
        String href = entity.getHref();
        if (href != null) {
            stmt.bindString(2, href);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(3, title);
        }
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(4, content);
        }
 
        String time = entity.getTime();
        if (time != null) {
            stmt.bindString(5, time);
        }
 
        String thumb_url = entity.getThumb_url();
        if (thumb_url != null) {
            stmt.bindString(6, thumb_url);
        }
    }

    /** @inheritdoc */
    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    /** @inheritdoc */
    @Override
    public CategoryListAtom readEntity(Cursor cursor, int offset) {
        CategoryListAtom entity = new CategoryListAtom( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // cat
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // href
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // title
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // content
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // time
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // thumb_url
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, CategoryListAtom entity, int offset) {
        entity.setCat(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setHref(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTitle(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTime(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setThumb_url(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Void updateKeyAfterInsert(CategoryListAtom entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    /** @inheritdoc */
    @Override
    public Void getKey(CategoryListAtom entity) {
        return null;
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
