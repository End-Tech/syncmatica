package io.github.samipourquoi.syncmatica.litematica;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import net.minecraft.client.gui.screen.Screen;

public class ButtonListenerChangeMenu implements IButtonActionListener {
	
	private ButtonType type;
	private Screen parent;
	
	public ButtonListenerChangeMenu(ButtonType type, Screen parent) {
		this.type = type;
		this.parent = parent;
	}
	
	@Override
	public void actionPerformedWithButton(ButtonBase arg0, int arg1) {
		switch (this.type) {
		case MATERIAL_GATHERINGS:
			System.out.println("Opened Material Gatherings GUI - currently unsupported operation");
			break;
		case VIEW_SYNCMATICS:
			System.out.println("Opened View Syncmatics GUI - currently unsupported operation");
			break;
		default:
			break;
			
		}
	}
	
}