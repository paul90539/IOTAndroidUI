package com.lalako.user.iotandroidui;

import android.app.Application;
import org.eclipse.paho.android.service.MqttAndroidClient;

/**
 * Created by Administrator on 2018/1/13.
 */

//全域變數
public class GlobalVariable extends Application {
    public String MQTTHOST = "tcp://iot.eclipse.org:1883";
    public String topicStr = "lalako";
    public String AESKey = "";
    public MqttAndroidClient client = null;
}
