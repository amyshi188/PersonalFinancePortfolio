package pmk6vc_ds2ca.personalportfoliomanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by varunkulkarni122 on 4/28/16.
 */
public class SummaryActivity extends AppCompatActivity {

    private static ArrayList<Stock> portfolioList = new ArrayList<>(); // Holds most up to date set of stocks
    private String toastMsgNoDB = "No pre-existing portfolio. Try adding positions!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary); // Set summary layout here
        // Check if DB exists
        if (databaseExists(getApplicationContext())) {
            // Query DB here
            updatePortfolio();
        } else {
            // Indicate no DB yet
            SummaryActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(SummaryActivity.this, toastMsgNoDB, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        // Check if DB exists
        if (databaseExists(getApplicationContext())) {
            updatePortfolio();
        };
        super.onResume();
    }

    // Update portfolio by clearing list and querying DB
    public void updatePortfolio() {
        portfolioList.clear();
        Log.d("SummaryActivity", "Portfolio cleared, ready for update...");

        SQLiteDatabase staticDB = PortfolioDatabase.getInstance(this).getWritableDatabase();
        String tableName = PortfolioDatabase.TABLE_NAME;
        Cursor cursor = staticDB.rawQuery("select * from " + tableName, null);
        if (cursor .moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                // Create stock object by querying DB and add to list
                String ticker = cursor.getString(cursor.getColumnIndex(PortfolioDatabase.TICKER_INDEX));
                String name = cursor.getString(cursor.getColumnIndex(PortfolioDatabase.NAME_INDEX));
                int price = cursor.getInt(cursor.getColumnIndex(PortfolioDatabase.PRICE_INDEX));
                float percChange = cursor.getFloat(cursor.getColumnIndex(PortfolioDatabase.PC_INDEX));
                int yearHigh = cursor.getInt(cursor.getColumnIndex(PortfolioDatabase.YH_INDEX));
                int yearLow = cursor.getInt(cursor.getColumnIndex(PortfolioDatabase.YL_INDEX));
                float eps = cursor.getFloat(cursor.getColumnIndex(PortfolioDatabase.EPS_INDEX));
                float pe = cursor.getFloat(cursor.getColumnIndex(PortfolioDatabase.PE_INDEX));
                int numShares = cursor.getInt(cursor.getColumnIndex(PortfolioDatabase.NUMSHARES_INDEX));

                Stock s = new Stock(ticker, name, price, percChange, yearHigh, yearLow, eps, pe, numShares);
                portfolioList.add(s);
                Log.d("SummaryActivity", ticker + " successfully added to portfolio list!");

                cursor.moveToNext();
            }
        }
    }

    // Listener for Manage Portfolio button
    public void managePortfolioScreen(View view) {
        // Kick off ManagePortfolio activity
        Intent myIntent = new Intent(SummaryActivity.this, ManagePortfolio.class);
        Log.d("SummaryActivity", "Moving to ManagePortfolio activity...");
        SummaryActivity.this.startActivity(myIntent);
    }

    // Check if DB exists
    private static boolean databaseExists(Context context) {
        File dbFile = context.getDatabasePath(PortfolioDatabase.DATABASE_NAME);
        if (dbFile.exists()) {
            Log.d("ManagePortfolio", "Database exists!");
        } else {
            Log.d("ManagePortfolio", "Database does not exist yet...");
        }
        return dbFile.exists();
    }

}
