package pmk6vc_ds2ca.personalportfoliomanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

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

            // Chart
            createChart();

            // Analytics
            displayPortfolioValue();
            displayPortfolioPE();
            displayPortfolioEPS();
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
            createChart();
        };
        super.onResume();
    }

    // Create chart
    // https://www.numetriclabz.com/android-pie-chart-using-mpandroidchart-library-tutorial/ is your friend
    public void createChart() {
        PieChart pieChart = (PieChart) findViewById(R.id.chart);
        // Create data values
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        // Iterate through portfolio
        // Guaranteed to be updated vals, since called right after updatePortfolio()
        int i = 0;
        for (Stock s : portfolioList) {
            // Add data
            double portval = calculatePortfolioValue();
            double posval = s.getNumShares() * s.getCurrentPrice();
            float val = (float)(100*posval/portval);
            entries.add(new Entry(val, i));
            // Add label
            labels.add(s.getTicker());

            i++;
        }

        PieDataSet dataset = new PieDataSet(entries, "Position weights");
        dataset.setColors(ColorTemplate.PASTEL_COLORS);
        PieData data = new PieData(labels, dataset);
        pieChart.setData(data);
        pieChart.setDescription("Portfolio breakdown by position, in %");
        //pieChart.animateY(2000);
    }

    // Update portfolio by clearing list and querying DB
    // http://stackoverflow.com/questions/10111166/get-all-rows-from-sqlite
    public void updatePortfolio() {
        portfolioList.clear();
        Log.d("SummaryActivity", "Portfolio and view cleared, ready for update...");

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

    // ANALYTICS
    /*
     * Portfolio value
     * Portfolio PE
     * Portfolio EPS
     */
    public double calculatePortfolioValue() {
        double value = 0;
        for (Stock s : portfolioList) {
            double tempValue = s.getNumShares() * s.getCurrentPrice();
            value += tempValue;
        }

        return value; // Return value as CENTS
    }

    public void displayPortfolioValue() {
        double valueDol = calculatePortfolioValue() / 100; // Value in dollars
        TextView pval = (TextView) findViewById(R.id.portfolioValue);
        pval.setText("$" + Double.toString(valueDol));

    }

    public double calculatePortfolioPE() {
        double price = 0;
        double earnings = 0;
        double pe = 0;
        for (Stock s : portfolioList) {
            double tempValue = s.getNumShares() * s.getCurrentPrice() / 100; // Need to go back to dollars
            double tempEarnings = s.getNumShares() * s.getEps();
            price += tempValue;
            earnings += tempEarnings;
        }
        pe = price / earnings;

        return pe;
    }

    public void displayPortfolioPE() {
        double pe = calculatePortfolioPE();
        DecimalFormat df = new DecimalFormat("#.##");
        TextView ppe = (TextView) findViewById(R.id.portfolioPE);
        ppe.setText(df.format(pe));
    }

    public double calculatePortfolioEPS() {
        double earnings = 0;
        double numShares = 0;
        double eps = 0;
        for (Stock s : portfolioList) {
            earnings += (s.getEps() * s.getNumShares());
            numShares += s.getNumShares();
        }
        //eps = earnings/portfolioList.size();
        eps = earnings / numShares;
        return eps;
    }

    public void displayPortfolioEPS() {
        double eps = calculatePortfolioEPS();
        DecimalFormat df = new DecimalFormat("#.##");
        TextView peps = (TextView) findViewById(R.id.portfolioEPS);
        peps.setText(df.format(eps));
    }

    // Listener for Manage Portfolio button
    public void managePortfolioScreen(View view) {
        // Kick off ManagePortfolio activity
        Intent myIntent = new Intent(SummaryActivity.this, ManagePortfolio.class);
        Log.d("SummaryActivity", "Moving to ManagePortfolio activity...");
        SummaryActivity.this.startActivity(myIntent);
    }

    // Check if DB exists
    // http://stackoverflow.com/questions/3386667/query-if-android-database-exists
    private static boolean databaseExists(Context context) {
        File dbFile = context.getDatabasePath(PortfolioDatabase.DATABASE_NAME);
        if (dbFile.exists()) {
            Log.d("ManagePortfolio", "Database exists!");
        } else {
            Log.d("ManagePortfolio", "Database does not exist yet...");
        }
        return dbFile.exists();
    }

    //Find advisors button on the main screen
    public void findAdvisers(View v) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

}
