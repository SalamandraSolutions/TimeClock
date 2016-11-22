package network;

import android.content.SharedPreferences;
import android.util.Log;


import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import za.co.salamandra.timeclock.LoginActivity;

/**
 * Created by PaulMurdoch on 2016/11/15.
 */

public class postRequest {

    public Boolean postLogin(String mEmail, String mPassword) {
        SharedPreferences.Editor editor = LoginActivity.preferences.edit();
        URL url;
        HttpURLConnection conn;

        try {
            //create connection
            url = new URL("http://api.salamandra.co.za/v1/login");
            //set the parameters
            String param = "email=" + mEmail +
                    "&password=" + mPassword;
            //begin the connection
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            conn.setFixedLengthStreamingMode(param.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //push params
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.close();
            //build response and get it from post
            String response = "";
            Scanner inStream = new Scanner(conn.getInputStream());
            //process and store response
            while (inStream.hasNextLine()) {
                response += (inStream.nextLine());
            }
            //convert to JSON
            JSONObject login = new JSONObject(response);
            //get strings
            boolean error = login.getBoolean("error");
            String name = login.getString("name");
            String email = login.getString("email");
            String apikey = login.getString("apiKey");
            //push values to preferences
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("apikey", apikey);
            editor.apply();
            //verify preferences work
            Log.v("MyActivity", LoginActivity.preferences.getString("name", null));
            Log.v("MyActivity", LoginActivity.preferences.getString("email", null));
            Log.v("MyActivity", LoginActivity.preferences.getString("apikey", null));
            //check response
            if(!error) {
                if(email.equalsIgnoreCase(mEmail)) {
                    return true;
                }
            }

        } catch (MalformedURLException mal) {
            mal.printStackTrace();
            return false;
        } catch (IOException io) {
            io.printStackTrace();
            return false;
        } catch (Exception error) {
            error.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean postEmployee (String image, String firstName, String lastName, String empCellNum, String empEmailAddress, String empID) {
        URL url;
        HttpURLConnection conn;
        String api_key = LoginActivity.preferences.getString("apikey", null);
        try {
            //create connection string
            url = new URL("http://api.salamandra.co.za/v1/employee");
            //set the parameters
            String param = "first_name=" + firstName + "&last_name=" +
                    lastName + "&cell_num=" + empCellNum + "&email_address=" +
                    empEmailAddress + "&photo=" + image + "&barcode=" + empID;
            //begin the connection
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            conn.setFixedLengthStreamingMode(param.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("api", api_key);

            //push params
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.close();
            //build response
            String response = "";
            Scanner inStream = new Scanner(conn.getInputStream());
            //process and store response
            while (inStream.hasNextLine()) {
                response += (inStream.nextLine());
            }
            //Convert to JSON
            JSONObject employee = new JSONObject(response);
            //get Strings
            boolean error = employee.getBoolean("error");
            String message = employee.getString("message");
            int barcode_id = employee.getInt("barcode_id");
            //check response
            if (!error) {
                return true;
            }
        } catch (MalformedURLException mal) {
            mal.printStackTrace();
            return false;
        } catch (IOException io) {
            io.printStackTrace();
            return false;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
        return false;
    }
}
