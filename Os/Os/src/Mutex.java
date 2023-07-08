import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mutex{
    private ArrayList<Integer> Accblock;
    private List<Integer> Priblock;//printBlock
    private List<Integer> Userblock;
    private MutexState Access;
    private MutexState UserIn;
    private MutexState Print;

    private int UproccessID;
    private int PproccessID;
    private int AproccessID;



    public Mutex(){
        Accblock=new ArrayList<>();
        Priblock=new ArrayList<>();
        Userblock=new ArrayList<>();
        Access=MutexState.UNLOCKED;
        UserIn=MutexState.UNLOCKED;
        Print=MutexState.UNLOCKED;
        AproccessID=-1;
        PproccessID=-1;
        UproccessID=-1;
    }
    public void semWaitAccess(int ProcessId) throws InterruptedException, IOException {
        if(Access.equals(MutexState.LOCKED)){
            Accblock.add(ProcessId);
            removeProgram(ProcessId);
        }
        else{
            AproccessID=ProcessId;
            Access=MutexState.LOCKED;
        }
    }
    public  void semSignalAccess(int ProcessId) throws InterruptedException, IOException {
        if(AproccessID!= -1 && AproccessID!=ProcessId){
            removeProgram(ProcessId);
        }
        else{
            Access=MutexState.UNLOCKED;
            if(!Accblock.isEmpty()){
                int pid = Accblock.remove(0);
                system.scheduler.UnBlock(pid);
            }
        }
    }
    public  void semWaitUserIn(int ProcessId) throws InterruptedException, IOException {
        if(UserIn.equals(MutexState.LOCKED)){
            Userblock.add(ProcessId);
            removeProgram(ProcessId);

        }
        else{
            UproccessID=ProcessId;
            UserIn=MutexState.LOCKED;
        }
    }
    public void semSignalUserIn(int ProcessId) throws InterruptedException, IOException {
       if(UproccessID != -1 && UproccessID!=ProcessId){
           removeProgram(ProcessId);
       }
       else{
           UserIn=MutexState.UNLOCKED;
           if(!Userblock.isEmpty()){
               int PId = Userblock.remove(0);
               system.scheduler.UnBlock(PId);
           }
       }
    }
    public void removeProgram(int ProcessId) throws IOException, InterruptedException {
        Program p=system.getWholeProgram(ProcessId);
        system.scheduler.Block(p);
        system.removePCB(p.getP());
        if(system.scheduler.getReady().size()!=0){
            int PID=system.scheduler.getReady().remove(0);
            Program newp= system.ReadProgram(PID);
            system.addPCB(newp.getP(),newp.getProcces());
        }
    }
    public  void semWaitPrint(int ProcessId) throws InterruptedException, IOException {
        if(Print.equals(MutexState.LOCKED)){
            Priblock.add(ProcessId);
            removeProgram(ProcessId);
        }
        else{
            PproccessID=ProcessId;
            Print = MutexState.LOCKED;
        }
    }
    public void semSignalPrint(int ProcessId) throws InterruptedException, IOException {
        if(PproccessID!= -1 && PproccessID!=ProcessId){
            removeProgram(ProcessId);
        }
        else{
            Print=MutexState.UNLOCKED;
            if(!Priblock.isEmpty()){
                int PID = Priblock.remove(0);
                system.scheduler.UnBlock(PID);
            }
        }
    }
}
