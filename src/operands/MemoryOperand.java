package operands;

import components.VirtualMachine;
import utils.utilities;

public class MemoryOperand extends Operand {
    
    
    public MemoryOperand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    public int getValue() throws Exception {
        int offset = this.data & 0x0000FFFF;
        int codreg = (this.data >> 16) & 0x1F;
        int reg = this.vm.getRegisters().getRegister(codreg);
        int value = 0;
        int size = (this.data >> 22) & 0x3;

        offset += (reg & 0xFFFF);
        offset = offset & 0xFFFF;

        reg = (reg & 0xFFFF0000) | offset;

        switch (size) {
            case 0:
                size = 4;
                break;
            case 3:
                size = 1; 
                break;     
        default:
                break;
        }

        value = signExtend(this.vm.getVirtualMemory().readNbytes(vm.getSegTable().LogicToPhysic(reg),size),size);
        this.setMemoryAccess(reg, vm.getSegTable().LogicToPhysic(reg), size, value);
        
        return value;
    }

    @Override
    public void setValue(int value) throws Exception {
        int offset = this.data & 0x0000FFFF;
        int codreg = (this.data >> 16) & 0x1F;
        int reg = this.vm.getRegisters().getRegister(codreg);
        int size = (this.data >> 22) & 0x3;


        offset += (reg & 0xFFFF);
        offset = offset & 0xFFFF;

        reg = (reg & 0xFFFF0000) | offset;

        switch (size) {
            case 0:
                size = 4;
                break;
            case 3:
                size = 1; 
                break;     
        default:
                break;
        }

        value = signExtend(value, size);
        this.vm.getVirtualMemory().writeNbytes(vm.getSegTable().LogicToPhysic(reg),size,value);
        this.setMemoryAccess(reg,  vm.getSegTable().LogicToPhysic(reg), size, value);

    }  

    public void setMemoryAccess(int logic_address, int fisic_address, int size, int value) {
        this.vm.getRegisters().setRegister(0, logic_address & 0xFFFFFFFF);

        int MAR = ((size & 0xFFFF) << 16) | (fisic_address & 0xFFFF);
        this.vm.getRegisters().setRegister(1, MAR);

        this.vm.getRegisters().setRegister(2, value & 0xFFFFFFFF);
    }

    @Override
    public String toString() {
        String result = "";
        int offset = this.data & 0x0000FFFF;
        int codreg = (this.data >> 16) & 0x1F;
        int size = (this.data >> 22) & 0x3;
        char access_modificator;

        switch (size) {
            case 0:
                access_modificator = 'l';
                break;
            case 3:
                access_modificator = 'b';
                break;
            default:
                access_modificator = 'w';
        }
        

        result += String.valueOf(access_modificator) + "[";
        if(codreg == 1){
            result += Integer.toString(offset);
        }else{
            result += utilities.nameRegisters[codreg];
            if(offset != 0){
                result += "+" + Integer.toString(offset);
            }   
        }

        result += "]";
        
        return result;
    }

    private int signExtend(int value, int cell_size){
        switch (cell_size) {
            case 1:
                return ((value << 24) >> 24);
            case 2:
                return ((value << 16)>>16);
            default:
                return value;
        }
    }
} 

