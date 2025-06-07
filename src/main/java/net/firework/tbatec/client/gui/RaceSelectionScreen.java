package net.firework.tbatec.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.firework.tbatec.Race;
import net.firework.tbatec.TBATEMod;
import net.firework.tbatec.network.RaceSelectionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RaceSelectionScreen extends Screen {
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(TBATEMod.MODID, "textures/gui/race_selection_bg.png");
    private static final int BACKGROUND_WIDTH = 256;
    private static final int BACKGROUND_HEIGHT = 166;

    private Race selectedRace = null;
    private Button confirmButton;

    public RaceSelectionScreen() {
        super(Component.literal("Choose Your Race"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Race selection buttons
        int buttonWidth = 120;
        int buttonHeight = 40;
        int buttonSpacing = 10;
        int totalWidth = (buttonWidth * 3) + (buttonSpacing * 2);
        int startX = centerX - (totalWidth / 2);
        int raceButtonY = centerY - 20;

        // Human button
        this.addRenderableWidget(Button.builder(
                Component.literal("Human"),
                button -> selectRace(Race.HUMAN)
        ).bounds(startX, raceButtonY, buttonWidth, buttonHeight).build());

        // Elf button
        this.addRenderableWidget(Button.builder(
                Component.literal("Elf"),
                button -> selectRace(Race.ELF)
        ).bounds(startX + buttonWidth + buttonSpacing, raceButtonY, buttonWidth, buttonHeight).build());

        // Dwarf button
        this.addRenderableWidget(Button.builder(
                Component.literal("Dwarf"),
                button -> selectRace(Race.DWARF)
        ).bounds(startX + (buttonWidth + buttonSpacing) * 2, raceButtonY, buttonWidth, buttonHeight).build());

        // Confirm button
        confirmButton = Button.builder(
                Component.literal("Confirm Selection"),
                button -> confirmRaceSelection()
        ).bounds(centerX - 75, centerY + 50, 150, 20).build();
        confirmButton.active = false;
        this.addRenderableWidget(confirmButton);
    }

    private void selectRace(Race race) {
        this.selectedRace = race;
        this.confirmButton.active = true;
    }

    private void confirmRaceSelection() {
        if (selectedRace != null) {
            // Send packet to server
            TBATEMod.PACKET_HANDLER.sendToServer(new RaceSelectionPacket(selectedRace));
            this.onClose();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Render background texture
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int bgX = centerX - (BACKGROUND_WIDTH / 2);
        int bgY = centerY - (BACKGROUND_HEIGHT / 2);

        guiGraphics.blit(BACKGROUND, bgX, bgY, 0, 0, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Render title
        guiGraphics.drawCenteredString(this.font, "Choose Your Race", centerX, centerY - 80, 0xFFFFFF);

        // Render race description
        if (selectedRace != null) {
            guiGraphics.drawCenteredString(this.font, selectedRace.getDescription(),
                    centerX, centerY + 25, selectedRace.getColor());
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false; // Prevent closing with ESC to force race selection
    }
}
