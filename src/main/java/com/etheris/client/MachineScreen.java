package com.etheris.client;
import com.etheris.machine.MachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class MachineScreen extends AbstractContainerScreen<MachineMenu> {
    private Button pause;
    public MachineScreen(MachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title); imageHeight = 196; inventoryLabelY = 101;
    }
    @Override protected void init() {
        super.init();
        pause = addRenderableWidget(Button.builder(Component.translatable("gui.etheris.pause"), button -> send(0)).bounds(leftPos + 8, topPos + 78, 76, 18).build());
        addRenderableWidget(Button.builder(Component.translatable("gui.etheris.unlink"), button -> send(1)).bounds(leftPos + 92, topPos + 78, 76, 18).build());
    }
    private void send(int action) { if (minecraft != null && minecraft.gameMode != null) minecraft.gameMode.handleInventoryButtonClick(menu.containerId, action); }
    @Override protected void containerTick() { super.containerTick(); pause.setMessage(Component.translatable(menu.paused() ? "gui.etheris.resume" : "gui.etheris.pause")); }
    @Override protected void renderBg(GuiGraphics graphics, float partial, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF151924);
        graphics.fill(x + 1, y + 1, x + imageWidth - 1, y + 2, 0xFFD6AE60);
        graphics.fill(x + 8, y + 31, x + 168, y + 36, 0xFF33384B);
        int width = Math.clamp(menu.energy() * 160 / menu.capacity(), 0, 160);
        graphics.fill(x + 8, y + 31, x + 8 + width, y + 36, 0xFF9C86EC);
        for (var slot : menu.slots) {
            graphics.fill(x + slot.x - 1, y + slot.y - 1, x + slot.x + 17, y + slot.y + 17, 0xFF55556A);
            graphics.fill(x + slot.x, y + slot.y, x + slot.x + 16, y + slot.y + 16, 0xFF232738);
        }
        graphics.drawString(font, "→", x + 83, y + 51, 0xFFD6AE60, false);
    }
    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, 8, 7, 0xFFE8D9BA, false);
        graphics.drawString(font, Component.translatable("gui.etheris.energy", menu.energy(), menu.capacity()), 8, 20, 0xFFE6E4F5, false);
        graphics.drawCenteredString(font, Component.translatable("gui.etheris.status." + menu.status()), imageWidth / 2, 66, 0xFFDFD9E9);
        graphics.drawString(font, playerInventoryTitle, 8, inventoryLabelY, 0xFFD3D1DB, false);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partial) {
        super.render(graphics, mouseX, mouseY, partial); renderTooltip(graphics, mouseX, mouseY);
    }
}
