package org.twak.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketQuery {

	PrintWriter out;
	BufferedReader in;
	Socket sock;

	public SocketQuery( String host, int port ) {

		try {
			sock = new Socket( host, port );
			out = new PrintWriter( sock.getOutputStream(), true );

			in = new BufferedReader( new InputStreamReader( sock.getInputStream() ) );
		} catch ( Throwable th ) {
			th.printStackTrace();
		}
	}

	public double queryD( String query ) {

		out.write( query+"\n" );
		out.flush();

		try {
			String answer = in.readLine();
			System.out.println( "answer "+answer );
			return Double.parseDouble( answer );
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
		System.out.println( "warning invalid response" );
		return Double.NaN;
	}
	
	public double[] queryDA( String query ) {
		
		out.write( query+"\n" );
		out.flush();
		
		try {
			
			String answer = in.readLine();
			System.out.println( "answer "+answer );
			
			String[] ps = answer.split( "," );
			double[] da = new double[ps.length];
			
			for (int i = 0; i < ps.length; i++)
				da[i] = Double.parseDouble( ps[i] ); 
			
			return da;
			
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
		
		System.out.println( "warning invalid response" );
		return null;
	}

	public int queryI( String query ) {

		out.write( query + "\n" );
		out.flush();

		try {
			String answer = in.readLine();		
			System.out.println( "answer "+answer );
			return Integer.parseInt( answer );
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
		System.out.println( "warning invalid response" );
		return -1;
	}

	public void close() {
		try {
			if (out != null)
				out.close();
			if (sock != null)
				sock.close();
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

}
