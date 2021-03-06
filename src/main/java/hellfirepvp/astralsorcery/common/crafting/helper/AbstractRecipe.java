/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractRecipe
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public abstract class AbstractRecipe {

    private ItemStack output;

    public AbstractRecipe(ItemStack output) {
        this.output = output;
    }

    public ItemStack getOutput() {
        return output;
    }

    public abstract void register();

}
