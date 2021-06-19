package io.github.samipourquoi.syncmatica.service;

import io.github.samipourquoi.syncmatica.Syncmatica;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;

public class DebugService extends AbstractService {

    private boolean doPacketLogging = false;

    public void logReceivePacket(final Identifier packageType) {
        if (doPacketLogging) {
            LogManager.getLogger(Syncmatica.class).info("Syncmatica - received packet:[type={}]", packageType);
        }
    }

    public void logSendPacket(final Identifier packetType, final String targetIdentifier) {
        if (doPacketLogging) {
            LogManager.getLogger(Syncmatica.class).info(
                    "Sending packet[type={}] to ExchangeTarget[id={}]",
                    packetType,
                    targetIdentifier
            );
        }
    }

    @Override
    public void getDefaultConfiguration(final IServiceConfiguration configuration) {
        configuration.saveBoolean("doPackageLogging", false);
    }

    @Override
    public String getConfigKey() {
        return "debug";
    }

    @Override
    public void configure(final IServiceConfiguration configuration) {
        configuration.loadBoolean("doPackageLogging", b -> doPacketLogging = b);
    }

    @Override
    public void startup() { //NOSONAR
    }

    @Override
    public void shutdown() { //NOSONAR
    }
}
