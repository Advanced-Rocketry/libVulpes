package zmaster587.libVulpes.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import net.minecraft.item.ItemStack;
import zmaster587.libVulpes.api.LibVulpesBlocks;

@JEIPlugin
public class LibVulpesPlugin implements IModPlugin {

    @Override
    public void register(IModRegistry registry) {
        IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
        //Hide problematic blocks
        blacklist.addIngredientToBlacklist(new ItemStack(LibVulpesBlocks.blockPhantom));
        blacklist.addIngredientToBlacklist(new ItemStack(LibVulpesBlocks.blockPlaceHolder));
    }

}
