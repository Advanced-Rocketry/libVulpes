package zmaster587.libVulpes.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModCompatDictionary {

    public static void registerIECoils() {
        OreDictionary.registerOre("blockCoil", new ItemStack(blusunrize.immersiveengineering.common.IEContent.blockMetalDecoration0, 1, blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDecoration0.COIL_LV.getMeta()));
        OreDictionary.registerOre("blockCoil", new ItemStack(blusunrize.immersiveengineering.common.IEContent.blockMetalDecoration0, 1, blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDecoration0.COIL_MV.getMeta()));
        OreDictionary.registerOre("blockCoil", new ItemStack(blusunrize.immersiveengineering.common.IEContent.blockMetalDecoration0, 1, blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDecoration0.COIL_HV.getMeta()));
        OreDictionary.registerOre("coilCopper", new ItemStack(blusunrize.immersiveengineering.common.IEContent.blockMetalDecoration0, 1, blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDecoration0.COIL_LV.getMeta()));
        OreDictionary.registerOre("coilElectrum", new ItemStack(blusunrize.immersiveengineering.common.IEContent.blockMetalDecoration0, 1, blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDecoration0.COIL_MV.getMeta()));
        OreDictionary.registerOre("coilHighVoltage", new ItemStack(blusunrize.immersiveengineering.common.IEContent.blockMetalDecoration0, 1, blusunrize.immersiveengineering.common.blocks.metal.BlockTypes_MetalDecoration0.COIL_HV.getMeta()));
    }
}
