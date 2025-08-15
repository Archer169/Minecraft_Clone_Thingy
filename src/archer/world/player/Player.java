package archer.world.player;

import archer.world.Camera;
import org.joml.Vector3f;
import archer.world.World;

public class Player {
    public Vector3f position;
    public Vector3f velocity;

    public float width = 0f;
    public float height = 1.333333f;

    private boolean onGround = false;

    private final float walkSpeed = 4.3f;     // m/s
    private final float sprintSpeed = 8.6f;   // m/s
    private final float jumpStrength = 5f;
    private final float gravity = -9.81f;// in degrees, 0 means facing -Z

    public Player(Vector3f startPosition) {
        this.position = new Vector3f(startPosition);
        this.velocity = new Vector3f();
    }

    public void update(float deltaTime, World world, boolean forward, boolean back, boolean left, boolean right, boolean jump, boolean sprint) {
        float speed = sprint ? sprintSpeed : walkSpeed;

        float yawRadians = (float) Math.toRadians(Camera.yaw);

// Forward vector in XZ plane
        Vector3f forwardVec = new Vector3f((float)Math.cos(yawRadians), 0, (float)Math.sin(yawRadians));
// Right vector (perpendicular)
        Vector3f rightVec = new Vector3f(forwardVec.z, 0, -forwardVec.x);

        Vector3f moveDir = new Vector3f();
        if (forward) moveDir.add(forwardVec);
        if (back)    moveDir.sub(forwardVec);
        if (left)    moveDir.sub(rightVec);
        if (right)   moveDir.add(rightVec);

        if (moveDir.lengthSquared() > 0) {
            moveDir.normalize().mul(speed * deltaTime);
        }

// Apply horizontal movement
        tryMove(moveDir.x, 0, moveDir.z, world);


// Apply gravity
        velocity.y += gravity * deltaTime;

// Jump
        if (jump && onGround) {
            velocity.y = jumpStrength;
            onGround = false;
        }

// Apply vertical movement
        tryMove(0, velocity.y * deltaTime, 0, world);

// Ground check
        if (velocity.y < 0 && isColliding(position.x, position.y - 0.01f, position.z, world)) {
            velocity.y = 0;
            onGround = true;
        }

    }

    private void tryMove(float dx, float dy, float dz, World world) {
        // Move X
        if (!isColliding(position.x + dx, position.y, position.z, world)) {
            position.x += dx;
        } else {
            velocity.x = 0;
        }

        // Move Y
        if (!isColliding(position.x, position.y + dy, position.z, world)) {
            position.y += dy;
        } else {
            velocity.y = 0;
        }

        // Move Z
        if (!isColliding(position.x, position.y, position.z + dz, world)) {
            position.z += dz;
        } else {
            velocity.z = 0;
        }
    }

    private boolean isColliding(float x, float y, float z, World world) {
        int minX = (int) Math.floor(x - width / 2);
        int maxX = (int) Math.floor(x + width / 2);
        int minY = (int) Math.floor(y);
        int maxY = (int) Math.floor(y + height);
        int minZ = (int) Math.floor(z - width / 2);
        int maxZ = (int) Math.floor(z + width / 2);

        for (int bx = minX; bx <= maxX; bx++) {
            for (int by = minY; by <= maxY; by++) {
                for (int bz = minZ; bz <= maxZ; bz++) {
                    if (world.isBlockSolid(bx, by, bz)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
