package io.github.samipourquoi.syncmatica.litematica.gui;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.LocalLitematicState;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class WidgetSyncmaticaServerPlacementEntry extends WidgetListEntryBase<ServerPlacement> {

    private final ServerPlacement placement;
    private final boolean isOdd;

    public WidgetSyncmaticaServerPlacementEntry(final int x, int y, final int width, final int height, final ServerPlacement entry,
                                                final int listIndex) {
        super(x, y, width, height, entry, listIndex);
        placement = entry;
        isOdd = (listIndex % 2 == 1);
        y += 1;

        int posX = x + width;
        int len;
        ButtonListener listener;
        String text;

        text = StringUtils.translate("syncmatica.gui.button.remove");
        len = getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.REMOVE, this);
        addButton(new ButtonGeneric(posX, y, len, 20, text), listener);

        text = StringUtils.translate("syncmatica.gui.button.material_gathering_placement");
        len = getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.MATERIAL_GATHERING, this);
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
        }, new ButtonListener(ButtonListener.Type.DOWNLOAD, this)));
        multi.add(new BaseButtonType("syncmatica.gui.button.load"
                , () -> {
            return !LitematicManager.getInstance().isRendered(placement);
        }, new ButtonListener(ButtonListener.Type.LOAD, this)));
        multi.add(new BaseButtonType("syncmatica.gui.button.unload"
                , () -> {
            return LitematicManager.getInstance().isRendered(placement);
        }, new ButtonListener(ButtonListener.Type.UNLOAD, this)));

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

        public ButtonListener(final Type type, final WidgetSyncmaticaServerPlacementEntry placement) {
            this.type = type;
            this.placement = placement;
        }

        @Override
        public void actionPerformedWithButton(final ButtonBase button, final int arg1) {
            if (type == null) {
                return;
            }
            button.setEnabled(false);
            type.onAction(placement);
        }

        public static enum Type {
            LOAD() {
                @Override
                void onAction(final WidgetSyncmaticaServerPlacementEntry placement) {
                    final String dimension = MinecraftClient.getInstance().getCameraEntity().getEntityWorld().getRegistryKey().getValue().toString();
                    LitematicManager.getInstance().renderSyncmatic(placement.placement);
                }
            },
            UNLOAD() {
                @Override
                void onAction(final WidgetSyncmaticaServerPlacementEntry placement) {
                    LitematicManager.getInstance().unrenderSyncmatic(placement.placement);
                }
            },
            DOWNLOAD() {
                @Override
                void onAction(final WidgetSyncmaticaServerPlacementEntry placement) {
                    final Context con = LitematicManager.getInstance().getActiveContext();
                    final ExchangeTarget server = ((ClientCommunicationManager) con.getCommunicationManager()).getServer();
                    if (con.getCommunicationManager().getDownloadState(placement.placement)) {
                        return;
                    }
                    try {
                        con.getCommunicationManager().download(placement.placement, server);
                    } catch (final NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            },
            REMOVE() {
                @Override
                void onAction(final WidgetSyncmaticaServerPlacementEntry placement) {
                    final Context con = LitematicManager.getInstance().getActiveContext();
                    final ExchangeTarget server = ((ClientCommunicationManager) con.getCommunicationManager()).getServer();
                    final PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
                    packetBuf.writeUuid(placement.placement.getId());
                    server.sendPacket(PacketType.REMOVE_SYNCMATIC.IDENTIFIER, packetBuf);
                }
            },
            MATERIAL_GATHERING() {
                @Override
                void onAction(final WidgetSyncmaticaServerPlacementEntry placement) {
                    // TODO: Material Gatherings
                    System.out.println("DISPLAY MATERIAL GATHERINGS");
                }
            };

            abstract void onAction(WidgetSyncmaticaServerPlacementEntry placement);
        }

    }

}
