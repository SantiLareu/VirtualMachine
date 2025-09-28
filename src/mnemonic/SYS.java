package mnemonic;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.Scanner.*;

import components.VirtualMachine;
import operands.*;

public class SYS extends Mnemonic1 {
    
    public SYS(VirtualMachine vm) {
        super(vm, "SYS");
    }
    
    @Override
    public void operate(Operand A) throws Exception {
        int ECX = this.vm.getRegisters().getRegister(12);
        int EDX = this.vm.getRegisters().getRegister(13);
        int EAX = this.vm.getRegisters().getRegister(10);
        int type = A.getValue();

        int numberCells = ECX & 0xFFFF;
        int sizeCells = (ECX >> 16) & 0xFFFF; 

        switch (type) {
            case 1:
                this.READ(numberCells, sizeCells, EAX, EDX);
                break;
            case 2:
                this.WRITE(numberCells, sizeCells, EAX, EDX);
                break;
            default:
                break;
        }



    }
    
    //FUNCIONES COMUNES
    private void writeInMemory(int value, int sizeCells, int logic_address) throws Exception {

        int address = this.vm.getSegTable().LogicToPhysic(logic_address);
        this.vm.getVirtualMemory().writeNbytes(address, sizeCells, value);
    }
    
    private int readFromMemory(int sizeCells, int logic_address) throws Exception {
        int address = this.vm.getSegTable().LogicToPhysic(logic_address);
        return this.vm.getVirtualMemory().readNbytes(address, sizeCells);

    }
    //FIN FUNCIONES COMUNES

    //INICIO BLOQUE READ (SYS 1)

    private void READ(int numberCells,int sizeCells, int format, int EDX) throws Exception {
        int value;
        int address = EDX;
        Scanner input = new Scanner(System.in);

        for(int i = 0; i < numberCells; i++){
            try{
                System.out.print("["+ String.format("%04X",this.vm.getSegTable().LogicToPhysic(address)) + "]: ");
                String data = input.nextLine();
                value = convertInt(data,format);
                writeInMemory(value, sizeCells, address);
                address += sizeCells;
            }catch(Exception e){
                throw e;
            }
        }
        
    }

    private int convertInt(String data, int format) throws Exception {
        int value = 0;

        //CONVIERTE A HEXA SI LO PERMITE  format
        if((format & 0b01000) != 0){
            value = Integer.parseInt(data,16);
        }
        //CONVIERTE A BINARIO SI LO PERMITE  format
        else if((format & 0b10000) != 0){
            value = Integer.parseInt(data,2);
        }
        //CONVIERTE A OCTAL SI LO PERMITE format
        else if((format & 0b00100) != 0){
            value = Integer.parseInt(data, 8);
        }
        //CONVIERTE UN CARECTER A DECIMAL SI LO PERMITE format
        else if(data.length() == 1 && (format & 0b00010) != 0){
            value = (int)data.charAt(0);
        }
        //CONVIERTE A DECIMAL SI LO PERMITE format
        else if((format & 0b00001) != 0){
            value = Integer.parseInt(data, 10);
        
        }else{
            throw new Exception("Invalid conversion type for AL register.");
        }

        return value;
    }

    //FIN BLOQUE READ (SYS 1)

    
    //INICIO BLOQUE WRITE (SYS 2)

    private void WRITE(int numberCells,int sizeCells, int format, int EDX) throws Exception {
        int address = EDX;

        for (int i = 0; i < numberCells; i++) {
            try {
                int value = readFromMemory(sizeCells, address);
                String output = convertToString(value, format);
                System.out.println("["+ String.format("%04X",this.vm.getSegTable().LogicToPhysic(address)) + "] " + output);
                address += sizeCells;
            } catch (Exception e) {
                throw e;
            }
        }
    }

    private String convertToString(int value, int format) throws Exception {
        String result = "";


        // CONVIERTE A HEXA SI LO PERMITE format
        if ((format & 0b01000) != 0) {
            result = String.format("0x%08X", value);
        }
        // CONVIERTE A BINARIO SI LO PERMITE format
        if ((format & 0b10000) != 0) {
            if (result.length() > 0) 
                result += " ";
            result += "0b" + Integer.toBinaryString(value);
        }
        // CONVIERTE A OCTAL SI LO PERMITE format
        if ((format & 0b00100) != 0) {
            if (result.length() > 0) 
                result += " ";
            result += "0o" + Integer.toOctalString(value);
        }
        // CONVIERTE A CHAR SI LO PERMITE format
        if ((format & 0b00010) != 0) {
            if (result.length() > 0) 
                result += " ";
            
            StringBuilder chars = new StringBuilder();
            boolean printable = false;
            for (int i=3; i>=0;i--){
                int b = (value >> (i*8)) & 0xFF;
                if (b >= 32 && b <= 126 ) {
                    chars.append((char) b);
                    printable = true;
                }
            }
            if (!printable){
                chars.append(".");
            }
            result += chars.toString();
        }
        // CONVIERTE A DECIMAL SI LO PERMITE format
        if ((format & 0b00001) != 0) {
            if (result.length() > 0) 
                result += " ";
            result += Integer.toString(value);
        }

        if (result.length() == 0) {
            throw new Exception("Invalid conversion type for AL register.");
        }

        return result;
    }
    //FIN BLOQUE WRITE (SYS 2)
}
