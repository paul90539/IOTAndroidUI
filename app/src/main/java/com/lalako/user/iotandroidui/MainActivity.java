package com.lalako.user.iotandroidui;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {

    GlobalVariable globalVariable = null;
    public String MQTTHOST = "tcp://iot.eclipse.org:1883";
    public String topicStr = "lalako"; //訂閱的主題
    public String AESKey = "";
    public int statusOnline = 0; //看機器在線狀態
    MqttAndroidClient client; //MQTT Client連線
    int status = 0; //決定重連或開始
    TextView keyStatusText;
    TextView onlineStatusText;
    TextView mqttStatusText;
    TextView keyText;
    boolean mqttOnline = false; //MQTT是否有連線成功
    Button ConnectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        keyStatusText = findViewById(R.id.TextView2);
        onlineStatusText = findViewById(R.id.TextView3);
        mqttStatusText = findViewById(R.id.TextView4);
        keyText = findViewById(R.id.KeyText);
        ConnectBtn = findViewById(R.id.startBtn);

        globalVariable = (GlobalVariable)getApplicationContext();

        setMqttConnect();
    }

    //按下開始按鈕的事件
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void submitConnect(View v) throws Exception {
        //未連上
        if(status == 0){
            //setMqttConnect();
            getKey();
            Log.d("TAG", "ONLINE: " + statusOnline);
            if( !(AESKey.equals("") || statusOnline == 0 || !mqttOnline) ){
                status = 1;
                ConnectBtn.setText("開始");
                Toast.makeText(MainActivity.this, "連線成功!!", Toast.LENGTH_LONG).show();
            }
            else{
                ConnectBtn.setText("重連");
                Toast.makeText(MainActivity.this, "連線失敗!!", Toast.LENGTH_LONG).show();
            }
            keyStatusText.setText(AESKey.equals("") ? "AESKey取得    失敗" : "AESKey取得     成功");
            onlineStatusText.setText(statusOnline == 0 ? "裝置狀態           離線" : "裝置狀態           在線");
            mqttStatusText.setText(!mqttOnline ?        "MQTT連線       失敗" : "MQTT連線       成功");
            keyText.setText(AESKey);
        }
        //已連上
        else if(status == 1){
            globalVariable.client = client;
            globalVariable.AESKey = AESKey;

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ControlActivity.class);
            startActivity(intent);
        }
    }

    //設定MQTT Client
    public boolean setMqttConnect(){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    //Toast.makeText(MainActivity.this, "connected!!", Toast.LENGTH_LONG).show();
                    mqttOnline = true;
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //Toast.makeText(MainActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
                    mqttOnline = false;
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return mqttOnline;
    }

    //取得AESKey
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean getKey() throws Exception {
        Thread thread = new Thread(postThread); //進行http post索取需要的AESKey
        thread.start();
        thread.join(); //等待Thread執行結束
        if(AESKey.equals("")){
            return false;
        }
        return true;
    }

    //注意!!! 在Android不能在MainThread進行網路連線
    //進行http post索取需要的AESKey
    private Runnable postThread = new Runnable(){
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void run() {
            IOTKeyGet postServer = null;
            try {
                postServer = new IOTKeyGet();
                if( postServer.getInfo() ){
                    AESKey = postServer.getAESKey();
                    statusOnline = postServer.getStatus();
                }
                else{
                    AESKey = "";
                    statusOnline = 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
