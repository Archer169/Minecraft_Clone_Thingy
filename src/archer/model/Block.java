package archer.model;

import archer.textures.Colour;

public class Block {
    public int VAOID;
    public int VBOID;
    public int EBOID;

    public float[] VERTICES;

    // Position of this block center
    private float posX, posY, posZ;

    // Bounding box extents for collision
    private float minX, maxX;
    private float minY, maxY;
    private float minZ, maxZ;

    public Block() {
        // Default constructor if needed
    }

    public float[] blockAll(float posx, float posy, float posz, Colour colour) {
        this.posX = posx;
        this.posY = posy;
        this.posZ = posz;

        // Define bounding box extents based on center and half-size (0.5)
        this.minX = posx - 0.5f;
        this.maxX = posx + 0.5f;
        this.minY = posy - 0.5f;
        this.maxY = posy + 0.5f;
        this.minZ = posz - 0.5f;
        this.maxZ = posz + 0.5f;

        float r = colour.red();
        float g = colour.green();
        float b = colour.blue();

        VERTICES = new float[]{
                // positions           // colors           // normals
                // Back face (normal 0,0,-1)
                posx - 0.5f, posy - 0.5f, posz - 0.5f,  r, g, b,  0f, 0f, -1f,
                posx + 0.5f, posy - 0.5f, posz - 0.5f,  r, g, b,  0f, 0f, -1f,
                posx + 0.5f, posy + 0.5f, posz - 0.5f,  r, g, b,  0f, 0f, -1f,
                posx - 0.5f, posy + 0.5f, posz - 0.5f,  r, g, b,  0f, 0f, -1f,

                // Front face (normal 0,0,1)
                posx - 0.5f, posy - 0.5f, posz + 0.5f,  r, g, b,  0f, 0f, 1f,
                posx + 0.5f, posy - 0.5f, posz + 0.5f,  r, g, b,  0f, 0f, 1f,
                posx + 0.5f, posy + 0.5f, posz + 0.5f,  r, g, b,  0f, 0f, 1f,
                posx - 0.5f, posy + 0.5f, posz + 0.5f,  r, g, b,  0f, 0f, 1f,

                // Left face (normal -1,0,0)
                posx - 0.5f, posy - 0.5f, posz - 0.5f,  r, g, b,  -1f, 0f, 0f,
                posx - 0.5f, posy + 0.5f, posz - 0.5f,  r, g, b,  -1f, 0f, 0f,
                posx - 0.5f, posy + 0.5f, posz + 0.5f,  r, g, b,  -1f, 0f, 0f,
                posx - 0.5f, posy - 0.5f, posz + 0.5f,  r, g, b,  -1f, 0f, 0f,

                // Right face (normal 1,0,0)
                posx + 0.5f, posy - 0.5f, posz - 0.5f,  r, g, b,  1f, 0f, 0f,
                posx + 0.5f, posy + 0.5f, posz - 0.5f,  r, g, b,  1f, 0f, 0f,
                posx + 0.5f, posy + 0.5f, posz + 0.5f,  r, g, b,  1f, 0f, 0f,
                posx + 0.5f, posy - 0.5f, posz + 0.5f,  r, g, b,  1f, 0f, 0f,

                // Top face (normal 0,1,0)
                posx - 0.5f, posy + 0.5f, posz - 0.5f,  r, g, b,  0f, 1f, 0f,
                posx + 0.5f, posy + 0.5f, posz - 0.5f,  r, g, b,  0f, 1f, 0f,
                posx + 0.5f, posy + 0.5f, posz + 0.5f,  r, g, b,  0f, 1f, 0f,
                posx - 0.5f, posy + 0.5f, posz + 0.5f,  r, g, b,  0f, 1f, 0f,

                // Bottom face (normal 0,-1,0)
                posx - 0.5f, posy - 0.5f, posz - 0.5f,  r, g, b,  0f, -1f, 0f,
                posx + 0.5f, posy - 0.5f, posz - 0.5f,  r, g, b,  0f, -1f, 0f,
                posx + 0.5f, posy - 0.5f, posz + 0.5f,  r, g, b,  0f, -1f, 0f,
                posx - 0.5f, posy - 0.5f, posz + 0.5f,  r, g, b,  0f, -1f, 0f
        };

        return VERTICES;
    }

    public float[] getFaceVertices(int faceIndex, float posx, float posy, float posz, Colour colour) {
        float r = colour.red();
        float g = colour.green();
        float b = colour.blue();

        switch(faceIndex) {
            case 0: // back face
                return new float[]{
                        posx - 0.5f, posy - 0.5f, posz - 0.5f, r, g, b, 0f, 0f, -1f,
                        posx + 0.5f, posy - 0.5f, posz - 0.5f, r, g, b, 0f, 0f, -1f,
                        posx + 0.5f, posy + 0.5f, posz - 0.5f, r, g, b, 0f, 0f, -1f,
                        posx - 0.5f, posy + 0.5f, posz - 0.5f, r, g, b, 0f, 0f, -1f
                };
            case 1: // front face
                return new float[]{
                        posx - 0.5f, posy - 0.5f, posz + 0.5f, r, g, b, 0f, 0f, 1f,
                        posx + 0.5f, posy - 0.5f, posz + 0.5f, r, g, b, 0f, 0f, 1f,
                        posx + 0.5f, posy + 0.5f, posz + 0.5f, r, g, b, 0f, 0f, 1f,
                        posx - 0.5f, posy + 0.5f, posz + 0.5f, r, g, b, 0f, 0f, 1f
                };
            case 2: // left face
                return new float[]{
                        posx - 0.5f, posy - 0.5f, posz - 0.5f, r, g, b, -1f, 0f, 0f,
                        posx - 0.5f, posy + 0.5f, posz - 0.5f, r, g, b, -1f, 0f, 0f,
                        posx - 0.5f, posy + 0.5f, posz + 0.5f, r, g, b, -1f, 0f, 0f,
                        posx - 0.5f, posy - 0.5f, posz + 0.5f, r, g, b, -1f, 0f, 0f
                };
            case 3: // right face
                return new float[]{
                        posx + 0.5f, posy - 0.5f, posz - 0.5f, r, g, b, 1f, 0f, 0f,
                        posx + 0.5f, posy + 0.5f, posz - 0.5f, r, g, b, 1f, 0f, 0f,
                        posx + 0.5f, posy + 0.5f, posz + 0.5f, r, g, b, 1f, 0f, 0f,
                        posx + 0.5f, posy - 0.5f, posz + 0.5f, r, g, b, 1f, 0f, 0f
                };
            case 4: // top face
                return new float[]{
                        posx - 0.5f, posy + 0.5f, posz - 0.5f, r, g, b, 0f, 1f, 0f,
                        posx + 0.5f, posy + 0.5f, posz - 0.5f, r, g, b, 0f, 1f, 0f,
                        posx + 0.5f, posy + 0.5f, posz + 0.5f, r, g, b, 0f, 1f, 0f,
                        posx - 0.5f, posy + 0.5f, posz + 0.5f, r, g, b, 0f, 1f, 0f
                };
            case 5: // bottom face
                return new float[]{
                        posx - 0.5f, posy - 0.5f, posz - 0.5f, r, g, b, 0f, -1f, 0f,
                        posx + 0.5f, posy - 0.5f, posz - 0.5f, r, g, b, 0f, -1f, 0f,
                        posx + 0.5f, posy - 0.5f, posz + 0.5f, r, g, b, 0f, -1f, 0f,
                        posx - 0.5f, posy - 0.5f, posz + 0.5f, r, g, b, 0f, -1f, 0f
                };
        }
        throw new IllegalArgumentException("Invalid face index");
    }

    public int[] INDICES = {
            0, 1, 2, 2, 3, 0,         // back
            4, 5, 6, 6, 7, 4,         // front
            8, 9, 10, 10, 11, 8,      // left
            12, 13, 14, 14, 15, 12,   // right
            16, 17, 18, 18, 19, 16,   // top
            20, 21, 22, 22, 23, 20    // bottom
    };

    public static final int[] FACE_INDICES = {
            0, 1, 2, 2, 3, 0
    };

    public boolean isSolid() {
        // You could make some block types non-solid later
        return true;
    }
}
