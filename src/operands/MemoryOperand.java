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

        offset += (reg & 0xFFFF);
        offset = offset & 0xFFFF;

        reg = (reg & 0xFFFF0000) | offset;

        value = (this.vm.getVirtualMemory().read4bytes(vm.getSegTable().LogicToPhysic(reg)));
        return value;

    }

    @Override
    public void setValue(int value) throws Exception {
        int offset = this.data & 0x0000FFFF;
        int codreg = (this.data >> 16) & 0x1F;
        int reg = this.vm.getRegisters().getRegister(codreg);

        offset += (reg & 0xFFFF);
        offset = offset & 0xFFFF;

        reg = (reg & 0xFFFF0000) | offset;

        this.vm.getVirtualMemory().write4bytes(vm.getSegTable().LogicToPhysic(reg),value);

    }  

    @Override
    public String toString() {
        String result = "";
        int offset = ((this.data & 0x00FFFF00) << 8) >> 16;
        int codreg = (this.data >> 4) & 0x0F;
        int cell_size = this.data & 0x3;
        char access_modificator;

        switch (cell_size) {
            case 0:
                access_modificator = 'l';
                break;
            case 3:
                access_modificator = 'b';
                break;
            default:
                access_modificator = 'w';
        }

        result += access_modificator + "[";
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

