package keygenerationcenter;

import keygenerationcenter.dao.CommandDaoBethenImpl;
import keygenerationcenter.dao.CommandDaoPiratteImpl;
import keygenerationcenter.dao.CommandFailedException;

/**
 * @author Isabelle Tingzon
 */
public final class KeyGen {
     
    private final CommandDaoBethenImpl cmdBethen = new CommandDaoBethenImpl();
    private final CommandDaoPiratteImpl cmdPiratte = new CommandDaoPiratteImpl();
    private final String MK;
    private final String PK;
    private final String DIR;
    
    public KeyGen(String pk, String mk){
        DIR = System.getProperty("user.dir") + "/";
        PK = DIR + pk;
        MK = DIR + mk;        
    } 
    
    public void generateKeysBethen() throws CommandFailedException{
        cmdBethen.setup(PK, MK);
    }
    
    public void generateKeysPiratte()throws CommandFailedException{
        cmdPiratte.setup(PK, MK);
    }
    
    public void generateSecretKeyBethen(String userId, String[] attributes) throws CommandFailedException{
        cmdBethen.keygen(userId, PK, MK, attributes);
    }  
    
    public void generateSecretKeyPiratte(String userId, String[] attributes) throws CommandFailedException{
        cmdPiratte.keygen(userId, PK, MK, attributes);
    } 
    
    public void remove(String filename) throws CommandFailedException{
        cmdBethen.remove(filename);
    }
}