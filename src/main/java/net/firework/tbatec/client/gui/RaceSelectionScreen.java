package net.firework.tbatec.client.gui;

import net.firework.tbatec.Race;
import net.firework.tbatec.network.RaceSelectionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RaceSelectionScreen extends Screen {
    private Race selectedRace = null;
    private Button confirmButton;
    private boolean hasSelected = false;

    public RaceSelectionScreen() {
        super(Component.translatable("gui.tbatec.race_selection.title"));
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
                Component.translatable("race.tbatec.human"),
                button -> selectRace(Race.HUMAN)
        ).bounds(startX, raceButtonY, buttonWidth, buttonHeight).build());

        // Elf button
        this.addRenderableWidget(Button.builder(
                Component.translatable("race.tbatec.elf"),
                button -> selectRace(Race.ELF)
        ).bounds(startX + buttonWidth + buttonSpacing, raceButtonY, buttonWidth, buttonHeight).build());

        // Dwarf button
        this.addRenderableWidget(Button.builder(
                Component.translatable("race.tbatec.dwarf"),
                button -> selectRace(Race.DWARF)
        ).bounds(startX + (buttonWidth + buttonSpacing) * 2, raceButtonY, buttonWidth, buttonHeight).build());

        // Confirm button
        confirmButton = Button.builder(
                Component.translatable("gui.tbatec.race_selection.confirm"),
                button -> confirmRaceSelection()
        ).bounds(centerX - 75, centerY + 50, 150, 20).build();
        confirmButton.active = false;
        this.addRenderableWidget(confirmButton);
    }

    private void selectRace(Race race) {
        if (!hasSelected) {
            this.selectedRace = race;
            this.confirmButton.active = true;
        }
    }

    private void confirmRaceSelection() {
        if (selectedRace != null && !hasSelected) {
            hasSelected = true;
            // Send packet to server using new method
            RaceSelectionPacket.sendToServer(selectedRace);
            this.onClose();
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Render title
        guiGraphics.drawCenteredString(this.font, this.title, width / 2, height / 2 - 80, 0xFFFFFF);

        // Render race description
        if (selectedRace != null) {
            Component description = Component.translatable("race.tbatec." + selectedRace.name().toLowerCase() + ".description");
            guiGraphics.drawCenteredString(this.font, description,
                    width / 2, height / 2 + 25, selectedRace.getColor());
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !hasSelected; // Allow ESC after selection
    }
}