package com.kishan.prime;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
//	  OkHttpClient client = new OkHttpClient();
//
//	  String run(String url) throws IOException {
//	    Request request = new Request.Builder()
//	        .url(url)
//	        .build();
//
//	    Response response = client.newCall(request).execute();
//	    return response.body().string();
//	  }
//
//	  public static void main(String[] args) throws IOException {
//	    App example = new App();
//	    String response = example.run("https://primes.firebaseio.com/largestprime.json");
//	    System.out.println(response);
//	  }
	
    public static void main( String[] args ) throws IOException
    {
		System.out.println("How many more you want to generate? ");
		try(Scanner sc = new Scanner(System.in)){
			BigInteger noOfPrimesMore = new BigInteger(sc.nextLine().trim());
			new PrimeGen().generate(noOfPrimesMore);
		}
	}
}
