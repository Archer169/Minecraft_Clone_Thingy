package archer.model;

import archer.textures.Colour;

public class Block {
    public int VAOID;
    public int VBOID;
    public int EBOID;

    public float[] VERTICES;

    public float[] blockAll(float posx, float posy, float posz, Colour colour) {

        VERTICES = new float[]{
                // positions         // colors
                posx - 0.5f, posy - 0.5f, posz - 0.5f, colour.red(), colour.green(), colour.blue(),
                posx + 0.5f, posy - 0.5f, posz - 0.5f,  colour.red(), colour.green(), colour.blue(),
                posx + 0.5f, posy + 0.5f, posz - 0.5f,  colour.red(), colour.green(), colour.blue(),
                posx - 0.5f, posy + 0.5f, posz - 0.5f,  colour.red(), colour.green(), colour.blue(),

                posx - 0.5f, posy - 0.5f, posz + 0.5f,  colour.red(), colour.green(), colour.blue(),
                posx + 0.5f, posy - 0.5f, posz + 0.5f,  colour.red(), colour.green(), colour.blue(),
                posx + 0.5f, posy + 0.5f, posz + 0.5f,  colour.red(), colour.green(), colour.blue(),
                posx - 0.5f, posy + 0.5f, posz + 0.5f,  colour.red(), colour.green(), colour.blue()
        };

        return VERTICES;
    }


    public int[] INDICES = {
            0, 1, 2, 2, 3, 0, // back
            4, 5, 6, 6, 7, 4, // front
            0, 4, 7, 7, 3, 0, // left
            1, 5, 6, 6, 2, 1, // right
            3, 2, 6, 6, 7, 3, // top
            0, 1, 5, 5, 4, 0  // bottom
    };

}
