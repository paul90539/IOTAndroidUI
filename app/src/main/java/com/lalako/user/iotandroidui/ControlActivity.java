package com.lalako.user.iotandroidui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import org.apache.commons.codec.DecoderException;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

public class ControlActivity extends AppCompatActivity {

    public GlobalVariable globalVariable = null;
    public String topic = "";
    public MqttAndroidClient client = null;
    public Button goBtn, backBtn, leftBtn, rightBtn, stopBtn;
    public AESCrypt AES = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        globalVariable = (GlobalVariable)getApplicationContext();
        topic = globalVariable.topicStr;
        client = globalVariable.client;

        Log.d("TAG", "AES: " + globalVariable.AESKey);
        //取得AES
        try {
            AES = new AESCrypt(globalVariable.AESKey);
        } catch (DecoderException e) {
            e.printStackTrace();
        }

        goBtn = (Button) findViewById(R.id.goBtn);
        backBtn = (Button) findViewById(R.id.backBtn);
        leftBtn = (Button) findViewById(R.id.leftBtn);
        rightBtn = (Button) findViewById(R.id.rightBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);

        //監聽按鈕
        btnTouchTrigger(goBtn, 1);
        btnTouchTrigger(backBtn, 2);
        btnTouchTrigger(leftBtn, 3);
        btnTouchTrigger(rightBtn, 4);
        btnTouchTrigger(stopBtn, 5);
    }

    //監聽按鈕設定
    public void btnTouchTrigger(Button btn, int type){
        btn.setOnTouchListener( new Button.OnTouchListener() {
            int passCommand; //傳送的訊息
            //當按下按鈕
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //goBtn.setText("按下");
                    publish("{\"opt\":" + passCommand + "}"); //發送訊息給MQTT
                    Log.d("TAG", "COM: " + passCommand);
                }
                /*
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    //goBtn.setText("彈起");
                    publish(passCommand + "_" + 0);
                }
                */
                return false;
            }
            //取得按鈕編號
            public View.OnTouchListener init(int passCommand){
                this.passCommand = passCommand;
                return this;
            }
        }.init(type));
    }

    //發送訊息給MQTT
    public void publish(String message){
        String topic = globalVariable.topicStr;
        //byte[] encodedPayload = new byte[0];
        try {
            String encryptMsg = "";
            Log.d("TAG", "MSG: " + message);
            encryptMsg = AES.getEncryptHexString(message);
            client.publish(topic, encryptMsg.getBytes(),0,false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
