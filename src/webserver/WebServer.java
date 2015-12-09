package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import server.ServerSocketThread;
import utilities.Html;

public class WebServer extends Thread{
	  private int port;
	public WebServer() {
	    port = 80;
	    this.start();
	}

	private void s(String s2) { 
	    System.out.println(s2);
	  }
	public void run() {
	    ServerSocket serversocket = null;
	    try {
	      serversocket = new ServerSocket(port);
	    }
	    catch (Exception e) { //catch any errors and print errors to gui
	      s("error:" + e.getMessage());
	      return;
	    }
	    while (true) {
	      s("WebServer listening...");
	      try {
	        Socket connectionsocket = serversocket.accept();
	        InetAddress client = connectionsocket.getInetAddress();
	        s(client.getHostName() + " connected to server.\n");
	       
	        BufferedReader input =
	            new BufferedReader(new InputStreamReader(connectionsocket.
	            getInputStream()));
	  
	        DataOutputStream output =
	            new DataOutputStream(connectionsocket.getOutputStream());
	        updateHTML();
	        http_handler(input, output);
	      }
	      catch (Exception e) { //catch any errors, and print them
	        s("error:" + e.getMessage());
	      }
	
	    } 
	  }
	  private void updateHTML(){
		  FileWriter fw = null;
		  PrintWriter pw = null;
		  try{
			  fw = new FileWriter("index.html");
			  pw = new PrintWriter(fw);
			  pw.print(Html.strFirst);
			  String tempStr = "<table border=\"1\">\n";
			  tempStr = tempStr+"<tr id=\"title2\"><td>Name</td>\n<td>Score</td></tr>\n";
			  try {
					Scanner sc = new Scanner(new File("src/score/scores"));
					while(sc.hasNext()) {
						tempStr = tempStr+"<tr>\n	<td>"+sc.next()+"</td>\n";
						tempStr = tempStr+"<td>"+Integer.toString(sc.nextInt())+"</td>\n </tr>";	
					}
					sc.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			  pw.print(tempStr);
			  pw.print(Html.strSecond);
			  pw.flush();
		  }catch (IOException ioe){
			  System.out.println("IOException: " + ioe.getMessage());
		  }finally{
			  try{
				  if(pw != null){
					  pw.close();
				  }
				  if(fw != null){
					  fw.close();
				  }
			  }catch (IOException ioe) {
				  System.out.println("IOException closing file: "+ ioe.getMessage());
			  }
		  }
	  }
	  private void http_handler(BufferedReader input, DataOutputStream output) {
	    int method = 0; //1 get, 2 head, 0 not supported
	    String path = "index.html"; //the various things, what http v, what path,
	    try {
	     
	      String tmp = input.readLine(); //read from the stream
	      String tmp2 = new String(tmp);
	      tmp.toUpperCase(); //convert it to uppercase
	      if (tmp.startsWith("GET")) { //compare it is it GET
	        method = 1;
	      } 
	      if (tmp.startsWith("HEAD")) { //same here is it HEAD
	        method = 2;
	      } 
	      if (method == 0) { // not supported
	        try {
	          output.writeBytes(construct_http_header(501, 0));
	          output.close();
	          return;
	        }
	        catch (Exception e3) {
	          s("error:" + e3.getMessage());
	        } 
	      }
	      int start = 0;
	      int end = 0;
	      for (int a = 0; a < tmp2.length(); a++) {
	        if (tmp2.charAt(a) == ' ' && start != 0) {
	          end = a;
	          break;
	        }
	        if (tmp2.charAt(a) == ' ' && start == 0) {
	          start = a;
	        }
	      }
	      path = "index.html"; 
	    }
	    catch (Exception e) {
	      s("errorr" + e.getMessage());
	    }
	    s("Client requested:" + new File(path).getAbsolutePath());
	    FileInputStream requestedfile = null;

	    try {
	      requestedfile = new FileInputStream(path);
	    }
	    catch (Exception e) {
	      try {
	        output.writeBytes(construct_http_header(404, 0));
	        output.close();
	      }
	      catch (Exception e2) {}
	      ;
	      s("error" + e.getMessage());
	    } //print error to gui

	    try {
	      int type_is = 0;
	      output.writeBytes(construct_http_header(200, 5));

	      if (method == 1) { //1 is GET 2 is head and skips the body
	        while (true) {
	          int b = requestedfile.read();
	          if (b == -1) {
	            break; //end of file
	          }
	          output.write(b);
	        }
	        
	      }
	      output.close();
	      requestedfile.close();
	    }

	    catch (Exception e) {}

	  }

	  private String construct_http_header(int return_code, int file_type) {
	    String s = "HTTP/1.0 ";
	    switch (return_code) {
	      case 200:
	        s = s + "200 OK";
	        break;
	      case 400:
	        s = s + "400 Bad Request";
	        break;
	      case 403:
	        s = s + "403 Forbidden";
	        break;
	      case 404:
	        s = s + "404 Not Found";
	        break;
	      case 500:
	        s = s + "500 Internal Server Error";
	        break;
	      case 501:
	        s = s + "501 Not Implemented";
	        break;
	    }

	    s = s + "\r\n"; //other header fields,
	    s = s + "Connection: close\r\n"; //we can't handle persistent connections
	    s = s + "Server: SimpleHTTPtutorial v0\r\n"; //server name

	    switch (file_type) {
	      case 0:
	        break;
	      case 1:
	        s = s + "Content-Type: image/jpeg\r\n";
	        break;
	      case 2:
	        s = s + "Content-Type: image/gif\r\n";
	      case 3:
	        s = s + "Content-Type: application/x-zip-compressed\r\n";
	      default:
	        s = s + "Content-Type: text/html\r\n";
	        break;
	    }
	    s = s + "\r\n"; 
	    return s;
	  	}
}




