package in.flobizAPI.utils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

public class RandomString {
	
	public static void main(String[] args) {
		String s=RandomStringUtils.randomAlphanumeric(8);
		
	String s1=s.toUpperCase();
		System.out.println(s1);
		
	}
	
	

}
