package io.github.samipourquoi.syncmatica.litematica;

import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.util.StringUtils;

public enum ButtonType
{
    
	VIEW_SYNCMATICS("syncmatica.gui.button.view_syncmatics"),
	MATERIAL_GATHERINGS("syncmatica.gui.button.material_gatherings");
	
    private final String labelKey;

    private ButtonType(String labelKey)
    {
        this.labelKey = labelKey;
    }

    public String getLabelKey()
    {
        return this.labelKey;
    }

    public String getDisplayName()
    {
        return StringUtils.translate(this.getLabelKey());
    }

    public IGuiIcon getIcon()
    {
        return null;
    }
}