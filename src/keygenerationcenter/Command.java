package keygenerationcenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/*
 * @author Isabelle Tingzon
 * @edited Angelu Kaye Tiu
 */
public class Command{
    
    // Executes setup, keygen, and revoke bash commands in java
    // PIRATTE Command Line Tool by Sonia Jahid, University of Illinois at Urbana-Champaign.
    // For more information, visit: http://www.soniajahid.com	
    
    private final String abeLocation;

    public Command() {
        this.abeLocation = System.getProperty("user.dir")+"/piratte/";
    }
          
    public String setup(String pub_key, String master_key){      
        String[] command = {abeLocation + "easier-setup", "-p",  pub_key, "-m", master_key};
        return execute(command, "easier-setup");
    }
    
    public String keygen(String private_key, String pub_key, String master_key, String[] attributes){
        String[] command = {abeLocation + "easier-keygen", "-o", private_key, pub_key, master_key};
        String[] commandInput = new String[command.length + attributes.length];
        System.arraycopy(command, 0, commandInput, 0, command.length);
        System.arraycopy(attributes, 0, commandInput, command.length, attributes.length); 
        return execute(commandInput, "easier-keygen");        
    }
    
    public String revoke(String proxy_key, String pub_key, String master_key, String[] revoked_users){
        String[] command = {abeLocation + "easier-revoke", "-o", proxy_key, pub_key, master_key};
        String[] commandInput = new String[command.length + revoked_users.length];
        System.arraycopy(command, 0, commandInput, 0, command.length);
        System.arraycopy(revoked_users, 0, commandInput, command.length, revoked_users.length); 
        return execute(commandInput, "easier-revoke");
    }
      
    public String execute(String[] command, String strcom){
        StringBuilder output = new StringBuilder();
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            Process p = pb.start();
            BufferedReader reader = 
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader error = 
                            new BufferedReader(new InputStreamReader(p.getErrorStream()));

            //Read output of commands (if status is not 0)
            String line;           
            while ((line = reader.readLine())!= null || (line = error.readLine())!= null) {
                output.append(line).append("\n");
            }

            //returns status code of command; 0 if successful
            int status_code = p.waitFor();
            System.out.println(strcom + " exited with status code " + status_code + ".");
        } catch (IOException | InterruptedException e) {}
        return output.toString();
    }
}