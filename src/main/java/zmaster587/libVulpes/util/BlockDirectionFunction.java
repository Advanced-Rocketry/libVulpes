package zmaster587.libVulpes.util;

import net.minecraft.nbt.CompoundNBT;

public class BlockDirectionFunction {

	byte[] states = new byte[6];
	byte maxNumStates;
	
	public BlockDirectionFunction(int maxStates) {
		maxNumStates = (byte) maxStates;
	}
	
	public void advanceState(int direction) {
		states[direction]++;
		if(states[direction] >= maxNumStates)
			states[direction] = 0;
	}
	
	public void retractState(int direction) {
		states[direction]--;
		if(states[direction] < 0)
			states[direction] = (byte) (maxNumStates - 1);
	}
	
	public int getState(int direction) {
		return states[direction];
	}
	
	public void setState(int direction, int state) {
		states[direction] = (byte)state;
	}
	
	public void write(CompoundNBT nbt) {
		nbt.putByteArray("BDFstates", states);
	}
	
	public void readFromNBT(CompoundNBT nbt) {
		if(nbt.contains("BDFstates"))
			states = nbt.getByteArray("BDFstates");
	}
}
