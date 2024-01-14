package com.mjuAppSW.joA.common.encryption;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component

public class EncryptManager {
	private static String alg = "AES/CBC/PKCS5Padding";

	public String makeRandomString(){
		byte[] randomBytes = new byte[16];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(randomBytes);
		return Base64.getEncoder().encodeToString(randomBytes);
	}

	public String encrypt(String text, String encryptionKey){
		try {
			Cipher cipher = Cipher.getInstance(alg);
			SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
			IvParameterSpec IV = new IvParameterSpec(encryptionKey.substring(0,16).getBytes());
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, IV);
			byte[] encryptedBytes = cipher.doFinal(text.getBytes("UTF-8"));
			return Base64.getEncoder().encodeToString(encryptedBytes);
		}catch(Exception e){

		}
		return null;
	}

	public String decrypt(String cipherText, String encryptionKey){
		try{
			Cipher cipher = Cipher.getInstance(alg);
			SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
			IvParameterSpec IV = new IvParameterSpec(encryptionKey.substring(0,16).getBytes());
			cipher.init(Cipher.DECRYPT_MODE, keySpec, IV);
			byte[] decodeByte = Base64.getDecoder().decode(cipherText);
			return new String(cipher.doFinal(decodeByte), "UTF-8");
		}catch(Exception e){

		}
		return null;
	}
}
