
# IOTAndroidUI

## 環境

1. Android -> Need API 19 or later
2. IDE -> Android Studio
3. PC OS -> Windows 10
4. Arduino Yun
5. ibadge hardware encrpty

## 說明
物聯網安全期末小專題<br>
題目: 遙控車安全連線系統<br><br>
這repository是Cinent端即Android手機端<br>
Yun 跟 Pi 的部分在 [這裡](https://github.com/paul90539/IOTYunAndPi)<br>
目的利用手機以加密方式跟遙控車端進行連線並傳輸命令<br>
加密方式是利用ibadge這塊晶片來做硬體的加密<br>
密文傳輸方式是利用MQTT server<br>
車子方面是Yun會解密訊息給然後用socket傳給Pi<br>
然後Pi使用uart傳控制訊息給車子<br>

## 程式架構
IOTAndroidUI.apk -> Android手機車控介面<br>
 |-- MainActivity.java -> 登入介面，顯示取得 Session Key 跟 MQTT的狀況<br>
 |-- IOTKeyGet.java -> 有關 http post 資料給 server 取得 key 所有步驟<br>
 |-- GlobalVariable.java -> 全域變數，在切換場景時會用的到<br>
 |-- ControlActivity.java -> 控制介面，這裡會依照按鈕發送密文指令給Yun<br>
 |-- AESCrypt.java -> 將訊息做 AES 的 CBC 方式加密的部分<br><br>
 
 Arduino Yun<br>
 |-- mqtt_basic.ino -> mqtt連線、上傳及取得 Session Key、監聽mqtt發送過來的密文、<br>
                       用 Key 解密、socket 連線到 Pi、發送解密後的命令給Pi<br><br>
 
 Pi<br>
 |-- yunSocket.cpp -> 建立一個 socket server 等 yun 連線，連線後接收傳過來的命令，<br>
                      依照命令使用uart使車子運作(往前、往後等)<br><br>
 
 
## 系統流程架構
[IOTYunAndPi](https://github.com/paul90539/IOTYunAndPi)<br>
ibadge -> power on and send session key to ibadge vendor server<br><br>

[IOTAndroidUI](https://github.com/paul90539/IOTAndroidUI)<br>
**手機 -> post login -> ibadge vendor server<br>
手機 -> post device uid -> ibadge vendor server<br>
手機 <- response session key <- ibadge vendor server<br>
手機 <- response device now alive or not <- ibadge vendor server<br>
手機 -> use session key do AES encrpty command mseeage<br>
手機 -> connect to mqtt server -> publish encrpty mseeage**<br><br>

[IOTYunAndPi](https://github.com/paul90539/IOTYunAndPi)<br>
Yun <- receive encrpty mseeage <- listen mqtt server<br>
Yun -> use uart to request ibadge device session key<br>
Yun -> use session key do AES decrpty command mseeage<br>
Yun -> use socket send plaintext to raspberryPi<br>
Pi  <- receive plaintext command<br>
Pi  -> use uart to let car working<br>

## 參考資料

[Android MQTT 教學(Website)](https://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service)<br>
[Android MQTT 教學(Video)](https://www.youtube.com/watch?v=BAkGm02WBc0)<br>
[Java Json Library 教學](https://dotblogs.com.tw/michaelchen/2015/01/12/java_decode_json)<br>
[jasperYen GitHub -- use java to do http post example](https://github.com/jasperyen)<br>
[android.os.NetworkOnMainThreadException](http://kuosun.blogspot.tw/2013/12/androidosnetworkonmainthreadexception.html)<br>
[android Global variable 寫法](https://bella-study.blogspot.tw/2017/03/android-global-variable.html)<br>
[AES encrpty/decrpty online](http://aes.online-domain-tools.com/)
