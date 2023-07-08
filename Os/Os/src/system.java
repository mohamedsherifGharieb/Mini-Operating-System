import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class system {
    private static Scanner scanner;
    private static Object[] memory;
    private static int processes;
    private static int completedProcesses;

    private static int CurrentProccesId;

    private static Mutex mutex;
     static Scheduler scheduler;

     private File hardDisk;

     private static int Clock;

     private static int Quantem;
     private  static int TempQuantem;

     private static int t1;
     private static int t2;
     private static int t3;

     private static boolean  AssignIns;
    private static Var temp1;
    private static Var temp2;
    private static Var temp3;

    public system()
    {
        scanner = new Scanner(System.in);
        memory = new Object[40];
        processes = 0;
        completedProcesses = 0;
        mutex=new Mutex();
        hardDisk =new File("HardDisk.txt");
        scheduler=new Scheduler();
        this.Quantem=0;
        this.Clock=0;
        this.t2=0;
        this.t3=0;
        this.t1=0;
        this.temp1=new Var();
        temp1.setValue("");
        this.temp2=new Var();
        temp2.setValue("");
        this.temp3=new Var();
        temp3.setValue("");
    }
    static Program ReadProgram(int proccessId) {//Getting the Program from the HardDisk Aka txt file
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("HardDisk.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] Builder= searchLine(lines,proccessId);
        Program program=new Program();
        PCB pcb=new PCB();
        ArrayList<String> Proccess=new ArrayList<>();
        if(Builder != null ){
            pcb.setProcessID(Integer.parseInt(Builder[0]));
            pcb.setProcessState(ProccessState.valueOf(Builder[1]));
            pcb.setPC(Integer.parseInt(Builder[2]));
            pcb.setCodeStart(Integer.parseInt(Builder[3]));
            pcb.setCodeEnd(Integer.parseInt(Builder[4]));
            for(int i =0 ;i<3 ;i++){ // to get the Variables
                if(!(Builder[5+i].equals("none"))){
                    String[] Variable=Builder[5+i].split(":");
                    Var Temp=new Var();
                    Temp.setName(Variable[0]);
                    Temp.setValue((Variable[1]));
                    pcb.AddVar(Temp);
                }
            }
            for(int i =7 ; i<Builder.length ;i++)
            {
               if(!(Builder[i].equals("none"))){
                   Proccess.add(Builder[i]);
               }
            }
            program.setP(pcb);
            program.setProcces(Proccess);
            RemoveProgramFromDisk(pcb.getProcessID());
            return  program;
        }
        else{
            return null;
        }
    }
    private static String[] searchLine(ArrayList<String> lines, int proccessId) {
        for (int i = 0; i < lines.size(); i++) {
            String[] Line = lines.get(i).split(",");
            if(Integer.parseInt(Line[0]) == proccessId){
                return Line;
            }
        }
        return null; // Not found
    }
    public static void WriteProgram(Program program) throws IOException {//Writing Program to Txt (Aka DiskFile)
        ArrayList<String> lines = program.getProcces();
        PCB p = program.getP();
        if(ReadProgram(program.getP().getProcessID())== null){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("HardDisk.txt", true))) {
                writer.write(p.getProcessID() + "");
                writer.write("," + p.getProcessState());
                writer.write("," + p.getPC());
                writer.write("," + p.getCodeStart());
                writer.write("," + p.getCodeEnd());
                for(int i=0; i<3;i++){
                    Var variable = p.getVariables()[i];
                    if(variable == null) {
                        writer.write("," + "none");
                    }
                    else {
                        writer.write(","+p.getVariables()[i].getName()+":"+p.getVariables()[i].getValue());
                    }
                }
                for (String line : lines) {
                    if(!(line.equals("none"))){
                        writer.write(","+ line);
                    }
                }
                writer.newLine();
            }
        }
    }

    public void ArrivalTime(int t1, int t2, int t3) throws IOException, InterruptedException {
        String s="";
        scanner=new Scanner(System.in);
        System.out.println("Please Enter Quantem  Value:");
        s= scanner.next();
        if(s==null)
            throw new IOException("NO VALUE ENTERED");
        this.Quantem=Integer.parseInt(s);
        this.t1=t1;
        this.t2=t2;
        this.t3=t3;
        CheckArrival();
    }

    public static void CheckArrival() throws IOException, InterruptedException{
        if(Clock==t1){
            readFile("Program_1");
        }
        if(Clock==t2){
            readFile("Program_2");
        }
        if(Clock==t3){
            readFile("Program_3");
        }
    }
    public static boolean EmptySlice1() {
        for(int i =0 ; i < 5 ;i++){
            if(memory[i]!=null){
                return  false;
            }
        }
        return true; // No non-null elements found, array is empty
    }
    public static boolean EmptySlice2() {
        for(int i =5 ; i < 10 ;i++){
            if(memory[i]!=null){
                return  false;
            }
        }
        return true; // No non-null elements found, array is empty
    }
    public static void print(String s){
        System.out.println(s.length()==0 ? "no data entered ":s);
    }

    public static void assign(String x, String y) throws IOException, InterruptedException{
        String s="";
        if(x==null || y==null){
            throw new IOException("No Data Provided");
        }
        System.out.println(x + " = " + y);
        if(memory[0] != null && CurrentProccesId==(int)memory[0]){
            for(int i=0;i<25;i++){
                if(memory[22+i]==null){
                    memory[22+i]=new Var(x,y);
                    break;
                }
            }
        }
        else if( memory[5] != null && CurrentProccesId==(int)memory[5]){
            for(int i=0;i<40;i++){
                if(memory[37+i]==null){
                    memory[37+i]=new Var(x,y);
                    break;
                }
            }
        }
    }
    public static void addPCB(PCB P, ArrayList<String> process) throws InterruptedException {
        boolean Slice1=true;
        boolean Slice2=true;
        int slice1=10;
        int slice2=25;
        int SizeInstruction=process.size();
        if(SizeInstruction>12){
            throw new InterruptedException("Instruction Are too many not Valid");
        }
        for(int i=0;i<5;i++){
            if(memory[i]!=null){
                Slice1=false;
                break;
            }
        }
        for(int i=5;i<10;i++){
            if(memory[i]!=null){
                Slice2=false;
                break;
            }
        }
        if(Slice1==true){
            memory[0]= P.getProcessID();
            memory[1]= P.getProcessState();
            memory[2]= P.getPC();
            memory[3]= 10;
            P.setProcessState(ProccessState.RUNNING);
            for(int i=0;i<SizeInstruction;i++){
                memory[slice1++]=process.remove(0);

            }
            P.setCodeStart(10);
            memory[4]=P.getCodeStart()+SizeInstruction-1;
            for(int i=0;i<P.getVariables().length;i++){
                Var variable=P.getVariables()[i];
                if(variable != null)
                {
                    memory[22+i]=variable;
                }
            }

        }
        else if(Slice2==true){
            memory[5]= P.getProcessID();
            memory[6]= P.getProcessState();
            memory[7]= P.getPC();
            memory[8]= 25;
            P.setProcessState(ProccessState.RUNNING);
            for(int i=0;i<SizeInstruction;i++){
                memory[slice2++]=process.remove(0);
            }
            P.setCodeStart(25);
            memory[9]=P.getCodeStart()+SizeInstruction-1;
            for(int i=0;i<P.getVariables().length;i++){
                Var variable=P.getVariables()[i];
                if(variable != null)
                {
                    memory[37+i]=variable;
                }
            }
        }

    }
    public static void removePCB(PCB p) throws InterruptedException, IOException {
        int codeS=p.getCodeStart();
        int codeE=p.getCodeEnd();
        int pc=p.getPC();
        int pid=p.getProcessID();
        ArrayList<String> Proccess= new ArrayList<>();
        if(memory[0]!=null && pid==(int)memory[0]){// handle case where the memory is null
            memory[0]=null;
            memory[1]=null;
            memory[2]=null;
            memory[3]=null;
            memory[4]=null;
            for(int i=codeS; i<=codeE;i++){
                Proccess.add((String) memory[i]);
                memory[i]=null;
            }
            for(int i=0;i<p.getVariables().length;i++){
                if(memory[22+i]!= null)
                {
                    p.AddVar((Var)memory[22+i]);
                    memory[22+i]=null;
                }
            }
        }
        else if(memory[5]!=null && pid==(int)memory[5]){
            memory[5]=null;
            memory[6]=null;
            memory[7]=null;
            memory[8]=null;
            memory[9]=null;
            for(int i=codeS; i<codeE;i++){
                Proccess.add ((String) memory[i]);
                memory[i]=null;
            }
            for(int i=0;i<p.getVariables().length;i++){
                if(memory[37+i]!= null)
                {
                    p.AddVar((Var)memory[37+i]);
                    memory[37+i]=null;
                }
            }
        }
        if(pc+codeS<=codeE){
            Program Block=new Program();
            Block.setP(p);
            Block.setProcces(Proccess);
          if(Block.getP().getProcessState()!=ProccessState.BLOCKED){
                scheduler.setReady(Block.getP().getProcessID());
            }

        }
        else{ //pc+code == code end the program finished
            Program Block=new Program();
            Block.setP(p);
            Block.setProcces(Proccess);
            Block.getP().setProcessState(ProccessState.FINSHED);
            WriteProgram(Block);
            completedProcesses++;
        }
    }
    public static void readFile(String filename) throws IOException, InterruptedException { //For Program Exceution
        PCB Program=new PCB(processes,ProccessState.NOTRUNNING,0,0);
        StringBuilder result = new StringBuilder();
        String path=GetFile(filename);
        if(path==null){
            throw new FileNotFoundException("File not found");
        }
        ArrayList<String> process=new ArrayList<>();
        try {
            File file=new File(path);
            BufferedReader br=new BufferedReader(new FileReader(file));
            String st;
            while((st=br.readLine())!=null){
                if(result.length()!=0)
                    result.append("\n");
                result.append(st);
                process.add(st);
            }
        }catch (Exception e){
            System.out.println("error at readFile");
        }
        Program Block=new Program();
        Block.setP(Program);
        Block.setProcces(process);
        if(EmptySlice1() || EmptySlice2() ){
            system.AllocateToMemory(Block);
        }
        else{
            WriteProgram(Block);
            Block.getP().setProcessState(ProccessState.NOTRUNNING);
            scheduler.setReady(Program.getProcessID());
        }
        if(processes==0){
            processes=processes+1;
            Run();
        }
        processes=processes+1;
    }
    public static String ReadFile(String filename) throws FileNotFoundException { // to return a variable value
        StringBuilder result = new StringBuilder();
        String path=GetFile(filename);
        if(path==null){
            throw new FileNotFoundException("File not found");
        }
        try {
            File file=new File(path);
            BufferedReader br=new BufferedReader(new FileReader(file));
            String st;
            while((st=br.readLine())!=null){
                if(result.length()!=0)
                    result.append("\n");
                result.append(st);
            }
        }catch (Exception e){
            System.out.println("error at readFile");
        }
        return result.toString();
    }
    public static boolean CheckTemp(int ID){
        if(ID==0 && temp1.getValue().equals(""))
            return true;
        if(ID==1 && temp2.getValue().equals(""))
            return true;
        if(ID==2 && temp3.getValue().equals(""))
            return true;
       return false;
    }

    public static void Run() throws InterruptedException, IOException {
        while (memory[0] != null || memory[5] != null) {
            if (memory[0] != null){
                CurrentProccesId = (int) memory[0];
                PCB p = FirstProcessPCB();
                System.out.println("Now Program "+ p.getProcessID() +"-"+"IsRunning");
                String Cell = "";
                for (int i = Quantem; i > 0; i--){
                    if(p.getProcessState()==ProccessState.BLOCKED){
                        continue;
                    }
                    if(memory[0]!=null && (int)memory[0]==CurrentProccesId){
                        if (p.getCodeEnd() < p.getPC() + p.getCodeStart()) {
                            removePCB(p);
                            break;
                        }
                        Cell = (String) memory[p.getCodeStart() + p.getPC()];
                        if (CheckAssignIns(Cell) && CheckTemp(p.getProcessID())) {
                            PutValueInTemp(Cell, p.getProcessID());
                            Clock++;
                            CheckArrival();
                            continue;
                        }
                        p.setPC(p.getPC() + 1);
                        memory[2] = p.getPC();//changing the pc value inside the memory
                        Excecute(Cell);
                        Clock++;
                        CheckArrival();
                    }
                    else
                        break;
                }
            }


            if (memory[5] != null) {
                CurrentProccesId = (int) memory[5];
                PCB p = SecondProccessPCB();
                System.out.println("Now Program "+ p.getProcessID()+"-"+"IsRunning");
                String Cell = "";
                for (int i = Quantem; i > 0; i--) {
                    if(p.getProcessState()==ProccessState.BLOCKED){
                        continue;
                    }
                   if(memory[5] != null && (int)memory[5]==CurrentProccesId){
                       if (p.getCodeEnd() < p.getPC() + p.getCodeStart()){
                           removePCB(p);
                           break;
                       }
                       Cell = (String) memory[p.getCodeStart() + p.getPC()];
                       if (CheckAssignIns(Cell) && CheckTemp(p.getProcessID())) {
                           PutValueInTemp(Cell, p.getProcessID());
                           Clock++;
                           CheckArrival();
                           continue;
                       }
                       p.setPC(p.getPC() + 1);
                       memory[7] = p.getPC();//changing the pc value inside the memory
                       Excecute(Cell);
                       Clock++;
                       CheckArrival();
                   }
                   else
                       break;
                }
            }
            if (!(scheduler.getReady().isEmpty())) {
                if (EmptySlice1() || EmptySlice2()) {
                    Integer Block2 = scheduler.getReady().remove(0);
                    Program program2 = ReadProgram(Block2);
                    AllocateToMemory(program2);
                }
                else {
                    Swap();
                }

            }
        }
    }
public static void Swap() throws IOException, InterruptedException {
    Program oldP = getWholeProgram(FirstProcessPCB().getProcessID());
    Program oldP2 = getWholeProgram(SecondProccessPCB().getProcessID());
    removePCB(oldP.getP());
    removePCB(oldP2.getP());
    WriteProgram(oldP);
    WriteProgram(oldP2);
    Integer Block = scheduler.getReady().remove(0);//getting the two process
    Program p1 = ReadProgram(Block);
    Integer Block2 = scheduler.getReady().remove(0);
    Program program2 = ReadProgram(Block2);
    AllocateToMemory(p1);
    AllocateToMemory(program2);
    }
    private static void AllocateToMemory(Program P) throws InterruptedException {
        PCB pcb=P.getP();
        ArrayList<String> procces=  P.getProcces();
        addPCB(pcb,procces);
    }

    private static void PutValueInTemp(String cell, int processID) throws FileNotFoundException {
        String[] Cell = cell.trim().split("\\s+");
        String Command = Cell[0].toLowerCase();
        String Value = "";
        if (Cell.length == 4){
            String S1="";
            if(memory[0] != null &&CurrentProccesId==(int)memory[0]){
                for(int i=0;i<3;i++){
                    Var var= (Var) memory[22+i];
                    if(var != null){
                        if(Cell[3].equalsIgnoreCase(var.getName())){
                            S1 = var.getValue();//the String name
                             }}}}

            else if(memory[5] != null && CurrentProccesId==(int)memory[5]){
                for(int i=0;i<3;i++){
                    Var var= (Var) memory[37+i];
                    if(var != null){
                        if(Cell[3].equalsIgnoreCase(var.getName())) {
                            S1 = var.getValue();//the String name
                        }}}}
            Value = ReadFile(S1);
        } else {
            scanner = new Scanner(System.in);
            System.out.println("Please Enter A Value:");
            Value = scanner.next();
        }
        switch (processID){
        case 0: temp1.setValue((Value)); break;
        case 1: temp2.setValue((Value)); break;
        default: temp3.setValue((Value)); break;
    }
    }

    public static boolean CheckAssignIns(String Cell){
        String[] cell=Cell.trim().split("\\s+");
        String Command=cell[0].toLowerCase();
        if(Command.equalsIgnoreCase("assign")){
            return true;
        }
      return false;
    }
    public static void Excecute(String cell) throws InterruptedException, IOException {
       String[] Cell=cell.trim().split("\\s+");
       String Command=Cell[0].toLowerCase();
        if("writefile".equalsIgnoreCase(Command)){
            String S1="";
            String S2="";
            if(memory[0] != null &&CurrentProccesId==(int)memory[0]){
                for(int i=0;i<3;i++){
                    Var var= (Var) memory[22+i];
                    if(var != null){
                        if(Cell[1].equalsIgnoreCase(var.getName()))
                        {
                            S1 = var.getValue();//the String name
                        }
                        else if(Cell[2].equalsIgnoreCase(var.getName())){
                            S2 = var.getValue();//the String name
                        }
                    }
                }
            }

            else if(memory[5] != null && CurrentProccesId==(int)memory[5]){
                for(int i=0;i<3;i++){
                    Var var= (Var) memory[37+i];
                    if(var != null){
                        if(Cell[1].equalsIgnoreCase(var.getName()))
                        {
                            S1 = var.getValue();//the String name
                        }
                        else if(Cell[2].equalsIgnoreCase(var.getName())){
                            S2 = var.getValue();//the String name
                        }
                    }
                }
            }
            writeFile(S1,S2);
        }
        if("print".equalsIgnoreCase(Command)){
            String S1="";
            if(memory[0] != null &&CurrentProccesId==(int)memory[0]){
                for(int i=0;i<3;i++){
                    Var var= (Var) memory[22+i];
                    if(var != null){
                        if(Cell[1].equalsIgnoreCase(var.getName())){
                            S1 = var.getValue();//the String name
                        }}}}

            else if(memory[5] != null && CurrentProccesId==(int)memory[5]){
                for(int i=0;i<3;i++){
                    Var var= (Var) memory[37+i];
                    if(var != null){
                        if(Cell[1].equalsIgnoreCase(var.getName())){
                            S1 = var.getValue();//the String name
                        }}}}
            print(S1);
        }
        if("assign".equalsIgnoreCase(Command)){
            if(CurrentProccesId==0){
                assign(Cell[1], String.valueOf(temp1.getValue()));
                temp1.setValue("");
            }
            if(CurrentProccesId==1){
                assign(Cell[1], String.valueOf(temp2.getValue()));
                temp2.setValue("");
            }
            if(CurrentProccesId==2){
                assign(Cell[1], String.valueOf(temp3.getValue()));
                temp3.setValue("");
            }
        }
        if("printFromto".equalsIgnoreCase(Command)){
            int number=0;
            int number2=0;
            if(memory[0] != null &&CurrentProccesId==(int)memory[0]){
                for(int i=0;i<3;i++){
                    Var var= (Var) memory[22+i];
                    if(var != null){
                        if(Cell[1].equalsIgnoreCase(var.getName()))
                        {
                            number = Integer.parseInt(var.getValue());
                        }
                        else if(Cell[2].equalsIgnoreCase(var.getName())){
                            number2 = Integer.parseInt(var.getValue());
                        }
                    }
                }
            }

        else if(memory[5] != null && CurrentProccesId==(int)memory[5]){
                for(int i=0;i<3;i++){
                    Var var= (Var) memory[37+i];
                    if(var != null){
                        if(Cell[1].equalsIgnoreCase(var.getName()))
                        {
                            number = Integer.parseInt(var.getValue());
                        }
                        else if(Cell[2].equalsIgnoreCase(var.getName())){
                            number2 = Integer.parseInt(var.getValue());
                        }
                    }
                }
            }
            printFromTo(number , number2);
        }
        if("semwait".equalsIgnoreCase(Command)){
            SemWait(Cell[1]);
        }
        if("semsignal".equalsIgnoreCase(Command)){
            SemSignal(Cell[1]);
        }
    }

    public static boolean CheckFinish(int pc, int codeStart, int codeEnd) {
        if(pc+codeStart>codeEnd)
            return true;
        return false;
    }

    public static void writeFile(String filename, String Data) throws FileNotFoundException {
        if(filename ==null || Data ==null)
            System.out.println("no Filename or Data provided");

        if(GetFile(filename)==null){//CreateNew File to write in
         createFile(filename);
        }
        String path=GetFile(filename);
        if(path == null || Data == null )
            throw new FileNotFoundException("File is not there");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(Data);
            System.out.println("Content written to file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }

    }
    public static void printFromTo(Integer  x, Integer  y) throws IOException {
        if(x == null || y == null)
           throw new IOException("Values not provided");
        for(int i=x;i<=y;i++){
           print(i+"-");
        }
        print("");
    }
    public static void RemoveProgramFromDisk(int id) {
        try {
            File inputFile = new File(GetFile("HardDisk"));
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // extract the first number from the line using regular expressions
                String firstNumber = currentLine.replaceAll("[^0-9]+.*", "");
                int lineNumber = Integer.parseInt(firstNumber);

                // compare the first number with the given id
                if (lineNumber != id) {
                    writer.write(currentLine);
                    writer.newLine();
                }
            }

            writer.close();
            reader.close();
            // Delete the original file
            if (inputFile.delete()) {
                // Rename the temporary file to the original file name
                if (!tempFile.renameTo(inputFile)) {
                    System.out.println("Failed to rename the temporary file.");
                }
            }
            else {
                System.out.println("Failed to delete the original file.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String GetFile(String filename){
        String Fdirectory = System.getProperty("user.dir");
        File directory = new File(Fdirectory);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                     if (file.getName().equals(filename+".txt")) {
                       return (file.getAbsolutePath());
                    }
                }
            }
        }
        return null;
    }
    public static void createFile(String filename){
        try {
            File file = new File(filename+".txt");
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            }
        } catch (IOException e) {

            System.out.println("An error occurred.");
        }
    }
    public static Program CurrentProgram(){
        if(memory[0]!=null){
            if((int)memory[0]==CurrentProccesId){
                return getWholeProgram(CurrentProccesId);
            }
        }
        return getWholeProgram(CurrentProccesId);

    }
    public static PCB FirstProcessPCB(){
        PCB p=  new PCB();
        p.setProcessID((Integer) memory[0]);
        p.setProcessState((ProccessState) memory[1]);
        p.setPC((Integer) memory[2]);
        p.setCodeStart((Integer) memory[3]);
        p.setCodeEnd((Integer) memory[4]);
        return p;
    }
    public static PCB SecondProccessPCB(){
        PCB p=  new PCB();
        p.setProcessID((Integer) memory[5]);
        p.setProcessState((ProccessState) memory[6]);
        p.setPC((Integer) memory[7]);
        p.setCodeStart((Integer) memory[8]);
        p.setCodeEnd((Integer) memory[9]);
        return p;
    }
    public static ArrayList<String> FirstProccessIns(PCB P){
        ArrayList<String> Proccess=new ArrayList<>();
        int codeS=P.getCodeStart();
        for(int i=codeS; i<=P.getCodeEnd();i++){
            Proccess.add ((String) memory[i]);
        }
        return Proccess;
    }
    public static ArrayList<String> SecondProccessInstructions(PCB P){
        ArrayList<String> Proccess=new ArrayList<>();
        int codeS=P.getCodeStart();
        for(int i=codeS; i<=P.getCodeEnd();i++){//fe case in el instruction a2al mn 15 fa hykon fe null pointer Exceptiqon
            Proccess.add((String) memory[i]);
        }
        return Proccess;
    }
    public static Program getWholeProgram(int PID){
        if(memory[0] != null && PID == (int)memory[0]) {
        PCB p=FirstProcessPCB();
        ArrayList<String> Ins=FirstProccessIns(p);
        Program Block=new Program();
        Block.setP(p);
        Block.setProcces(Ins);
        for(int i=0;i<3;i++){
            Var var=(Var) memory[22+i];
            if(var != null){
                p.getVariables()[i]=var;
            }
        }
        return (Block);
        }
        PCB p=SecondProccessPCB();
        ArrayList<String> Ins=SecondProccessInstructions(p);
        Program Block=new Program();
        Block.setP(p);
        Block.setProcces(Ins);
        for(int i=0;i<3;i++){
            Var var=(Var) memory[37+i];
            if(var != null){
                p.getVariables()[i]=var;
            }
        }
        return (Block);
    }


    public static void SemWait(String Source) throws InterruptedException, IOException {
       if(Source.equalsIgnoreCase("file")){
           mutex.semWaitAccess(CurrentProccesId);
       }
       else if(Source.equalsIgnoreCase("userInput")){
           mutex.semWaitUserIn(CurrentProccesId);
       }
       else{
           mutex.semWaitPrint(CurrentProccesId);
       }
    }
    public static void SemSignal(String Source) throws InterruptedException, IOException {
        if(Source.equalsIgnoreCase("file")){
            mutex.semSignalAccess(CurrentProccesId);
        }
        else if(Source.equalsIgnoreCase("userInput")){
            mutex.semSignalUserIn(CurrentProccesId);
        }
        else{
            mutex.semSignalPrint(CurrentProccesId);
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        system os=new system();
        os.ArrivalTime(0,5,10);
        System.out.println("Processes Done:"+""+completedProcesses);
    }
}
