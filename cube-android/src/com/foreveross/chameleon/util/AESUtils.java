package com.foreveross.chameleon.util;



import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;
import android.util.Log;

public class AESUtils {
    // private final String KEY_GENERATION_ALG = "PBEWITHSHAANDTWOFISH-CBC";

    private final String KEY_GENERATION_ALG = "PBKDF2WithHmacSHA1";

    private final int HASH_ITERATIONS = 10000;
    private final int KEY_LENGTH = 256;

    private char[] humanPassphrase = { '1', '2', '3', '4', '5', '6', '7', '8',
            '9', '0', '1', '2', '3', '4', '5', '6' };

    // char[] humanPassphrase = { 'v', 't', 'i', 'o', 'n','s','f','o','t', '.',
    // 'c', 'o', 'm',
    // 'p'};
    private byte[] salt = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xA, 0xB, 0xC, 0xD,
            0xE, 0xF }; // must save this for next time we want the key

    private PBEKeySpec myKeyspec = new PBEKeySpec(humanPassphrase, salt,
            HASH_ITERATIONS, KEY_LENGTH);
    private final String CIPHERMODEPADDING = "AES/CBC/PKCS5Padding";

    private SecretKeyFactory keyfactory = null;
    private SecretKey sk = null;
    private SecretKeySpec skforAES = null;
    private byte[] iv = { 0xA, 1, 0xB, 5, 4, 0xF, 7, 9, 0x17, 3, 1, 6, 8, 0xC,
            0xD, 91 };

    private IvParameterSpec IV;

    public AESUtils() {

        try {
            keyfactory = SecretKeyFactory.getInstance(KEY_GENERATION_ALG);
            sk = keyfactory.generateSecret(myKeyspec);

        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
//            Log.e("AESdemo",
//                    "no key factory support for PBEWITHSHAANDTWOFISH-CBC");
        } catch (InvalidKeySpecException ikse) {
            ikse.printStackTrace();
//            Log.e("AESdemo", "invalid key spec for PBEWITHSHAANDTWOFISH-CBC");
        }

        // This is our secret key. We could just save this to a file instead of
        // regenerating it
        // each time it is needed. But that file cannot be on the device (too
        // insecure). It could
        // be secure if we kept it on a server accessible through https.
        byte[] skAsByteArray = sk.getEncoded();
        // Log.d("",
        // "skAsByteArray=" + skAsByteArray.length + ","
        // + Base64Encoder.encode(skAsByteArray));
        skforAES = new SecretKeySpec(skAsByteArray, "AES");
        IV = new IvParameterSpec(iv);

    }

    public byte[] encrypt(byte[] plaintext) {

//        String tmp = Base64.encodeToString(plaintext,Base64.DEFAULT);
    	byte[] ciphertext = encrypt(CIPHERMODEPADDING, skforAES, IV, plaintext);
//        String base64_ciphertext = Base64.encodeToString(ciphertext,Base64.DEFAULT);
        return ciphertext;
    }

    public byte[] decrypt(byte[] s) {
    	
//        byte[] s = Base64.decode(ciphertext_base64,Base64.DEFAULT);
//        Log.d("cube", ciphertext_base64.getBytes().length+"");
        byte[] newbytes = decrypt(CIPHERMODEPADDING, skforAES, IV,
        		s);
        Log.d("cube", newbytes.length+"");
        return newbytes;
        
    }

    // Use this method if you want to add the padding manually
    // AES deals with messages in blocks of 16 bytes.
    // This method looks at the length of the message, and adds bytes at the end
    // so that the entire message is a multiple of 16 bytes.
    // the padding is a series of bytes, each set to the total bytes added (a
    // number in range 1..16).
    private byte[] addPadding(byte[] plain) {
        byte plainpad[] = null;
        int shortage = 16 - (plain.length % 16);
        // if already an exact multiple of 16, need to add another block of 16
        // bytes
        if (shortage == 0)
            shortage = 16;

        // reallocate array bigger to be exact multiple, adding shortage bits.
        plainpad = new byte[plain.length + shortage];
        for (int i = 0; i < plain.length; i++) {
            plainpad[i] = plain[i];
        }
        for (int i = plain.length; i < plain.length + shortage; i++) {
            plainpad[i] = (byte) shortage;
        }
        return plainpad;
    }

    // Use this method if you want to remove the padding manually
    // This method removes the padding bytes
    private byte[] dropPadding(byte[] plainpad) {
        byte plain[] = null;
        int drop = plainpad[plainpad.length - 1]; // last byte gives number of
        // bytes to drop

        // reallocate array smaller, dropping the pad bytes.
        plain = new byte[plainpad.length - drop];
        for (int i = 0; i < plain.length; i++) {
            plain[i] = plainpad[i];
            plainpad[i] = 0; // don't keep a copy of the decrypt
        }
        return plain;
    }

    private byte[] encrypt(String cmp, SecretKey sk, IvParameterSpec IV,
                           byte[] msg) {
        try {
            Cipher c = Cipher.getInstance(cmp);
            c.init(Cipher.ENCRYPT_MODE, sk, IV);
            return c.doFinal(msg);
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
            Log.e("AESdemo", "no cipher getinstance support for " + cmp);
        } catch (NoSuchPaddingException nspe) {
            nspe.printStackTrace();
            Log.e("AESdemo", "no cipher getinstance support for padding " + cmp);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            Log.e("AESdemo", "invalid key exception");
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            Log.e("AESdemo", "invalid algorithm parameter exception");
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            Log.e("AESdemo", "illegal block size exception");
        } catch (BadPaddingException e) {
            e.printStackTrace();
            Log.e("AESdemo", "bad padding exception");
        }
        return null;
    }

    private byte[] decrypt(String cmp, SecretKey sk, IvParameterSpec IV,
                           byte[] ciphertext) {
        try {
        	Log.d("DEARG", cmp);
            Cipher c = Cipher.getInstance(cmp);
            c.init(Cipher.DECRYPT_MODE, sk, IV);
            return c.doFinal(ciphertext);
        } catch (NoSuchAlgorithmException nsae) {
        	nsae.printStackTrace();
            Log.e("AESdemo", "no cipher getinstance support for " + cmp);
        } catch (NoSuchPaddingException nspe) {
        	nspe.printStackTrace();
            Log.e("AESdemo", "no cipher getinstance support for padding " + cmp);
        } catch (InvalidKeyException e) {
        	e.printStackTrace();
            Log.e("AESdemo", "invalid key exception");
        } catch (InvalidAlgorithmParameterException e) {
        	e.printStackTrace();
            Log.e("AESdemo", "invalid algorithm parameter exception");
        } catch (IllegalBlockSizeException e) {
        	e.printStackTrace();
            Log.e("AESdemo", "illegal block size exception");
        } catch (BadPaddingException e) {
            Log.e("AESdemo", "bad padding exception");
            e.printStackTrace();
        }
        return null;
    }

    public static  void main(String[] args)
    {
        AESUtils aes = new AESUtils();
        File file = new File("/Users/apple/Downloads/zipTmp/a.zip");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            FileInputStream inputStream = new FileInputStream(file);
            IOUtils.copy(inputStream,outputStream);
            IOUtils.closeQuietly(inputStream);
            System.out.println("原长度："+outputStream.toByteArray().length);
            byte[] enBytes = aes.encrypt(outputStream.toByteArray());
            System.out.println("加密长度："+enBytes.length);
//            byte[] deBytes = aes.decrypt(enBytes);
//            System.out.println("解密长度："+deBytes.length);
//            ByteArrayInputStream bin = new ByteArrayInputStream(deBytes);
//            FileOutputStream fos = new FileOutputStream(new File("/Users/apple/Downloads/zipTmp/a1.zip"));
//            byte [] buffer = new byte[1024];
//            int length = 0;
//            while((length = bin.read(buffer)) != -1)
//            {
//                fos.write(buffer,0,length);
//            }
//            fos.close();
//            bin.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }


}