package keygenerationcenter.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/*
 * @author Isabelle Tingzon
 * @edited Angelu Kaye Tiu
 */
public class CommandDaoPiratteImpl implements CommandDao{
    
    // Executes bash commands in java
    // PIRATTE Command Line Tool by Sonia Jahid, University of Illinois at Urbana-Champaign.
    // For more information, visit: http://www.soniajahid.com	
    
    private final String ABEIMPL;
    private final String SETUP;
    private final String KEYGEN;
    
    
    public CommandDaoPiratteImpl() {
        this.ABEIMPL = System.getProperty("user.dir")+"/piratte/";
        this.SETUP = ABEIMPL + "easier-setup";
        this.KEYGEN = ABEIMPL + "easier-keygen";
    }
    
    @Override
    public void setup(String pub_key, String master_key) throws CommandFailedException{      
        String[] command = {SETUP, "-p",  pub_key, "-m", master_key};
        execute(command, "easier-setup");
    }
    
    public void keygen(String private_key, String pub_key, String master_key, String[] attributes) throws CommandFailedException{
        String[] command = {KEYGEN, "-o", private_key, pub_key, master_key};
        String[] commandInput = new String[command.length + attributes.length];
        System.arraycopy(command, 0, commandInput, 0, command.length);
        System.arraycopy(attributes, 0, commandInput, command.length, attributes.length); 
        execute(commandInput, "easier-keygen");        
    } 
    
    public void remove(String filename) throws CommandFailedException {
         String[] command = {"shred", "-f", "-u", filename};
         execute(command, "shred");
    }
    
    @Override
    public void execute(String[] command, String strcom) throws CommandFailedException{
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
            while ((line = reader.readLine())!= null) {
                output.append(line).append("\n");
            }
            while ((line = error.readLine())!= null) {
                output.append(line).append("\n");
            }

            //returns status code of command; 0 if successful
            int status_code = p.waitFor();
            System.out.println(strcom + " exited with status code " + status_code + "." + "\n " + output);

            if (status_code!=0) throw new CommandFailedException();
        } catch (IOException | InterruptedException e) {}
        
    }
}