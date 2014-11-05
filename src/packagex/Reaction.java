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
import beast.core.parameter.RealParameter;

/**
 * A reaction involving individuals of different types.
 *
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class Reaction extends ProtoReaction {

    public Input<RealParameter> rateInput = new Input<>("rate",
        "Constant reaction rate", Validate.REQUIRED);

    /**
     * Calculate and retrieve reaction propensity for given state.
     * 
     * @param state
     * @return reaction propensity
     */
    public double getPropensity(SystemState state) {
        return getReactantPermutations(state)*rateInput.get().getValue();
    }
}
