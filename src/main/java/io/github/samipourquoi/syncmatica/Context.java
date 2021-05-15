package io.github.samipourquoi.syncmatica;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.samipourquoi.syncmatica.communication.CommunicationManager;
import io.github.samipourquoi.syncmatica.communication.FeatureSet;
import io.github.samipourquoi.syncmatica.service.IService;
import io.github.samipourquoi.syncmatica.service.JsonConfiguration;
import io.github.samipourquoi.syncmatica.service.QuotaService;

import java.io.*;
import java.util.Arrays;

public class Context {

    private final IFileStorage files;
    private final CommunicationManager comMan;
    private final SyncmaticManager synMan;
    private FeatureSet fs = null;
    private final boolean server;
    private final File litematicFolder;
    private boolean isStarted = false;
    private final QuotaService quota;

    public Context(final IFileStorage fs, final CommunicationManager comMan, final SyncmaticManager synMan, final boolean isServer, final File litematicFolder) {
        files = fs;
        fs.setContext(this);
        this.comMan = comMan;
        comMan.setContext(this);
        this.synMan = synMan;
        synMan.setContext(this);
        server = isServer;
        if (isServer) {
            quota = new QuotaService();
        } else {
            quota = null;
        }
        this.litematicFolder = litematicFolder;
        litematicFolder.mkdirs();
    }

    public IFileStorage getFileStorage() {
        return files;
    }

    public CommunicationManager getCommunicationManager() {
        return comMan;
    }

    public SyncmaticManager getSyncmaticManager() {
        return synMan;
    }

    public QuotaService getQuotaService() {
        return quota;
    }

    public FeatureSet getFeatureSet() {
        if (fs == null) {
            generateFeatureSet();
        }
        return fs;
    }

    public boolean isServer() {
        return server;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public File getLitematicFolder() {
        return litematicFolder;
    }

    private void generateFeatureSet() {
        fs = new FeatureSet(Arrays.asList(Feature.values()));
    }

    public void startup() {
        loadConfiguration();
        isStarted = true;
        synMan.startup();
    }

    public void shutdown() {
        isStarted = false;
        synMan.shutdown();
    }

    public boolean checkPartnerVersion(final String version) {
        return !version.equals("0.0.1");
    }

    public File getConfigFolder() {
        return new File(new File("."), "config" + File.separator + Syncmatica.MOD_ID);
    }

    public File getConfigFile() {
        return new File(getConfigFolder(), "config.json");
    }

    public void loadConfiguration() {
        boolean attemptToLoad = false;
        JsonObject configuration;
        try {
            configuration = new Gson().fromJson(new BufferedReader(new FileReader(getConfigFile())), JsonObject.class);
            attemptToLoad = true;
        } catch (final Exception ignored) {
            configuration = new JsonObject();
        }
        boolean needsRewrite = false;
        if (isServer()) {
            needsRewrite = loadConfigurationForService(quota, configuration, attemptToLoad);
        }
        if (needsRewrite) {
            try (
                    final Writer writer = new BufferedWriter(new FileWriter(getConfigFile()));
            ) {
                final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                final String jsonString = gson.toJson(configuration);
                writer.write(jsonString);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Boolean loadConfigurationForService(final IService service, final JsonObject configuration, final boolean attemptToLoad) {
        final String configKey = service.getConfigKey();
        JsonObject serviceJson = null;
        JsonConfiguration serviceConfiguration = null;
        boolean started = false;

        if (attemptToLoad && configuration.has(configKey)) {
            try {
                serviceJson = configuration.getAsJsonObject(configKey);
                if (serviceJson != null) {
                    serviceConfiguration = new JsonConfiguration(serviceJson);
                    service.startup(serviceConfiguration);
                    started = true;
                    if (!serviceConfiguration.hadError()) {
                        return false;
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        if (serviceJson == null) {
            serviceJson = new JsonObject();
            configuration.add(configKey, serviceJson);
        }
        if (serviceConfiguration == null) {
            serviceConfiguration = new JsonConfiguration(serviceJson);
        }
        service.getDefaultConfiguration(serviceConfiguration);
        if (!started) {
            service.startup(serviceConfiguration);
        }
        return true;
    }
}
