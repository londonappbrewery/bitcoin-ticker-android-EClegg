package com.londonappbrewery.bitcointicker;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONObject;

import java.text.NumberFormat;

import cz.msebera.android.httpclient.Header;


public class  MainActivity extends AppCompatActivity {

    // Constants:
    // TODO: Create the base URL
    private static final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/{symbol_set}/ticker/{symbol}";
    private static final String SYMBOL_SET = "global";
    private static final String BITCOIN_CODE = "BTC";
    private static final String TAG = "MainActivity";

    private static final String secretKey = "YjVjNzMxNmI0M2IyNDdmMDgwYTFkYTk3NmFmMmJjNjg0Y2NlZjA0OGY3Yjg0YmU4YTE2YTAwNTk4NzdkZmRjOQ";
    private static final String publicKey = "YzAyYzc3MWMyNTBjNGM3ZDk3NjkyNzk3OWJkZjViY2I";


    // Member Variables:
    TextView mPriceTextView;

    String tickerUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPriceTextView = (TextView) findViewById(R.id.priceLabel);
        Spinner spinner = (Spinner) findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currency_array, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        // TODO: Set an OnItemSelected listener on the spinner
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "spinner.onItemSelected: " + position);
                Log.d(TAG, "spinner.onItemSelected: " + parent.getItemAtPosition(position));

                tickerUrl = BASE_URL.replace("{symbol_set}",SYMBOL_SET).replace("{symbol}",BITCOIN_CODE + parent.getItemAtPosition(position));
                doNetworkCall(tickerUrl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "spinner.onNothingSelected: ");
            }
        });

    }

    // TODO: complete the letsDoSomeNetworking() method
    private void doNetworkCall(String url) {
        Log.d(TAG, "doNetworkCall: " + url);

        try {
            Uri uri = Uri.parse(Uri.encode(url));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Invalid URL " + url, Toast.LENGTH_SHORT).show();
            mPriceTextView.setText(R.string.label_default_text);
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();

        client.addHeader("x-ba-key", publicKey);

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "doNetworkCall.onSuccess: " + response.toString());

                try {
                    Double askPrice = response.getDouble("ask");
                    NumberFormat nf = NumberFormat.getNumberInstance();
                    mPriceTextView.setText(nf.format(askPrice));
                } catch (Exception e) {
                    Log.d(TAG, "onSuccess.response.getString Exception: " + e.getMessage());
                    e.printStackTrace();
                    mPriceTextView.setText(R.string.label_default_text);
                    Toast.makeText(getApplicationContext(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "onFailure: ####################################################");
                Log.d(TAG, "doNetworkCall.onFailure.StatusCode: " + statusCode);
                Log.d(TAG, "doNetworkCall.onFailure.exception: " + throwable.getLocalizedMessage());
                throwable.printStackTrace();
                mPriceTextView.setText(R.string.label_default_text);
                Toast.makeText(getApplicationContext(), "HTTP Request Failed " + statusCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "onFailure: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                //if (errorResponse != null) {
                    Log.d(TAG, "doNetworkCall.onFailure.errorResponse " + errorResponse.toString());
                //}
                Log.d(TAG, "doNetworkCall.onFailure.StatusCode: " + statusCode);
                Log.d(TAG, "doNetworkCall.onFailure.exception " + throwable.getLocalizedMessage());
                throwable.printStackTrace();
                mPriceTextView.setText(R.string.label_default_text);
                Toast.makeText(getApplicationContext(), "HTTP Request Failed " + statusCode, Toast.LENGTH_SHORT).show();
            }
        });
    } // doNetworkCall


}
