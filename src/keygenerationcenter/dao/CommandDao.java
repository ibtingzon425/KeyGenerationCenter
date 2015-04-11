package keygenerationcenter.dao;

/**
 *
 * @author angelukayetiu
 */
public interface CommandDao { 
    public void setup(String pub_key, String master_key) throws CommandFailedException;
    public void keygen(String private_key, String pub_key, String master_key, String[] attributes) throws CommandFailedException;
    public void remove(String filename) throws CommandFailedException;
    public void execute(String[] command, String strcom) throws CommandFailedException;
}
