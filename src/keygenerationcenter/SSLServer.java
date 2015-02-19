package keygenerationcenter;

import keygenerationcenter.view.KeyGenGUI;
import java.io.*;
import java.security.KeyStore;
import javax.net.ssl.*;
import keygenerationcenter.dao.CommandFailedException;

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
    
    public SSLServer(int port, String keystore, char[] pwd) throws CommandFailedException{
        String dir = System.getProperty("user.dir");
        PORT = port;
        KEYSTORE = dir + "/SSLkeys/server.jks";
        PWD = "password".toCharArray();
        //KEYSTORE = keystore;
        //PWD = pwd;
        
        //Generate system-wide public and master keys
        kg = new KeyGen();
        File pub_key = new File("pub_key");
        File master_key = new File("master_key");
        
        if (!pub_key.exists() && !master_key.exists())
            kg.generateKeys();
        
        //Call Attribute Authority GUI 
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
             System.out.println("Socket running on port " + PORT + "...");
             
             //Begin connection with clients
             while(!Thread.currentThread().isInterrupted()){
             
             //Connect to Client
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("Accepted connection : " + clientSocket);    
                streamOut = new DataOutputStream(clientSocket.getOutputStream());
                streamIn = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                
             //Server receives Client's message 
                String line = streamIn.readUTF();
                System.out.println(line);
                
                if (line.equals("attributes")){
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
                    FileInputStream fis = new FileInputStream(myFile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
                    byte[] bytes = new byte[(int)myFile.length()];
                    
                    int count;
                    while ((count = bis.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }
                    
                    out.flush();
                    out.close();
                    fis.close();
                    bis.close();
                    
                    System.out.println("File successfully sent!");
                    streamOut.flush();
                }
             }
        } catch (Exception e) {
             e.printStackTrace();
        }
    }
}