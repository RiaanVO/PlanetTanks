/*
 * Copyright (C) 2017 Riaan Van Onselen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.riaanvo.planettanks.GameObjects;

/**
 * This class adds a basic health system onto the game object superclass. Provides methods to take
 * damage and handle an objects death
 */

public abstract class LivingGameObject extends GameObject {
    protected int mHealth;
    protected boolean mDeathHandled = false;

    /**
     * Applies damage to the health stored in the game object
     *
     * @param amount of damage to inflict
     */
    public void takeDamage(int amount) {
        //Apply damage if health is > 0
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

    /**
     * Used to remove references from the game managers and run any ending code for the game object
     */
    protected abstract void handelDeath();
}
