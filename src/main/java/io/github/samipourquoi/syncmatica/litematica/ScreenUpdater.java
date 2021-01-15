package io.github.samipourquoi.syncmatica.litematica;

import fi.dy.masa.malilib.gui.widgets.WidgetListBase;

import java.util.function.Consumer;

import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.ServerPlacement;

public class ScreenUpdater {
	
	private static ScreenUpdater instance;
	
	private Consumer<ServerPlacement> updateListener;
	private WidgetListBase<?,?> currentWidget = null;
	private Context context;
	
	public static ScreenUpdater getInstance() {
		if (instance == null) {
			throw new RuntimeException("This shouldnt get called");
		}
		return instance;
	}
	
	public static void init() {
		if (instance != null) {
			instance.detatch();
		}
		instance = new ScreenUpdater();
	}
	
	public static void close() {
		if (instance != null) {
			instance.detatch();
		}
		instance = null;
	}
	
	public ScreenUpdater() {}
	
	public void setActiveContext(Context con) {
		detatch();
		context = con;
		attatch();
		updateCurrentScreen();
	}
	
	public void setCurrentWidget(WidgetListBase<?,?> w) {
		currentWidget = w;
	}

	private void updateCurrentScreen() {
		if (currentWidget != null) {
			currentWidget.refreshEntries();
		}
	}
	
	private void attatch() {
		updateListener = (p) -> updateCurrentScreen();
		context.getSyncmaticManager().addServerPlacementConsumer(updateListener);
	}
	
	private void detatch() {
		if (context != null) {
			context.getSyncmaticManager().removeServerPlacementConsumer(updateListener);
		}
	}
	

}
