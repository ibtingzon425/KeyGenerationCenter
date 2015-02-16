package keygenerationcenter;

import java.io.*;
import java.security.KeyStore;
import javax.net.ssl.*;

/**
 * @author Isabelle Tingzon
 * @author Angelu Kaye Tiu
 */
public class SSLServer implements Runnable{
    
    //Referenced http://www.herongyang.com/JDK/SSL-Socket-Communication-Testing-Program.html
    
    protected int PORT;
    protected String KEYSTORE;
    protected char[] PWD;
    
    private SSLServerSocket serverSocket;
    private DataInputStream streamIn =  null;
    private DataOutputStream streamOut = null;
    
    private KeyGen kg;
    private KeyGenGUI aa;
    
    public SSLServer(int port, String keystore, char[] pwd){
        String dir = System.getProperty("user.dir");
        PORT = port;
        
        //KEYSTORE = keystore;
        KEYSTORE = dir + "/SSLkeys/server.jks";
        
        //PWD = pwd;
        PWD = "password".toCharArray();
        
        //Generate system-wide public and master keys
        kg = new KeyGen();
        
        aa = new KeyGenGUI();
        aa.setVisible(true);
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
                
                streamOut = new DataOutputStream(clientSocket.getOutputStream());
                streamIn = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                kg.generateKeys();
                
                //Server receives Client's message 
                String line = streamIn.readUTF();
                System.out.println(line);
                
                if (line.equals("delete")){ //Delete keys - KGC should not store any keys
                    kg.deleteKeys();
                }
                else if (line.equals("attributes")){
                    //Server recieves Client's username
                    String username = streamIn.readUTF();
                    System.out.println(username);  
                    aa.addUserToList(username);
                    
                    String appId = null;
                }
                else if (line.equals("master_key") || line.equals("pub_key")){
                    // Server sends key file
                    File myFile = new File (line);

                    //Server sends file size
                    String filesize = "" + (int)myFile.length();
                    streamOut.writeUTF(filesize);
                    streamOut.flush();

                    //Server sends file
                    sendFile(streamOut, myFile, (int)myFile.length());
                }
             }
        } catch (Exception e) {
             e.printStackTrace();
        }
    }
    
    public void sendFile(DataOutputStream os, File myFile, int filesize) throws IOException{
         if(os != null && myFile.exists() && myFile.isFile()){
            FileInputStream fis = null;
            
            byte[] mybytearray = new byte[filesize];
            fis = new FileInputStream(myFile);

            int count;
            while ((count = fis.read(mybytearray)) >= 0) {
                os.write(mybytearray, 0, count);
            }
            os.flush();
            System.out.println("File successfully sent!");
        }
    }
   
    //This piece of desn't wrk for Binary Files :P
    /*FileInputStream input = new FileInputStream(myFile);
            os.writeLong(myFile.length());
            System.out.println(myFile.getAbsolutePath());
            int read = 0;
            while ((read = input.read()) != -1)
                os.writeByte(read);
            os.flush();
            input.close();*/
        
   private static void printServerSocketInfo(SSLServerSocket s) {
        System.out.println("   Server socket class: " + s.getClass());
        System.out.println("   Socker address = " + s.getInetAddress().toString());
        System.out.println("   Socker port = " + s.getLocalPort());
        System.out.println("   Need client authentication = " + s.getNeedClientAuth());
        System.out.println("   Want client authentication = " + s.getWantClientAuth());
        System.out.println("   Use client mode = " + s.getUseClientMode());
   }
}
