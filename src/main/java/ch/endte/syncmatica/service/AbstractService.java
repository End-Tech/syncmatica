package ch.endte.syncmatica.service;

import ch.endte.syncmatica.Context;

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
