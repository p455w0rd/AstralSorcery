/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.page;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IGuiRenderablePage
 * Created by HellFirePvP
 * Date: 30.08.2016 / 11:21
 */
public interface IGuiRenderablePage {

    static final BindableResource resStar = AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "star1");

    public void render(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY);

    default public void postRender(float offsetX, float offsetY, float pTicks, float zLevel, float mouseX, float mouseY) {}

    default public boolean propagateMouseClick(int mouseX, int mouseZ) {
        return false;
    }

    default public Rectangle drawItemStack(ItemStack stack, int offsetX, int offsetY, float zLevel) {
        return drawItemStack(stack, offsetX, offsetY, zLevel, getStandardFontRenderer(), getRenderItem());
    }

    default public Rectangle drawItemStack(ItemStack stack, int offsetX, int offsetY, float zLevel, FontRenderer fontRenderer, RenderItem ri) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        float zIR = ri.zLevel;
        ri.zLevel = zLevel;
        RenderHelper.enableGUIStandardItemLighting();

        ri.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().player, stack, offsetX, offsetY);
        ri.renderItemOverlayIntoGUI  (fontRenderer,                       stack, offsetX, offsetY, null);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha(); //Because Mc item rendering..
        ri.zLevel = zIR;
        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        return new Rectangle(offsetX, offsetY, 16, 16);
    }

    default public Rectangle drawInfoStar(float offsetX, float offsetY, float zLevel, float widthHeightBase, float pTicks) {

        float tick = ClientScheduler.getClientTick() + pTicks;
        float deg = (tick * 2) % 360F;
        float wh = widthHeightBase - (widthHeightBase / 6F) * (MathHelper.sin((float) Math.toRadians(((tick) * 4) % 360F)) + 1F);
        drawInfoStarSingle(offsetX, offsetY, zLevel, wh, Math.toRadians(deg));

        deg = ((tick + 22.5F) * 2) % 360F;
        wh = widthHeightBase - (widthHeightBase / 6F) * (MathHelper.sin((float) Math.toRadians(((tick + 45F) * 4) % 360F)) + 1F);
        drawInfoStarSingle(offsetX, offsetY, zLevel, wh, Math.toRadians(deg));

        return new Rectangle(MathHelper.floor(offsetX - widthHeightBase / 2F), MathHelper.floor(offsetY - widthHeightBase / 2F),
                MathHelper.floor(widthHeightBase), MathHelper.floor(widthHeightBase));
    }

    default public void drawInfoStarSingle(float offsetX, float offsetY, float zLevel, float widthHeight, double deg) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        resStar.bind();
        Vector3 offset = new Vector3(-widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv01   = new Vector3(-widthHeight / 2D,  widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv11   = new Vector3( widthHeight / 2D,  widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv10   = new Vector3( widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);

        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX + uv01.getX(),   offsetY + uv01.getY(),   zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + uv11.getX(),   offsetY + uv11.getY(),   zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + uv10.getX(),   offsetY + uv10.getY(),   zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX + offset.getX(), offsetY + offset.getY(), zLevel).tex(0, 0).endVertex();
        tes.draw();

        TextureHelper.refreshTextureBindState();
        TextureHelper.setActiveTextureToAtlasSprite();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    default public void drawRect(double offsetX, double offsetY, double width, double height, double zLevel) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(0, 0).endVertex();
        tes.draw();
    }

    default public void drawRectPart(double offsetX, double offsetY, double width, double height, double zLevel, double u, double v, double uLength, double vLength) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(u,           v + vLength).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(u + uLength, v + vLength).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(u + uLength, v)          .endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(u,           v)          .endVertex();
        tes.draw();
    }

    default public RenderItem getRenderItem() {
        return Minecraft.getMinecraft().getRenderItem();
    }

    default public FontRenderer getStandardFontRenderer() {
        return Minecraft.getMinecraft().fontRendererObj;
    }

    default public FontRenderer getStandardGalFontRenderer() {
        return Minecraft.getMinecraft().standardGalacticFontRenderer;
    }

    default public String getDescriptionFromStarlightAmount(int amtRequired) {
        String base = "astralsorcery.journal.recipe.amt.";
        String ext;
        if(amtRequired <= 100) {
            ext = "lowest";
        } else if(amtRequired <= 500) {
            ext = "low";
        } else if(amtRequired <= 1000) {
            ext = "avg";
        } else if(amtRequired <= 2500) {
            ext = "more";
        } else if(amtRequired <= 4000) {
            ext = "high";
        } else {
            ext = "highest";
        }
        return base + ext;
    }

}
