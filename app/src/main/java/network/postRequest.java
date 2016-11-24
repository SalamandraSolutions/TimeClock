package network;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import org.json.JSONObject;
import org.jibble.simpleftp.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.TimeZone;

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

    public boolean postEmployee (Bitmap image, String firstName, String lastName, String empCellNum, String empEmailAddress, String empID) {
        String imagePath = firstName + "_" + lastName + "-" + empID + ".jpg";
        //send picture
        try {
            SimpleFTP ftp = new SimpleFTP();

            //Connect to server on port 21
            ftp.connect("197.81.192.113", 21, "root", "gr1Mc0_1");
            //set binary mode
            ftp.bin();

            //change to a new working directory on the ftp server
            ftp.cwd("/var/www/images");
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            //get files
            File f = new File(path, "/" + imagePath);
            f.createNewFile();

            //convert bitmap to byte array
            Bitmap bitmap = image;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            //Upload some file
            ftp.stor(f);
            //close
            ftp.disconnect();

        } catch (Exception err) {
            err.printStackTrace();

        }

        URL url;
        HttpURLConnection conn;
        String api_key = LoginActivity.preferences.getString("apikey", null);
        try {
            //create connection string
            url = new URL("http://api.salamandra.co.za/v1/employee");
            //set the parameters
            String param = "first_name=" + firstName + "&last_name=" +
                    lastName + "&cell_num=" + empCellNum + "&email_address=" +
                    empEmailAddress + "&photo=" + imagePath + "&barcode=" + empID;
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

    public boolean postTimeClockStart() {
        URL url;
        HttpURLConnection conn;
        String api_key = LoginActivity.preferences.getString("apikey", null);
        try {
            //create connection string
            url = new URL("http://api.salamandra.co.za/v1/timeIn");
            Long time = (Long) (System.currentTimeMillis());
            //set the parameters
            String param = "time=" + getDateCurrentTimeZone(time);
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
            JSONObject JSONtime = new JSONObject(response);
            //getStrings
            boolean error = JSONtime.getBoolean("error");
            String message = JSONtime.getString("message");
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
        }catch (Exception err) {
            err.printStackTrace();
            return false;
        }
        return false;

    }

    public boolean postTimeClockEnd() {
        URL url;
        HttpURLConnection conn;
        String api_key = LoginActivity.preferences.getString("apikey", null);
        try {
            //create connection string
            url = new URL("http://api.salamandra.co.za/v1/timeOut");
            Long time = (Long) (System.currentTimeMillis());
            //set the parameters
            String param = "time=" + getDateCurrentTimeZone(time);
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
            JSONObject JSONtime = new JSONObject(response);
            //get strings
            boolean error = JSONtime.getBoolean("error");
            String message = JSONtime.getString("message");
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

    public  String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }
}
