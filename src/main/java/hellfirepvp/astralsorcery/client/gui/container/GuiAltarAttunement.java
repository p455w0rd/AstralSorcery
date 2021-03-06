/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.container;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderConstellation;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.crafting.altar.AbstractAltarRecipe;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiAltarAttunement
 * Created by HellFirePvP
 * Date: 16.10.2016 / 17:13
 */
public class GuiAltarAttunement extends GuiAltarBase {

    private static final Random rand = new Random();

    private static final BindableResource texAltarAttenuation = AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "guiAltar2");
    private static final BindableResource texBlack = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "black");

    public GuiAltarAttunement(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(playerInv, tileAltar);
    }

    @Override
    public void initGui() {
        this.xSize = 256;
        this.ySize = 202;
        super.initGui();
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        AbstractAltarRecipe rec = findCraftableRecipe();
        if(rec != null) {
            ItemStack out = rec.getOutputForRender();
            zLevel = 10F;
            itemRender.zLevel = 10F;

            RenderHelper.enableGUIStandardItemLighting();

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();
            GL11.glTranslated(190, 35, 0);
            GL11.glScaled(2.5, 2.5, 2.5);

            itemRender.renderItemAndEffectIntoGUI(mc.player, out, 0, 0);
            itemRender.renderItemOverlayIntoGUI(fontRendererObj, out, 0, 0, null);

            GL11.glPopMatrix();
            GL11.glPopAttrib();

            //RenderHelper.disableStandardItemLighting();

            zLevel = 0F;
            itemRender.zLevel = 0F;

            TextureHelper.refreshTextureBindState();
        }

        IMajorConstellation c = containerAltarBase.tileAltar.getFocusedConstellation();
        if(c != null && containerAltarBase.tileAltar.getMultiblockState() && ResearchManager.clientProgress.hasConstellationDiscovered(c.getUnlocalizedName())) {
            rand.setSeed(0x61FF25A5B7C24109L);

            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            GL11.glPushMatrix();

            GL11.glColor4f(1F, 1F, 1F, 1F);

            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            Blending.DEFAULT.apply();

            RenderConstellation.renderConstellationIntoGUI(c, 10, 20, zLevel, 70, 70, 2, new RenderConstellation.BrightnessFunction() {
                @Override
                public float getBrightness() {
                    return RenderConstellation.conCFlicker(Minecraft.getMinecraft().world.getTotalWorldTime(), Minecraft.getMinecraft().getRenderPartialTicks(), 5 + rand.nextInt(5));
                }
            }, true, false);

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_ALPHA_TEST);

            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    @Override
    public void renderGuiBackground(float partialTicks, int mouseX, int mouseY) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float percFilled;
        if(containerAltarBase.tileAltar.getMultiblockState()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            percFilled = containerAltarBase.tileAltar.getAmbientStarlightPercent();
        } else {
            GL11.glColor4f(1.0F, 0F, 0F, 1.0F);
            percFilled = 1.0F;
        }

        texBlack.bind();
        drawRect(guiLeft + 11, guiTop + 104, 232, 10);

        if(percFilled > 0) {
            SpriteSheetResource spriteStarlight = SpriteLibrary.spriteStarlight;
            spriteStarlight.getResource().bind();
            int t = containerAltarBase.tileAltar.getTicksExisted();
            Tuple<Double, Double> uvOffset = spriteStarlight.getUVOffset(t);
            drawRect(guiLeft + 11, guiTop + 104, (int) (232 * percFilled), 10,
                    uvOffset.key, uvOffset.value,
                    spriteStarlight.getULength() * percFilled, spriteStarlight.getVLength() * percFilled);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        texAltarAttenuation.bind();
        drawRect(guiLeft, guiTop, xSize, ySize);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

}
