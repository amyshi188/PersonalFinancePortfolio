package pmk6vc_ds2ca.personalportfoliomanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by varunkulkarni122 on 4/16/16.
 */
public class PortfolioDatabase extends SQLiteOpenHelper {

    private static PortfolioDatabase STATIC_PORTFOLIO; // Required for singleton pattern

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Portfolio.db";
    public static final String TABLE_NAME = "Portfolio";
    public static final String TICKER_INDEX = "Ticker";
    public static final String NAME_INDEX = "Name";
    public static final String PRICE_INDEX = "Price";
    public static final String PC_INDEX = "PercChange";
    public static final String YH_INDEX = "YearHigh";
    public static final String YL_INDEX = "YearLow";
    public static final String EPS_INDEX = "EPS";
    public static final String PE_INDEX = "PriceEarnings";
    public static final String NUMSHARES_INDEX = "NumShares";


    // Implement singleton pattern to ensure static DB reference throughout application
    // See: http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html
    public static synchronized PortfolioDatabase getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (STATIC_PORTFOLIO == null) {
            STATIC_PORTFOLIO = new PortfolioDatabase(context.getApplicationContext());
            Log.d("PortfolioDatabase", "PORTFOLIO CREATED!");
        }
        return STATIC_PORTFOLIO;
    }

    private PortfolioDatabase (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
        Name: varchar([Number of characters])
        Price (cents): integer (note: convert price to cents so they can be represented as integers!)
        % change: float (will need to parse!)
        52 wk high: integer (see Price)
        52 wk low: integer (see Price)
        EPS: float
        P/E: float
        Number of shares: integer
     */
    // Don't forget to add number of shares as a field!
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " " +
                "(" +
                TICKER_INDEX + " varchar(100), " +
                NAME_INDEX + " varchar(100), " +
                PRICE_INDEX + " integer, " +
                PC_INDEX + " float, " +
                YH_INDEX + " integer, " +
                YL_INDEX + " integer, " +
                EPS_INDEX + " float, " +
                PE_INDEX + " float," +
                NUMSHARES_INDEX + " integer" +
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
