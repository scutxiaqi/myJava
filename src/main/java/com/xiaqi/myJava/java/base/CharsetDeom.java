package com.xiaqi.myJava.java.base;

import java.io.UnsupportedEncodingException;

public class CharsetDeom {
	public static void main(String[] args) throws UnsupportedEncodingException {
		String ss = "I am 君山";//unicode字符集
		byte[] bb = {};
		bb = ss.getBytes();//eclipse默认编码utf-8
		toHex(bb);
		bb = ss.getBytes("ISO-8859-1");
		toHex(bb);
		bb = ss.getBytes("GBK");
		toHex(bb);
		bb = ss.getBytes("UTF-16");
		toHex(bb);
	}

	/**
	 * 转16进制
	 * 
	 * @param src
	 */
	public static void toHex(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv + " ");
		}
		System.out.println(stringBuilder.toString());
	}
}
