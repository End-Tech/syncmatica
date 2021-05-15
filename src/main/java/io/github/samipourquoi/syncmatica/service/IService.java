package io.github.samipourquoi.syncmatica.service;

import io.github.samipourquoi.syncmatica.Context;

public interface IService {

    public void setContext(Context context);

    public Context getContext();

    public void getDefaultConfiguration(IServiceConfiguration configuration);

    public String getConfigKey();

    public void startup(IServiceConfiguration configuration);

    public void shutdown();
}
