package keygenerationcenter;
/**
 * @author Isabelle Tingzon
 */
public final class KeyGen {
     
    Command cmd = new Command();
    String mk = "/master_key";
    String pk = "/public_key";
    String dir;
    
    public KeyGen(){
        dir = System.getProperty("user.dir");
    } 
    
    public void generateKeys(String mk, String pk){
        cmd.setup(pk, mk);
    }
    
    public void deleteKeys(){
        String[] remove = {"rm", "-f", dir + mk};
        cmd.execute(remove, "rm");
        remove[2] = dir + pk;
        cmd.execute(remove, "rm");
    }
    
    public void setMkLocation(String dir){
        mk = dir + mk;
    }
    
    public void setPkLocation(String dir){
        pk = dir + pk;
    }
    
    public String getMkLocation(){
        return mk;
    }
    
    public String getPkLocation(){
        return pk;
    }
    
}
