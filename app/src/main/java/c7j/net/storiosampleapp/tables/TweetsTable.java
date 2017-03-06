package c7j.net.storiosampleapp.tables;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.queries.Query;

/**
 * Created by Ivan.Zh on Q1 2016.
 */

public class TweetsTable {

    @NonNull public static final String TABLE = "tweets";

    @NonNull public static final String COLUMN_ID = "_id";
    @NonNull public static final String COLUMN_AUTHOR = "author";
    @NonNull public static final String COLUMN_CONTENT = "content";


    private TweetsTable() {
        throw new IllegalStateException("No instances please");
    }


    @NonNull
    public static final Query QUERY_ALL = Query.builder()
            .table(TABLE)
            .build();

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_AUTHOR + " TEXT NOT NULL, "
                + COLUMN_CONTENT + " TEXT NOT NULL"
                + ");";
    }
}
