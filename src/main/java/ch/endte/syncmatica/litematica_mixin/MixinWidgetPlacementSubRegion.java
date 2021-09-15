package ch.endte.syncmatica.litematica_mixin;

import ch.endte.syncmatica.litematica.LitematicManager;
import fi.dy.masa.litematica.gui.widgets.WidgetListPlacementSubRegions;
import fi.dy.masa.litematica.gui.widgets.WidgetPlacementSubRegion;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.schematic.placement.SubRegionPlacement;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.widgets.WidgetBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WidgetPlacementSubRegion.class)
public abstract class MixinWidgetPlacementSubRegion extends WidgetListEntryBase<SubRegionPlacement> {
    protected MixinWidgetPlacementSubRegion(final int x, final int y, final int width, final int height, final SubRegionPlacement entry, final int listIndex) {
        super(x, y, width, height, entry, listIndex);
    }

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    public void disableSubregionButtons(
            final int x,
            final int y,
            final int width,
            final int height,
            final boolean isOdd,
            final SchematicPlacement schematicPlacement,
            final SubRegionPlacement placement,
            final int listIndex,
            final WidgetListPlacementSubRegions parent,
            final CallbackInfo ci) {
        if (LitematicManager.getInstance().isSyncmatic(schematicPlacement)) {
            for (final WidgetBase widgetBase : subWidgets) {
                if (widgetBase instanceof ButtonBase) {
                    ((ButtonBase) widgetBase).setEnabled(false);
                }
            }
        }
    }
}
