/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerks;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncKnowledge;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerProgress
 * Created by HellFirePvP
 * Date: 07.05.2016 / 13:34
 */
public class PlayerProgress {

    private List<String> knownConstellations = new ArrayList<>();
    private IMajorConstellation attunedConstellation = null;
    private Map<ConstellationPerk, Integer> appliedPerks = new HashMap<>(); //Perk -> Level Of Unlock
    private List<ResearchProgression> researchProgression = new LinkedList<>();
    private ProgressionTier tierReached = ProgressionTier.DISCOVERY;
    private int alignmentCharge = 0;

    public void load(NBTTagCompound compound) {
        knownConstellations.clear();
        researchProgression.clear();
        appliedPerks.clear();
        attunedConstellation = null;
        tierReached = ProgressionTier.DISCOVERY;
        alignmentCharge = 0;

        if (compound.hasKey("constellations")) {
            NBTTagList list = compound.getTagList("constellations", 8);
            for (int i = 0; i < list.tagCount(); i++) {
                knownConstellations.add(list.getStringTagAt(i));
            }
        }
        if(compound.hasKey("listPerks")) {
            NBTTagList list = compound.getTagList("listPerks", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                ConstellationPerks perkEnum = ConstellationPerks.getById(tag.getInteger("perkId"));
                Integer unlockLevel = tag.getInteger("perkLevel");
                if(perkEnum != null) {
                    appliedPerks.put(perkEnum.createPerk(), unlockLevel);
                }
            }
        }

        if (compound.hasKey("attuned")) {
            String cst = compound.getString("attuned");
            IConstellation c = ConstellationRegistry.getConstellationByName(cst);
            if(c == null || !(c instanceof IMajorConstellation)) {
                AstralSorcery.log.warn("[AstralSorcery] Failed to load attuned Constellation: " + cst + " - constellation doesn't exist or isn't major.");
            } else {
                attunedConstellation = (IMajorConstellation) c;
            }
        }

        if (compound.hasKey("tierReached")) {
            int tierOrdinal = compound.getInteger("tierReached");
            tierReached = ProgressionTier.values()[MathHelper.clamp(tierOrdinal, 0, ProgressionTier.values().length - 1)];
        }

        if (compound.hasKey("research")) {
            int[] research = compound.getIntArray("research");
            for (int resOrdinal : research) {
                researchProgression.add(ResearchProgression.getById(resOrdinal));
            }
        }

        if(compound.hasKey("alignmentCharge")) {
            this.alignmentCharge = compound.getInteger("alignmentCharge");
        }
    }

    public void store(NBTTagCompound cmp) {
        NBTTagList list = new NBTTagList();
        for (String s : knownConstellations) {
            list.appendTag(new NBTTagString(s));
        }
        cmp.setTag("constellations", list);
        cmp.setInteger("tierReached", tierReached.ordinal());
        int[] researchArray = new int[researchProgression.size()];
        for (int i = 0; i < researchProgression.size(); i++) {
            ResearchProgression progression = researchProgression.get(i);
            researchArray[i] = progression.getProgressId();
        }
        cmp.setIntArray("research", researchArray);
        if(attunedConstellation != null) {
            cmp.setString("attuned", attunedConstellation.getUnlocalizedName());
        }
        list = new NBTTagList();
        for (ConstellationPerk perk : appliedPerks.keySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("perkId", perk.getId());
            tag.setInteger("perkLevel", appliedPerks.get(perk));
            list.appendTag(tag);
        }
        cmp.setTag("listPerks", list);
        cmp.setInteger("alignmentCharge", alignmentCharge);
    }

    protected boolean forceGainResearch(ResearchProgression progression) {
        if(!researchProgression.contains(progression)) {
            researchProgression.add(progression);
            return true;
        }
        return false;
    }

    protected void setAttunedConstellation(IMajorConstellation constellation) {
        this.attunedConstellation = constellation;
    }

    public void addPerk(ConstellationPerk singleInstance, Integer alignmentLevelUnlocked) {
        this.appliedPerks.put(singleInstance, alignmentLevelUnlocked);
    }

    public void clearPerks() {
        this.appliedPerks.clear();
    }

    public List<ResearchProgression> getResearchProgression() {
        return Lists.newLinkedList(researchProgression);
    }

    public ProgressionTier getTierReached() {
        return tierReached;
    }

    public IMajorConstellation getAttunedConstellation() {
        return attunedConstellation;
    }

    public Map<ConstellationPerk, Integer> getAppliedPerks() {
        /*Map<ConstellationPerk, Integer> perks = new HashMap<>();
        for (ConstellationPerks c : ConstellationPerks.values()) {
            perks.put(c.getSingleInstance(), 1);
        }
        return perks;*/
        return Collections.unmodifiableMap(appliedPerks);
    }

    public boolean hasPerkUnlocked(ConstellationPerks perk) {
        return hasPerkUnlocked(perk.getSingleInstance());
    }

    public boolean hasPerkUnlocked(ConstellationPerk perk) {
        return appliedPerks.containsKey(perk);
    }

    public boolean isPerkActive(ConstellationPerk perk) {
        return hasPerkUnlocked(perk) && appliedPerks.get(perk) <= getAlignmentLevel();
    }

    // -1 -> no free level
    public int getNextFreeLevel() {
        int level = getAlignmentLevel();
        for (int i = 0; i <= level; i++) {
            if(!appliedPerks.values().contains(i)) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasFreeAlignmentLevel() {
        return getNextFreeLevel() > -1;
        /*int lowestFree = getNextFreeLevel();
        int level = getAlignmentLevel();
        int highestFound = 0;
        for (ConstellationPerk p : appliedPerks.keySet()) {
            int claimedAtLevel = appliedPerks.get(p);
            if(claimedAtLevel > highestFound) highestFound = claimedAtLevel;
        }
        if(highestFound == 0 && appliedPerks.isEmpty()) {
            return true; //First one is free.
        }
        return level > highestFound;*/
    }

    public int getAlignmentCharge() {
        return alignmentCharge;
    }

    public int getAlignmentLevel() {
        return ConstellationPerkLevelManager.getAlignmentLevel(this);
    }

    protected void modifyCharge(int charge) {
        this.alignmentCharge = Math.max(0, this.alignmentCharge + charge);
    }

    protected void forceCharge(int charge) {
        this.alignmentCharge = Math.max(0, charge);
    }

    protected boolean stepTier() {
        if(getTierReached().hasNextTier()) {
            setTierReached(ProgressionTier.values()[getTierReached().ordinal() + 1]);
            return true;
        }
        return false;
    }

    protected void setTierReached(ProgressionTier tier) {
        this.tierReached = tier;
    }

    public List<String> getKnownConstellations() {
        return knownConstellations;
    }

    public boolean hasConstellationDiscovered(String constellation) {
        return knownConstellations.contains(constellation);
    }

    protected void discoverConstellation(String name) {
        if (!knownConstellations.contains(name)) knownConstellations.add(name);
    }

    protected void receive(PktSyncKnowledge message) {
        this.knownConstellations = message.knownConstellations;
        this.researchProgression = message.researchProgression;
        this.tierReached = ProgressionTier.values()[MathHelper.clamp(message.progressTier, 0, ProgressionTier.values().length - 1)];
        this.attunedConstellation = message.attunedConstellation;
        this.appliedPerks = message.appliedPerks;
        this.alignmentCharge = message.alignmentCharge;
    }

}
