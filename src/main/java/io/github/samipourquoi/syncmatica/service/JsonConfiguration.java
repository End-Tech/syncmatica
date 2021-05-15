package io.github.samipourquoi.syncmatica.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class JsonConfiguration implements IServiceConfiguration {

    public final JsonObject configuration;
    private Boolean wasError;

    public JsonConfiguration(final JsonObject configuration) {
        this.configuration = configuration;
        wasError = false;
    }

    @Override
    public void loadBoolean(final String key, final Consumer<Boolean> loader) {
        try {
            final JsonElement elem = configuration.get(key);
            if (elem != null) {
                loader.accept(elem.getAsBoolean());
            }
        } catch (final Exception ignored) {
            wasError = true;
        }
    }

    @Override
    public void saveBoolean(final String key, final Boolean value) {
        configuration.addProperty(key, value);
    }

    @Override
    public void loadInteger(final String key, final IntConsumer loader) {
        try {
            final JsonElement elem = configuration.get(key);
            if (elem != null) {
                loader.accept(elem.getAsInt());
            }
        } catch (final Exception ignored) {
            wasError = true;
        }
    }

    @Override
    public void saveInteger(final String key, final Integer value) {
        configuration.addProperty(key, value);
    }

    public Boolean hadError() {
        return wasError;
    }
}
