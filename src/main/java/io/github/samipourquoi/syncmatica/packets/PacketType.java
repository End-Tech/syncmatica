package io.github.samipourquoi.syncmatica.packets;

import net.minecraft.util.Identifier;

public enum PacketType {
	REGISTER("sycnmatica:register");

	public final Identifier IDENTIFIER;

	PacketType(String id) {
		this.IDENTIFIER = new Identifier(id);
	}
}
