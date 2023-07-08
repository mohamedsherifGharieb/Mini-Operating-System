import java.util.ArrayList;

public class Program {


    Object[] Program;
    PCB P;


    ArrayList<String> process;

 public Program(){
     this.Program=new Object[2];
     this.P=new PCB();
     this.process=new ArrayList<>();
     Program[0]=P;
     Program[1]=process;
 }
    public PCB getP() {
        return (PCB) Program[0];
    }

    public void setP(PCB p) {
        Program[0] = p;
    }
    public ArrayList<String> getProcces() {
        return (ArrayList<String>) Program[1];
    }

    public void setProcces(ArrayList<String> process) {
        Program[1] = process;
    }


}
