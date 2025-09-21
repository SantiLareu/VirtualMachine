package mnemonic;

import components.VirtualMachine;
import operands.Operand;

public class JMP extends Mnemonic1 {


    public JMP(VirtualMachine vm) {
        super(vm, "JMP");
    }

    @Override
    public void operate(Operand A) throws Exception {
        int address = this.vm.getRegisters().getRegister(26);
        address += A.getValue();
        this.vm.getRegisters().setRegister(3, address);
    }
}   