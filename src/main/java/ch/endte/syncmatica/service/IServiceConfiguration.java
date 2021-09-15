package ch.endte.syncmatica.service;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface IServiceConfiguration {

    void loadBoolean(String key, Consumer<Boolean> loader);

    void saveBoolean(String key, Boolean value);

    void loadInteger(String key, IntConsumer loader);

    void saveInteger(String key, Integer value);
}
