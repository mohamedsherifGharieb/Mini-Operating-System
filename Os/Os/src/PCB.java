import java.util.ArrayList;

public class PCB {


    public void setProcessID(int processID) {
        this.processID = processID;
    }

    private int processID;
    private ProccessState processState;
    private int PC;

    private int codeStart;
    private int codeEnd;
    private int i =0;
    private Var[] Variables;

    public PCB(int processID, ProccessState processState,  int codeStart, int codeEnd) {
        this.processID = processID;
        this.processState = processState;
        this.PC = 0;
        this.codeStart = codeStart;
        this.codeEnd = codeEnd;
        this.Variables=new Var[4];
    }
    public PCB (){
        this.processID= 0;
        this.PC= 0;
        this.processState=ProccessState.NOTRUNNING;
        this.codeEnd=0;
        this.codeStart=0;
        this.Variables=new Var[3];
    }
    public int getProcessID() {
        return processID;
    }
    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }
    public ProccessState getProcessState() {
        return processState;
    }
    public int getCodeStart() {
        return codeStart;
    }

    public void setCodeStart(int codeStart) {
        this.codeStart = codeStart;
    }
    public int getCodeEnd() {
        return codeEnd;
    }

    public void setCodeEnd(int codeEnd) {
        this.codeEnd = codeEnd;
    }

    public void setProcessState(ProccessState P){
        this.processState=P;
    }
    public Var[] getVariables() {
        return Variables;
    }

    public void setVariables(Var[] variables) {
        Variables = variables;
    }
    public  void AddVar(Var x){
        Variables[this.i++]=x;
    }
}
