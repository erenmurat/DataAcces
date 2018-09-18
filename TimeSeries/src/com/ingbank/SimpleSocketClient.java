package com.ingbank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
 import java.util.Hashtable;
import java.util.Scanner;
 
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * 
 *
 */
public class SimpleSocketClient {

 
	 
	public String simpleSocketClient() {

		String result = "";

		try {

			String time, user, url, tpsCount, line;
			/*
			 * 2018-09-03T16:51:32 url1 user1 session1 17 
			 * 2018-09-03T16:51:32 url1 user2 session2 1 
			 * 2018-09-03T16:51:32 url2 user1 session3 18 
			 * 2018-09-03T16:51:32 url2 user1 session4 12
			 */
			TimerProps ts = new TimerProps();
			ts.getPropValues();

			  SSLSocket socket = openSSLSocket(ts.hostname, Integer.parseInt(ts.port));

			  printSocketInfo(socket);

			File file = new File(ts.fileName.trim());

			Scanner sc = new Scanner(file);

 			Hashtable<String, TPSInfo> ht = new Hashtable<>();
			while (sc.hasNextLine()) {

				line = sc.nextLine();
				addInfoToObject(line, ht);
			}

			for (Object key : ht.keySet()) {
				TPSInfo info = ht.get(key);
				//com.ingbank.url.user --> time series db stores data primarily for first column is time
				String writeInfo =info.getTime() + " " + info.getUrl() + " " + info.getUser() 	+ " " + info.getCount();
				try {
					result =  writeToAndReadFromSocket(socket, writeInfo);
					if (result.length() > 0)
						System.out.println(result.substring(0, 100));
				 
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					//iff error occured, continue
				}
			}// end loop	

 			  socket.close();
		     	sc.close();
		} catch (Exception e) {
			System.out.println("SimpleSocketClient.simpleSocketClient():error:" + e.getMessage());
			result = e.getMessage();
			e.printStackTrace();
		}

		return result;
	}

	public String writeToAndReadFromSocket(Socket socket, String writeTo) throws Exception {
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(writeTo);

			bufferedWriter.flush();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String str;
			while ((str = bufferedReader.readLine()) != null) {
				sb.append(str + "\n");
			}
			bufferedReader.close();
			return sb.toString();
		} catch (IOException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Open a socket connection to the given server on the given port.
	 */
	public Socket openSocket(String server, int port) throws Exception {
		Socket socket;

		// create a socket with a timeout
		try {

			InetAddress inteAddress = InetAddress.getByName(server);
			SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);
			socket = new Socket();
			int timeoutInMs = 10 * 5000; // 10 seconds
			socket.connect(socketAddress, timeoutInMs);
			return socket;
		} catch (SocketTimeoutException ste) {
			System.out.println("Timed out waiting for the socket.:" + ste.getMessage());
			System.err.println("Timed out waiting for the socket.");
			ste.printStackTrace();
			throw ste;
		}
	}

	public SSLSocket openSSLSocket(String server, int port) throws Exception {
		Socket socket;

		// create a socket with a timeout
		try {

			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(server, port);

			return sslsocket;
		} catch (SocketTimeoutException ste) {
			System.out.println("SimpleSocketClient.openSSLSocket()|hata:" + ste.getMessage());
			System.err.println("Timed out waiting for the socket.");
			ste.printStackTrace();
			throw ste;
		}
	}

	private static void printSocketInfo(SSLSocket s) {

		SSLSession ss = s.getSession();
	}

	private void addInfoToObject(String info, Hashtable<String,TPSInfo> ht) {

		TPSInfo tp = getTpsInfo(info);
		String  key = tp.getTime() + "#" + tp.getUrl() + "#" + tp.getUser();
 		if (ht.containsKey(key) ){// if exists add old and new value
 			TPSInfo temp = (TPSInfo) ht.get(key);
			int Count = temp.getCount() + tp.getCount();
			tp.setCount(Count);
			ht.remove(key);
			ht.put(key, tp);
		} else {
			// if not exists
			ht.put(key, tp);
		}

	}

	/*
	 * split line by " "
	 */
	public TPSInfo getTpsInfo(String info) {

		String tempVar[] = null;
		tempVar = info.split(" ");
		TPSInfo tp = new TPSInfo();
		tp.setTime(tempVar[0]);
		tp.setUrl(tempVar[1]);
		tp.setUser(tempVar[2]);
		tp.setSession(tempVar[3]);
		tp.setCount(Integer.parseInt(tempVar[4]));
		return tp;
	}

	public static void main(String[] args) {
		SimpleSocketClient client = new SimpleSocketClient();
		client.simpleSocketClient();

	}

}
