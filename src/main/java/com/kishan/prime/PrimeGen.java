package com.kishan.prime;
import java.io.IOException;
import java.math.BigInteger;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * The Class PrimeGen.
 */
public class PrimeGen
{

	/** The client. */
	private final OkHttpClient client = new OkHttpClient(); 

	/** The one. */
	private final BigInteger one = new BigInteger("1");

	/** The zero. */
	private final BigInteger zero = new BigInteger("0");

	/** The Constant TEXT. */
	public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");

	/**
	 * Generate.
	 *
	 * @param noOfPrimesMore the no of primes more
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void generate(BigInteger noOfPrimesMore) throws IOException
	{
		BigInteger largestPrime = fetchLargestPrime();
		for(BigInteger nextVal = largestPrime.add(one); noOfPrimesMore.compareTo(zero) > 0; nextVal = nextVal.add(one)){
			if(isPrime(nextVal)){
				push(String.format("{\"%s\":true}", nextVal),"https://primes.firebaseio.com/primalitytest.json");
				write(String.format("\"%s\"", nextVal), "https://primes.firebaseio.com/largestprime.json");
				BigInteger i = fetchNoOfPrimes().add(one);
				push(String.format("{\"%s\":\"%s\"}", i, nextVal), "https://primes.firebaseio.com/ithprime.json");
				write(String.format("\"%s\"", i), "https://primes.firebaseio.com/noofprimes.json");
				noOfPrimesMore = noOfPrimesMore.subtract(one);
			}else{
				push(String.format("{\"%s\":false}", nextVal),"https://primes.firebaseio.com/primalitytest.json");
			}
		}
	}

	/**
	 * Checks if is prime.
	 *
	 * @param value the value
	 * @return true, if is prime
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private boolean isPrime(BigInteger value) throws IOException
	{
		boolean isPrime = true;
		for(BigInteger i = one; true; i = i.add(one)){
			BigInteger primeVal = fetchIthPrime(i);
			if(primeVal.multiply(primeVal).compareTo(value) > 0){
				break;
			}
			if(value.mod(primeVal).equals(zero)){
				isPrime = false;
				break;
			}
		}
		return isPrime;
	}

	/**
	 * Gets the URL data.
	 *
	 * @param url the url
	 * @return the URL data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String getURLData(String url) throws IOException 
	{
		Request request = new Request.Builder()
				.url(url)
				.build();

		Response response = client.newCall(request).execute();
		return response.body().string().replaceAll("\"", "").trim();
	}

	/**
	 * Write.
	 *
	 * @param data the data
	 * @param url the url
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String write(String data, String url) throws IOException
	{
		RequestBody body = RequestBody.create(TEXT, data);
		Request request = new Request.Builder()
				.url(url)
				.put(body)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();		
	}
	
	/**
	 * Push.
	 *
	 * @param data the data
	 * @param url the url
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String push(String data, String url) throws IOException
	{
		RequestBody body = RequestBody.create(TEXT, data);
		Request request = new Request.Builder()
				.url(url)
				.patch(body)
				.build();
		Response response = client.newCall(request).execute();
		return response.body().string();		
	}


	/**
	 * Fetch largest prime.
	 *
	 * @return the big integer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private BigInteger fetchLargestPrime() throws IOException
	{
		return new BigInteger(getURLData("https://primes.firebaseio.com/largestprime.json"));
	}

	/**
	 * Fetch no of primes.
	 *
	 * @return the big integer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private BigInteger fetchNoOfPrimes() throws IOException
	{		
		return new BigInteger(getURLData("https://primes.firebaseio.com/noofprimes.json"));
	}

	/**
	 * Fetch ith prime.
	 *
	 * @param i the i
	 * @return the big integer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private BigInteger fetchIthPrime(BigInteger i) throws IOException
	{
		return new BigInteger(getURLData(String.format("https://primes.firebaseio.com/ithprime/%s.json", i.toString())));
	}

}
