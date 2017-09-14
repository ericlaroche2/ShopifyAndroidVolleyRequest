/*
Adapted code from "Android JSON parsing using Volley" by Ravi Tamada. Source: https://www.androidhive.info/2014/09/android-json-parsing-using-volley/

 */

package tutadv.symbol.com.volleyrequests;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    // json object response url
    private String urlJsonObj = "https://shopicruit.myshopify.com/admin/orders.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6";

    private static String TAG = MainActivity.class.getSimpleName();
    private Button btnMakeObjectRequest, btnMakeArrayRequest;

    // Progress dialog
    private ProgressDialog pDialog;

    private TextView txtResponse;
    private TextView txtResponse2;
    private EditText editText1;
    private EditText editText2;
    // temporary string to show the parsed response
    private String jsonResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnMakeObjectRequest = (Button) findViewById(R.id.btnObjRequest);

        btnMakeArrayRequest = (Button) findViewById(R.id.btnArrayRequest);
        txtResponse = (TextView) findViewById(R.id.txtResponse);
        txtResponse2 = (TextView) findViewById(R.id.txtResponse2);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        editText1 = (EditText) findViewById(R.id.editText1);

        editText2 = (EditText) findViewById(R.id.editText2);

        btnMakeObjectRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // making json object request
                makeJsonObjectRequest();
            }
        });

        btnMakeArrayRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // making json array request
                makeJsonArrayRequest();
            }
        });

    }
    public void onClick(View v) {

        if(v == editText1){
            editText1.setText("");
        }
        if(v == editText2){
            editText2.setText("");
        }
    }

    private void makeJsonObjectRequest() {

        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    Double totalSpent=0.00;
                    Double price;
                    Double quantity;
                    boolean isValidEmail=false;
                    JSONArray ordersList=response.getJSONArray("orders");
                    for (int i = 0 ; i< ordersList.length();i++){


                        JSONObject currentOrder = (JSONObject) ordersList.get(i);

                        String aa= editText1.getText().toString();
                        if (currentOrder.getString("email").equals(editText1.getText().toString())){
                            isValidEmail=true;
                            JSONArray lineItems=currentOrder.getJSONArray("line_items");

                            for(int j=0; j<lineItems.length();j++){
                                JSONObject currentItem= (JSONObject) lineItems.get(j);

                                price=Double.parseDouble(currentItem.getString("price"));
                                quantity=Double.parseDouble(currentItem.getString("quantity"));
                                totalSpent+= price*quantity;
                            }
                        }
                    }

                    if (isValidEmail)
                    jsonResponse = "Customer has spent a total of $"+ totalSpent;
                    else
                        jsonResponse = editText1.getText().toString()+" is not a valid email";
                    txtResponse.setText(jsonResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void makeJsonArrayRequest() {
        showpDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    int counter=0;

                    String itemTitle="";
                    String quantity="";

                    JSONArray ordersList=response.getJSONArray("orders");
                    for (int i = 0 ; i< ordersList.length();i++){

                        JSONObject currentOrder = (JSONObject) ordersList.get(i);

                        JSONArray items = currentOrder.getJSONArray("line_items");
                        for(int j=0; j<items.length();j++){
                            JSONObject itemNum= (JSONObject) items.get(j);
                            itemTitle=itemNum.getString("title");
                            if(itemTitle.equals(editText2.getText().toString())){
                                quantity=itemNum.getString("quantity");
                                counter+=parseInt(quantity);
                                //bronze bag = 8
                            }
                        }



                    }


                    jsonResponse = "The number of  "+ editText2.getText().toString()+ "s sold is "+counter +"\n\n";


                    txtResponse2.setText(jsonResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
