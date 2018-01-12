package com.lalako.user.iotandroidui;

import android.util.Log;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by user on 2018/1/12.
 */



public class AESCrypt {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    String msg = "asd";

    public AESCrypt(String getStr){
        init(getStr);
    }

    public void init(String getStr){

        //byte[] key = getStr.getBytes();
        byte[] key = new BigInteger(getStr,16).toByteArray();
        Log.d("TAG", "key length:  " + key.length);
        try {
            SecretKeySpec spec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, spec);
            //將字串加密，並取得加密後的資料
            byte[] encryptData = cipher.doFinal(msg.getBytes());
            //System.out.println("加密後字串："+new String(encryptData));
            for(byte showB : encryptData){

                Log.d("TAG", "" + showB);

            }
            Log.d("TAG", "HEX: " + bytesToHex(encryptData));
            Log.d("TAG", "encryptData:  " + encryptData.toString());

            spec = new SecretKeySpec(key, "AES");
            cipher = Cipher.getInstance("AES");
            //設定為解密模式
            cipher.init(Cipher.DECRYPT_MODE, spec);
            byte[] original = cipher.doFinal(encryptData);
            System.out.println("解密後字串："+new String(original));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
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
