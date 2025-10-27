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

	public void initRegisters(byte[] segments,int param_size){
		int CS = ((int)(segments[0] << 8)|(int)(segments[1]) & 0xFF);
        int DS = ((int)(segments[2] << 8)|(int)(segments[3]) & 0xFF);
        int ES = ((int)(segments[4] << 8)|(int)(segments[5]) & 0xFF);
        int SS = ((int)(segments[6] << 8)|(int)(segments[7]) & 0xFF);
        int KS = ((int)(segments[8] << 8)|(int)(segments[9]) & 0xFF);
        int entry_point = ((int)(segments[10] << 8)|(int)(segments[11]) & 0xFF);
        int i = 1;

        if(param_size > 0){
            this.setRegister(31, 0x00000000);
        }else{
            this.setRegister(31, -1);
        }

		//KS
        if(KS > 0){
            this.setRegister(30, i<<16);
            i++;
        }else{
            this.setRegister(30,-1);
        }
        //CS
        this.setRegister(26,(i << 16));
        i++;
        

        //DS
        if (DS > 0){
            this.setRegister(27,(i << 16));
            i++;
        }else{
            this.setRegister(27,-1);
        }
        

        //ES
        if (ES > 0) {
            this.setRegister(28,(i << 16));
            i++;
        }else{
            this.setRegister(28,-1);
        }
        
        //SS
        if (SS > 0) {
            this.setRegister(29,(i << 16)); 
            i++;
        }else{
            this.setRegister(29,-1);
        }

        //IP
        this.setRegister(3, this.getRegister(26) + entry_point);
	}
}
