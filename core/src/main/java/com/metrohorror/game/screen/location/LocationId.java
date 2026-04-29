package com.metrohorror.game.screen.location;

public enum LocationId {
    BEDROOM(BedroomLocation.INDEX, BedroomLocation.DISPLAY_NAME),
    CHAPEL(ChapelLocation.INDEX, ChapelLocation.DISPLAY_NAME),
    DUNGEON(DungeonLocation.INDEX, DungeonLocation.DISPLAY_NAME),
    SHANTY(ShantyLocation.INDEX, ShantyLocation.DISPLAY_NAME),
    BOSS(BossLocation.INDEX, BossLocation.DISPLAY_NAME);

    private final int index;
    private final String displayName;

    LocationId(int index, String displayName) {
        this.index = index;
        this.displayName = displayName;
    }

    public int getIndex() {
        return index;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static LocationId fromIndex(int index) {
        for (LocationId location : values()) {
            if (location.index == index) {
                return location;
            }
        }
        return BEDROOM;
    }
}
