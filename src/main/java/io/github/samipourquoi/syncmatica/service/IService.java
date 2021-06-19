package io.github.samipourquoi.syncmatica.service;

import io.github.samipourquoi.syncmatica.Context;

public interface IService {

    void setContext(Context context);

    Context getContext();

    void getDefaultConfiguration(IServiceConfiguration configuration);

    String getConfigKey();

    void configure(IServiceConfiguration configuration);

    void startup();

    void shutdown();
}
