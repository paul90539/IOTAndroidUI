
# IOTAndroidUI

## 環境

1. Android -> Need API 19 or later
2. IDE -> Android Studio
3. PC OS -> Windows 10
4. Arduino Yun
5. ibadge hardware encrpty

## 說明
物聯網安全期末小專題
題目: 遙控車安全連線系統<br>
這repository是Cinent端即Android手機端
目的利用手機以加密方式跟遙控車端進行連線並傳輸命令
加密方式是利用ibadge這塊晶片來做硬體的加密
密文傳輸方式是利用MQTT server
車子方面是Yun會解密訊息給然後用socket傳給Pi
然後Pi使用uart傳控制訊息給車子


## 架構
[IOTYunAndPi](https://github.com/paul90539/IOTYunAndPi)
ibadge -> power on and send session key to ibadge vendor server

[IOTAndroidUI](https://github.com/paul90539/IOTAndroidUI)
**手機 -> post login -> ibadge vendor server
手機 -> post device uid -> ibadge vendor server
手機 <- response session key <- ibadge vendor server
手機 <- response device now alive or not <- ibadge vendor server
手機 -> use session key do AES encrpty command mseeage
手機 -> connect to mqtt server -> publish encrpty mseeage**

[IOTYunAndPi](https://github.com/paul90539/IOTYunAndPi)
Yun <- receive encrpty mseeage <- listen mqtt server
Yun -> use uart to request ibadge device session key
Yun -> use session key do AES decrpty command mseeage
Yun -> use socket send plaintext to raspberryPi
Pi  <- receive plaintext command
Pi  -> use uart to let car working

## 參考資料

[Android MQTT 教學(Website)](https://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service)
[Android MQTT 教學(Video)](https://www.youtube.com/watch?v=BAkGm02WBc0)
[Java Json Library 教學](https://dotblogs.com.tw/michaelchen/2015/01/12/java_decode_json)
[jasperYen GitHub -- use java to do http post example](https://github.com/jasperyen)
[android.os.NetworkOnMainThreadException](http://kuosun.blogspot.tw/2013/12/androidosnetworkonmainthreadexception.html)
[android Global variable 寫法](https://bella-study.blogspot.tw/2017/03/android-global-variable.html)
