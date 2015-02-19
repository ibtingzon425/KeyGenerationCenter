package keygenerationcenter;

import keygenerationcenter.dao.CommandDaoBethenImpl;
import keygenerationcenter.dao.CommandFailedException;

/**
 * @author Isabelle Tingzon
 */
public final class KeyGen {
     
    CommandDaoBethenImpl cmd = new CommandDaoBethenImpl();
    public String mk;
    public String pk;
    public String dir;
    
    public KeyGen(){
        dir = System.getProperty("user.dir");
        mk = dir + "/master_key";
        pk = dir + "/pub_key";
    } 
    
    public void generateKeys() throws CommandFailedException{
        //System.out.println(pk + " " + mk);
        //cmd.setup(pk, mk);
        cmd.setup(pk, mk);
    }
    
    public void generateSecretKey(String username, String[] attributes) throws CommandFailedException{
        cmd.keygen(username + "_key", pk, mk, attributes);
    }  
}
