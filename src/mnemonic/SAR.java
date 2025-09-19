package mnemonic;

import components.VirtualMachine;
import operands.Operand;

public class SAR extends Mnemonic2{

    public SAR (VirtualMachine vm){
        super (vm,"SAR");
    }

    @Override
    public void operate(Operand A, Operand B) throws Exception {
        int value = A.getValue() >> B.getValue();
        A.setValue(value);
        ModifyCC(value);
    }

}
