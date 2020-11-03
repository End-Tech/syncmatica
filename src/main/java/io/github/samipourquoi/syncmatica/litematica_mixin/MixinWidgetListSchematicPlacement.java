package io.github.samipourquoi.syncmatica.litematica_mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import fi.dy.masa.litematica.gui.GuiSchematicPlacementsList;
import fi.dy.masa.litematica.gui.widgets.WidgetListSchematicPlacements;
import fi.dy.masa.litematica.gui.widgets.WidgetSchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.Syncmatica;
import io.github.samipourquoi.syncmatica.litematica.ScreenUpdater;

@Mixin(WidgetListSchematicPlacements.class)
public abstract class MixinWidgetListSchematicPlacement extends WidgetListBase<SchematicPlacement, WidgetSchematicPlacement> {
	
	@Unique
	Consumer<ServerPlacement> updateListener;
	
	public MixinWidgetListSchematicPlacement(int x, int y, int width, int height,
			ISelectionListener<SchematicPlacement> selectionListener) {
		super(x, y, width, height, selectionListener);
	}

	@Inject(method = "<init>", at = @At("TAIL"), remap = false)
	public void setupListener(int x, int y, int width, int height, GuiSchematicPlacementsList parent, CallbackInfo ci) {
		ScreenUpdater.getInstance().setCurrentWidget(this);
		updateListener = (p) -> this.refreshBrowserEntries();
		Syncmatica.getSyncmaticManager().addServerPlacementConsumer(updateListener);
	}

}
