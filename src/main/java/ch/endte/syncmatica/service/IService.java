package ch.endte.syncmatica.service;

import ch.endte.syncmatica.Context;

public interface IService {

    void setContext(Context context);

    Context getContext();

    void getDefaultConfiguration(IServiceConfiguration configuration);

    String getConfigKey();

    void configure(IServiceConfiguration configuration);

    void startup();

    void shutdown();
}
