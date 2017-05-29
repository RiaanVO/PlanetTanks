package com.riaanvo.planettanks.GameObjects;

/**
 * Created by riaanvo on 24/5/17.
 */

public abstract class LivingGameObject extends GameObject {
    protected int mHealth;
    protected boolean deathHandled = false;

    public void takeDamage(int amount) {
        if (mHealth >= 0) {
            mHealth -= amount;
        }
    }

    public boolean isDead() {
        return mHealth <= 0;
    }

    public void setHealth(int health) {
        mHealth = health;
    }

    public int getHealth() {
        return mHealth;
    }

    protected abstract void handelDeath();
}
