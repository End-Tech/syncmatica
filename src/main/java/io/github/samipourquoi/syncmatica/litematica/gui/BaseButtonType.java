package io.github.samipourquoi.syncmatica.litematica.gui;

import java.util.List;
import java.util.function.Supplier;

import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.util.StringUtils;

public class BaseButtonType implements IButtonType {
	
	private String translatedKey;
	private IButtonActionListener listener;
	private Supplier<Boolean> activeFunction;
	
	public BaseButtonType(String untranslatedKey, Supplier<Boolean> activeFunction, IButtonActionListener listener) {
		translatedKey = StringUtils.translate(untranslatedKey);
		this.listener = listener;
		this.activeFunction = activeFunction;
	}
	
	@Override
	public String getTranslatedKey() {
		return translatedKey;
	}

	@Override
	public IButtonActionListener getButtonListener() {
		return listener;
	}

	@Override
	public boolean isActive() {
		return activeFunction.get();
	}
	
	@Override
	public IGuiIcon getIcon() {
		return null;
	}

	@Override
	public List<String> getHoverStrings() {
		return null;
	}

}
