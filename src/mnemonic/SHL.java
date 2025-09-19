package mnemonic;

import components.VirtualMachine;
import operands.Operand;

public class SHL extends Mnemonic2 {

    public SHL(VirtualMachine vm) {
        super(vm, "SHL");
    }

    @Override
    public void operate(Operand A, Operand B) throws Exception {

        int value = (A.getValue() << B.getValue()); 
        A.setValue(value); 
        ModifyCC(value); 

    }

}
