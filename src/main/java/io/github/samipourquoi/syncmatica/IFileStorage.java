package io.github.samipourquoi.syncmatica;

import java.io.File;

import io.github.samipourquoi.syncmatica.communication.CommunicationManager;

public interface IFileStorage {
	public LocalLitematicState getLocalState(ServerPlacement placement);
	public File createLocalLitematic(ServerPlacement placement);
	public File getLocalLitematic(ServerPlacement placement);
	public void setCommunitcationManager(CommunicationManager man);
}
