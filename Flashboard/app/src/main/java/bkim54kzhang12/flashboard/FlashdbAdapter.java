package bkim54kzhang12.flashboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by Kevin on 3/2/2015.
 */
public class FlashdbAdapter {

    private SQLiteDatabase db;
    private FlashdbHelper dbHelper;
    private final Context context;

    private static final String DB_NAME = "flashboard.db";
    private static final int DB_VERSION = 3;  // when you add or delete fields, you must update the version number!

    private static final String FLASH_TABLE = "flashcards";
    public static final String FLASH_ID = "flash_id";   // column 0
    public static final String FLASH_SUBJECT = "flash_subject";
    public static final String FLASH_QUESTION = "flash_question";
    public static final String FLASH_ANSWER = "flash_answer";
    public static final String[] FLASH_COLS = {FLASH_ID, FLASH_SUBJECT, FLASH_QUESTION, FLASH_ANSWER};

    public FlashdbAdapter(Context ctx) {
        context = ctx;
        dbHelper = new FlashdbHelper(context, DB_NAME, null, DB_VERSION);
    }

    public void open() throws SQLiteException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        db.close();
    }

    public long insertCard(CardItem card) {
        // create a new row of values to insert
        ContentValues cvalues = new ContentValues();
        // assign values for each col
        cvalues.put(FLASH_SUBJECT, card.getSubject());
        cvalues.put(FLASH_QUESTION, card.getQuestion());
        cvalues.put(FLASH_ANSWER, card.getAnswer());
        // add to course table in database
        return db.insert(FLASH_TABLE, null, cvalues);
    }

    public CardItem getCourseItem(long ri) throws SQLException {
        Cursor cursor = db.query(true, FLASH_TABLE, FLASH_COLS, FLASH_ID+"="+ri, null, null, null, null, null);
        if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
            throw new SQLException("No course items found for row: " + ri);
        }
        // must use column indices to get column values
        int whatIndex = cursor.getColumnIndex(FLASH_QUESTION);
        CardItem result = new CardItem(cursor.getString(whatIndex), cursor.getString(2), cursor.getString(3));
        return result;
    }

    private static class FlashdbHelper extends SQLiteOpenHelper {

        // SQL statement to create a new database table
        private static final String DB_CREATE = "CREATE TABLE " + FLASH_TABLE
                + " (" + FLASH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FLASH_SUBJECT + " TEXT,"
                + FLASH_QUESTION + " TEXT, " + FLASH_ANSWER + " TEXT);";

        public FlashdbHelper(Context context, String name, SQLiteDatabase.CursorFactory fct, int version) {
            super(context, name, fct, version);
        }

        @Override
        public void onCreate(SQLiteDatabase adb) {
            adb.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase adb, int oldVersion, int newVersion) {
            Log.w("GPAdb", "upgrading from version " + oldVersion + " to "
                    + newVersion + ", destroying old data");
            // drop old table if it exists, create new one
            // better to migrate existing data into new table
            adb.execSQL("DROP TABLE IF EXISTS " + FLASH_TABLE);
            onCreate(adb);
        }
    } // FlashdbHelper class

}