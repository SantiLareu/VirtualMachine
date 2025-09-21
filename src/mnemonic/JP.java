package mnemonic;

import components.*;
import operands.Operand;

public class JP extends Mnemonic1 {

    public JP(VirtualMachine vm) {
        super(vm, "JP");
    }

    @Override
    public void operate(Operand A) throws Exception {

        int cc = this.vm.getRegisters().getRegister(17);
        if ((cc & 0xC0000000) == 0) { 
            int address = this.vm.getRegisters().getRegister(26);
            address += A.getValue();
            this.vm.getRegisters().setRegister(3, address);

        } 
    }

}
