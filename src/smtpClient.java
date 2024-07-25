
import java.io.*;
import java.net.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.bind.DatatypeConverter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Elahe
 */

public class smtpClient {
  
  PrintStream out;
  BufferedReader reader;
  
  Socket socket;
  
  public void smtpClient() {
 
  }
  
  /*
  public static void main(String args[]) {
    new smtpClient().send();
  }
*/
  
  public void send(String user, String pass, String to, String subject, String data) {
        
    System.out.println("sending is starting...");
    
    try {

      //String host = "smtp.gmail.com";
      //String host = "smtp-mail.outlook.com";
      String host = "smtp.mail.yahoo.com";
      
      InetAddress adress = InetAddress.getByName(host);
      int port = 25;

      socket = new Socket(adress, port);
      System.out.println("socket: " + socket.getInetAddress());
      
      out = new PrintStream(socket.getOutputStream());
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      
      String command = "";
      
      String response;
      response = reader.readLine();
      System.out.println("response: " + response);
      
      if (!response.startsWith("220")) {
        socket.close();
      }
            
      command = "EHLO " + InetAddress.getLocalHost().getHostName();
  
      out.println(command);
      System.out.println("command: " + command);      

      response = reader.readLine();
      while(!response.startsWith("250 ")) {
        System.out.println("response: " + response);
        response = reader.readLine();
      }
      System.out.println("response: " + response);
      
      sendCmd("STARTTLS");
      
      SSLSocket sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(
       socket, 
       socket.getInetAddress().getHostAddress(), 
       socket.getPort(), 
       true);                 

      //sslSocket.setUseClientMode(true);
      //sslSocket.setEnableSessionCreation(true);        
      //System.err.println("CLIENT: securing connection");
      //sslSocket.startHandshake();
      //System.err.println("CLIENT: secured");

      socket = sslSocket;
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));    
      out = new PrintStream(socket.getOutputStream());
      
      command = "EHLO " + InetAddress.getLocalHost().getHostName();
  
      out.println(command);
      System.out.println("command: " + command);        
      
      response = reader.readLine();
      while(!response.startsWith("250 ")) {
        System.out.println("response: " + response);
        response = reader.readLine();
      }
      System.out.println("response: " + response);
      
      String username = DatatypeConverter.printBase64Binary(user.getBytes());
      String password = DatatypeConverter.printBase64Binary(pass.getBytes());
      
      sendCmd("AUTH LOGIN");
      
      sendCmd(username);
      
      sendCmd(password);
      
      sendCmd("MAIL FROM: " + "<" + user + ">");
      
      sendCmd("RCPT TO: " + "<" + to + ">");
      
      sendCmd("DATA");
      
      sendCmd("Subject: "+ subject + "\nFrom: " + user + "\nto: " + to + "\n\n" + data + "\n.");
      
      sendCmd("QUIT");
      
      socket.close();
      
    } catch (Exception ex) {
      ex.getStackTrace();
    }
    
  }
  
  private void sendCmd(String command) throws Exception {
    out.println(command);
    System.out.println("command: " + command);
      
    String response = reader.readLine();
    System.out.println("response: " + response);
    
  }

}
