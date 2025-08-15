package archer.world;

import archer.textures.Colour;

public class BlockData {
    public boolean solid;
    public Colour colour;

    public BlockData(boolean solid, Colour colour) {
        this.solid = solid;
        this.colour = colour;
    }
}
