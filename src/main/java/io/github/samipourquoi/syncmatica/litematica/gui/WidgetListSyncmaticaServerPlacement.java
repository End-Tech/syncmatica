package io.github.samipourquoi.syncmatica.litematica.gui;



import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import fi.dy.masa.litematica.gui.Icons;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.samipourquoi.syncmatica.SyncmaticaServerPlacement;
import io.github.samipourquoi.syncmatica.SyncmaticaServerPlacementStorage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class WidgetListSyncmaticaServerPlacement extends WidgetListBase<SyncmaticaServerPlacement, WidgetSyncmaticaServerPlacementEntry> {
	
    private final int infoWidth;
    private final int infoHeight;
    private final GuiSyncmaticaServerPlacementList parent;
	
	public WidgetListSyncmaticaServerPlacement(int x, int y, int width, int height, GuiSyncmaticaServerPlacementList parent,
			ISelectionListener<SyncmaticaServerPlacement> selectionListener) {
		super(x, y, width, height, selectionListener);
		this.browserEntryHeight = 22;
        this.infoWidth = 170;
        this.infoHeight = 290;
		this.widgetSearchBar = new WidgetSearchBar(x + 2, y + 4, width - 14, 14, 0, Icons.FILE_ICON_SEARCH, LeftRight.LEFT);
        this.browserEntriesOffsetY = this.widgetSearchBar.getHeight() + 3;
        this.parent = parent;
        this.setSize(width, height);
	}
	
    @Override
    public void setSize(int width, int height)
    {
        super.setSize(width, height);

        this.browserWidth = this.getBrowserWidthForTotalWidth(width);
        this.browserEntryWidth = this.browserWidth - 14;
    }
	
    
    protected int getBrowserWidthForTotalWidth(int width)
    {
        return width - 6 - this.infoWidth;
    }
	
	// source: WidgetFileBrowserBase
    @Override
    public void drawContents(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Draw an outline around the entire widget
        RenderUtils.drawOutlinedBox(this.posX, this.posY, this.browserWidth, this.browserHeight, 0xB0000000, COLOR_HORIZONTAL_BAR);

        super.drawContents(matrixStack, mouseX, mouseY, partialTicks);

        this.drawPlacementInfo(this.getLastSelectedEntry(), matrixStack);
    }

    private void drawPlacementInfo(SyncmaticaServerPlacement placement, MatrixStack matrixStack) {
        int x = this.posX + this.totalWidth - this.infoWidth;
        int y = this.posY;
        int height = Math.min(this.infoHeight, this.parent.getMaxInfoHeight());

        RenderUtils.drawOutlinedBox(x, y, this.infoWidth, height, 0xA0000000, COLOR_HORIZONTAL_BAR);

        if (placement == null) {
            return;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);

        x += 3;
        y += 3;
        int textColor = 0xC0C0C0C0;
        int valueColor = 0xFFFFFFFF;
        
        String str = StringUtils.translate("syncmatica.gui.label.placement_info.file_name");
        drawString(matrixStack, str, x, y, textColor);
        y += 12;
        drawString(matrixStack, placement.getFileName(), x + 4, y, valueColor);
        y += 12;
        
        str = StringUtils.translate("syncmatica.gui.label.placement_info.dimension_id");
        drawString(matrixStack, str, x, y, textColor);
        y += 12;
        drawString(matrixStack, placement.getDimension(), x + 4, y, valueColor);
        y += 12;

        str = StringUtils.translate("syncmatica.gui.label.placement_info.position");
        drawString(matrixStack, str, x, y, textColor);
        y += 12;
        BlockPos origin = placement.getPosition();
        String tmp = String.format("%d %d %d", origin.getX(), origin.getY(), origin.getZ());
        this.drawString(matrixStack, tmp, x + 4, y, valueColor);
        y += 12;
    }
	
    @Override
    protected List<String> getEntryStringsForFilter(SyncmaticaServerPlacement entry) {
        String metaName = entry.getFileName().toLowerCase();
        return ImmutableList.of(metaName);
    }

	@Override
	protected WidgetSyncmaticaServerPlacementEntry createListEntryWidget(int x, int y, int listIndex, boolean isOdd, SyncmaticaServerPlacement entry) {
		// TODO Auto-generated method stub
		return new WidgetSyncmaticaServerPlacementEntry(x, y, this.browserEntryWidth, this.getBrowserEntryHeightFor(entry), entry, listIndex);
	}
	
    @Override
    protected Collection<SyncmaticaServerPlacement> getAllEntries()
    {
        return SyncmaticaServerPlacementStorage.getEntries();
    }

}
