/***********************************************************************
 *        FILE:
 * DESCRIPTION:
 *        DATE:
 *          BY: Darren Ting
 *
 **********************************************************************/
package com.fun_picks.fpphoto;

import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import android.widget.Toast;

public class PBudDBAdapter extends Activity{
	private static final String TAG = "PBudDBAdapter";
	private static final String DATABASE_NAME = "photobuddy.db";
	private static final String DATABASE_TABLE = "pbudItems";
	private static final String DATABASE_SCORES_TABLE = "ScoresTable";
	private static final String DATABASE_AWARD_TABLE = "awardTable";
    private static final String DATABASE_FAVORITES_TABLE = "favoritesTable";
    private static final String DATABASE_TEST_TABLE = "testTable";
	private static final int DATABASE_VERSION = 1;

	// Photo image item table
	public static final String KEY_ID = "_id";
	public static final String KEY_CREATION_DATE = "_creation_date";
	public static final String KEY_SEQUENCE = "_sequence";
	public static final String KEY_MODE = "_mode";
	public static final String KEY_GROUP = "_group";
	public static final String KEY_GROUP_SEQ = "_group_seq";
	public static final String KEY_RATING = "_rating";
	public static final String KEY_FLAG = "_flag";
	public static final String KEY_IMAGE_FILE = "_img_file";
	public static final String KEY_COMMENTS = "_img_comments";
    public static final String KEY_LATITUDE = "_latitude";
    public static final String KEY_LONGITUDE = "_longitude";
	public static final String KEY_LOCATION = "_location";
	public static final String KEY_STR_USER1 = "_str_user1";
	public static final String KEY_STR_USER2 = "_str_user2";
	public static final String KEY_LONG_USER3 = "_long_user3";
	public static final String KEY_LONG_USER4 = "_long_user4";


	// Scoreboard Table
	public static final String KEY_ST_ID = "_id";
	public static final String KEY_ST_USER_NAME = "_user_name";
	public static final String KEY_ST_CREATION_DATE = "_creation_date";
	public static final String KEY_ST_CREATION_TIME = "_creation_time";
	public static final String KEY_ST_GAME_MODE = "_game_mode";
	public static final String KEY_ST_SCORE = "_score";
	public static final String KEY_ST_SCORE_TIME = "_score_time";
	
	// Award Table
	public static final String KEY_AT_ID = "_id";
	public static final String KEY_AT_USER_NAME = "_user_name";
	public static final String KEY_AT_CREATION_TIME = "_creation_time";
	public static final String KEY_AT_GAME_MODE = "_game_mode";
	public static final String KEY_AT_AWARD_ID = "_award_id";
	public static final String KEY_AT_AWARD_LEVEL = "_award_level";

    // Favorites Table
    public static final String KEY_FT_ID = "_id";
    public static final String KEY_FT_USER_NAME = "_user_name";
    public static final String KEY_FT_FAVORITE_IMAGE_ID = "_favorite_image_id";

    public static final String KEY_TT_ID = "_id";
    public static final String KEY_TT_IMAGE_ID = "_test_image_id";

    public static final String DB_ERROR_RET_STRING = "NO ITEM";

	private SQLiteDatabase db;
	private final Context context;
	private pBudDBOpenHelper dbHelper;

	public PBudDBAdapter(Context _context) {
		this.context = _context;
		dbHelper = new pBudDBOpenHelper(context, DATABASE_NAME,
				null, DATABASE_VERSION);
	}

	public void close() {
		db.close();
	}

	public void open() throws SQLiteException {  
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			Log.e(TAG, " open(): getWritableDatabase failed.");
			//db = dbHelper.getReadableDatabase();
		}
	}

    public void delete(){
        this.deleteDatabase(DATABASE_NAME);
    }

	public long insertItem(long _sequence, long _mode, long _group, long _groupSeq, long _rating, long _flag, String _imageFile, String _comments ) {
        // Create a new row of values to insert.
        ContentValues newTaskValues = new ContentValues();
        // Assign values for each row.
        newTaskValues.put(KEY_CREATION_DATE, new Date(java.lang.System.currentTimeMillis()).getTime());
        newTaskValues.put(KEY_SEQUENCE, _sequence);
        newTaskValues.put(KEY_MODE, _mode);
        newTaskValues.put(KEY_GROUP, _group);
        newTaskValues.put(KEY_GROUP_SEQ, _groupSeq);
        newTaskValues.put(KEY_RATING, _rating);
		newTaskValues.put(KEY_FLAG, _flag);
        newTaskValues.put(KEY_IMAGE_FILE, _imageFile);
        newTaskValues.put(KEY_COMMENTS, _comments);
        // Insert the row.
        return db.insert(DATABASE_TABLE, null, newTaskValues);
    }

    public long insertItem(long _sequence, long _mode, long _group, long _groupSeq, long _rating, long _flag,String _imageFile, String _comments, Float _latitude, Float _longitude, String _location ) {
        // Create a new row of values to insert.
        ContentValues newTaskValues = new ContentValues();
        // Assign values for each row.
        newTaskValues.put(KEY_CREATION_DATE, new Date(java.lang.System.currentTimeMillis()).getTime());
        newTaskValues.put(KEY_SEQUENCE, _sequence);
        newTaskValues.put(KEY_MODE, _mode);
        newTaskValues.put(KEY_GROUP, _group);
        newTaskValues.put(KEY_GROUP_SEQ, _groupSeq);
        newTaskValues.put(KEY_RATING, _rating);
		newTaskValues.put(KEY_FLAG, _flag);
        newTaskValues.put(KEY_IMAGE_FILE, _imageFile);
        newTaskValues.put(KEY_COMMENTS, _comments);
        newTaskValues.put(KEY_LATITUDE, _latitude);
        newTaskValues.put(KEY_LONGITUDE, _longitude);
		newTaskValues.put(KEY_LOCATION, _location);
        // Insert the row.
        return db.insert(DATABASE_TABLE, null, newTaskValues);
    }
	
	public boolean deleteAllItemsInTable() {

		return db.delete(DATABASE_TABLE, "1", null) > 0;
	}

	public Cursor getAllItemsCursorByRatingSortSequenceAscend(long _rating) {
		return db.query(DATABASE_TABLE, 
				new String[] { KEY_ID, KEY_SEQUENCE, KEY_MODE, KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS },
				KEY_RATING + " = " + _rating + " ORDER BY " + KEY_SEQUENCE + " ASC", null, null, null, null);
	}
	
	public Cursor getAllItemsCursorGreaterThanEqualRatingSortSequenceAscend(long _rating) {
		return db.query(DATABASE_TABLE, 
				new String[] { KEY_ID, KEY_SEQUENCE, KEY_MODE, KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS },
				KEY_RATING + " >= " + _rating + " ORDER BY " + KEY_SEQUENCE + " ASC", null, null, null, null);
	}
	
	public Cursor getAllItemsCursorLessThanRatingSortSequenceAscend(long _rating) {
		return db.query(DATABASE_TABLE, 
				new String[] { KEY_ID, KEY_SEQUENCE, KEY_MODE, KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS },
				KEY_RATING + " <= " + _rating + " ORDER BY " + KEY_SEQUENCE + " ASC", null, null, null, null);
	}
	
	public Cursor getAllItemsCursorBetweenRatingsSortSequenceAscend(long _rating_low, long _rating_high) {
		return db.query(DATABASE_TABLE, 
				new String[] { KEY_ID, KEY_SEQUENCE,KEY_MODE,  KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS },
				KEY_RATING + " BETWEEN " + _rating_low + " AND " + _rating_high +" ORDER BY " + KEY_SEQUENCE + " ASC", null, null, null, null);
	}
	
	public Cursor getAllItemsCursorByModeBetweenRatingsSortSequenceAscend(long _mode, long _rating_low, long _rating_high) {
		return db.query(DATABASE_TABLE, 
				new String[] { KEY_ID, KEY_SEQUENCE, KEY_MODE,  KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS },
				KEY_MODE + " = " + _mode + " AND " + KEY_RATING + " BETWEEN " + _rating_low + " AND " + _rating_high +" ORDER BY " + KEY_SEQUENCE + " ASC", null, null, null, null);
	}


	
	public Cursor getAllItemsCursorSortSequenceAscend(long _rating) {
		return db.query(DATABASE_TABLE, 
				new String[] { KEY_ID, KEY_SEQUENCE, KEY_MODE, KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS },
				" ORDER BY " + KEY_SEQUENCE + " ASC", null, null, null, null);
	}

	public Cursor getAllItemsCursorByGroupSortSeqAscend(long _group) {
		return db.query(DATABASE_TABLE, 
				new String[] { KEY_ID, KEY_SEQUENCE,KEY_MODE,  KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS },
				KEY_GROUP + "=" + _group + " ORDER BY " + KEY_SEQUENCE + " ASC", null, null, null, null);
	}
	
	public Cursor getAllItemsCursorBySequence(long _sequence) {
		return db.query(DATABASE_TABLE, 
				new String[] { KEY_ID, KEY_SEQUENCE, KEY_MODE, KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS, KEY_LATITUDE, KEY_LONGITUDE, KEY_LOCATION },
				KEY_SEQUENCE + "=" + _sequence + " ORDER BY " + KEY_SEQUENCE + " ASC", null, null, null, null);
	}

    public long deleteItemsBySequence(long _sequence ) throws SQLException{
        int ret;
        try {
            ret = db.delete(DATABASE_TABLE,
                    KEY_SEQUENCE + " = " + _sequence, null);
            //        + " AND " + KEY_FT_USER_NAME + " = " + _userName

        }
        catch (Exception e)
        {
            Toast.makeText(this, " deleteItemsBySequence(): No item found for id: " + _sequence,
                    Toast.LENGTH_LONG).show();
            throw new SQLException("deleteItemsBySequence: No item found for id: " + _sequence);

        }

        return ret;
    }
	
	public Cursor getAllItemsCursorWithGroupNumber() {
		return db.query(true, DATABASE_TABLE, 
				new String[] { KEY_ID, KEY_SEQUENCE, KEY_MODE, KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS },
				KEY_GROUP + "<>" + " 0 ",null, KEY_GROUP, null, null, null);
	}

	public int getRatingItemCount(long _rating) throws SQLException{
		Cursor cursor = db.query(DATABASE_TABLE, 
				new String[] { KEY_ID, KEY_SEQUENCE, KEY_MODE, KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS },
				KEY_RATING + "=" + _rating, null, null, null, null);
		int count = cursor.getCount(); 
		cursor.close();
		return count;  
	}

	public int getGroupItemCount(long _group) throws SQLException{
		Cursor cursor = db.query(DATABASE_TABLE,
				new String[] { KEY_ID, KEY_SEQUENCE, KEY_MODE, KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE,KEY_COMMENTS },
				KEY_GROUP + "=" + _group, null, null, null, null);
		int count = cursor.getCount(); 
		cursor.close();
		return count;  
	}

	public int getTotalGroupCount() throws SQLException {
		Cursor cursor = db.query(true, DATABASE_TABLE,
				new String[]{KEY_ID, KEY_SEQUENCE, KEY_MODE, KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE, KEY_COMMENTS},
				KEY_GROUP + " > 0", null, KEY_GROUP, null, null,null,null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

    public int getTotalItemCount() throws SQLException {
        Cursor cursor = db.query(DATABASE_TABLE,
                new String[]{KEY_ID, KEY_SEQUENCE, KEY_MODE, KEY_GROUP, KEY_GROUP_SEQ, KEY_RATING, KEY_FLAG, KEY_IMAGE_FILE, KEY_COMMENTS},
                KEY_SEQUENCE + " > 0", null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

        private static class pBudDBOpenHelper extends SQLiteOpenHelper {

		public pBudDBOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// SQL Statement to create a new database.
		private static final String DATABASE_ITEM_TABLE_CREATE = "create table " + 
		DATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, " +
		KEY_CREATION_DATE + " long,"
				+ KEY_SEQUENCE + " long, "
				+ KEY_MODE + " long, "
				+ KEY_GROUP + " long, "
				+ KEY_GROUP_SEQ + " long, "
				+ KEY_IMAGE_FILE + " text not null, "
				+ KEY_COMMENTS + " text not null, "
				+ KEY_LATITUDE + " real,"
				+ KEY_LONGITUDE + " real,"
				+ KEY_LOCATION + " text not null, "
				+ KEY_FLAG + " long, "
				+ KEY_RATING + " long); ";

		/*	+ KEY_STR_USER1 + " text not null, "
					+ KEY_STR_USER2 + " text not null, "
					+ KEY_LONG_USER3 + " long, "
					+ KEY_LONG_USER4 + " long); ";*/

		private static final String DATABASE_SCORES_TABLE_CREATE = "create table " + 
				DATABASE_SCORES_TABLE + " (" + KEY_ST_ID + " integer primary key autoincrement, " +
				KEY_ST_USER_NAME + " text not null, " 
				+ KEY_ST_CREATION_DATE + " long, " 
				+ KEY_ST_CREATION_TIME + " long, "
				+ KEY_ST_GAME_MODE + " long, "
				+ KEY_ST_SCORE + " long, "
				+ KEY_ST_SCORE_TIME + " long);";
		
		private static final String DATABASE_AWARD_TABLE_CREATE = "create table " + 
				DATABASE_AWARD_TABLE + " (" + KEY_AT_ID + " integer primary key autoincrement, " +
				KEY_AT_USER_NAME + " text not null, " 
				+ KEY_AT_CREATION_TIME + " long, "
				+ KEY_AT_GAME_MODE + " long, "
				+ KEY_AT_AWARD_ID + " long, "
				+ KEY_AT_AWARD_LEVEL + " long);";

        private static final String DATABASE_FAVORITES_TABLE_CREATE = "create table " +
                DATABASE_FAVORITES_TABLE + " (" + KEY_FT_ID + " integer primary key autoincrement, " +
                KEY_FT_USER_NAME + " text not null, "
                + KEY_FT_FAVORITE_IMAGE_ID + " long);";

        private static final String DATABASE_TEST_TABLE_CREATE = "create table " +
                    DATABASE_TEST_TABLE + " (" + KEY_TT_ID + " integer primary key autoincrement, "
                    + KEY_TT_IMAGE_ID + " long);";
		
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_ITEM_TABLE_CREATE);
			_db.execSQL(DATABASE_SCORES_TABLE_CREATE);
			_db.execSQL(DATABASE_AWARD_TABLE_CREATE);
            _db.execSQL(DATABASE_FAVORITES_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
			Log.w("TaskDBAdapter", "Upgrading from version " + 
					_oldVersion + " to " +
					_newVersion + ", which will destroy all old data");

            switch(_oldVersion){
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    //_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
                    //_db.execSQL(DATABASE_ITEM_TABLE_CREATE);
                    //_db.execSQL(DATABASE_TEST_TABLE_CREATE);

                    break;
                default:
                    break;
            }
/*			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_SCORES_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_AWARD_TABLE);
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_FAVORITES_TABLE);

			onCreate(_db);*/
		}
		
	}

	public long insertAward(String _userName, long _gameMode, long _award_id, long _award_level ) {
		// Create a new row of values to insert.
		ContentValues newTaskValues = new ContentValues();
		// Assign values for each row.
		
		newTaskValues.put(KEY_AT_USER_NAME, _userName);
		newTaskValues.put(KEY_AT_CREATION_TIME,java.lang.System.currentTimeMillis());
		newTaskValues.put(KEY_AT_GAME_MODE, _gameMode);
		newTaskValues.put(KEY_AT_AWARD_ID, _award_id);
		newTaskValues.put(KEY_AT_AWARD_LEVEL, _award_level);

		// Insert the row.
		return db.insert(DATABASE_AWARD_TABLE, null, newTaskValues);
	}
	
	public Cursor getAllAwardsCursorByMode(long _gameMode) {
		return db.query(DATABASE_AWARD_TABLE, 
				new String[] { KEY_AT_ID, KEY_AT_USER_NAME, KEY_AT_CREATION_TIME, KEY_AT_GAME_MODE, KEY_AT_AWARD_ID, KEY_AT_AWARD_LEVEL },
				KEY_AT_GAME_MODE + " = " + _gameMode + " ORDER BY " + KEY_AT_AWARD_LEVEL + " ASC", null, null, null, null);
	}
	
	public Cursor getAllAwardsCursorAll() {
		return db.query(DATABASE_AWARD_TABLE, 
				new String[] { KEY_AT_ID, KEY_AT_USER_NAME, KEY_AT_CREATION_TIME, KEY_AT_GAME_MODE, KEY_AT_AWARD_ID, KEY_AT_AWARD_LEVEL },
				KEY_AT_AWARD_ID + " >= 0 " + " ORDER BY " + KEY_AT_AWARD_LEVEL + " ASC", null, null, null, null);
	}

    public long insertFavorite(String _userName, long _image_id )throws SQLException {
        // Create a new row of values to insert.
        ContentValues newTaskValues = new ContentValues();
        // Assign values for each row.

        newTaskValues.put(KEY_FT_USER_NAME, _userName);
        newTaskValues.put(KEY_FT_FAVORITE_IMAGE_ID, _image_id);

        // Insert the row.
        return db.insert(DATABASE_FAVORITES_TABLE, null, newTaskValues);
    }

    public boolean getFavoritesCursorByImageId(String _userName, long _image_id )throws SQLException {
        Cursor cursor = db.query(DATABASE_FAVORITES_TABLE,
                new String[] { KEY_FT_ID, KEY_FT_USER_NAME,  KEY_FT_FAVORITE_IMAGE_ID },
                KEY_FT_FAVORITE_IMAGE_ID + " = " + _image_id,
                null, null, null, null);


        if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
            //throw new SQLException("No favorite ID found for id: " + _image_id);
            return (false);
        } else
            return(true);
    }

    public Cursor getAllFavoritesCursorByUser(String _userName)throws SQLException {
        return db.query(DATABASE_FAVORITES_TABLE,
                new String[] { KEY_FT_ID, KEY_FT_USER_NAME,  KEY_FT_FAVORITE_IMAGE_ID },
                KEY_FT_FAVORITE_IMAGE_ID + " > 0 " + " ORDER BY " + KEY_FT_FAVORITE_IMAGE_ID + " ASC", null, null, null, null);
    }

    public long deleteFavoriteByImageId(String _userName, long _image_id ) throws SQLException{
        int ret;
        try {
            ret = db.delete(DATABASE_FAVORITES_TABLE,
                    KEY_FT_FAVORITE_IMAGE_ID + " = " + _image_id, null);
                    //        + " AND " + KEY_FT_USER_NAME + " = " + _userName

        }
        catch (Exception e)
        {
            Toast.makeText(this, " deleteFavoriteByImageId(): No item found for id: " + _image_id,
                    Toast.LENGTH_LONG).show();
            throw new SQLException("deleteFavoriteByImageId: No item found for id: " + _image_id);

        }

        return ret;
    }

   

}