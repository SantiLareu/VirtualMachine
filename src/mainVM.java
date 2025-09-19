

import java.nio.file.Files;
import java.nio.file.Paths;
import components.VirtualMachine;

public class mainVM {	
	public static void main(String[] args){
		try{
			VirtualMachine vm = new VirtualMachine();
			String file_path = args[0]; 
			vm.validateExtension(file_path); 
			byte[] allbytes = Files.readAllBytes(Paths.get(file_path)); 
			vm.verify(allbytes);

			if (args.length > 1 && args[1].equals("-d")){
				vm.setDisassembler(true);
			}
			
			vm.startMemory(allbytes);

			if(vm.getDissasembler()){
				byte[] offset = new byte[2];
				System.arraycopy(allbytes, 16, offset, 0, 2);
				vm.disassembler(offset);
			}

			vm.execute();


		}catch(Exception e){
			System.out.println("Error: " + e.getMessage());
		}
	}
}