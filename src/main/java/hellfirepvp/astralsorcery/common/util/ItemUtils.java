/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemUtils
 * Created by HellFirePvP
 * Date: 31.07.2016 / 17:51
 */
public class ItemUtils {

    private static final Random rand = new Random();

    public static EntityItem dropItem(World world, double x, double y, double z, ItemStack stack) {
        if(world.isRemote) return null;
        EntityItem ei = new EntityItem(world, x, y, z, stack);
        ei.motionX = 0;
        ei.motionY = 0;
        ei.motionZ = 0;
        world.spawnEntityInWorld(ei);
        ei.setDefaultPickupDelay();
        return ei;
    }
    public static EntityItem dropItemNaturally(World world, double x, double y, double z, ItemStack stack) {
        if(world.isRemote) return null;
        EntityItem ei = new EntityItem(world, x, y, z, stack);
        applyRandomDropOffset(ei);
        world.spawnEntityInWorld(ei);
        ei.setDefaultPickupDelay();
        return ei;
    }

    private static void applyRandomDropOffset(EntityItem item) {
        item.motionX = rand.nextFloat() * 0.3F - 0.15D;
        item.motionY = rand.nextFloat() * 0.3F - 0.15D;
        item.motionZ = rand.nextFloat() * 0.3F - 0.15D;
    }

    public static List<ItemStack> scanInventoryFor(IItemHandler handler, Item i) {
        List<ItemStack> out = new LinkedList<>();
        for (int j = 0; j < handler.getSlots(); j++) {
            ItemStack s = handler.getStackInSlot(j);
            if(s != null && s.getItem() != null && s.getItem() == i)
                out.add(copyStackWithSize(s, s.stackSize));
        }
        return out;
    }

    public static void dropInventory(IItemHandler handle, World worldIn, BlockPos pos) {
        if(worldIn.isRemote) return;
        for (int i = 0; i < handle.getSlots(); i++) {
            ItemStack stack = handle.getStackInSlot(i);
            if(stack == null) continue;
            dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        }
    }

    public static boolean drainFluidFromItem(ItemStack stack, Fluid fluid, int mbAmount, boolean doDrain) {
        return drainFluidFromItem(stack, new FluidStack(fluid, mbAmount), doDrain);
    }

    //Returns true if the fluid with the specified amount could be drained, false if not.
    public static boolean drainFluidFromItem(ItemStack stack, FluidStack fluidStack, boolean doDrain) {
        if(stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem ifci = (IFluidContainerItem) stack.getItem();
            FluidStack containing = ifci.getFluid(stack);
            if(containing != null && containing.getFluid() != null) {
                if(containing.getFluid().equals(fluidStack.getFluid())) {
                    FluidStack drained = ifci.drain(stack, fluidStack.amount, doDrain);
                    if(drained != null && drained.amount >= fluidStack.amount) {
                        return true;
                    }
                }
            }
        }
        IFluidHandler handle = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
        if(handle != null) {
            FluidStack tryDrain = handle.drain(fluidStack.copy(), doDrain);
            if(tryDrain != null && tryDrain.amount >= fluidStack.amount) {
                return true;
            }
        }
        return false;
    }

    /*public static void decrStackInInventory(ItemStack[] stacks, int slot) {
        if(slot < 0 || slot >= stacks.length) return;
        ItemStack st = stacks[slot];
        if(st == null) return;
        st.stackSize--;
        if(st.stackSize <= 0) {
            stacks[slot] = null;
        }
    }*/

    public static void decrStackInInventory(ItemStackHandler handler, int slot) {
        if(slot < 0 || slot >= handler.getSlots()) return;
        ItemStack st = handler.getStackInSlot(slot);
        if(st == null) return;
        st.stackSize--;
        if(st.stackSize <= 0) {
            handler.setStackInSlot(slot, null);
        }
    }

    public static boolean tryPlaceItemInInventory(ItemStack stack, IItemHandler handler) {
        return tryPlaceItemInInventory(stack, handler, 0, handler.getSlots());
    }

    public static boolean tryPlaceItemInInventory(ItemStack stack, IItemHandler handler, int start, int end) {
        ItemStack toAdd = stack.copy();
        if(!hasInventorySpace(toAdd, handler, start, end)) return false;
        int max = stack.getMaxStackSize();

        for (int i = start; i < end; i++) {
            ItemStack in = handler.getStackInSlot(i);
            if (in == null) {
                int added = Math.min(stack.stackSize, max);
                stack.stackSize -= added;
                handler.insertItem(i, copyStackWithSize(stack, added), false);
                return true;
            } else {
                if (stackEqualsNonNBT(stack, in) && matchTags(stack, in)) {
                    int space = max-in.stackSize;
                    int added = Math.min(stack.stackSize, space);
                    stack.stackSize -= added;
                    handler.getStackInSlot(i).stackSize += added;
                    if (stack.stackSize <= 0)
                        return true;
                }
            }
        }
        return stack.stackSize == 0;
    }

    public static boolean hasInventorySpace(ItemStack stack, IItemHandler handler, int rangeMin, int rangeMax) {
        int size = stack.stackSize;
        int max = stack.getMaxStackSize();
        for (int i = rangeMin; i < rangeMax && size > 0; i++) {
            ItemStack in = handler.getStackInSlot(i);
            if (in == null) {
                size -= max;
            } else {
                if (stackEqualsNonNBT(stack, in) && matchTags(stack, in)) {
                    int space = max-in.stackSize;
                    size -= space;
                }
            }
        }
        return size <= 0;
    }

    public static boolean stackEqualsNonNBT(ItemStack stack, ItemStack other) {
        if (stack == null && other == null)
            return true;
        if (stack == null || other == null || stack.getItem() == null || other.getItem() == null)
            return false;
        Item sItem = stack.getItem();
        Item oItem = other.getItem();
        if(sItem.getHasSubtypes() || oItem.getHasSubtypes()) {
            return sItem.equals(other.getItem()) &&
                    (stack.getItemDamage() == other.getItemDamage() ||
                    stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ||
                    other.getItemDamage() == OreDictionary.WILDCARD_VALUE);
        } else {
            return sItem.equals(other.getItem());
        }
    }

    public static ItemStack copyStackWithSize(ItemStack stack, int amount) {
        if(stack == null || stack.getItem() == null || amount <= 0) return null;
        ItemStack s = stack.copy();
        s.stackSize = amount;
        return s;
    }

    public static boolean matchTags(ItemStack stack, ItemStack other) {
        return ItemStack.areItemStackTagsEqual(stack, other);
    }

    public static boolean matchStacksStrict(ItemStack stack, ItemStack other) {
        return ItemStack.areItemStacksEqual(stack, other);
    }

    public static boolean matchStacks(ItemStack stack, ItemStack other) {
        if(!ItemStack.areItemsEqual(stack, other)) return false;
        return ItemStack.areItemStackTagsEqual(stack, other);
    }

    public static boolean matchesOreDict(String oreDictKey, ItemStack other) {
        List<ItemStack> stacks = OreDictionary.getOres(oreDictKey);
        for (ItemStack stack : stacks) {
            if(stack == null) continue;
            if(matchStacks(stack, other))
                return true;
        }
        return false;
    }

}
