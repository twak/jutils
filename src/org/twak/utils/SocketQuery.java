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
			return Double.parseDouble( answer );
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
		System.out.println( "warning invalid query" );
		return Double.NaN;
	}

	public int queryI( String query ) {

		out.write( query + "\n" );
		out.flush();

		try {
			return Integer.parseInt( in.readLine() );
		} catch ( Throwable e ) {
			e.printStackTrace();
		}
		System.out.println( "warning invalid query" );
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
