package mnemonic;

import components.VirtualMachine;
import operands.Operand;

public class JZ extends Mnemonic1 {

    public JZ(VirtualMachine vm) {
        super(vm, "JZ");
    }

    @Override
    public void operate(Operand A) throws Exception {
        int cc = this.vm.getRegisters().getRegister(17);
        if ((cc & 0x40000000) != 0) { 
            int address = this.vm.getRegisters().getRegister(26);
            address += A.getValue();
            this.vm.getRegisters().setRegister(3, address);

        } 
    }

}
