package io.github.samipourquoi.syncmatica.litematica.gui;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.samipourquoi.syncmatica.SyncmaticaServerPlacement;
import io.github.samipourquoi.syncmatica.SyncmaticaServerPlacementStorage;
import net.minecraft.client.util.math.MatrixStack;

public class WidgetSyncmaticaServerPlacementEntry extends WidgetListEntryBase<SyncmaticaServerPlacement> {

	private WidgetListSyncmaticaServerPlacement parent;
	private SyncmaticaServerPlacement placement;
	private boolean isOdd;
	
	public WidgetSyncmaticaServerPlacementEntry(int x, int y, int width, int height, SyncmaticaServerPlacement entry,
			int listIndex, WidgetListSyncmaticaServerPlacement parent) {
		super(x, y, width, height, entry, listIndex);
        this.parent = parent;
        this.placement = entry;
        this.isOdd = (listIndex % 2 == 1);
        y += 1;

        int posX = x + width;
        int len;
        ButtonListener listener;
        String text;
        
        text = StringUtils.translate("syncmatica.gui.button.remove");
        len = this.getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.REMOVE, this);
        this.addButton(new ButtonGeneric(posX, y, len, 20, text), listener);
        
        if (!SyncmaticaServerPlacementStorage.hasLocalLitematic(entry)) {
            text = StringUtils.translate("syncmatica.gui.button.download");
            listener = new ButtonListener(ButtonListener.Type.DOWNLOAD, this);
        } else if (!SyncmaticaServerPlacementStorage.isLoaded(entry)) {
            text = StringUtils.translate("syncmatica.gui.button.load");
            listener = new ButtonListener(ButtonListener.Type.LOAD, this);
        } else {
            text = StringUtils.translate("syncmatica.gui.button.unload");
            listener = new ButtonListener(ButtonListener.Type.UNLOAD, this);       	
        }
        
        len = this.getStringWidth(text) + 10;
        posX -= (len + 2);
        this.addButton(new ButtonGeneric(posX, y, len, 20, text), listener);
	}
	
	public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack)
    {
		// Source: WidgetSchematicEntry
		RenderUtils.color(1f, 1f, 1f, 1f);

		// Draw a lighter background for the hovered and the selected entry
		if (selected || this.isMouseOver(mouseX, mouseY))
		{
			RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x70FFFFFF);
		}
		else if (this.isOdd)
		{
			RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x20FFFFFF);
		}
		// Draw a slightly lighter background for even entries
		else
		{
			RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x50FFFFFF);
		}

		String schematicName = this.placement.getFileName();
		this.drawString(this.x + 20, this.y + 7, 0xFFFFFFFF, schematicName, matrixStack);
		this.drawSubWidgets(mouseX, mouseY, matrixStack);
    }
	
	private static class ButtonListener implements IButtonActionListener {
		
		Type type;
		WidgetSyncmaticaServerPlacementEntry placement;
		
		public ButtonListener(Type type, WidgetSyncmaticaServerPlacementEntry placement) {
			this.type = type;
			this.placement = placement;
		}
		
		@Override
		public void actionPerformedWithButton(ButtonBase arg0, int arg1) {
			type.onAction(placement);			
		}
		
		public static enum Type {
			LOAD() {
				@Override
				void onAction(WidgetSyncmaticaServerPlacementEntry placement) {
					System.out.println("LOADING SYNCMATIC");
				}
			},
			UNLOAD() {
				@Override
				void onAction(WidgetSyncmaticaServerPlacementEntry placement) {
					System.out.println("UNLOADING SYNCMATIC");
				}
			},
			DOWNLOAD() {
				@Override
				void onAction(WidgetSyncmaticaServerPlacementEntry placement) {
					System.out.println("DOWNLOADING SYNCMATIC");
				}
			},
			REMOVE() {
				@Override
				void onAction(WidgetSyncmaticaServerPlacementEntry placement) {
					System.out.println("REMOVING SYNCMATIC");					
				}
			};
			abstract void onAction(WidgetSyncmaticaServerPlacementEntry placement);
		}

	}

}
