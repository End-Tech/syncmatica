package io.github.samipourquoi.syncmatica.service;

import io.github.samipourquoi.syncmatica.Context;

abstract class AbstractService implements IService {

    Context context;

    @Override
    public void setContext(final Context context) {
        this.context = context;
    }

    @Override
    public Context getContext() {
        return context;
    }
}
