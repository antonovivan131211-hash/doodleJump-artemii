package io.github.some_example_name.Managers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.objects.DoodleObject;
import io.github.some_example_name.objects.PlateObject;
import io.github.some_example_name.objects.BulletObject;
import io.github.some_example_name.objects.EnemyObject;

public class ContactManager implements ContactListener {

    World world;
    private DoodleObject doodle;
    private MyGdxGame game;

    public ContactManager(World world, DoodleObject doodle, MyGdxGame game) {
        this.world = world;
        this.doodle = doodle;
        this.game = game;

        world.setContactListener(this);
    }

    @Override
    public void beginContact(Contact contact) {
        Object objA = contact.getFixtureA().getUserData();
        Object objB = contact.getFixtureB().getUserData();

        handleContact(objA, objB, true);
    }

    @Override
    public void endContact(Contact contact) {
        Object objA = contact.getFixtureA().getUserData();
        Object objB = contact.getFixtureB().getUserData();

        handleContact(objA, objB, false);
    }

    private void handleContact(Object objA, Object objB, boolean begin) {
        if (objA instanceof DoodleObject && objB instanceof PlateObject) {
            handlePlatformContact((DoodleObject) objA, (PlateObject) objB, begin);
        } else if (objB instanceof DoodleObject && objA instanceof PlateObject) {
            handlePlatformContact((DoodleObject) objB, (PlateObject) objA, begin);
        }

        if (begin) {
            if (objA instanceof BulletObject && objB instanceof EnemyObject) {
                handleBulletEnemyContact((BulletObject) objA, (EnemyObject) objB);
            } else if (objB instanceof BulletObject && objA instanceof EnemyObject) {
                handleBulletEnemyContact((BulletObject) objB, (EnemyObject) objA);
            }

            if (objA instanceof DoodleObject && objB instanceof EnemyObject) {
                handleDoodleEnemyContact((DoodleObject) objA, (EnemyObject) objB);
            } else if (objB instanceof DoodleObject && objA instanceof EnemyObject) {
                handleDoodleEnemyContact((DoodleObject) objB, (EnemyObject) objA);
            }
        }
    }

    private void handlePlatformContact(DoodleObject doodle, PlateObject platform, boolean begin) {
        if (begin) {
            float doodleVelocityY = doodle.body.getLinearVelocity().y;

            if (doodleVelocityY <= 2f) {
                doodle.setOnPlatform(true);
            }
        } else {
            doodle.setOnPlatform(false);
        }
    }

    private void handleBulletEnemyContact(BulletObject bullet, EnemyObject enemy) {
        enemy.hit(2);

        bullet.setHasToBeDestroyed(true);

        if (enemy.getHealth() <= 0 && game != null && game.achievementManager != null) {
            game.achievementManager.unlockAchievement("first_kill");
        }
    }

    private void handleDoodleEnemyContact(DoodleObject doodle, EnemyObject enemy) {
        doodle.die();
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Object objA = contact.getFixtureA().getUserData();
        Object objB = contact.getFixtureB().getUserData();

        if ((objA instanceof DoodleObject && objB instanceof PlateObject) ||
            (objB instanceof DoodleObject && objA instanceof PlateObject)) {

            DoodleObject doodle = objA instanceof DoodleObject ? (DoodleObject) objA : (DoodleObject) objB;
            PlateObject platform = objA instanceof PlateObject ? (PlateObject) objA : (PlateObject) objB;

            float doodleY = doodle.getY();
            float platformY = platform.getY();
            float doodleHeight = doodle.height;
            float platformHeight = platform.height;

            float doodleBottom = doodleY - doodleHeight / 2f;
            float platformTop = platformY + platformHeight / 2f;
            float doodleVelocityY = doodle.body.getLinearVelocity().y;

            boolean isAbovePlatform = doodleBottom >= platformTop - 10f;
            boolean isFalling = doodleVelocityY <= 1f;

            boolean shouldCollide = isAbovePlatform && isFalling;

            contact.setEnabled(shouldCollide);
        }

        if ((objA instanceof DoodleObject && objB instanceof EnemyObject) ||
            (objB instanceof DoodleObject && objA instanceof EnemyObject)) {
            contact.setEnabled(false);
        }

        if ((objA instanceof BulletObject && objB instanceof EnemyObject) ||
            (objB instanceof BulletObject && objA instanceof EnemyObject)) {
            contact.setEnabled(true);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
