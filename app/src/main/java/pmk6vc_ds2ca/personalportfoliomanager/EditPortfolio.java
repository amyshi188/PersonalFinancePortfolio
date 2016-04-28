package pmk6vc_ds2ca.personalportfoliomanager;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by varunkulkarni122 on 4/28/16.
 */
public class EditPortfolio extends AppCompatActivity {

    private String toastMsgNoDB = "No pre-existing portfolio. Try adding positions!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_portfolio); // Set summary layout here
        // Check if DB exists
        if (databaseExists(getApplicationContext())) {
            // Query DB here

        } else {
            // Indicate no DB yet
            EditPortfolio.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(EditPortfolio.this, toastMsgNoDB, Toast.LENGTH_LONG).show();
                }
            });
        }
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

}
