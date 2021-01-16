package io.github.samipourquoi.syncmatica.communication.exchange;

import io.github.samipourquoi.syncmatica.Context;
import io.github.samipourquoi.syncmatica.communication.ExchangeTarget;
import io.github.samipourquoi.syncmatica.communication.FeatureSet;
import io.github.samipourquoi.syncmatica.communication.PacketType;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class FeatureExchange extends AbstractExchange {
	
	boolean request = false;

	public FeatureExchange(ExchangeTarget partner, Context con) {
		super(partner, con);
	}
	
	public FeatureExchange(ExchangeTarget partner, Context con, boolean request) {
		this(partner, con);
		this.request = request;
	}
	
	@Override
	public boolean checkPacket(Identifier id, PacketByteBuf packetBuf) {
		return id.equals(PacketType.FEATURE_REQUEST.IDENTIFIER)
				||id.equals(PacketType.FEATURE.IDENTIFIER);
	}

	@Override
	public void handle(Identifier id, PacketByteBuf packetBuf) {
		if (id.equals(PacketType.FEATURE_REQUEST.IDENTIFIER)) {
			sendFeatures();
		} else if (id.equals(PacketType.FEATURE.IDENTIFIER)) {
			FeatureSet fs = FeatureSet.fromString(packetBuf.readString(32767));
			getPartner().setFeatureSet(fs);
			onFeatureSetReceive();
		}
	}
	
	protected void onFeatureSetReceive() {
		succeed();
	};

	public void requestFeatureSet() {
		getPartner().sendPacket(PacketType.FEATURE_REQUEST.IDENTIFIER, new PacketByteBuf(Unpooled.buffer()));
	}
	
	private void sendFeatures() {
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		FeatureSet fs = getContext().getFeatureSet();
		buf.writeString(fs.toString(), 32767);
		getPartner().sendPacket(PacketType.FEATURE.IDENTIFIER, buf);
	}

	@Override
	public void init() {
		if (request) {
			requestFeatureSet();
		} else {
			sendFeatures(); // does that really need encapsulation in this class?
			succeed(); // even had to add special handling to this case which might never occur
		}
	}

	@Override
	protected void sendCancelPacket() {}
	
}
