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

import beast.core.BEASTObject;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.parameter.RealParameter;
import beast.evolution.tree.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A model, involving individual types and reactions between those types.
 * 
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class Model extends BEASTObject {

    public Input<RealParameter> originInput = new Input<>("timeOfOrigin",
        "Time of the origin of the process.",
        Validate.REQUIRED);

    public Input<List<Type>> typesInput = new Input<>("type",
        "Specifies a type in the model.", new ArrayList<>());

    public Input<List<Reaction>> reactionsInput = new Input<>("reaction",
        "Specifies a reaction in the model.", new ArrayList<>());

    public Input<List<PopulationSize>> initialPopSizesInput = new Input<>(
        "initialPopSize", "Initial population size", new ArrayList<>());

    public Input<List<Type>> originTypeInput = new Input<>("originType",
            "Type of ancestral lineage at origin.", new ArrayList<>());

    Map<Reaction, Double> reactionPropensities = new HashMap<>();
    double totalReactionPropensity;

    @Override
    public void initAndValidate() throws Exception {
        if (originTypeInput.get().isEmpty()) {
            throw new IllegalArgumentException("Must specify at least one"
                    + " originType.");
        }
    }

    /**
     * @return a copy of the initial system state.
     */
    public SystemState getInitialState() {
        SystemState initialState = new SystemState();

        for (PopulationSize popSize : initialPopSizesInput.get())
            initialState.put(popSize.getType(), popSize.getSize());

        return initialState;
    }

    /**
     * Calculate reaction propensities under given state.
     * 
     * @param state system state
     */
    public void calculatePropensities(SystemState state) {
        reactionPropensities.clear();
        totalReactionPropensity = 0.0;
        for (Reaction react : reactionsInput.get()) {
            double thisProp = react.getPropensity(state);
            reactionPropensities.put(react, thisProp);
            totalReactionPropensity += thisProp;
        }
    }

    /**
     * @return previously computed reaction propensities.
     */
    public Map<Reaction, Double> getPropensities() {
        return reactionPropensities;
    }

    /**
     * @return total of previously computed reaction propensities.
     */
    public double getTotalPropensity() {
        return totalReactionPropensity;
    }

    /**
     * Obtain (forward) time of node relative to the model origin.
     * 
     * @param node
     * @return node time
     */
    public double getNodeTime(Node node) {
        return originInput.get().getValue() - node.getHeight();
    }
}
