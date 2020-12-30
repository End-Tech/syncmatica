package io.github.samipourquoi.syncmatica.litematica_mixin;

import org.spongepowered.asm.mixin.Mixin;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import io.github.samipourquoi.syncmatica.litematica.gui.ButtonListenerChangeMenu;
import io.github.samipourquoi.syncmatica.litematica.gui.MainMenuButtonType;
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
		createChangeMenuButton(x, y, width, MainMenuButtonType.VIEW_SYNCMATICS);
		y += 22;
		createChangeMenuButton(x, y, width, MainMenuButtonType.MATERIAL_GATHERINGS).setEnabled(false);
	}
	
    private ButtonGeneric createChangeMenuButton(int x, int y, int width, MainMenuButtonType type)
    {
        ButtonGeneric button = new ButtonGeneric(x, y, width, 20, type.getTranslatedKey(), type.getIcon());
        Context con = LitematicManager.getInstance().getActiveContext(); 
        button.setEnabled(con != null && con.isStarted());
        addButton(button, new ButtonListenerChangeMenu(type, this));
        return button;
    }
    
    private int getButtonWidth()
    {
        int width = 0;

        for (MainMenuButtonType type : MainMenuButtonType.values())
        {
            width = Math.max(width, this.getStringWidth(type.getTranslatedKey()) + 30);
        }

        for (SelectionMode mode : SelectionMode.values())
        {
            String label = StringUtils.translate("litematica.gui.button.area_selection_mode", mode.getDisplayName());
            width = Math.max(width, this.getStringWidth(label) + 10);
        }

        return width;
    }
}
