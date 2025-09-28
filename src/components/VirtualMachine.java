package components;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import components.SegmentTable;
import mnemonic.*;
import operands.*;
import utils.utilities;



public class VirtualMachine {
    protected VirtualMainMemory virtualMemory;
    protected SegmentTable segTable;
    protected Registers registers;
    protected HashMap<Integer,Mnemonic0> mnemonics0;
    protected HashMap<Integer,Mnemonic1> mnemonics1;
    protected HashMap<Integer,Mnemonic2> mnemonics2;
    protected Operand[] operandA;
    protected Operand[] operandB;
    protected boolean disassembler = false;


    // BLOQUE CONSTRUCTORES GETTERS Y SETTERS
    
    public VirtualMachine() {
        this.virtualMemory = new VirtualMainMemory();
        this.segTable = new SegmentTable();
        this.registers = new Registers();
        this.mnemonics0 = new HashMap<>();
        this.mnemonics1 = new HashMap<>();
        this.mnemonics2 = new HashMap<>();

        this.setMnemonics();
        this.setOperando();
    }
    

    protected void setMnemonics() {
        mnemonics0.put(0x0F, new STOP(this));

        mnemonics1.put(0x00, new SYS(this));
        mnemonics1.put(0x01, new JMP(this));
        mnemonics1.put(0x02, new JZ(this));
        mnemonics1.put(0x03, new JP(this));
        mnemonics1.put(0x04, new JN(this));
        mnemonics1.put(0x05, new JNZ(this));
        mnemonics1.put(0x06, new JNP(this));
        mnemonics1.put(0x07, new JNN(this));
        mnemonics1.put(0x08, new NOT(this));

        mnemonics2.put(0x10, new MOV(this));
        mnemonics2.put(0x11, new ADD(this));
        mnemonics2.put(0x12, new SUB(this));
        mnemonics2.put(0x13, new MUL(this));
        mnemonics2.put(0x14, new DIV(this));
        mnemonics2.put(0x15, new CMP(this));
        mnemonics2.put(0x16, new SHL(this));
        mnemonics2.put(0x17, new SHR(this));
        mnemonics2.put(0x18, new SAR(this));
        mnemonics2.put(0x19, new AND(this));
        mnemonics2.put(0x1A, new OR(this));
        mnemonics2.put(0x1B, new XOR(this));
        mnemonics2.put(0x1C, new SWAP(this));
        mnemonics2.put(0x1D, new LDL(this));
        mnemonics2.put(0x1E, new LDH(this));
        mnemonics2.put(0x1F, new RND(this));
    }

    protected void setOperando() {
        this.operandA = new Operand[4];
        this.operandB = new Operand[4];

        this.operandA[0] = null;
        this.operandA[1] = new RegisterOperand(this); 
        this.operandA[2] = new InmediateOperand(this);
        this.operandA[3] = new MemoryOperand(this);

        this.operandB[0] = null;
        this.operandB[1] = new RegisterOperand(this);
        this.operandB[2] = new InmediateOperand(this);
        this.operandB[3] = new MemoryOperand(this);
    }

    public VirtualMainMemory getVirtualMemory() {
        return virtualMemory;
    }

    public SegmentTable getSegTable() { 
        return segTable;
    }

    public Registers getRegisters() {
        return registers;
    }

    public HashMap<Integer, Mnemonic0> getMnemonics0() {
        return mnemonics0;
    }


    public HashMap<Integer, Mnemonic1> getMnemonics1() {
        return mnemonics1;
    }


    public HashMap<Integer, Mnemonic2> getMnemonics2() {
        return mnemonics2;
    }

    public void setDisassembler(boolean disassembler) {
        this.disassembler = disassembler;
    }

    public boolean getDissasembler(){
        return this.disassembler;
    }

    //FIN BLOQUE CONSTRUCTORES GETTERS Y SETTERS
    

    //BLOQUE DE VALIDACIONES


    public void verify(byte[] allbytes) throws Exception{
        try {
            
            byte[] header = new byte[5];
            byte[] version = new byte[1];
            System.arraycopy(allbytes,0,header,0, 5);
            validateHeader(header);
            System.arraycopy(allbytes, 5, version, 0, 1);
            validateVersion(version);
        } catch (Exception e) {
            throw e;
        }
    }

    protected void validateVersion(byte[] version) throws Exception{
        if(version[0] != 1) {
            throw new Exception("Invalid version");
        }
    }

    protected void validateHeader (byte[] header) throws Exception{
        byte[] expected = {'V','M','X','2','5'};
        
        for(int i = 0;i < 5;i++) {
            if(header[i] != expected[i]) {
                throw new Exception("Invalid Header");
            }  
        }
    }

    public void validateExtension(String file_path) throws Exception {
        Path path = Paths.get(file_path);
        
        if(!file_path.toLowerCase().endsWith(".vmx")){
            throw new Exception("Invalid extension");
        }
        
        if(!Files.exists(path)) {
            throw new Exception("File not found");
        }
    }

    // FIN BLOQUE VALIDACIONES


    //BLOQUE DE CARGA DE PROGRAMA EN MEMORIA PRINCIPAL
    
    public void startMemory(byte[] allbytes) throws Exception {
        byte[] size = new byte[2]; 
        byte[] memory = new byte[allbytes.length - 8];
        
        System.arraycopy(allbytes,6,size,0,2);
        System.arraycopy(allbytes,8,memory,0,allbytes.length - 8);
        
        this.virtualMemory.setMemory(memory, size,0);
        this.segTable.setSegmentTable(size, this.virtualMemory.getMemorySize());
        this.registers.loadRegisters(utilities.defaultRegisters);
    }

    //FIN BLOQUE CARGA DE PROGRAMA EN MEMORIA PRINCIPAL

    // INICIA BLOQUE DISASSEMBLER

    public void disassembler() throws Exception{
        System.out.println("--------------------DISASSEMBLER--------------------");
        CSDisassembler();
        System.out.println("----------------------------------------------------");

    }
    public void CSDisassembler() throws Exception{
        int index = 0; 
		int instruction,codop,opA,opB,tipoOpA,tipoOpB,cantOp;
        int base = this.segTable.getBase(this.registers.getRegister(26) >> 16);
		byte[] codeSegment = new byte[this.segTable.getSize(this.registers.getRegister(26) >> 16)];
		System.arraycopy(this.virtualMemory.getMemory(),base, codeSegment, 0, codeSegment.length);

		while (index < codeSegment.length) {
			instruction = ((int)codeSegment[index])&0xFF;
			codop = this.getOpcode(instruction);
			cantOp = this.cantOP(instruction);
            tipoOpA = (instruction >> 4) & 0x3; tipoOpB = (instruction >> 6) & 0x3;
            opA = 0; opB = 0;

            System.out.print(" ");

            System.out.print(String.format("[%04X] ",base));
            System.out.print(String.format("%02X ", instruction));
			if (cantOp==0) { 
                printPad(8, 0);
                System.out.println("| "+ this.mnemonics0.get(codop).toString());
			}else if (cantOp == 1){
				opB = PrintAndGetOpData(codeSegment, index, tipoOpB, 0);
				this.operandA[tipoOpB].setData(opB);
                printPad(8, tipoOpB);
                System.out.println("| " + this.mnemonics1.get(codop).toString(this.operandA[tipoOpB]));
			}else {
				opB = PrintAndGetOpData(codeSegment, index, tipoOpB, 0);
				opA = PrintAndGetOpData(codeSegment, index, tipoOpA, tipoOpB);
				this.operandA[tipoOpA].setData(opA);
				this.operandB[tipoOpB].setData(opB);        
                printPad(8, tipoOpA + tipoOpB);
				System.out.println("| " + this.mnemonics2.get(codop).toString(this.operandA[tipoOpA], this.operandB[tipoOpB]));	
			}

            index += tipoOpA + tipoOpB + 1;
            base += tipoOpA + tipoOpB + 1;
		}           

    }

    private void printPad(int maxPad, int start){
        for (int i = start; i < maxPad; i++) {
			System.out.print("   ");
		}
    }

    private int PrintAndGetOpData(byte[] codeSegment, int index , int type, int offset){
        int data = 0;
        for (int i=1; i<=type ;i++) {
            data = ((data << 8) | ((int)codeSegment[index + i + offset] & 0xFF));
            System.out.print(String.format("%02X ",codeSegment[index + i + offset]));
		}

        return data;
    }



    //FIN BLOQUE DISASSEMBLER

    //BLOQUE DE EJECUCION DEL PROGRAMA

    public void execute() throws Exception {
        Operand A,B;
        
        int instruction;
        int codop;
        int CS = this.registers.getRegister(26);
        int limitcodesegment = this.segTable.LogicToPhysic(CS) + this.segTable.getSize(CS >> 16);

        while(this.registers.getRegister(3) != -1 && this.segTable.LogicToPhysic(this.registers.getRegister(3)) < limitcodesegment) {
            
            instruction = this.getInstruction();
            codop = this.getOpcode(instruction);
            this.registers.setRegister(4, codop); 

            A = this.getOperand(((instruction >> 4) & 0x3), this.operandA, this.getData(instruction, ((instruction >> 6) & 0x3)));
            B = this.getOperand(((instruction >> 6) & 0x3), this.operandB, this.getData(instruction,0));

            
            this.setOp1Op2(A, B, instruction);
            this.addIP(instruction);
            this.Operation(codop, A, B, this.cantOP(instruction));
        }

    }

    protected int getInstruction() throws Exception {
        int instruction;
    
        instruction = this.virtualMemory.readByte(this.segTable.LogicToPhysic(this.registers.getRegister(3)));

        return instruction;
    }

    protected int cantOP(int register){
        int cant;

        if((register & 0b11100000) == 0) {
            cant = 0;
        }else if((register & 0b00010000) == 0) {
            cant = 1;
        }else {
            cant = 2;
        }
        return cant;
        
    }


    protected void addIP(int register){
        int cant =(((register >> 6) & 0x3) + ((register >> 4) & 0x3));
        cant = cant + 1;
        this.registers.add(3, cant);
    }


    protected int getData(int register, int offset) throws Exception {
        int tipo;
        if(offset > 0){
            tipo = (register >> 4) & 0x3;
        }else{
            tipo = (register >> 6) & 0x3;
        }
        
        int data = 0;
        
        for(int i = 1; i <= tipo; i++) {
            data = ((data << 8) | this.virtualMemory.readByte(this.segTable.LogicToPhysic(this.registers.getRegister(3) + offset + i)));
        }
        return data;
    }

    protected int getOpcode(int register) {
        return (register & 0x1F);
    } 

    protected Operand getOperand(int type, Operand[] operands, int data) {
        Operand operand = null;
        if(type > 0 && type < 4){
            operand = operands[type];
            operand.setData(data);
        }
        return operand;
    }

    void setOp1Op2(Operand op1, Operand op2, int instruction) {
        int tipoA = (instruction >> 4) & 0x3;
        int tipoB = (instruction >> 6) & 0x3;

        int regOp1 = 0, regOp2 = 0;

        if (op1 != null){
            regOp1 = (tipoA & 0xFF) << 24 | ((op1.getData() & 0x00FFFFFF)); //cambio 
            regOp2 = (tipoB << 24) | ((op2.getData() & 0x00FFFFFF));
        }else if(op2 != null){
            regOp1 = (tipoB << 24) | ((op2.getData() & 0x00FFFFFF));
        }

        this.registers.setRegister(5, regOp1);
        this.registers.setRegister(6, regOp2);
    }


    protected void Operation(int codop, Operand A, Operand B, int cantop) throws Exception{
        if(cantop == 0 && codop == 0x0F) {
            mnemonics0.get(codop).operate();

        }else if(cantop == 1 && codop >= 0x00 && codop <= 0x08) {
            mnemonics1.get(codop).operate(B);

        }else if(cantop == 2 && codop >= 0x10 && codop <= 0x1F) {
            mnemonics2.get(codop).operate(A, B);

        }else {
            throw new Exception("Invalid code operation");
        }
        
    }
    //FIN BLOQUE DE EJECUCION DEL PROGRAMA  
}