package io.github.samipourquoi.syncmatica.litematica_mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.litematica.gui.widgets.WidgetListSchematicPlacements;
import fi.dy.masa.litematica.gui.widgets.WidgetSchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

@Mixin(WidgetSchematicPlacement.class)
public abstract class MixinWidgetSchematicPlacement {
	
	@Shadow
    private int buttonsStartX;
	@Shadow
	private SchematicPlacement placement;
	
	@Shadow
	public abstract int getStringWidth(String text);
	
	@Shadow
	protected abstract <T extends ButtonBase> T addButton(T button, IButtonActionListener listener);
	
	@Inject(method = "<init>(IIIIZLfi/dy/masa/litematica/schematic/placement/SchematicPlacement;ILfi/dy/masa/litematica/gui/widgets/WidgetListSchematicPlacements;)V", at = @At("TAIL"), remap = false)
	public void addUploadButton(int x, int y, int width, int height, boolean isOdd,
			SchematicPlacement placement, int listIndex, WidgetListSchematicPlacements parent, CallbackInfo ci) {
		ButtonGeneric share = new ButtonGeneric(buttonsStartX, y+1, -1, true, "syncmatica.gui.button.share");
		buttonsStartX = addButton(share, null).getX()-1;
	}
	
	public SchematicPlacement getPlacement() {
		return placement;
	}

}
