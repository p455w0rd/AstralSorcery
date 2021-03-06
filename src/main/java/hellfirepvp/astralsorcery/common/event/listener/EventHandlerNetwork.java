/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncAlignmentLevels;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncConfig;
import hellfirepvp.astralsorcery.common.network.packet.server.PktWorldHandlerSyncEarly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerNetwork
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:10
 */
public class EventHandlerNetwork {

    @SubscribeEvent
    public void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        EntityPlayerMP p = (EntityPlayerMP) e.player;
        AstralSorcery.log.info("Synchronizing configuration to " + p.getName());
        PacketChannel.CHANNEL.sendTo(new PktSyncConfig(), p);
        PacketChannel.CHANNEL.sendTo(new PktSyncAlignmentLevels(ConstellationPerkLevelManager.levelsRequired), p);

        ResearchManager.sendInitClientKnowledge(p);

        SyncDataHolder.syncAllDataTo(p);
    }

    @SubscribeEvent
    public void onLoginEarly(FMLNetworkEvent.ServerConnectionFromClientEvent event) {
        event.getManager().sendPacket(new PktWorldHandlerSyncEarly(((DataWorldSkyHandlers) SyncDataHolder.getDataServer(SyncDataHolder.DATA_SKY_HANDLERS)).getSkyHandlerDimensions()));
    }

    @SubscribeEvent
    public void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        EntityPlayer player = e.player;

        //ResearchManager.logoutResetClient(player);
    }

}
