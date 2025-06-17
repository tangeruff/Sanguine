package com.tangeruff.sanguine.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class GoldScroller {

    private static class GoldMessage {
        String text;
        boolean critical;
        float xPos;
        float yPos;

        public GoldMessage(String text, float startX, float startY, boolean critical) {
            this.text = text;
            this.xPos = startX;
            this.yPos = startY;
            this.critical = critical;
        }
    }

    private final List<GoldMessage> messages = new LinkedList<GoldMessage>();

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();

        if (message.matches(".*\\+\\d+ Gold( \\(Critical Hit\\))?")) {
            String goldPart = message.replaceAll(".*(\\+\\d+ Gold( \\(Critical Hit\\))?).*", "$1");
            String goldAmount = goldPart.replaceAll("[^0-9]", "");
            if (!goldAmount.isEmpty()) {
                try {
                    int gold = Integer.parseInt(goldAmount);
                    System.out.println("Gold received: " + gold);

                    ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

                    float startX = res.getScaledWidth() - 160
                            - Minecraft.getMinecraft().fontRendererObj.getStringWidth("+" + gold);
                    float startY = res.getScaledHeight() - 80;

                    if (message.contains("Critical Hit")) {
                        messages.add(new GoldMessage("+" + gold, startX, startY, true));
                    } else {
                        messages.add(new GoldMessage("+" + gold, startX, startY, false));
                    }

                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse gold amount: " + goldAmount);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);

        Iterator<GoldMessage> iterator = messages.iterator();
        while (iterator.hasNext()) {

            // FIXME: janky
            GoldMessage gm = iterator.next();

            GL11.glPushMatrix();
            GL11.glScalef(2.0f, 2.0f, 1.0f);

            if (gm.critical) {
                mc.fontRendererObj.drawStringWithShadow(
                        EnumChatFormatting.RED + gm.text,
                        gm.xPos / 2,
                        gm.yPos / 2,
                        0xFF0000);
            } else {
                mc.fontRendererObj.drawStringWithShadow(
                        EnumChatFormatting.GOLD + gm.text,
                        gm.xPos / 2,
                        gm.yPos / 2,
                        0xFFFF00);
            }

            gm.yPos += 1f;
            gm.xPos -= 1.5f;

            // remove when offscreen
            if (gm.yPos > res.getScaledHeight() || gm.xPos < -mc.fontRendererObj.getStringWidth(gm.text)) {
                iterator.remove();
            }

            GL11.glPopMatrix();
        }

        GL11.glPushMatrix();
        GL11.glScalef(3.0f, 3.0f, 1.0f);

        float gcX = res.getScaledWidth() - 130 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("2192");
        float gcY = res.getScaledHeight() - 120;
        float acX = res.getScaledWidth() - 130 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("9/10");
        float acY = res.getScaledHeight() - 80;
        mc.fontRendererObj.drawStringWithShadow(
                EnumChatFormatting.WHITE + "2192", // placeholder (fix)
                gcX / 3,
                gcY / 3,
                0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow(
                EnumChatFormatting.WHITE + "9/10", // placeholder (fix)
                acX / 3,
                acY / 3,
                0xFFFFFF);

        GL11.glPopMatrix();
    }
}