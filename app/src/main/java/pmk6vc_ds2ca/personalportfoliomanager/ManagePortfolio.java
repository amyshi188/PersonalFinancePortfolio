package pmk6vc_ds2ca.personalportfoliomanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.seismic.ShakeDetector;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ManagePortfolio extends AppCompatActivity implements ShakeDetector.Listener {

    private String[] stockInfo = new String[7]; // Info for the current stock in question
    private final CharSequence toastMsgFindData = "Data not available. Check internet connection and spelling.";
    private final CharSequence toastMsgNaN = "Please input an integer number of shares";
    private final CharSequence toastMsgAddPosition = "Data in incompatible format. Cannot be stored";
    private final CharSequence toastMsgSuccess = "Position added to portfolio!";
    private SensorManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_portfolio);
        this.sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sm);
    }

    // Shake listener
    public void hearShake() {
        EditText nameText = (EditText) findViewById(R.id.name);
        EditText currentPriceText = (EditText) findViewById(R.id.current_price);
        EditText pctchangeText = (EditText) findViewById(R.id.pctchange);
        EditText yearhighText = (EditText) findViewById(R.id.yearhigh);
        EditText yearlowText = (EditText) findViewById(R.id.yearlow);
        EditText epsText = (EditText) findViewById(R.id.eps);
        EditText peratioText = (EditText) findViewById(R.id.peratio);

        try {
            nameText.setText("");
            currentPriceText.setText("");
            pctchangeText.setText("");
            yearhighText.setText("");
            yearlowText.setText("");
            epsText.setText("");
            peratioText.setText("");
        } catch (Exception e) {
            // Stuff
        }

        Toast.makeText(this, "Screen contents cleared due to shake!", Toast.LENGTH_SHORT).show();
    }

    public void getStockInfo(View v) {
        Log.d("ManagePortfolio", "Button clicked!");

        // Fetch inputted ticker
        EditText tickerBox = (EditText) findViewById(R.id.ticker_input);
        String ticker = tickerBox.getText().toString(); // NOTE TO SELF: Add error checking here

        // Execute task
        YahooPuller fetcher = new YahooPuller();
        fetcher.execute(ticker);

    }

    public void addPosition(View v) {

        // Fetch inputted ticker
        EditText tickerBox = (EditText) findViewById(R.id.ticker_input);
        String ticker = tickerBox.getText().toString();
        EditText numSharesBox = (EditText) findViewById(R.id.numShares_input);
        String numSharesString = numSharesBox.getText().toString();
        int numShares = 0;

        // ERROR CHECKING
        // Small bug: When this button is clicked directly after an invalid input is sitting in text fields, the incorrect toast pops up
        // Incorrect value still not added in DB, so it's not a major issue
        if (ticker.equals("")) {
            Log.d("ManagePortfolio", "Empty ticker");

            ManagePortfolio.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ManagePortfolio.this, toastMsgFindData, Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        try {
            numShares = Integer.parseInt(numSharesString);
        } catch (Exception e) {
            Log.d("ManagePortfolio", "Non-integral share number");

            ManagePortfolio.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ManagePortfolio.this, toastMsgNaN, Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }


        getStockInfo(v); // Fetch online data

        SQLiteDatabase portfolioDB = PortfolioDatabase.getInstance(this).getWritableDatabase(); // Static DB reference
        ContentValues values = new ContentValues(); // Map of values, column names are keys

        // Populate StockInfo array

        // Parse HTTP results to appropriate data format
        try {
            String name = stockInfo[0];
            int currentPrice = Math.round(Float.parseFloat(stockInfo[1]) * 100); // Convert to cents
            float pctchange = percentChangeAsFloat(stockInfo[2]);
            int yearhigh = Math.round(Float.parseFloat(stockInfo[3]) * 100);
            int yearlow = Math.round(Float.parseFloat(stockInfo[4]) * 100);
            float eps = Float.parseFloat(stockInfo[5]);
            float pe = Float.parseFloat(stockInfo[6]);

            // Put items in map
            values.put(PortfolioDatabase.TICKER_INDEX, ticker);
            values.put(PortfolioDatabase.NAME_INDEX, name);
            values.put(PortfolioDatabase.PRICE_INDEX, currentPrice);
            values.put(PortfolioDatabase.PC_INDEX, pctchange);
            values.put(PortfolioDatabase.YH_INDEX, yearhigh);
            values.put(PortfolioDatabase.YL_INDEX, yearlow);
            values.put(PortfolioDatabase.EPS_INDEX, eps);
            values.put(PortfolioDatabase.PE_INDEX, pe);
            values.put(PortfolioDatabase.NUMSHARES_INDEX, numShares);

            portfolioDB.insert("Portfolio",
                    null,
                    values);
        } catch (Exception e) { // More error checking
            Log.d("YahooPuller", "Nonsensical input", e);

            ManagePortfolio.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ManagePortfolio.this, toastMsgAddPosition, Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        Log.d("ManagePortfolio", "Values entered in database!");
        ManagePortfolio.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(ManagePortfolio.this, toastMsgSuccess, Toast.LENGTH_SHORT).show();
            }
        });


    }

    //Find advisors button on the main screen
    public void findAdvisers(View v) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    // Return percent change as float
    private float percentChangeAsFloat(String s) {
        float pctChange = 1;
        s = s.replaceAll("%", ""); // Get rid of % symbol
        s = s.replaceAll("\"", ""); // Get rid of "

        String sign = s.substring(0, 1); // Get sign
        if (sign.equals("-")) {
            pctChange = -1;
        }

        pctChange = pctChange * Float.parseFloat(s.substring(1));

        return pctChange;
    }

    /** Inner class definition for AsyncTask class
     * URL flags in order:
     * n: name
     * l1: current price
     * p2: % change
     * k: 52 week high
     * j: 52 week low
     * e: EPS
     * r: P/E ratio
     */
    private class YahooPuller extends AsyncTask<String, Void, String[]> {

        private final String BASE_URL_String = "http://download.finance.yahoo.com/d/quotes.csv";

        // First parameter will be ticker symbol as string
        protected String[] doInBackground(String... params) {
            BufferedReader reader = null;
            try {
                // Create URL
                String ticker = params[0];
                String tickerURLString = BASE_URL_String + "?s=" + ticker + "&f=nl1p2kjer"; // Name and price flag for now
                URL tickerURL = new URL(tickerURLString);
                Log.d("YahooPuller", tickerURLString);

                // Open connection and set parsers
                HttpURLConnection yfConnection = (HttpURLConnection) tickerURL.openConnection();
                InputStream is = yfConnection.getInputStream(); // Internet permission added to manifest file (required)
                reader = new BufferedReader(new InputStreamReader(is));
                Log.d("YahooPuller", "Connection and readers created");
            } catch (Exception e) {
                // Either Internet not available or ticker not available on YHOO finance
                Log.d("YahooPuller", "Setting connection failed", e);

                ManagePortfolio.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ManagePortfolio.this, toastMsgFindData, Toast.LENGTH_SHORT).show();
                    }
                });

                return null;
            }


            try {
                // Store information
                String data = reader.readLine();
                stockInfo = data.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                Log.d("YahooPuller", "Name: " + stockInfo[0]);
                Log.d("YahooPuller", "Price: " + stockInfo[1]);
                Log.d("YahooPuller", "% change: " + stockInfo[2]);
                Log.d("YahooPuller", "52 week high: " + stockInfo[3]);
                Log.d("YahooPuller", "52 week low: " + stockInfo[4]);
                Log.d("YahooPuller", "EPS: " + stockInfo[5]);
                Log.d("YahooPuller", "P/E ratio: " + stockInfo[6]);

            } catch (Exception e) {
                Log.e("YahooPuller", "Parsing data failed", e);

                ManagePortfolio.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(ManagePortfolio.this, toastMsgFindData, Toast.LENGTH_SHORT).show();
                    }
                });

                return null;
                // NOTE: spaces break URLs, I think.
            }


            return stockInfo;
        }

        // Set fields on UI
        @Override
        protected void onPostExecute(String stockInfo[]) {
            EditText nameText = (EditText) findViewById(R.id.name);
            EditText currentPriceText = (EditText) findViewById(R.id.current_price);
            EditText pctchangeText = (EditText) findViewById(R.id.pctchange);
            EditText yearhighText = (EditText) findViewById(R.id.yearhigh);
            EditText yearlowText = (EditText) findViewById(R.id.yearlow);
            EditText epsText = (EditText) findViewById(R.id.eps);
            EditText peratioText = (EditText) findViewById(R.id.peratio);

            try {
                nameText.setText(stockInfo[0]);
                currentPriceText.setText(stockInfo[1]);
                pctchangeText.setText(stockInfo[2]);
                yearhighText.setText(stockInfo[3]);
                yearlowText.setText(stockInfo[4]);
                epsText.setText(stockInfo[5]);
                peratioText.setText(stockInfo[6]);
            } catch (Exception e) {
                // Stuff
            }

        }

    }


}


