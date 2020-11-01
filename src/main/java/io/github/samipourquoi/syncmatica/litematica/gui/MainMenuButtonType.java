package io.github.samipourquoi.syncmatica.litematica.gui;

import java.util.List;

import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.util.StringUtils;

public enum MainMenuButtonType implements IButtonType
{
    
	VIEW_SYNCMATICS("syncmatica.gui.button.view_syncmatics"),
	MATERIAL_GATHERINGS("syncmatica.gui.button.material_gatherings");
	
    private final String labelKey;

    private MainMenuButtonType(String labelKey)
    {
        this.labelKey = labelKey;
    }

    public IGuiIcon getIcon()
    {
        return null;
    }

	@Override
	public String getTranslatedKey() {
		return StringUtils.translate(this.labelKey);
	}

	@Override
	public List<String> getHoverStrings() {
		return null;
	}

	@Override
	public IButtonActionListener getButtonListener() {
		return null;
	}

	@Override
	public boolean isActive() {
		return false;
	}
}