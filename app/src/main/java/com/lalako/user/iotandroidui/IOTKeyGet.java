package com.lalako.user.iotandroidui;

/**
 * Created by Administrator on 2018/1/12.
 */


import android.os.AsyncTask;
import android.os.Build;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.os.NetworkOnMainThreadException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import static java.lang.System.out;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.*;

/**
 *
 * @author Administrator
 */
public class IOTKeyGet{

    String loginURL = "http://60.250.111.124/vendorTrial/api/iBadgeService.php";
    String Host = "60.250.111.124";
    List<String> cookie;
    List<String> deviceInfo;
    //String account = "iot01@ttu.edu.tw";
    String account = "test@example.com";
    String password = "testpw123";
    String targetUID = "0514011A2888028AF1282522";
    String AESKey = "";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public IOTKeyGet() throws Exception{
        Log.d("TAG", "INIT");
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String sendKey() throws Exception{
        Log.d("TAG", "login before");
        getIOTLogin();
        getIOTKey();

        //AndroidPostLogin();
        return AESKey;

    }

    public void AndroidPostLogin(){
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(loginURL);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("endpoint", "UserLogin"));
        params.add(new BasicNameValuePair("mail", "iot01@ttu.edu.tw"));
        params.add(new BasicNameValuePair("passphrase", "testpw123"));

        String strResult = "";
        try{
            //發送Http Request，內容為params，且為UTF8格式
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            //接收Http Server的回應
            HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
            //判斷Http Server是否回傳OK(200)
            if(httpResponse.getStatusLine().getStatusCode() == 200){
                //將Post回傳的值轉為String，將轉回來的值轉為UTF8，否則若是中文會亂碼
                strResult = EntityUtils.toString(httpResponse.getEntity(),HTTP.UTF_8);

                Log.d("TAG", strResult);

                Message msg = Message.obtain();
                //設定Message的內容
                msg.what = 123;
                msg.obj=strResult;
                //使用MainActivity的static handler來丟Message
            }

        }catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getIOTLogin() throws Exception{

        String postData = "endpoint=UserLogin&mail=" + account + "&passphrase=" + password + "&submit=Login";

        try{
            HttpURLConnection conn = (HttpURLConnection)new URL(loginURL).openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(true);
            conn.setFollowRedirects(true);
            conn.setInstanceFollowRedirects(true);

            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
            conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.9,en;q=0.8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Host", Host);
            conn.setRequestProperty("Connection", "keep-alive");

            Log.d("TAG", "DataOutputStream before");

            try ( DataOutputStream postStream = new DataOutputStream(conn.getOutputStream()) ){
                postStream.writeBytes(postData);
            }
            catch (Exception ex){
                Log.d("TAG", "-----POST_Failed-----");
                out.println("-----POST_Failed-----");
                ex.printStackTrace();
            }

            InputStream ips;
            String encode = conn.getContentEncoding();
            Log.d("TAG", "DataOutputStream after");

            if (encode != null) {
                out.println("content-encoding :: " + encode);
                if (!encode.equals("gzip"))
                    throw new Exception("CanNotEncodeInputStream");
                ips = new GZIPInputStream(conn.getInputStream());
            }
            else {
                out.println("content-encoding :: null, encode in default !");
                ips = conn.getInputStream();
            }
            try (
                    BufferedReader buff = new BufferedReader( new InputStreamReader(ips, "UTF-8")
                    )
            ){
                cookie = new ArrayList<String>();
                //out.println("Reponse Cookie : ");

                for (Object str : conn.getHeaderFields().get("Set-Cookie")){
                    cookie.add(str.toString().split(";", 2)[0]);
                    //out.println(str.toString().split(";", 2)[0]);
                }
                //System.out.print(cookie.get(0));
            }
            finally{
                ips.close();
            }
            //out.println("-----取得登入頁面成功-----\n");
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception("GetLoginFailed");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getIOTKey() throws Exception{
        String option = "all";
        String postData = "endpoint=ListDevices&statusFilter=" + option + "&submit=Login";
        String requestCookie = cookie.get(0).toString();
        for (int i = 1; i < cookie.size(); i++){
            requestCookie = requestCookie + "; " + cookie.get(i);
        }

        try{
            HttpURLConnection conn = (HttpURLConnection)new URL(loginURL).openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(true);
            conn.setFollowRedirects(true);
            conn.setInstanceFollowRedirects(true);


            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            conn.setRequestProperty("Upgrade-Insecure-Requests", "1");
            conn.setRequestProperty("Accept-Language", "zh-TW,zh;q=0.9,en;q=0.8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.102 Safari/537.36");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Host", Host);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Cookie", requestCookie);

            try ( DataOutputStream postStream = new DataOutputStream(conn.getOutputStream()) ){
                postStream.writeBytes(postData);
            }
            catch (Exception ex){
                out.println("-----POST_Failed-----");
                ex.printStackTrace();
            }

            InputStream ips;
            String encode = conn.getContentEncoding();

            if (encode != null) {
                out.println("content-encoding :: " + encode);
                if (!encode.equals("gzip"))
                    throw new Exception("CanNotEncodeInputStream");
                ips = new GZIPInputStream(conn.getInputStream());
            }
            else {
                out.println("content-encoding :: null, encode in default !");
                ips = conn.getInputStream();
            }
            try (
                    BufferedReader buff = new BufferedReader( new InputStreamReader(ips, "UTF-8")
                    )
            ){

                String getJSON = buff.readLine();
                AESKey = FindKeyInString(targetUID, getJSON);
                Log.d("TAG", AESKey);
            }
            finally{
                ips.close();
            }

            //out.println("-----取得登入頁面成功-----\n");
        }
        catch(Exception ex){
            ex.printStackTrace();
            throw new Exception("GetLoginFailed");
        }
    }
    public String FindKeyInString(String targetUID, String largeJson) throws JSONException{

        JSONObject j;
        j = new JSONObject(largeJson);

        JSONObject targetJSONObject;
        targetJSONObject = new JSONObject();
        for (int current = 0; current < j.getJSONArray("deviceList").length(); current++){
            if( j.getJSONArray("deviceList").getJSONObject(current).get("uid").toString().equals(targetUID) ){
                targetJSONObject = j.getJSONArray("deviceList").getJSONObject(current);
            }
        }

        String targetKey = targetJSONObject.getJSONArray("deviceAttributes").getJSONObject(1).get("attValue").toString();
        System.out.println(targetKey);


        return targetKey;
    }

}
