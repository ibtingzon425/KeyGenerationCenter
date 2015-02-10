package keygenerationcenter;
/**
 * @author Isabelle Tingzon
 */
public final class KeyGen {
     
    Command cmd = new Command();
    String mk = "/master_key";
    String pk = "/public_key";
    
    public KeyGen(){
        String dir = System.getProperty("user.dir");
        String[] mkdir = {"mkdir", "-p", dir}; 
        //cmd.execute(mkdir, "mkdir");
        mk = dir + mk;
        pk = dir + pk;
        //generateKeys(mk, pk);
    } 
    
    public void generateKeys(String mk, String pk){
        cmd.setup(pk, mk);
    }
    
    public void deleteKeys(){
        String[] remove = {"rm", "-f", mk};
        cmd.execute(remove, "rm");
        remove[2] = pk;
        cmd.execute(remove, "rm");
    }
    
    public String getMkLocation(){
        return mk;
    }
    
    public String getPkLocation(){
        return pk;
    }
    
}
