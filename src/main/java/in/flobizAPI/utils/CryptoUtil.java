package in.flobizAPI.utils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * This class provides util methods for encryption and decryption
 */
public class CryptoUtil {

	/** The Constant RSA_ECB_PKCS1_PADDING. */
	private static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";

	/**
	 * Decrypt using private key.
	 *
	 * @param key
	 * @param data
	 * @return the byte[]
	 */

	public static byte[] decryptUsingPrivateKey(byte[] key, byte[] data) {
		PrivateKey privateKey = null;
		try {
			privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(key));
		} catch (Exception e) {
			System.err.println("Error while forming private key from byte array" + data);
			e.printStackTrace();
		}
		return decryptUsingPrivateKey(privateKey, data);
	}

	/**
	 * Decrypt using private key.
	 *
	 * @param key
	 * @param data
	 * @return the byte[]
	 */
	public static byte[] decryptUsingPrivateKey(Key key, byte[] data) {
		byte[] decodedData = Base64.decodeBase64(data);
		byte[] decryptedText = null;
		try {
			final Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
			cipher.init(Cipher.DECRYPT_MODE, key);
			decryptedText = cipher.doFinal(decodedData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedText;
	}

	/**
	 * Encrypt using public key.
	 *
	 * @param key
	 *            the key
	 * @param data
	 *            the data
	 * @return the byte[]
	 */
	public static byte[] encryptUsingPublicKey(byte[] key, byte[] data) {
		PublicKey publicKey = null;
		try {
			publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
		} catch (Exception e) {
		}
		return encryptUsingPublicKey(publicKey, data);
	}

	/**
	 * Encrypt using public key.
	 *
	 * @param key
	 *            the key
	 * @param data
	 *            the data
	 * @return the byte[]
	 */

	public String getEncrypterPassword(String key, String password) throws Exception {
		final byte[] publicKeyAsBytes = Base64.decodeBase64(key);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyAsBytes));
		Cipher decrypt = Cipher.getInstance("RSA");
		decrypt.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] encryptedPassword = decrypt.doFinal(password.toString().getBytes());
		return Base64.encodeBase64String(encryptedPassword);
	}

	public static byte[] encryptUsingPublicKey(PublicKey key, byte[] data) {
		byte[] cipherText = null;
		try {
			final Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(data);
		} catch (Exception e) {
		}
		return Base64.encodeBase64(cipherText);
	}

	public static String convertByteToHex(byte data[]) {
		StringBuilder hexData = new StringBuilder();
		for (int byteIndex = 0; byteIndex < data.length; byteIndex++) {
			hexData.append(Integer.toString((data[byteIndex] & 0xff) + 0x100, 16).substring(1));
		}
		return hexData.toString();
	}

}
