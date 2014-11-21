/*
 * Copyright (C) 2014 Tim Vaughan (tgvaughan@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package packagex;

import beast.core.Input;
import beast.core.Input.Validate;

/**
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class RandomTypedTree extends ReactionTree {

    public Input<Model> modelInput = new Input<>(
        "model", "Model used in simulation", Validate.REQUIRED);

    public RandomTypedTree() { }

    @Override
    public void initAndValidate() {
        simulate();
    }

    /**
     * Simulate new tree from model provided.
     */
    private void simulate() {

        // Forward-time simulation to generate trajectory:
    }
    
}
