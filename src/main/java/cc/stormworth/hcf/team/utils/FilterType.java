package cc.stormworth.hcf.team.utils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum FilterType {

    HIGHEST_ONLINE("Teams with highest online members"),
    LOWEST_ONLINE("Teams with lowest online members"),
    HIGHEST_DTR("Teams with highest DTR"),
    LOWEST_DTR("Teams with lowest DTR");

    private final String name;

    public String getName() {
        return name;
    }
}