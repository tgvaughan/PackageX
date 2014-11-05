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
import beast.util.Randomizer;

/**
 *
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class MultiReaction extends ProtoReaction {

    public Input<RealParameter> reactionProbabilityInput = new Input<>(
        "reactionProbability",
        "Probability that reaction will fire between any"
            + "particular tuple of reactants.", Validate.REQUIRED);

    public Input<RealParameter> reactionTimeInput = new Input<>(
        "reactionTime", "Time at which reaction fires.", Validate.REQUIRED);

    public double getReactionTime() {
        return reactionTimeInput.get().getValue();
    }

    public double getReactionProbability() {
        return reactionProbabilityInput.get().getValue();
    }

    @Override
    public void incrementState(SystemState state) {

        int perms = (int)Math.round(getReactantPermutations(state));
        double p = reactionProbabilityInput.get().getValue();

        for (int i=0; i<perms; i++) {
            if (Randomizer.nextDouble()<p)
                super.incrementState(state);
            
        }

    }
    
}
