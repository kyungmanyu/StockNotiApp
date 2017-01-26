package com.joyful.stock.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class StockDBProvider extends ContentProvider {
	private final static String DATABASE_NAME = "stock.db";
	private SQLiteDatabase mDatabase = null;
	private final static int DATABASE_VERSION = 1;
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	protected String mTableName;

	private static final int JONGMOK_TABLE = 10;
	private static final int JONGMOK_TABLE_SERVER = 20;


	static {
		sURIMatcher.addURI(StockDBConstant.AUTHORITY, StockDBConstant.JONGMOK_TABLE_NAME, JONGMOK_TABLE);
		sURIMatcher.addURI(StockDBConstant.AUTHORITY, StockDBConstant.JONGMOK_TABLE_NAME_SERVER, JONGMOK_TABLE_SERVER);
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		this.mDatabase = new StockDBopenHelpler(getContext(), DATABASE_NAME, null, DATABASE_VERSION)
				.getWritableDatabase();
		if (mDatabase == null) {
			throw new NullPointerException("mDatabase == null!");
		}

		return false;
	}

	  @Override
	    public int delete(Uri uri, String selection, String[] selectionArgs) {
	        // TODO Auto-generated method stub
	        mTableName = getTableName(uri);
	        int result = mDatabase.delete(mTableName, selection, selectionArgs);
	        return result;
	    }

	    @Override
	    public String getType(Uri uri) {
	        // TODO Auto-generated method stub
	        return null;
	    }

	    @Override
	    public Uri insert(Uri uri, ContentValues values) {
	        // TODO Auto-generated method stub
	        mTableName = getTableName(uri);
	        long id = mDatabase.insert(mTableName, null, values);
	        return ContentUris.withAppendedId(uri, id);
	    }

	    @Override
	    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
	            String sortOrder) {
	        // TODO Auto-generated method stub
	        mTableName = getTableName(uri);
	        return mDatabase.query(mTableName, projection, selection, selectionArgs,
	                null, null, null);
	    }

	    @Override
	    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
	        // TODO Auto-generated method stub
	        mTableName = getTableName(uri);
	        int result = mDatabase.update(mTableName, values, selection, selectionArgs);
	        return result;
	    }

	    private String getTableName(Uri uri) {
	        int uriType = sURIMatcher.match(uri);
	        String tableName = null;
	        switch (uriType) {
	        case JONGMOK_TABLE:
	            tableName = StockDBConstant.JONGMOK_TABLE_NAME;
	            break;

	        case JONGMOK_TABLE_SERVER:
	            tableName = StockDBConstant.JONGMOK_TABLE_NAME_SERVER;
	            break;
	        default:
	            throw new IllegalArgumentException("Unknown URI: " + uri);
	        }

	        return tableName;
	    }


}
