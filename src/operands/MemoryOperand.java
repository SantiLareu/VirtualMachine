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
        int size = 4;

        offset += (reg & 0xFFFF);
        offset = offset & 0xFFFF;

        reg = (reg & 0xFFFF0000) | offset;

        value = (this.vm.getVirtualMemory().readNbytes(vm.getSegTable().LogicToPhysic(reg),size));
        this.setMemoryAccess(reg, vm.getSegTable().LogicToPhysic(reg), size, value);
        
        return value;

    }

    @Override
    public void setValue(int value) throws Exception {
        int offset = this.data & 0x0000FFFF;
        int codreg = (this.data >> 16) & 0x1F;
        int reg = this.vm.getRegisters().getRegister(codreg);
        int size = 4;

        offset += (reg & 0xFFFF);
        offset = offset & 0xFFFF;

        reg = (reg & 0xFFFF0000) | offset;

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
        

        result += "[";
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
} 

