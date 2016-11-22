package za.co.salamandra.timeclock;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class barcode_scanner extends AppCompatActivity {

    private Button btnScan;
    private TextView txtBarcodeMain;
    private TextView txtBarcodeDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //btnScan = (Button) findViewById(R.id.);
        //txtBarcodeMain = (TextView) findViewById(R.id.txtBarcodeType);
        //txtBarcodeDescription = (TextView) findViewById(R.id.txtBarcodeText);

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //action
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Read();
            }
        });
    }

    public void Read() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    //Pull result of barcode to variables
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanResult != null) {
            String ScanContent = scanResult.getContents();
            String ScanFormat = scanResult.getFormatName();

            txtBarcodeMain.setText(ScanFormat);
            txtBarcodeDescription.setText(ScanContent);
        } else {
            Toast.makeText(getApplicationContext(), "No Scan Data Received...", Toast.LENGTH_LONG).show();
        }
    }




    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
