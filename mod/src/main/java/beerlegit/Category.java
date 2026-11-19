package beerlegit;

public enum Category {
    COMBAT("Combat"), MOVEMENT("Movement"), PLAYER("Player"), RENDER("Render"), MISC("Misc"), CONFIGS("Configs");
    public final String label;
    Category(String label) { this.label = label; }
}
