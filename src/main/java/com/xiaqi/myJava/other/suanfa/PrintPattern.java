package com.xiaqi.myJava.other.suanfa;

public class PrintPattern {

	/**
	 * 打印出菱形
	 * 
	 * 
	 * 
	 * 思路： 
    *  第1行  空格数3(4-1) 星星数1(1*2-1)
  ***  第2行  空格数2(4-2) 星星数3(2*2-1)
 *****  第3行  空格数1(4-3) 星星数5(3*2-1)
*******  第4行  空格数0(4-4) 星星数7(4*2-1)
	 */
	
	public static void dd(int row) {
		for (int i = 1; i <= row; i++) {
			for (int j = 0; j < row - i; j++) {
				System.out.print(" ");
			}
			for (int k = 1; k < 2 * i; k++) {
				System.out.print("*");
			}
			System.out.println();
		}
		for (int i = row - 1; i > 0; i--) {
			for (int j = 0; j < row - i; j++){
				System.out.print(" ");
			}
			for (int k = 1; k < 2 * i; k++){
				System.out.print("*");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		dd(5);
	}

}
