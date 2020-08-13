package com.virtualparticle.mc.mckoth;

import I18n.I18n;

public class McKothException extends Throwable {

    protected static final I18n i18n = I18n.getInstance();

    public McKothException(String message) {
        super(message);
    }

}
