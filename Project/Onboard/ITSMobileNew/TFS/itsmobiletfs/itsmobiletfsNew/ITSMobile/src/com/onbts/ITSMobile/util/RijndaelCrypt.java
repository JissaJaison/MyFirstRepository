package com.onbts.ITSMobile.util;

import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/*

Example:

RijndaelCrypt crypt = new RijndaelCrypt("Test");
              
              
String dec = crypt.decrypt("nho+18yBDmapk2np6mOFeg==");
System.out.println (dec);
              
String enc = crypt.encrypt("its");
System.out.println(enc);


*/

public class RijndaelCrypt {
	public static Logger log = Logger.getLogger(RijndaelCrypt.class.getName());

	private static String TRANSFORMATION = "AES/CBC/PKCS5Padding";
	private static String ALGORITHM = "AES";
	private static String DIGEST = "MD5";

	private static Cipher _cipher;
	private static SecretKey _password;
	private static IvParameterSpec _IVParamSpec;

	// 16-byte private key
	// private static byte[] IV = "password".getBytes();

	/**
	 * Constructor
	 * 
	 * @password Public key
	 */
	public RijndaelCrypt(String password) {

		try {

			// Encode digest
			MessageDigest digest;
			digest = MessageDigest.getInstance(DIGEST);
			byte[] key = digest.digest(password.getBytes("UTF-8"));
			_password = new SecretKeySpec(key, ALGORITHM);

			// Initialize objects
			_cipher = Cipher.getInstance(TRANSFORMATION);
			_IVParamSpec = new IvParameterSpec(key);

		} catch (NoSuchAlgorithmException e) {
			log.info("No such algorithm " + ALGORITHM + e);
		} catch (NoSuchPaddingException e) {
			log.info("No such padding PKCS5" + e);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Encryptor.
	 * 
	 * @text String to be encrypted
	 * @return Base64 encrypted text
	 */
	public String encrypt(String text) {

		byte[] encryptedData;

		try {

			_cipher.init(Cipher.ENCRYPT_MODE, _password, _IVParamSpec);
			encryptedData = _cipher.doFinal(text.getBytes("UTF-16LE"));

		} catch (InvalidKeyException e) {
			log.info("Invalid key  (invalid encoding, wrong length, uninitialized+etc)."
					+ e);
			return null;
		} catch (InvalidAlgorithmParameterException e) {
			log.info("Invalid or inappropriate algorithm parameters for "
					+ ALGORITHM + e);
			return null;
		} catch (IllegalBlockSizeException e) {
			log.info(

			"The length of data provided to a block cipher is incorrect" + e);
			return null;
		} catch (BadPaddingException e) {
			log.info("The input data but the data is not padded properly." + e);
			return null;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return Base64.encodeToString  (encryptedData, Base64.NO_WRAP);

	}

	/**
	 * Decryptor.
	 * 
	 * @text Base64 string to be decrypted
	 * @return decrypted text
	 */
	public String decrypt(String text) {

		try {
			_cipher.init(Cipher.DECRYPT_MODE, _password, _IVParamSpec);

			byte[] decodedValue = Base64.decode(text, Base64.NO_WRAP);
			byte[] decryptedVal = _cipher.doFinal(decodedValue);
			return new String(decryptedVal, "UTF-16LE");

		} catch (InvalidKeyException e) {
			log.info(

			"Invalid key  (invalid encoding, wrong length, uninitialized+etc)."
					+ e);
			return null;
		} catch (InvalidAlgorithmParameterException e) {
			log.info("Invalid or inappropriate algorithm parameters for "
					+ ALGORITHM + e);
			return null;
		} catch (IllegalBlockSizeException e) {
			log.info("The length of data provided to a block cipher is incorrect"
					+ e);
			return null;
		} catch (BadPaddingException e) {
			log.info("The input data but the data is not padded properly." + e);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	 

}
