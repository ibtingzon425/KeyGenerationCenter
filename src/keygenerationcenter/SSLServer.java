package keygenerationcenter;

import java.io.*;
import java.security.KeyStore;
import java.security.cert.*;
import java.util.*;
import javax.net.ssl.*;

/**
 * @author Isabelle Tingzon
 */
public class SSLServer implements Runnable{
    
    //Referenced http://www.herongyang.com/JDK/SSL-Socket-Communication-Testing-Program.html
    protected int PORT;
    protected String KEYSTORE;
    protected char[] PWD;
    
    private SSLServerSocket serverSocket;
    private DataInputStream streamIn =  null;
    private DataOutputStream streamOut = null;
    
    public SSLServer(int port, String keystore, char[] pwd){
        PORT = port;
        KEYSTORE = "/home/issa/serv.jks";
        PWD = "password".toCharArray();
    }
    
    @Override
    public void run(){
        char ksPass[] = PWD;
        char ctPass[] = PWD;
        
        try {
            //Load Key Store File
             KeyStore ks = KeyStore.getInstance("JKS");
             ks.load(new FileInputStream(KEYSTORE), ksPass);
             KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
             kmf.init(ks, ctPass);
             
             //Initialize SSL Connection
             SSLContext sc = SSLContext.getInstance("TLS");
             sc.init(kmf.getKeyManagers(), null, null);
             SSLServerSocketFactory ssf = sc.getServerSocketFactory();
             serverSocket = (SSLServerSocket) ssf.createServerSocket(PORT);
             printServerSocketInfo(serverSocket);
             
             //Begin connection with clients
             while(!Thread.currentThread().isInterrupted()){
                
                //Connect to Client
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("Accepted connection : " + clientSocket);
                //printSocketInfo(clientSocket);
                                
                //Generate system-wide public and master keys
                KeyGen kg = new KeyGen();
                kg.generateKeys("master_key", "public_key");
                
                streamOut = new DataOutputStream(clientSocket.getOutputStream());
                streamIn = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                
                //Server receives Client's message "master_key"
                String line = streamIn.readUTF();
                System.out.println(line);
                
                // Server sends master key upon recieving the "master_key"
                if(line.equals("master_key")){
                    File myFile = new File (line);
                    //Server sends file size
                    String filesize = "" + (int)myFile.length();
                    streamOut.writeUTF(filesize);
                    streamOut.flush();
                    //Server sends file
                    sendFile(streamOut, myFile);
                    
                    //Thread.sleep(500);
                }
                
                // Server sends master key upon recieving the "public_key"
                if(line.equals("public_key")){
                    File myFile = new File (line);
                    //Server sends file size
                    String filesize = "" + (int)myFile.length();
                    streamOut.writeUTF(filesize);
                    streamOut.flush();
                    //Server sends file                    
                    sendFile(streamOut, myFile);
                }
             }
        }catch (Exception e) {
             System.err.println(e.toString());
             e.printStackTrace();
        }
    }
    
    public void sendFile(DataOutputStream os, File myFile) throws IOException{
         if(os != null && myFile.exists() && myFile.isFile()){
            FileInputStream input = new FileInputStream(myFile);
            os.writeLong(myFile.length());
            System.out.println(myFile.getAbsolutePath());
            int read = 0;
            while ((read = input.read()) != -1)
                os.writeByte(read);
            os.flush();
            input.close();
            System.out.println("File successfully sent!");
        }
    }
        
    private static void printSocketInfo(SSLSocket s) {
        System.out.println("Socket class: " + s.getClass());
        System.out.println("   Remote address = " + s.getInetAddress().toString());
        System.out.println("   Remote port = " + s.getPort());
        System.out.println("   Local socket address = " + s.getLocalSocketAddress().toString());
        System.out.println("   Local address = " + s.getLocalAddress().toString());
        System.out.println("   Local port = " + s.getLocalPort());
        SSLSession ss = s.getSession();
        System.out.println("   Protocol = " + ss.getProtocol());
   }
   private static void printServerSocketInfo(SSLServerSocket s) {
        System.out.println("Server socket class: " + s.getClass());
        System.out.println("   Socker address = " + s.getInetAddress().toString());
        System.out.println("   Socker port = " + s.getLocalPort());
        System.out.println("   Need client authentication = " + s.getNeedClientAuth());
        System.out.println("   Want client authentication = " + s.getWantClientAuth());
        System.out.println("   Use client mode = " + s.getUseClientMode());
   }
}