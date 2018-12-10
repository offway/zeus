package cn.offway.zeus.utils;

public class BitUtil {

	public static final int H5=1; // 0001 
	public static final int MINI=2; // 0010 
	public static final int APP=4; // 0100 
	public static final int OTHER=8; //1000
	
	public static int add(int a,int b){
		return a | b;
	}
	
	public static int del(int ab,int b){
		return ab & (~b);
	}
	
	public static boolean has(int ab,int b){
		return (ab & b) == b;
	}
	
}
