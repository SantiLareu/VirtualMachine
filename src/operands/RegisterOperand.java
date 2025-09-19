package operands;

import components.VirtualMachine;
import utils.utilities;

public class RegisterOperand extends Operand {
    
    public RegisterOperand(VirtualMachine vm) {
        super(vm);
    }
    
    @Override
    public int getValue() throws Exception {
        int codreg = this.data & 0x1F;
        int valor = this.vm.getRegisters().getRegister(codreg);
        return valor;
    }

    
    @Override
    public void setValue(int value) throws Exception {
        int codreg = this.data & 0x1F;
        vm.getRegisters().getRegister(codreg);
    } 

    @Override
    public String toString() {
        String result = "";
        int codreg = this.data & 0x1F;
        
        result += utilities.nameRegisters[codreg];
        return result;
    }
}


    