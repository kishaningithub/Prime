package com.kishan.prime;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// TODO: Auto-generated Javadoc
/**
 * The Class PrimeGen.
 */
public class PrimeGen
{

	/** The client. */
	private final OkHttpClient client; 

	/** The one. */
	private static final BigInteger ONE = new BigInteger("1");

	/** The zero. */
	private static final BigInteger ZERO = new BigInteger("0");

	/** The Constant TEXT. */
	private static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");

	/** The cache directory. */
	private static final File CACHE_DIRECTORY = new File("cache");
	
	/** The Constant CACHE_SIZE. */
	private static final long CACHE_SIZE = 1024 * 1024 * 1024; // 1 GiB

	/** The Constant REWRITE_CACHE_CONTROL_INTERCEPTOR. */
	private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
		@Override public Response intercept(Chain chain) throws IOException {
			Response originalResponse = chain.proceed(chain.request());
			return originalResponse.newBuilder()
					.header("Cache-Control", "max-age=31536000") // 1 year
					.build();
		}
	};

	/**
	 * Instantiates a new prime gen.
	 */
	public PrimeGen()
	{
		Cache cache = new Cache(CACHE_DIRECTORY, CACHE_SIZE);
		client = new OkHttpClient.Builder()
				.cache(cache)
				.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
				.build();
	}

	/**
	 * Generate.
	 *
	 * @param noOfPrimesMore the no of primes more
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void generate(BigInteger noOfPrimesMore) throws IOException
	{
		BigInteger largestPrime = fetchLargestPrime();
		BigInteger noOfPrimes = fetchNoOfPrimes();
		for(BigInteger nextVal = largestPrime.add(ONE); noOfPrimesMore.compareTo(ZERO) > 0; nextVal = nextVal.add(ONE)){
			if(isPrime(nextVal)){
				push(String.format("{\"%s\":true}", nextVal),"https://primes.firebaseio.com/primalitytest.json?print=silent");
				write(String.format("\"%s\"", nextVal), "https://primes.firebaseio.com/largestprime.json?print=silent");
				noOfPrimes = noOfPrimes.add(ONE);
				push(String.format("{\"%s\":\"%s\"}", noOfPrimes, nextVal), "https://primes.firebaseio.com/ithprime.json?print=silent");
				write(String.format("\"%s\"", noOfPrimes), "https://primes.firebaseio.com/noofprimes.json?print=silent");
				noOfPrimesMore = noOfPrimesMore.subtract(ONE);
			}else{
				push(String.format("{\"%s\":false}", nextVal),"https://primes.firebaseio.com/primalitytest.json?print=silent");
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
		for(BigInteger i = ONE; true; i = i.add(ONE)){
			BigInteger primeVal = fetchIthPrime(i);
			if(primeVal.multiply(primeVal).compareTo(value) > 0){
				break;
			}
			if(value.mod(primeVal).equals(ZERO)){
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
	 * @param cache the cache
	 * @return the URL data
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String getURLData(String url, boolean cache) throws IOException 
	{
		Request.Builder requestBuilder = new Request.Builder()
				.url(url);
		if(!cache){
			requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
		}
		Request request = requestBuilder.build();
		Response response = client.newCall(request).execute();
		return response.body().string().replaceAll("\"", "").trim();
	}

	/**
	 * Write.
	 *
	 * @param data the data
	 * @param url the url
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void write(String data, String url) throws IOException
	{
		RequestBody body = RequestBody.create(TEXT, data);
		Request request = new Request.Builder()
				.url(url)
				.put(body)
				.build();
		Response response = client.newCall(request).execute();
		response.body().close();
	}

	/**
	 * Push.
	 *
	 * @param data the data
	 * @param url the url
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void push(String data, String url) throws IOException
	{
		RequestBody body = RequestBody.create(TEXT, data);
		Request request = new Request.Builder()
				.url(url)
				.patch(body)
				.build();
		Response response = client.newCall(request).execute();
		response.body().close();	
	}


	/**
	 * Fetch largest prime.
	 *
	 * @return the big integer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private BigInteger fetchLargestPrime() throws IOException
	{
		return new BigInteger(getURLData("https://primes.firebaseio.com/largestprime.json", false));
	}

	/**
	 * Fetch no of primes.
	 *
	 * @return the big integer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private BigInteger fetchNoOfPrimes() throws IOException
	{		
		return new BigInteger(getURLData("https://primes.firebaseio.com/noofprimes.json", false));
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
		return new BigInteger(getURLData(String.format("https://primes.firebaseio.com/ithprime/%s.json", i.toString()), true));
	}

}
