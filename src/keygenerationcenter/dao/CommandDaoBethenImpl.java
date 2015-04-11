package keygenerationcenter.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author issa
 * @edited angelukayetiu
 */
public class CommandDaoBethenImpl implements CommandDao {
    
    private final String ABEIMPL;
    private final String SETUP;
    private final String KEYGEN;
    
    // Executes bash commands in java
    // Used for executing CP-ABE Command Line Tool by Bethencourt.
    // See http://acsc.cs.utexas.edu/cpabe/ for more details.

    public CommandDaoBethenImpl() {
        this.ABEIMPL = System.getProperty("user.dir")+"/cpabe-0.11/";
        this.SETUP = ABEIMPL + "cpabe-setup";
        this.KEYGEN = ABEIMPL + "cpabe-keygen";
    }
    
    @Override
    public void setup(String pub_key, String master_key) throws CommandFailedException{      
        String[] command = {SETUP};
        execute(command, "cpabe-setup");
    }
    
     public void keygen(String private_key, String pub_key, String master_key, String[] attributes) throws CommandFailedException{
        String[] command = {KEYGEN, "-o", private_key, pub_key, master_key};
        String[] commandInput = new String[command.length + attributes.length];
        System.arraycopy(command, 0, commandInput, 0, command.length);
        System.arraycopy(attributes, 0, commandInput, command.length, attributes.length);
        execute(commandInput, "cpabe-keygen");        
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
            while ((line = error.readLine())!= null) {
                output.append(line).append("\n");
            }
            while ((line = reader.readLine())!= null ) {
                output.append(line).append("\n");
            }

            //returns status code of command; 0 if successful
            int status_code = p.waitFor();
            System.out.println(strcom + " exited with status code " + status_code + "." + "\n " + output);

            if (status_code!=0) throw new CommandFailedException();
        } catch (IOException | InterruptedException e) {}   
    }
}