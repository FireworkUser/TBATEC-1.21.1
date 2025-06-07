package net.firework.tbatec;

public enum Race {
    HUMAN("Human", 0x8B4513, "Balanced race with no special abilities"),
    ELF("Elf", 0x228B22, "Agile and magical, bonus to archery and magic"),
    DWARF("Dwarf", 0x696969, "Strong and sturdy, bonus to mining and crafting");

    private final String displayName;
    private final int color;
    private final String description;

    Race(String displayName, int color, String description) {
        this.displayName = displayName;
        this.color = color;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public int getColor() { return color; }
    public String getDescription() { return description; }
}