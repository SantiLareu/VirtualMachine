package mnemonic;

import components.VirtualMachine;
import operands.Operand;

public class JNP extends Mnemonic1 {

    public JNP(VirtualMachine vm) {
        super(vm, "JNP");
    }

    @Override
    public void operate(Operand A) throws Exception {       
        int cc = this.vm.getRegisters().getRegister(17);
        if (((cc & 0x40000000) != 0 || (cc & 0x80000000) != 0)) {
            int address = this.vm.getRegisters().getRegister(26);
            address += A.getValue();
            this.vm.getRegisters().setRegister(3, address); 
        }
    }

}
