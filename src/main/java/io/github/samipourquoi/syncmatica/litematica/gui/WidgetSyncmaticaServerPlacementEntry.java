package io.github.samipourquoi.syncmatica.litematica.gui;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListEntryBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.LocalLitematicState;
import io.github.samipourquoi.syncmatica.communication.ClientCommunicationManager;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.github.samipourquoi.syncmatica.litematica.LitematicManager;
import io.netty.buffer.Unpooled;
import io.github.samipourquoi.syncmatica.ServerPlacement;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;



public class WidgetSyncmaticaServerPlacementEntry extends WidgetListEntryBase<ServerPlacement> {

	private ServerPlacement placement;
	private boolean isOdd;
	
	public WidgetSyncmaticaServerPlacementEntry(int x, int y, int width, int height, ServerPlacement entry,
			int listIndex) {
		super(x, y, width, height, entry, listIndex);
        this.placement = entry;
        this.isOdd = (listIndex % 2 == 1);
        y += 1;

        int posX = x + width;
        int len;
        ButtonListener listener;
        String text;
        
        text = StringUtils.translate("syncmatica.gui.button.remove");
        len = this.getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.REMOVE, this);
        this.addButton(new ButtonGeneric(posX, y, len, 20, text), listener);
        
        text = StringUtils.translate("syncmatica.gui.button.material_gathering_placement");
        len = this.getStringWidth(text) + 10;
        posX -= (len + 2);
        listener = new ButtonListener(ButtonListener.Type.MATERIAL_GATHERING, this);
        this.addButton(new ButtonGeneric(posX, y, len, 20, text), listener);
        
        ArrayList<IButtonType> multi = new ArrayList<>();
        multi.add(new BaseButtonType("syncmatica.gui.button.downloading"
        		,()-> false, new ButtonListener(null, null)));
        multi.add(new BaseButtonType("syncmatica.gui.button.download"
        		,()-> {
        			Context con = LitematicManager.getInstance().getActiveContext();
        			LocalLitematicState state = con.getFileStorage().getLocalState(placement);
        			return !state.isLocalFileReady()&&state.isReadyForDownload();
        }, new ButtonListener(ButtonListener.Type.DOWNLOAD, this)));
        multi.add(new BaseButtonType("syncmatica.gui.button.load"
        		,()-> {
        			return !LitematicManager.getInstance().isRendered(placement);
        }, new ButtonListener(ButtonListener.Type.LOAD, this)));
        multi.add(new BaseButtonType("syncmatica.gui.button.unload"
        		,()-> {
        			return LitematicManager.getInstance().isRendered(placement);
        }, new ButtonListener(ButtonListener.Type.UNLOAD, this)));
        
        ButtonGeneric button = new MultiTypeButton(posX, y, true, multi);
        this.addButton(button, null);
	}
	
	public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack)
    {
		// Source: WidgetSchematicEntry
		RenderUtils.color(1f, 1f, 1f, 1f);

		// Draw a lighter background for the hovered and the selected entry
		if (selected || this.isMouseOver(mouseX, mouseY))
		{
			RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x70FFFFFF);
		}
		else if (this.isOdd)
		{
			RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x20FFFFFF);
		}
		// Draw a slightly lighter background for even entries
		else
		{
			RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x50FFFFFF);
		}

		String schematicName = this.placement.getName();
		this.drawString(this.x + 20, this.y + 7, 0xFFFFFFFF, schematicName, matrixStack);
		this.drawSubWidgets(mouseX, mouseY, matrixStack);
    }
	
	private static class ButtonListener implements IButtonActionListener {
		
		Type type;
		WidgetSyncmaticaServerPlacementEntry placement;
		
		public ButtonListener(Type type, WidgetSyncmaticaServerPlacementEntry placement) {
			this.type = type;
			this.placement = placement;
		}
		
		@Override
		public void actionPerformedWithButton(ButtonBase button, int arg1) {
			if (type == null) {
				return;
			}
			button.setEnabled(false);
			type.onAction(placement);
		}
		
		public static enum Type {
			LOAD() {
				@Override
				void onAction(WidgetSyncmaticaServerPlacementEntry placement) {
					LitematicManager.getInstance().renderSyncmatic(placement.placement);
				}
			},
			UNLOAD() {
				@Override
				void onAction(WidgetSyncmaticaServerPlacementEntry placement) {
					LitematicManager.getInstance().unrenderSyncmatic(placement.placement);
				}
			},
			DOWNLOAD() {
				@Override
				void onAction(WidgetSyncmaticaServerPlacementEntry placement) {
					Context con = LitematicManager.getInstance().getActiveContext();
					ExchangeTarget server = ((ClientCommunicationManager)con.getCommunicationManager()).getServer();
					if (con.getCommunicationManager().getDownloadState(placement.placement)) {
						return;
					}
					try {
						con.getCommunicationManager().download(placement.placement, server);
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			},
			REMOVE() {
				@Override
				void onAction(WidgetSyncmaticaServerPlacementEntry placement) {
					Context con = LitematicManager.getInstance().getActiveContext();
					ExchangeTarget server = ((ClientCommunicationManager)con.getCommunicationManager()).getServer();
					PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
					packetBuf.writeUuid(placement.placement.getId());
					server.sendPacket(PacketType.REMOVE_SYNCMATIC.IDENTIFIER, packetBuf);
				}
			},
			MATERIAL_GATHERING() {
				@Override
				void onAction(WidgetSyncmaticaServerPlacementEntry placement) {
					// TODO: Material Gatherings
					System.out.println("DISLAY MATERIAL GATHERINGS");
				}
			}
			;
			abstract void onAction(WidgetSyncmaticaServerPlacementEntry placement);
		}

	}

}
