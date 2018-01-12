package com.lalako.user.iotandroidui;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {

    /*MQTT TEST*/

    static String MQTTHOST = "tcp://iot.eclipse.org:1883";
    static String topicStr = "lalako";
    public String AESKey = "";
    MqttAndroidClient client;
    TextView showKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showKey = (TextView) findViewById(R.id.textView);
        showKey.setText(AESKey);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(MainActivity.this, "connected!!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(MainActivity.this, "connection failed!!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void pub(View v){
        String topic = topicStr;
        String message = "Hi! This is a test message from LaLaKo's cellphone!";
        byte[] encodedPayload = new byte[0];
        try {
            client.publish(topic, message.getBytes(),0,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getKey(View v) throws Exception {
        Thread thread = new Thread(postThread);
        thread.start();
        thread.join();
        showKey = (TextView) findViewById(R.id.textView);
        showKey.setText(AESKey);
        AESCrypt AESC = new AESCrypt(AESKey);
    }

    private Runnable postThread = new Runnable(){
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void run() {
            IOTKeyGet postServer = null;
            try {
                Log.d("TAG", "function abefore");
                postServer = new IOTKeyGet();
                AESKey = postServer.sendKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
