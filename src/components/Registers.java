package components;


public class Registers {
	private final int SIZE_REG;
	private int[] registers;
	
	public Registers() {
		this.SIZE_REG = 32;
		this.registers = new int[SIZE_REG];
	}

	public void setRegister(int n, int value) {
		this.registers[n] = value;
	}

    public int[] getRegs(){
        return this.registers;
    }
	
	public void loadRegisters(byte[] registerData) {
        for(int i = 0; i < registerData.length; i+=4) {
            int reg = 0;
            for(int j = 0; j < 4; j++) {
                reg = (reg << 8) | (int)(registerData[i+j] & 0xFF);
            }
            this.registers[i/4] = reg;
        }
	}
	
	public int getRegister(int n) {
		return this.registers[n];	
	}
	
	public void add(int n,int cant) {
		this.registers[n] += cant;
	}	
}
