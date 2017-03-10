import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Random;

public class RSA {

	private static Random random = new Random();
	private final static int BITSIZE = 512;

	public static void main(String[] args) {
		//Create new RSA object to test with
		RSA rsa = new RSA();
		
		//Boolean to use in fermat primality test
		boolean isPrimalNumber = false;
		BigInteger p = null;
		BigInteger q = null;
		
		//Loop until a possible prime number passes the fermat primality test
		//to generate two prime numbers.
		while(!isPrimalNumber){
			p = new BigInteger(BITSIZE, random);
			if(rsa.fermatPrimalityTest(p))
				isPrimalNumber = true;
		}
		isPrimalNumber = false;
		while(!isPrimalNumber){
			q = new BigInteger(BITSIZE, random);
			if(rsa.fermatPrimalityTest(q))
				isPrimalNumber = true;
		}
		
		//Calculate n = p * q
		BigInteger n = p.multiply(q);
		
		//Calculate e as a relatively prime number to m = (p-1)(q-1)
		BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
		BigInteger e = getKey(m);
		
		//Calculate d as the modular inverse of e and m
		BigInteger d = e.modInverse(m);
		
		//Print each result to it's respective file
		rsa.printToFile("p_q.txt", p, q);
		rsa.printToFile("d_n.txt", d, n);
		rsa.printToFile("e_n.txt", e, n);
		
	}
	
	//Method to generate a key "e" for RSA encyption based on BigInteger m,
	//which is relatively prime to n.
	public static BigInteger getKey(BigInteger m) {
		int keyLength = m.bitLength() - 1;
		BigInteger e = BigInteger.probablePrime(keyLength, random);
		
		while(!(m.gcd(e)).equals(BigInteger.ONE)){
			e = BigInteger.probablePrime(keyLength, random);
		}
		return e;
	}
	
	//Method to implement the fermat primality test, which generates a number
	//which is probably prime.
	public boolean fermatPrimalityTest(BigInteger b){
		for(int x = 0; x < 100; x++){
			BigInteger a = generateBase(b);
			a = a.modPow(b.subtract(BigInteger.ONE), b);
		
			if(!a.equals(BigInteger.ONE))
				return false;
		}
		return true;
	}
	
	//Method to generate a base of type BigInteger for use in the fermat primality test
	//to test against each suspected prime number.
	private static BigInteger generateBase(BigInteger b){
		boolean isInSet = false;
		
		while(!isInSet){
			BigInteger ret = new BigInteger(b.bitLength(), random);
			if(BigInteger.ONE.compareTo(ret) <= 0 && ret.compareTo(b) < 0){
				isInSet = true;
				return ret;
			}
		}
		return BigInteger.ZERO;
	}
	
	//Method to create and print to a file of the specified string parameter
	//numbers a and b respectively.
	public void printToFile(String file, BigInteger a, BigInteger b){
		try(PrintWriter out = new PrintWriter(file)){
		    out.println(a);
		    out.println(b);
		    out.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}
	}
}
