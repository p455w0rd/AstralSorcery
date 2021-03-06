/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.auxiliary.link.ILinkableTile;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.starlight.IStarlightReceiver;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileReceiverBase
 * Created by HellFirePvP
 * Date: 05.08.2016 / 13:47
 */
public abstract class TileReceiverBase extends TileNetwork implements IStarlightReceiver, ILinkableTile {

    @Override
    public World getLinkWorld() {
        return getTrWorld();
    }

    @Override
    public BlockPos getLinkPos() {
        return getTrPos();
    }

    @Override
    public BlockPos getTrPos() {
        return getPos();
    }

    @Override
    public World getTrWorld() {
        return getWorld();
    }

    @Override
    public void onLinkCreate(EntityPlayer player, BlockPos other) {}

    @Override
    public boolean tryLink(EntityPlayer player, BlockPos other) {
        return false;
    }

    @Override
    public boolean tryUnlink(EntityPlayer player, BlockPos other) {
        return false;
    }

    @Override
    public List<BlockPos> getLinkedPositions() {
        return new LinkedList<>();
    }

    public <T extends ITransmissionReceiver> T tryGetNode() {
        IPrismTransmissionNode node = WorldNetworkHandler.getNetworkHandler(world).getTransmissionNode(getPos());
        if(node == null || !(node instanceof ITransmissionReceiver)) return null;
        return (T) node;
    }

}
