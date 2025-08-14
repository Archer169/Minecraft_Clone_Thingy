package archer.textures;

public record Colour(float blue, float green, float red) {
    public static final Colour BLUE = new Colour(1, 0, 0);
    public static final Colour GREEN = new Colour(0, 1, 0);
    public static final Colour BROWN = new Colour(0, 0.25f, 0.55f);
    public static final Colour GREY = new Colour(0.5f, 0.5f, 0.5f);
}
