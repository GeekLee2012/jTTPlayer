package xyz.rive.jttplayer.manager;

import xyz.rive.jttplayer.ApplicationContext;

public class AbstractManager {
    private final ApplicationContext context;

    public AbstractManager(ApplicationContext context) {
        this.context = context;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public String getWorkPath() {
        return context.getWorkPath();
    }
}
