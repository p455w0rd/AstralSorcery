/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.event.listener.EventHandlerServer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerk
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:03
 */
public abstract class ConstellationPerk extends ConfigEntry {

    protected static final Random rand = new Random();

    private final String unlocName;
    private final String unlocInfo;
    private final List<Target> perkEffectTargets;
    private int id = -1;

    protected ConstellationPerk(String name, Target... targets) {
        super(Section.PERKS, name);
        this.unlocName = "perk." + name;
        this.unlocInfo = unlocName + ".info";
        this.perkEffectTargets = Lists.newArrayList(targets);
    }

    void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUnlocalizedName() {
        return unlocName;
    }

    public String getUnlocalizedDescription() {
        return unlocInfo;
    }

    public boolean mayExecute(Target target) {
        return perkEffectTargets.contains(target);
    }

    @SideOnly(Side.CLIENT)
    public void addLocalizedDescription(List<String> tooltip) {
        tooltip.add(I18n.format(getUnlocalizedName()));
        tooltip.add(I18n.format(getUnlocalizedDescription()));
    }

    public void onPlayerTick(EntityPlayer player, Side side) {}

    public float onEntityAttack(EntityPlayer attacker, EntityLivingBase attacked, float dmgIn) {
        return dmgIn;
    }

    public float onEntityHurt(EntityPlayer hurt, DamageSource source, float dmgIn) {
        return dmgIn;
    }

    public void onEntityKnockback(EntityPlayer attacker, EntityLivingBase attacked) {}

    public void onEntityKilled(EntityPlayer attacker, EntityLivingBase killed) {}

    public void onTimeout(EntityPlayer player) {}

    public boolean hasConfigEntries() {
        return false;
    }

    public final boolean isCooldownActiveForPlayer(EntityPlayer player) {
        return EventHandlerServer.perkCooldowns.hasList(player) &&
                EventHandlerServer.perkCooldowns.getOrCreateList(player).contains(getId());
    }

    public final void setCooldownActiveForPlayer(EntityPlayer player, int cooldownTicks) {
        EventHandlerServer.perkCooldowns.getOrCreateList(player).setOrAddTimeout(cooldownTicks, getId());
    }

    public final int getActiveCooldownForPlayer(EntityPlayer player) {
        if(!EventHandlerServer.perkCooldowns.hasList(player)) {
            return -1;
        }
        return EventHandlerServer.perkCooldowns.getOrCreateList(player).getTimeout(getId());
    }

    public final void addAlignmentCharge(EntityPlayer player, double charge) {
        if(!player.getEntityWorld().isRemote) {
            ResearchManager.modifyAlignmentCharge(player, charge);
        }
    }

    @Override
    public String getConfigurationSection() {
        return super.getConfigurationSection() + "." + getKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstellationPerk that = (ConstellationPerk) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public static enum Target {

        /**
         * Called when a Player attacks some LivingEntityBase.
         * Calls {@link #onEntityAttack(net.minecraft.entity.player.EntityPlayer, net.minecraft.entity.EntityLivingBase, float)}
         */
        ENTITY_ATTACK,

        /**
         * Called when a EntityLivingBase gets knockbacked by a player's attack.
         * Calls {@link #onEntityKnockback(net.minecraft.entity.player.EntityPlayer, net.minecraft.entity.EntityLivingBase)}
         */
        ENTITY_KNOCKBACK,

        /**
         * Called when a Player attack kills an EntityLivingBase.
         * Calls {@link #onEntityKilled(net.minecraft.entity.player.EntityPlayer, net.minecraft.entity.EntityLivingBase)}
         */
        ENTITY_KILL,

        /**
         * Called when a Player gets generally hurt.
         * Calls {@link #onEntityHurt(net.minecraft.entity.player.EntityPlayer, net.minecraft.util.DamageSource, float)}
         */
        ENTITY_HURT,

        /**
         * Gets called on each player's tick.
         * Calls {@link #onPlayerTick(net.minecraft.entity.player.EntityPlayer, net.minecraftforge.fml.relauncher.Side)}
         */
        PLAYER_TICK

    }

}
