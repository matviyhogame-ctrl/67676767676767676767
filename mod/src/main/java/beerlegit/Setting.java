package beerlegit;

public class Setting {
    public final String name;
    public double value;
    public final double min, max;
    public final boolean bool;

    public Setting(String name, double def, double min, double max) {
        this.name = name; this.value = def; this.min = min; this.max = max; this.bool = false;
    }
    public Setting(String name, boolean def) {
        this.name = name; this.value = def ? 1 : 0; this.min = 0; this.max = 1; this.bool = true;
    }
    public boolean on() { return value > 0.5; }
}
