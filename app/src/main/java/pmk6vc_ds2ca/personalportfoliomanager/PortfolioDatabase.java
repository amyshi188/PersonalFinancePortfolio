package pmk6vc_ds2ca.personalportfoliomanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by varunkulkarni122 on 4/16/16.
 */
public class PortfolioDatabase extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Portfolio.db";


    public PortfolioDatabase (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // NEED TO UPDATE FOR STOCK INFO
    /*
        Name: varchar([Number of characters])
        Price (cents): integer (note: convert price to cents so they can be represented as integers!)
        % change: float (will need to parse!)
        52 wk high: integer (see Price)
        52 wk low: integer (see Price)
        EPS: float
        P/E: float
     */
    // Don't forget to add number of shares as a field!
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Portfolio " +
                "(" +
                "Name varchar(100), " +
                "Price integer, " +
                "PercChange float, " +
                "YearHigh integer, " +
                "YearLow integer, " +
                "EPS float, " +
                "PriceEarnings float" +
                ")"
        );
    }


    // See if this needs updating
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("delete table Portfolio");
        onCreate(db);
    }

}
