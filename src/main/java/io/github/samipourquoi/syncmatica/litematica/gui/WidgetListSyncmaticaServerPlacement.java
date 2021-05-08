package io.github.samipourquoi.syncmatica.litematica.gui;


import com.google.common.collect.ImmutableList;
import fi.dy.masa.litematica.gui.Icons;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import io.github.samipourquoi.syncmatica.litematica.ScreenHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.List;


public class WidgetListSyncmaticaServerPlacement extends WidgetListBase<ServerPlacement, WidgetSyncmaticaServerPlacementEntry> {

    private final int infoWidth;
    private final int infoHeight;
    private final GuiSyncmaticaServerPlacementList parent;

    public WidgetListSyncmaticaServerPlacement(final int x, final int y, final int width, final int height, final GuiSyncmaticaServerPlacementList parent,
                                               final ISelectionListener<ServerPlacement> selectionListener) {
        super(x, y, width, height, selectionListener);
        browserEntryHeight = 22;
        infoWidth = 170;
        infoHeight = 290;
        widgetSearchBar = new WidgetSearchBar(x + 2, y + 4, width - 14, 14, 0, Icons.FILE_ICON_SEARCH, LeftRight.LEFT);
        browserEntriesOffsetY = widgetSearchBar.getHeight() + 3;
        this.parent = parent;
        setSize(width, height);
        ScreenHelper.ifPresent((s) -> s.setCurrentGui(parent));
    }

    @Override
    public void setSize(final int width, final int height) {
        super.setSize(width, height);

        browserWidth = getBrowserWidthForTotalWidth(width);
        browserEntryWidth = browserWidth - 14;
    }


    protected int getBrowserWidthForTotalWidth(final int width) {
        return width - 6 - infoWidth;
    }

    // source: WidgetFileBrowserBase
    @Override
    public void drawContents(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float partialTicks) {
        // Draw an outline around the entire widget
        RenderUtils.drawOutlinedBox(posX, posY, browserWidth, browserHeight, 0xB0000000, GuiBase.COLOR_HORIZONTAL_BAR);

        super.drawContents(matrixStack, mouseX, mouseY, partialTicks);

        drawPlacementInfo(getLastSelectedEntry(), matrixStack);
    }

    private void drawPlacementInfo(final ServerPlacement placement, final MatrixStack matrixStack) {
        int x = posX + totalWidth - infoWidth;
        int y = posY;
        final int height = Math.min(infoHeight, parent.getMaxInfoHeight());

        RenderUtils.drawOutlinedBox(x, y, infoWidth, height, 0xA0000000, GuiBase.COLOR_HORIZONTAL_BAR);

        if (placement == null) {
            return;
        }

        RenderUtils.color(1f, 1f, 1f, 1f);

        x += 3;
        y += 3;
        final int textColor = 0xC0C0C0C0;
        final int valueColor = 0xFFFFFFFF;

        String str = StringUtils.translate("syncmatica.gui.label.placement_info.file_name");
        drawString(matrixStack, str, x, y, textColor);
        y += 12;
        drawString(matrixStack, placement.getName(), x + 4, y, valueColor);
        y += 12;

        str = StringUtils.translate("syncmatica.gui.label.placement_info.dimension_id");
        drawString(matrixStack, str, x, y, textColor);
        y += 12;
        drawString(matrixStack, placement.getDimension(), x + 4, y, valueColor);
        y += 12;

        str = StringUtils.translate("syncmatica.gui.label.placement_info.position");
        drawString(matrixStack, str, x, y, textColor);
        y += 12;
        final BlockPos origin = placement.getPosition();
        final String tmp = String.format("%d %d %d", origin.getX(), origin.getY(), origin.getZ());
        drawString(matrixStack, tmp, x + 4, y, valueColor);
        y += 12;
    }

    @Override
    protected List<String> getEntryStringsForFilter(final ServerPlacement entry) {
        final String metaName = entry.getName().toLowerCase();
        return ImmutableList.of(metaName);
    }

    @Override
    protected WidgetSyncmaticaServerPlacementEntry createListEntryWidget(final int x, final int y, final int listIndex, final boolean isOdd, final ServerPlacement entry) {
        return new WidgetSyncmaticaServerPlacementEntry(x, y, browserEntryWidth, getBrowserEntryHeightFor(entry), entry, listIndex);
    }

    @Override
    protected Collection<ServerPlacement> getAllEntries() {
        return LitematicManager.getInstance().getActiveContext().getSyncmaticManager().getAll();
    }
}
