package com.intfc;

import java.util.ArrayList;
import java.util.List;

/* Multiply by n length with n lentgh 2 number*/
public class Multiply {
	private static String multiply(String number1, String number2) {
    String total = "0";
    List<String> subTotals = new ArrayList<>();
    int zeroCount = 0;
    for (int i = number2.length() - 1; i >= 0; i--) {
        String subTotal = multiplyHelper(number1, "" + number2.charAt(i));
        for (int j = 0; j < zeroCount; j++) {
            subTotal += "0";
        }
        zeroCount++;
        subTotals.add(subTotal);
    }
    for (String subTotal : subTotals) {
        total = addHelper(subTotal, total);
    }
    return total;
}

private static String addHelper(String number1, String number2) {
    String total = "";
    int difference = number1.length() - number2.length();
    if (difference > 0) {
        for (int i = 0; i < difference; i++) {
            number2 = "0" + number2;
        }
    } else {
        for (int i = 0; i < difference; i++) {
            number1 = "0" + number1;
        }
    }
    String additional = "0";
    for (int i = number1.length() - 1; i >= 0; i--) {
        int num1 = new Integer("" + number1.charAt(i));
        int num2 = new Integer("" + number2.charAt(i));
        String subTotal = "" + (num1 + num2 + new Integer(additional));
        if (i == 0) {
            total = subTotal + total;
        } else if (subTotal.length() == 1) {
            total = subTotal + total;
            additional = "0";
        } else {
            total = subTotal.charAt(1) + total;
            additional = "" + subTotal.charAt(0);
        }
    }
    return total;
}

private static String multiplyHelper(String number1, String number2) {
    String total = "";
    String additional = "0";
    int num2 = new Integer(number2);
    for (int i = number1.length() - 1; i >= 0; i--) {
        int num1 = new Integer("" + number1.charAt(i));
        String multiplication = "" + (num1 * num2 + new Integer(additional));
        if(i == 0) {
            total = multiplication + total;
        } else if (multiplication.length() == 1) {
            total = multiplication + total;
            additional = "0";
        } else {
            total = multiplication.charAt(1) + total;
            additional = "" + multiplication.charAt(0);
        }
    }
    return total;
 }
}

	 
