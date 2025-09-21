package mnemonic;

import components.VirtualMachine;
import operands.*;

public class LDH extends Mnemonic2 {

    public LDH(VirtualMachine vm) {
        super(vm, "LDH");
    }

    @Override
    public void operate(Operand A, Operand B) throws Exception {

        int valueA = A.getValue() & 0x0000FFFF;
        int valueB = B.getValue() & 0x0000FFFF;
        valueB = valueB << 16;

        A.setValue(valueA | valueB); 


    }

}
