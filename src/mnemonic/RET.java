package mnemonic;

import components.VirtualMachine;
import operands.*;

public class RET extends Mnemonic0{

    public RET(VirtualMachine vm){
        super(vm, "RET");
    }

    @Override
    public void operate() throws Exception {
        int CS = this.vm.getRegisters().getRegister(26);
        //POP <IP>
        Operand IP = new RegisterOperand(this.vm);
        IP.setData(0x53);

        Mnemonic1 m = new POP(this.vm);
        m.operate(IP);

        CS |= this.vm.getRegisters().getRegister(3); 
        this.vm.getRegisters().setRegister(3, CS);   
    }

}
