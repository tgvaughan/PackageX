/*
 * Copyright (C) 2014 Tim Vaughan <tgvaughan@gmail.com>
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

import beast.core.Distribution;
import beast.core.Input;
import beast.core.Input.Validate;
import beast.core.State;
import beast.evolution.tree.Node;
import beast.util.Randomizer;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Computes the probability density of the given tree under the chosen model.
 * 
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class TreeDensity extends Distribution {

    public Input<Model> modelInput = new Input<>(
        "mode", "Tree-generating model.", Validate.REQUIRED);

    public Input<TreeEventList> treeEventListInput = new Input<>(
        "treeEventList", "Tree event list.", Validate.REQUIRED);

    public Input<Integer> nParticlesInput = new Input<>(
        "nParticles", "Number of particles to use in SMC calculation.",
        Validate.REQUIRED);

    private class ParticleState extends SystemState {
        public Map<TypedNode, Type> lineageTypes = new HashMap<>();
    }

    Model model;
    TreeEventList eventList;
    int nParticles;

    @Override
    public void initAndValidate() throws Exception {
        model = modelInput.get();
        eventList = treeEventListInput.get();
        nParticles = nParticlesInput.get();
    }

    @Override
    public double calculateLogP() throws Exception {
        logP = 0.0;

        List<Double> particleWeights = Lists.newArrayList();
        List<ParticleState> particleStates = Lists.newArrayList();
        List<ParticleState> particleStatesNew = Lists.newArrayList();
        
        // Initialize particles
        for (int p=0; p<nParticles; p++)
            particleStates.add(model.getInitialState());
        
        double t = 0.0;
        int k = 1;
        for (TreeEvent treeEvent : eventList.getEventList()) {
            
            // Update particles
            particleWeights.clear();
            double sumOfWeights = 0.0;
            for (int p=0; p<nParticles; p++) {
                
                double newWeight = updateParticle(particleStates.get(p), t, k, treeEvent);
                
                particleWeights.add(newWeight);
                sumOfWeights += newWeight;
            }
            
            // Update marginal likelihood estimate
            logP += Math.log(sumOfWeights/nParticles);
            
            if (!(sumOfWeights>0.0))
                return Double.NEGATIVE_INFINITY;
            
            // Sample particle with replacement
            particleStatesNew.clear();
            for (int p=0; p<nParticles; p++) {
                double u = Randomizer.nextDouble()*sumOfWeights;
                
                int pChoice;
                for (pChoice = 0; pChoice<nParticles; pChoice++) {
                    u -= particleWeights.get(pChoice);
                    if (u<0.0)
                        break;
                }
                
                if (pChoice == nParticles)
                    System.err.println("sumOfWeights: " + sumOfWeights);
                
                particleStatesNew.add(particleStates.get(pChoice).copy());
            }
            
            // Switch particleStates and particleStatesNew
            List<SystemState> temp = particleStates;
            particleStates = particleStatesNew;
            particleStatesNew = temp;
            
            // Update lineage counter
            if (!treeEvent.isLeaf)
                k += 1;
            else
                k -= treeEvent.multiplicity;
            
            // Update start interval time
            t = treeEvent.time;
        } 

        return logP;
    }

    /**
     * Updates weight and state of particle.
     *
     * @param particleState
     * @param startTime
     * @param lineages
     * @param finalTreeEvent
     * @return conditional prob of tree interval under trajectory
     */
    private double updateParticle(ParticleState particleState,
        double startTime, int lineages, TreeEvent finalTreeEvent) {
        double conditionalP = 1.0;

        return conditionalP;
    }

    @Override
    public List<String> getArguments() {
        return null;
    }

    @Override
    public List<String> getConditions() {
        return null;
    }

    /**
     * I don't like this method of drawing a sample, as it doesn't allow users
     * to specify _what_ should be initialized. (E.g. what if my state includes
     * multiple TypedTrees?)
     * 
     * Use RandomTypedTree instead.
     *
     * @param state
     * @param random
     */
    @Override
    public void sample(State state, Random random) {
    }
    
}
