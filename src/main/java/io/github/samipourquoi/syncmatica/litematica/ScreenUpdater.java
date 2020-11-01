package io.github.samipourquoi.syncmatica.litematica;

import fi.dy.masa.malilib.gui.widgets.WidgetListBase;

import java.util.function.Consumer;

import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.Syncmatica;

public class ScreenUpdater {
	
	private static ScreenUpdater instance;
	
	private Consumer<ServerPlacement> updateListener;
	private WidgetListBase<?,?> currentWidget = null;
	
	public static ScreenUpdater getInstance() {
		if (instance == null) {
			throw new RuntimeException("This shouldnt get called");
		}
		return instance;
	}
	
	public static void init() {
		if (instance != null) {
			instance.closeInstance();
		}
		instance = new ScreenUpdater();
	}
	
	public static void close() {
		if (instance != null) {
			instance.closeInstance();
		}
		instance = null;
	}
	
	public ScreenUpdater() {
		updateListener = (p) -> updateCurrentScreen();
		Syncmatica.getSyncmaticManager().addServerPlacementConsumer(updateListener);
	}
	
	public void setCurrentWidget(WidgetListBase<?,?> w) {
		currentWidget = w;
	}

	private void updateCurrentScreen() {
		if (currentWidget != null) {
			currentWidget.refreshEntries();
		}
	}
	
	private void closeInstance() {
		Syncmatica.getSyncmaticManager().removeServerPlacementConsumer(updateListener);
	}
	

}
