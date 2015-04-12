package keygenerationcenter;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import keygenerationcenter.dao.CommandDaoBethenImpl;
import keygenerationcenter.dao.CommandDaoPiratteImpl;
import keygenerationcenter.dao.CommandFailedException;

/**
 * @author Isabelle Tingzon
 */
public final class KeyGen {
     
    private SSLProxyClient proxyclient;
    private final CommandDaoBethenImpl cmdBethen = new CommandDaoBethenImpl();
    private final CommandDaoPiratteImpl cmdPiratte = new CommandDaoPiratteImpl();
    private final String MK;
    private final String PK;
    private final String DIR;
    private final int MODE;
    
    public KeyGen(String pk, String mk, int mode){
        DIR = System.getProperty("user.dir") + "/";
        PK = DIR + pk;
        MK = DIR + mk;       
        MODE = mode;
    } 
    
    public void generatePubKeys() throws CommandFailedException, SSLClientErrorException, NoSuchAlgorithmException{
        if (!(new File(this.PK).exists()) && !(new File(this.MK).exists())){
            if (MODE == 0){
                cmdPiratte.setup(PK, MK);
                proxyclient.sendFileToProxy(PK);
                proxyclient.sendFileToProxy(MK);
            }
            else {
                cmdBethen.setup(PK, MK);
            }
        }
    }
    
     public void setProxy(String PROXYHOST, int PROXYPORT, String PUBKEY, String PWD){
        proxyclient = new SSLProxyClient(PROXYHOST, PROXYPORT, PUBKEY, PWD);
    }
    
        
    public void generateSecretKey(String userId, String[] attributes) throws CommandFailedException, SSLClientErrorException, NoSuchAlgorithmException{
        if (MODE == 0){
            cmdPiratte.keygen(userId, PK, MK, attributes);
            proxyclient.sendFileToProxy(userId + ".id");
            remove(userId + ".id");
        }
        else {
            cmdBethen.keygen(userId, PK, MK, attributes);
        }
    } 
    
    
    
    public void remove(String filename) throws CommandFailedException{
        cmdBethen.remove(filename);
    }
}