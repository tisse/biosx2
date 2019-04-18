package tisse.service.holder;

import org.slf4j.Logger;

import javax.annotation.PostConstruct;

public abstract class BaseHolder {

    @PostConstruct
    private void init() {
        refresh();
    }

    public abstract void refresh();

    protected abstract Logger getLogger();
}
