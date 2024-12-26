package com.mysaml.mc.manager;

import com.mysaml.mc.api.MySamlAddon;
import com.mysaml.mc.manager.remote.Remote;

public class Main extends MySamlAddon {
    public Remote remote = null;
    @Override
    public void onDisabled() {
        if (remote != null) {
            remote.disconnect();
            remote = null;
        }
    }

    @Override
    public void onEnabled() {
        remote = new Remote();
        addEventListener("player events", new PlayerEvents(remote));
    }
}