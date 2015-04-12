package keygenerationcenter;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.*;

/**
 * @author Isabelle Tingzon
 * @author Angelu Kaye Tiu
 */
public class SSLProxyClient{
    
    private final String HOST;
    private final String PUBKEY;
    private final String PWD;
    private final int PORT;
    private String FILENAME;
        
    public SSLProxyClient(String host, int port, String pubkey, String password){
        String dir = System.getProperty("user.dir");
        pubkey = dir + "/SSLkeys/public.jks";
        password = "password";
        
        this.HOST = host;
        this.PORT = port;
        this.PUBKEY = pubkey;
        this.PWD = password;
    }
    
    //TODO find a way to send multiple files without having to close/open multiple client sockets.         
    public void sendFileToProxy(String filename) throws SSLClientErrorException, NoSuchAlgorithmException {
        this.FILENAME = filename; 
        
        try {
            //Initialize SSL Properties
            System.setProperty("javax.net.ssl.trustStore", PUBKEY);
            System.setProperty("javax.net.ssl.keyStorePassword", PWD);
                        
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();                        
            SSLSocket socket = (SSLSocket) factory.createSocket(HOST, PORT);
            socket.startHandshake();
            DataOutputStream streamOut = new DataOutputStream(socket.getOutputStream());
            DataInputStream  streamIn = new DataInputStream(new BufferedInputStream(socket.getInputStream())); 
            
            File newFile = new File(this.FILENAME);
            
            //Client sends message
            String message = "user-id";
            streamOut.writeUTF(message);
            streamOut.flush();
                        
            //Client sends message "<filename>"
            String newfilename = newFile.getName();
            streamOut.writeUTF(newfilename);
            streamOut.flush();
            
            //Client sends message "<filesize>"            
            String filesize = "" + (int)newFile.length();
            streamOut.writeUTF(filesize);
            streamOut.flush();
            
            //Client sends file to encrypt
            this.sendFile(socket, newFile);
            
            socket.close();
            
         } catch (IOException | NumberFormatException e) {
             e.printStackTrace();
            throw new SSLClientErrorException();
         }
    }
    
    private void sendFile(SSLSocket socket, File myFile) throws FileNotFoundException, IOException {     
        FileInputStream fis = new FileInputStream(myFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
        byte[] bytes = new byte[(int)myFile.length()];

        int count;
        while ((count = bis.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.flush();
        //out.close();
        //fis.close();
        //bis.close();
    }
   
    private void getFile(SSLSocket socket, String get_filename, int fileSize) throws IOException{
        BufferedInputStream get = new BufferedInputStream(socket.getInputStream());
        PrintWriter put = new PrintWriter(socket.getOutputStream(),true);
        
        // receive file
        byte [] mybytearray  = new byte [fileSize];
        InputStream is = socket.getInputStream();
        FileOutputStream fos = new FileOutputStream(get_filename);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray,0,mybytearray.length);
        int current = bytesRead;

        do {
           bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
           if(bytesRead >= 0) current += bytesRead;
        } while(current < fileSize);

        bos.write(mybytearray, 0 , current);
        bos.flush();
        System.out.println("File " + get_filename
            + " downloaded (" + current + " bytes read)");
    } 
}
