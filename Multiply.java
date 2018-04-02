package com.intfc;

import java.util.ArrayList;
import java.lang.Math; 



public class Multiply {
	
	
	
	
	
	
	///////////
	
 


	 
		public static void main(String[] args) {

//			Formula formula = new Formula() {
//				@Override
//				public double calculate(int a) {
//					return sqrt(a * 100);
//				}
//
//			};

			// System.out.println(formula.calculate(25));
			// System.out.println(formula.sqrt(16));

			///////////////////////////////////
			String sayi1 = "125";
			String sayi2 = "125963";

			char i[] = sayi1.toCharArray();
			char j[] = sayi2.toCharArray();

			ArrayList<Integer> carpim = new ArrayList<Integer>(25);
			System.out.println(Math.multiplyExact(4, 2));

			int x = 0;
			int s1 = 0;
			int s2 = 0;
			int carp = 0;
			int tasi = 0;
			
			int carpan []=   { 1,10,100,1000,10000,100000,1000000,10000000,100000000,1000000000};
			int y =0;
			for (int j2 = j.length - 1; j2 >= 0; j2--) {
			 	tasi = 0;
				x = 0;
			 	for (int k = i.length - 1; k >= 0; k--) {
			 		s1 = Character.getNumericValue(j[j2]);
					s2 = Character.getNumericValue(i[k]);
					if (k == 0) {
						carp = Math.multiplyExact(s1, s2) + tasi;

					} else {
						carp = (Math.multiplyExact(s1, s2) + carp) % 10; // elide tutulan
			 			tasi = (int) (tasi + Math.floor(Double.valueOf((Math.multiplyExact(s1, s2)) / 10)));
					}
	                System.out.println(carp +"  *  "+ carpan[x] + "  ** index "+ x);
					carpim.add(x, carp* carpan[x+y] );
					x++;
					carp = 0;
					
				}
			 		y++;
			}
			
			int q = 0;
			int toplam[]= new int[carpim.size()];
			for (Integer integer : carpim) {
				q= q + integer;
			 	 
			}
			System.out.println("result:"+ q);
		 }
		
		

	}

	
	/////////
	
	
	

 
