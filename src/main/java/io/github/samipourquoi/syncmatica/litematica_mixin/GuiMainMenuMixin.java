package io.github.samipourquoi.syncmatica.litematica_mixin;

import org.spongepowered.asm.mixin.Mixin;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.samipourquoi.syncmatica.litematica.ButtonListenerChangeMenu;
import io.github.samipourquoi.syncmatica.litematica.ButtonType;
import fi.dy.masa.litematica.gui.GuiMainMenu;
import fi.dy.masa.litematica.selection.SelectionMode;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiMainMenu.class)
public class GuiMainMenuMixin extends GuiBase {
	
	@Inject(method = "initGui", at = @At("RETURN"), remap = false)
	public void initGui(CallbackInfo ci) {
		int width = getButtonWidth();
		int x = 52 + 2*width;
		int y = 30;
		createChangeMenuButton(x, y, width, ButtonType.VIEW_SYNCMATICS);
		y += 22;
		createChangeMenuButton(x, y, width, ButtonType.MATERIAL_GATHERINGS);
	}
	
    private void createChangeMenuButton(int x, int y, int width, ButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, type.getDisplayName(), type.getIcon());
        addButton(button, new ButtonListenerChangeMenu(type, this));
    }
    
    private int getButtonWidth()
    {
        int width = 0;

        for (ButtonType type : ButtonType.values())
        {
            width = Math.max(width, this.getStringWidth(type.getDisplayName()) + 30);
        }

        for (SelectionMode mode : SelectionMode.values())
        {
            String label = StringUtils.translate("litematica.gui.button.area_selection_mode", mode.getDisplayName());
            width = Math.max(width, this.getStringWidth(label) + 10);
        }

        return width;
    }
}
