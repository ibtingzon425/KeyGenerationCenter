package keygenerationcenter;

import java.io.*;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.*;
import keygenerationcenter.dao.CommandFailedException;

/**
 * @author Isabelle Tingzon
 * @author Angelu Kaye Tiu
 */
public class SSLServer implements Runnable {

    //Referenced http://www.herongyang.com/JDK/SSL-Socket-Communication-Testing-Program.html
    private final String KEYSTORE;
    private final char[] PWD;
    private final int PORT;
    private final String DIR;
    private final int MODE = 0;

    private final KeyGen keygen;
    private final String PUBKEY = "pub_key";
    private final String MASTERKEY = "master_key";

    private SSLServerSocket serverSocket;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;

    public SSLServer(int port, String keystore, char[] pwd) throws CommandFailedException, SSLClientErrorException, NoSuchAlgorithmException {
        DIR = System.getProperty("user.dir");
        PORT = port;
        KEYSTORE = DIR + "/SSLkeys/server.jks"; //KEYSTORE = keystore;
        PWD = "password".toCharArray(); //PWD = pwd; 

        keygen = new KeyGen(PUBKEY, MASTERKEY, MODE);
        if (MODE == 0)
            keygen.setProxy("localhost", 4000, "", "");
        keygen.generatePubKeys();
    }

    @Override
    public void run() {
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
            while (!Thread.currentThread().isInterrupted()) {
                //Connect to Client
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("Accepted connection : " + clientSocket);
                streamOut = new DataOutputStream(clientSocket.getOutputStream());
                streamIn = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

                //Server receives client's request for a certain file
                File myFile;
                String filesize;
                String message = streamIn.readUTF();
                switch (message) {
                    case "master_key":
                    case "pub_key":
                        myFile = new File(message);

                        //Server sends file size
                        filesize = "" + (int) myFile.length();
                        streamOut.writeUTF(filesize);
                        streamOut.flush();

                        this.sendFile(clientSocket, myFile);
                        break;
                    case "secret_key":
                        //Server recieves Client's userid
                        String userid = streamIn.readUTF();
                        System.out.println("UserID received is: " + userid);

                        //Server recieves Client's requested attributes
                        String attributes = streamIn.readUTF();
                        System.out.println("Attributes recieved is: " + attributes);

                        //parse attributes into an array of strings
                        String strArray[] = attributes.split("\\s+");
                        
                        keygen.generateSecretKey(userid, strArray);
                        
                        message = userid;
                        myFile = new File(message);

                        //Server sends file size
                        filesize = "" + (int) myFile.length();
                        streamOut.writeUTF(filesize);
                        streamOut.flush();

                        this.sendFile(clientSocket, myFile);
                        break;
                    case "remove":
                        userid = streamIn.readUTF();
                        keygen.remove(userid);
                        break;
                }
            }
        } catch (CommandFailedException | KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
        } catch (SSLClientErrorException ex) {
            Logger.getLogger(SSLServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendFile(SSLSocket clientSocket, File newFile) throws IOException {
        //Server sends file
        FileInputStream fis = new FileInputStream(newFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
        byte[] bytes = new byte[(int) newFile.length()];
        int count;
        while ((count = bis.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }
        out.flush();
        out.close();
        fis.close();
        bis.close();
        streamOut.flush();
        System.out.println("File successfully sent.");
    }
}
