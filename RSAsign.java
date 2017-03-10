import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class RSAsign {
	
	public static void main(String[] args) {
		
		String c;
		
		//Get file path from the user
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the path of the file(if in same directory as program" + 
				"\njust put the name of the file i.e \"file.txt\"): ");
		String filePath = scan.nextLine();
		System.out.println("Would you like to sign or verify the file?(s for sign v for verify): ");
		c = scan.next();
		
		//Create SHA-256 hash value of file contents
		String hash = "";
		byte[] fileBytes = null;
		try {
			fileBytes = hashFile(filePath, "SHA-256");
			hash = convertByteArrayToHexString(fileBytes);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//Read in the keys from the files
		BigInteger e, d, n;
		e = d = n = null;
		String signKey = "";
		String verifyKey = "";
		try {
			BufferedReader br1 = new BufferedReader(new FileReader("e_n.txt"));
			verifyKey = br1.readLine();
			br1.close();
			br1 = new BufferedReader(new FileReader("d_n.txt"));
			signKey = br1.readLine();
			e = new BigInteger(verifyKey);
			d = new BigInteger(signKey);
			n = new BigInteger(br1.readLine());
			br1.close();
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//get big integer value of the hash value
		BigInteger origBig = new BigInteger(hash, 16);
		origBig = origBig.modPow(d, n);
		
		if(c.equals("s")){
		//Sign the file
		System.out.println("Signing the file...");
		FileOutputStream fos = null;
		PrintStream ps = null;
		try {
			fos = new FileOutputStream(filePath + ".signed");
			ps = new PrintStream(fos);
			ps.println(origBig);
			String s = convertByteArrayToHexString(fileBytes);
			ps.println(s);
			fos.close();
			ps.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		}
		
		else if(c.equals("v")){
		
		//Verify the file
		System.out.println("Verifying the file...");
		String data = "";
		BufferedReader read = null;
		BigInteger message = null;
		try {
			read = new BufferedReader(new FileReader(filePath));
			//read in the key 
			message = new BigInteger(read.readLine());
			//read in the data
			data = read.readLine();
			read.close();;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		BigInteger bigSignedFile = new BigInteger(data, 16);
		message = message.modPow(e, n);
	
		//Check whether the file has been modified or if it is authentic
		if(message.equals(bigSignedFile)){
			System.out.println("The file is authentic.");
		}
		else{
			System.out.println("The file has been modified.");
		}
		scan.close();
	}
	}
	
	//Method to convert a file into an array of bytes
	@SuppressWarnings("finally")
	private static byte[] hashFile(String file, String algorithm) throws Exception{
	    
		byte[] data = null;
		try {
	    	Path path = Paths.get(file);
	        MessageDigest digest = MessageDigest.getInstance(algorithm);
	    	data = digest.digest(Files.readAllBytes(path));
	    } finally {
	    	return data;
	    }
	}
	
	
	//Method to convert a byte array to a hexadecimal string
	private static String convertByteArrayToHexString(byte[] arrayBytes) {
	    StringBuffer stringBuffer = new StringBuffer();
	    for (int i = 0; i < arrayBytes.length; i++) {
	        stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 
	        		0x100, 16).substring(1));
	    }
	    return stringBuffer.toString();
	}
}
