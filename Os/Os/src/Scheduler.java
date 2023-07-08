import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scheduler {

    private List<Integer> Ready;
    private List<Integer> block;

    public Scheduler() {
        this.Ready = new ArrayList<Integer>();
        this.block = new ArrayList<Integer>();

    }

    public void Block(Program o) throws IOException {
        block.add(o.getP().getProcessID());
        o.getP().setProcessState(ProccessState.BLOCKED);
        o.getP().setPC(o.getP().getPC()-1);
        system.WriteProgram(o);
    }

    public void UnBlock(int procesID) throws InterruptedException, IOException {
        block.remove(0);
        Program program = system.ReadProgram(procesID);
        program.getP().setProcessState(ProccessState.NOTRUNNING);
        system.WriteProgram(program);
        setReady(procesID);
    }

    public void setReady(int procesID) throws InterruptedException, IOException {
        Ready.add(procesID);
    }

    public List<Integer> getReady() {
        return Ready;
    }
}

