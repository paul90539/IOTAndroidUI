package com.lalako.user.iotandroidui;

import android.util.Log;

import java.math.BigInteger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by user on 2018/1/12.
 */

public class AESCrypt {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public String AESKey = "";
    String msg = "asd";
    byte[] key = {};

    public AESCrypt(String getAESKeyString){
        AESKey = getAESKeyString;

        //將Hex String 轉成 byte 陣列
        key = new BigInteger(getAESKeyString,16).toByteArray();
        Log.d("TAG", "key length:  " + key.length);
    }

    public String getEncryptOriginString(String plainText){
        byte[] result = encryptString(plainText);
        String encryptOriginString = result.toString();

        return encryptOriginString;
    }

    public byte[] getEncryptByte(String plainText){
        byte[] result = encryptString(plainText);
        byte[] encryptByte = result;

        return encryptByte;
    }

    public String getEncryptHexString(String plainText){
        byte[] result = encryptString(plainText);
        String encryptHexString = bytesToHex(result);

        return encryptHexString;
    }

    public byte[] encryptString(String plainText){

        byte[] encryptData = {};

        try {
            //設定為加密模式
            SecretKeySpec spec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, spec);

            //將字串加密，並取得加密後的資料
            encryptData = cipher.doFinal(plainText.getBytes());

            Log.d("TAG", "HEX: " + bytesToHex(encryptData));
            Log.d("TAG", "encryptData:  " + encryptData.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return encryptData;
    }

    public String decrypt(byte[] encryptData){

        try {
            //設定為解密模式
            SecretKeySpec spec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, spec);

            //將字串加密，並取得加密後的資料
            byte[] original = cipher.doFinal(encryptData);
            Log.d("TAG", "decryptData: "+new String(original));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return "a";
    }

    //將 byte 陣列轉換成 Hex String 陣列
    public  String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
