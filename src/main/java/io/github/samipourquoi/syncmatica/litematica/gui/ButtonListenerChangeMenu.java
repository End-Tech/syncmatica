package io.github.samipourquoi.syncmatica.litematica.gui;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.gui.screen.Screen;

public class ButtonListenerChangeMenu implements IButtonActionListener {
	
	private MainMenuButtonType type;
	private Screen parent;
	
	public ButtonListenerChangeMenu(MainMenuButtonType type, Screen parent) {
		this.type = type;
		this.parent = parent;
	}
	
	@Override
	public void actionPerformedWithButton(ButtonBase arg0, int arg1) {
		GuiBase gui = null;
		switch (this.type) {
		case MATERIAL_GATHERINGS:
			System.out.println("Opened Material Gatherings GUI - currently unsupported operation");
			break;
		case VIEW_SYNCMATICS:
			System.out.println("Opened View Syncmatics GUI - should result in open window");
			gui = new GuiSyncmaticaServerPlacementList();
			break;
		default:
			break;
		}
		if (gui != null) {
            gui.setParent(this.parent);
            GuiBase.openGui(gui);
		}
	}
	
}