package com.kishan.prime;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * Entry point class
 *
 */
public class App 
{

    public static void main( String[] args ) throws IOException
    {
		System.out.println("How many more you want to generate? ");
		try(Scanner sc = new Scanner(System.in)){
			BigInteger noOfPrimesMore = new BigInteger(sc.nextLine().trim());
			new PrimeGen().generate(noOfPrimesMore);
		}
	}
}
