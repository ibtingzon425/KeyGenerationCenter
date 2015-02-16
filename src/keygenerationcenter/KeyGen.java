package keygenerationcenter;
/**
 * @author Isabelle Tingzon
 */
public final class KeyGen {
     
    Command cmd = new Command();
    public String mk;
    public String pk;
    public String dir;
    
    public KeyGen(){
        dir = System.getProperty("user.dir");
        mk = dir + "/master_key";
        pk = dir + "/pub_key";
    } 
    
    public void generateKeys(){
        System.out.println(pk + " " + mk);
        cmd.setup(pk, mk);
        //String[] setup = {"cpabe-setup"}; 
        //cmd.execute(setup, "cpabe-setup");
    }
    
    public void generateSecretKey(String username, String[] attributes){
        cmd.keygen(username + "_key", pk, mk, attributes);
    }
    
    public void deleteKeys(){
        String[] remove = {"rm", "-f", dir + mk};
        cmd.execute(remove, "rm");
        remove[2] = dir + pk;
        cmd.execute(remove, "rm");
    }    
}
