package ch.endte.syncmatica.litematica.gui;

import ch.endte.syncmatica.Context;
import ch.endte.syncmatica.LocalLitematicState;
import ch.endte.syncmatica.ServerPlacement;
import ch.endte.syncmatica.communication.ClientCommunicationManager;
import ch.endte.syncmatica.communication.ExchangeTarget;
import ch.endte.syncmatica.communication.PacketType;
import ch.endte.syncmatica.litematica.LitematicManager;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class WidgetSyncmaticaServerPlacementEntry extends WidgetListEntryBase<ServerPlacement> {

    private final ServerPlacement placement;
    private final WidgetListSyncmaticaServerPlacement parent;
    private final boolean isOdd;

    public WidgetSyncmaticaServerPlacementEntry(final int x, int y, final int width, final int height, final ServerPlacement entry,
                                                final int listIndex, final WidgetListSyncmaticaServerPlacement parent) {
        super(x, y, width, height, entry, listIndex);
        placement = entry;
        this.parent = parent;
        isOdd = (listIndex % 2 == 1);
        y += 1;

        int posX = x + width;
        int len;
        ButtonListener listener;
        String text;

        text = StringUtils.translate("syncmatica.gui.button.remove");
        len = getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.REMOVE, this, this.parent.parent);
        addButton(new ButtonGeneric(posX, y, len, 20, text), listener);

        text = StringUtils.translate("syncmatica.gui.button.material_gathering_placement");
        len = getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.MATERIAL_GATHERING, this, this.parent.parent);
        final ButtonGeneric matGathering = new ButtonGeneric(posX, y, len, 20, text);
        matGathering.setEnabled(false);
        addButton(matGathering, listener);

        final ArrayList<IButtonType> multi = new ArrayList<>();
        multi.add(new BaseButtonType("syncmatica.gui.button.downloading"
                , () -> LitematicManager.getInstance().getActiveContext().getCommunicationManager().getDownloadState(placement)
                , null));
        multi.add(new BaseButtonType("syncmatica.gui.button.download"
                , () -> {
            final Context con = LitematicManager.getInstance().getActiveContext();
            final LocalLitematicState state = con.getFileStorage().getLocalState(placement);
            return !state.isLocalFileReady() && state.isReadyForDownload();
        }, new ButtonListener(ButtonListener.Type.DOWNLOAD, this, this.parent.parent)));
        multi.add(new BaseButtonType("syncmatica.gui.button.load",
                () -> !LitematicManager.getInstance().isRendered(placement),
                new ButtonListener(ButtonListener.Type.LOAD, this, this.parent.parent)));
        multi.add(new BaseButtonType("syncmatica.gui.button.unload",
                () -> LitematicManager.getInstance().isRendered(placement),
                new ButtonListener(ButtonListener.Type.UNLOAD, this, this.parent.parent)));

        final ButtonGeneric button = new MultiTypeButton(posX, y, true, multi);
        addButton(button, null);
    }

    @Override
    public void render(final int mouseX, final int mouseY, final boolean selected, final MatrixStack matrixStack) {
        // Source: WidgetSchematicEntry
        RenderUtils.color(1f, 1f, 1f, 1f);

        // Draw a lighter background for the hovered and the selected entry
        if (selected || isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawRect(x, y, width, height, 0x70FFFFFF);
        } else if (isOdd) {
            RenderUtils.drawRect(x, y, width, height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else {
            RenderUtils.drawRect(x, y, width, height, 0x50FFFFFF);
        }

        final String schematicName = placement.getName();
        drawString(x + 20, y + 7, 0xFFFFFFFF, schematicName, matrixStack);
        drawSubWidgets(mouseX, mouseY, matrixStack);
    }

    private static class ButtonListener implements IButtonActionListener {

        Type type;
        WidgetSyncmaticaServerPlacementEntry placement;
        GuiBase messageDisplay;

        public ButtonListener(final Type type, final WidgetSyncmaticaServerPlacementEntry placement, final GuiBase messageDisplay) {
            this.type = type;
            this.placement = placement;
            this.messageDisplay = messageDisplay;
        }

        @Override
        public void actionPerformedWithButton(final ButtonBase button, final int arg1) {
            if (type == null) {
                return;
            }
            button.setEnabled(false);
            type.onAction(button, placement, messageDisplay);
        }

        public enum Type {
            LOAD() {
                @Override
                void onAction(final ButtonBase button, final WidgetSyncmaticaServerPlacementEntry placement, final GuiBase messageDisplay) {
                    LitematicManager.getInstance().renderSyncmatic(placement.placement);
                }
            },
            UNLOAD() {
                @Override
                void onAction(final ButtonBase button, final WidgetSyncmaticaServerPlacementEntry placement, final GuiBase messageDisplay) {
                    LitematicManager.getInstance().unrenderSyncmatic(placement.placement);
                }
            },
            DOWNLOAD() {
                @Override
                void onAction(final ButtonBase button, final WidgetSyncmaticaServerPlacementEntry placement, final GuiBase messageDisplay) {
                    final Context con = LitematicManager.getInstance().getActiveContext();
                    final ExchangeTarget server = ((ClientCommunicationManager) con.getCommunicationManager()).getServer();
                    if (con.getCommunicationManager().getDownloadState(placement.placement)) {
                        return;
                    }
                    try {
                        con.getCommunicationManager().download(placement.placement, server);
                    } catch (final NoSuchAlgorithmException | IOException e) {
                        e.printStackTrace();
                    }
                }
            },
            REMOVE() {
                @Override
                void onAction(final ButtonBase button, final WidgetSyncmaticaServerPlacementEntry placement, final GuiBase messageDisplay) {
                    if(!GuiBase.isShiftDown()) {
                        messageDisplay.addMessage(Message.MessageType.ERROR, "syncmatica.error.remove_without_shift");
                        button.setEnabled(true);
                        return;
                    }
                    final Context con = LitematicManager.getInstance().getActiveContext();
                    final ExchangeTarget server = ((ClientCommunicationManager) con.getCommunicationManager()).getServer();
                    final PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
                    packetBuf.writeUuid(placement.placement.getId());
                    server.sendPacket(PacketType.REMOVE_SYNCMATIC.identifier, packetBuf, LitematicManager.getInstance().getActiveContext());
                }
            },
            MATERIAL_GATHERING() {
                @Override
                void onAction(final ButtonBase button, final WidgetSyncmaticaServerPlacementEntry placement, final GuiBase messageDisplay) {
                    LogManager.getLogger().info("Opened Material Gatherings GUI - currently unsupported operation");
                }
            };

            abstract void onAction(ButtonBase button, WidgetSyncmaticaServerPlacementEntry placement, GuiBase messageDisplay);
        }

    }

}
