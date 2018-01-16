package com.lalako.user.iotandroidui;

/**
 * Created by Administrator on 2018/1/12.
 */

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.json.*;

/**
 *
 * @author Administrator
 */

public class IOTKeyGet{

    String loginURL = "http://60.250.111.124/vendorTrial/api/iBadgeService.php";
    String Host = "60.250.111.124";
    List<String> cookie;

    //String account = "iot01@ttu.edu.tw";
    String account = "iot11@ttu.edu.tw";
    String password = "testpw123";
    String targetUID = "0514011A2888028AD0F94522";
    String AESKey = "";
    int targetStatus = 0;


    public IOTKeyGet(){
        AESKey = "";
    }

    public IOTKeyGet(String inputAccount, String inputPassword, String inputTargetUID){
        this();
        account = inputAccount;
        password = inputPassword;
        targetUID = inputTargetUID;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean getInfo() throws Exception {
        return (getIOTLogin()  /*登入server*/ && getIOTKey() /*請求AESKey*/);
    }

    public String getAESKey(){
        return AESKey;
    }
    public int getStatus(){
        return targetStatus;
    }

    //回傳從Server取得的AESKey
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String sendKey() throws Exception{
        getIOTLogin(); //登入server
        getIOTKey(); //請求AESKey
        return AESKey;
    }

    //登入server
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean getIOTLogin() throws Exception{

        //要發送的登入請求資料
        String postData = "endpoint=UserLogin&mail=" + account + "&passphrase=" + password + "&submit=Login";

        try{
            HttpURLConnection conn = (HttpURLConnection)new URL(loginURL).openConnection();

            //設定連線型態
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(true);
            conn.setFollowRedirects(true);
            conn.setInstanceFollowRedirects(true);

            //設定header
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
            conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.9,en;q=0.8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Host", Host);
            conn.setRequestProperty("Connection", "keep-alive");

            //嘗試發送資料
            try ( DataOutputStream postStream = new DataOutputStream(conn.getOutputStream()) ){
                postStream.writeBytes(postData);
            }
            catch (Exception ex){
                Log.d("TAG", "-----POST_Failed-----");
                System.out.println("-----POST_Failed-----");
                ex.printStackTrace();
                return false;
            }

            //設定輸入型態
            InputStream ips;
            String encode = conn.getContentEncoding();

            if (encode != null) {
                System.out.println("content-encoding :: " + encode);
                if (!encode.equals("gzip"))
                    throw new Exception("CanNotEncodeInputStream");
                ips = new GZIPInputStream(conn.getInputStream());
            }
            else {
                System.out.println("content-encoding :: null, encode in default !");
                ips = conn.getInputStream();
            }

            //讀取輸入資料 這裡只取登入成功的Cookies
            try (
                    BufferedReader buff = new BufferedReader( new InputStreamReader(ips, "UTF-8")
                    )
            ){
                cookie = new ArrayList<String>();

                for (Object str : conn.getHeaderFields().get("Set-Cookie")){
                    cookie.add(str.toString().split(";", 2)[0]);
                }
            }
            finally{
                ips.close();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    //請求AESKey
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean getIOTKey() throws Exception{
        String option = "all"; // 選擇要取得的資料類型
        //要發送的登入請求資料
        String postData = "endpoint=ListDevices&statusFilter=" + option + "&submit=Login";

        //讀取cookies
        String requestCookie = cookie.get(0).toString();
        for (int i = 1; i < cookie.size(); i++){
            requestCookie = requestCookie + "; " + cookie.get(i);
        }

        try{
            HttpURLConnection conn = (HttpURLConnection)new URL(loginURL).openConnection();

            //設定連線型態
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(true);
            conn.setFollowRedirects(true);
            conn.setInstanceFollowRedirects(true);

            //設定header
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
            conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.9,en;q=0.8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Host", Host);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Cookie", requestCookie);

            //嘗試發送資料
            try ( DataOutputStream postStream = new DataOutputStream(conn.getOutputStream()) ){
                postStream.writeBytes(postData);
            }
            catch (Exception ex){
                System.out.println("-----POST_Failed-----");
                ex.printStackTrace();
                return false;
            }

            //設定輸入型態
            InputStream ips;
            String encode = conn.getContentEncoding();

            if (encode != null) {
                System.out.println("content-encoding :: " + encode);
                if (!encode.equals("gzip"))
                    throw new Exception("CanNotEncodeInputStream");
                ips = new GZIPInputStream(conn.getInputStream());
            }
            else {
                System.out.println("content-encoding :: null, encode in default !");
                ips = conn.getInputStream();
            }

            //讀取輸入資料 並取得AESKey
            try (
                    BufferedReader buff = new BufferedReader( new InputStreamReader(ips, "UTF-8")
                    )
            ){
                String getJSON = buff.readLine(); // 讀取回傳回來的JSON
                if( !FindKeyInString(targetUID, getJSON) /*分析JSON 取出想要的Key */){
                    return false;
                }
                Log.d("TAG", AESKey);
            }
            finally{
                ips.close();
            }
        }
        catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    //分析JSON 取出想要的Key
    public boolean FindKeyInString(String targetUID, String largeJson) throws JSONException{

        JSONObject j;
        j = new JSONObject(largeJson);
        boolean findFlag = false;

        JSONObject targetJSONObject;
        targetJSONObject = new JSONObject();
        for (int current = 0; current < j.getJSONArray("deviceList").length(); current++){
            if( j.getJSONArray("deviceList").getJSONObject(current).get("uid").toString().equals(targetUID) ){
                targetJSONObject = j.getJSONArray("deviceList").getJSONObject(current);
                findFlag = true;
            }
        }
        String status = targetJSONObject.get("deviceOnlineStatus").toString();
        targetStatus = Integer.valueOf(status);
        Log.d("TAG", "IOTstatus: " + status);
        String targetKey = targetJSONObject.getJSONArray("deviceAttributes").getJSONObject(1).get("attValue").toString();
        AESKey = targetKey;
        System.out.println(targetKey);

        return findFlag;
    }

}
