package io.github.samipourquoi.syncmatica.litematica_mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.litematica.gui.widgets.WidgetListSchematicPlacements;
import fi.dy.masa.litematica.gui.widgets.WidgetSchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import io.github.samipourquoi.syncmatica.litematica.gui.ButtonListenerShare;

@Mixin(WidgetSchematicPlacement.class)
public abstract class MixinWidgetSchematicPlacement extends WidgetListEntryBase<SchematicPlacement> {
	
	public MixinWidgetSchematicPlacement(int x, int y, int width, int height, SchematicPlacement entry, int listIndex) {
		super(x, y, width, height, entry, listIndex);
	}

	@Shadow(remap=false)
    private int buttonsStartX;
	@Shadow(remap=false)
	private SchematicPlacement placement;
	
	
	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	public void addUploadButton(int x, int y, int width, int height, boolean isOdd,
			SchematicPlacement placement, int listIndex, WidgetListSchematicPlacements parent, CallbackInfo ci) {
		int i = 0;
		if (LitematicManager.getInstance().isSyncmatic(placement)) {
			for (WidgetBase base: this.subWidgets) {
				if (base instanceof ButtonBase) {
					ButtonBase button = (ButtonBase)base;
					if (++i == 1) {
						IButtonActionListener oldAction = ((MixinButtonBase)button).getActionListener();
						button.setActionListener((b,k)-> {
							if (GuiBase.isShiftDown()) {
								LitematicManager.getInstance().unrenderSchematicPlacement(placement);
								return;
							}
							oldAction.actionPerformedWithButton(b, k);
						});
						
					} else if (i == 3) { 
						((ButtonBase) base).setEnabled(false);
					}
				}
			}
		}
	
		ButtonGeneric shareButton = new ButtonGeneric(buttonsStartX, y+1, -1, true, "syncmatica.gui.button.share");
		Context con = LitematicManager.getInstance().getActiveContext();
		boolean buttonEnabled = con != null && con.isStarted() && !LitematicManager.getInstance().isSyncmatic(placement);
		shareButton.setEnabled(buttonEnabled);
		addButton(shareButton, new ButtonListenerShare(placement));
		buttonsStartX = shareButton.getX()-1;
	}
	
	public SchematicPlacement getPlacement() {
		return placement;
	}

}
