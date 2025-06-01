package pepse.world.daynight;

import danogl.GameObject;
import pepse.world.clouds.Cloud;

public class SkyManager {
    private final GameObject sun;
    private final Cloud cloud;

    public SkyManager(GameObject sun, Cloud cloud) {
        this.sun = sun;
        this.cloud = cloud;
    }

    public void update(float avatarX) {
        float fixedSunY = sun.getCenter().y();
//        float fixedCloudY = cloud.getCenter().y();

        sun.setCenter(new danogl.util.Vector2(avatarX, fixedSunY));
//        cloud.setCenter(new danogl.util.Vector2(avatarX, fixedCloudY));
    }
}

