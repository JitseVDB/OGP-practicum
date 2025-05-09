public enum ArmorType {
    TIN(70),
    BRONZE(90);

    private final int maxProtection;

    ArmorType(int maxProtection) {
        this.maxProtection = maxProtection;
    }

    public int getMaxProtection() {
        return maxProtection;
    }
}
