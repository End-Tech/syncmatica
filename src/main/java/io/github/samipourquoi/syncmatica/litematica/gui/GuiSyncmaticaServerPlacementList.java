package io.github.samipourquoi.syncmatica.litematica.gui;

import fi.dy.masa.litematica.gui.GuiMainMenu.ButtonListenerChangeMenu;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.samipourquoi.syncmatica.SyncmaticaServerPlacement;

public class GuiSyncmaticaServerPlacementList extends GuiListBase<SyncmaticaServerPlacement, WidgetSyncmaticaServerPlacementEntry, WidgetListSyncmaticaServerPlacement> {

	public GuiSyncmaticaServerPlacementList() {
		super(12, 30);
		this.title = StringUtils.translate("syncmatica.gui.title.manage_server_placements");
	}

	@Override
	public void initGui() {
		super.initGui();
		// source GuiSchematicLoadedList
		ButtonListenerChangeMenu.ButtonType type = ButtonListenerChangeMenu.ButtonType.MAIN_MENU;
        String label = StringUtils.translate(type.getLabelKey());
        int buttonWidth = this.getStringWidth(label) + 20;
        int x = this.width - buttonWidth - 10;
        int y = this.height - 26;
        ButtonGeneric button = new ButtonGeneric(x, y, buttonWidth, 20, label);
        this.addButton(button, new ButtonListenerChangeMenu(type, this.getParent()));
	}
	
	@Override
	protected WidgetListSyncmaticaServerPlacement createListWidget(int listX, int listY) {
		return new WidgetListSyncmaticaServerPlacement(listX, listY, this.getBrowserWidth(), this.getBrowserHeight(), this, null);
	}

	@Override
	protected int getBrowserHeight() {
		return this.height-68;
	}

	@Override
	protected int getBrowserWidth() {
		return this.width-20;
	}

    public int getMaxInfoHeight()
    {
        return this.getBrowserHeight();
    }

}
