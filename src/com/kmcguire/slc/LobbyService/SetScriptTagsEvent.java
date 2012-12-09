package com.kmcguire.slc.LobbyService;

import java.util.Map;

public class SetScriptTagsEvent extends Event {
    private Map<String, String>         tags;

    public SetScriptTagsEvent(Map<String, String> tags) {
        this.tags = tags;
    }

    public Map<String, String> getTags() {
        return tags;
    }
}
