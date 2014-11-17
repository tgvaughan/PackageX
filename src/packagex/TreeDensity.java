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
import java.util.ArrayList;
import java.util.Comparator;
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

    public Input<TypedTree> treeInput = new Input<>(
        "typedTree", "Typed phylogenetic tree.", Validate.REQUIRED);

    public Input<Integer> nParticlesInput = new Input<>(
        "nParticles", "Number of particles to use in SMC calculation.",
        Validate.REQUIRED);

    /**
     * Tolerance used when comparing times.
     */
    public static double TOLERANCE = 1e-10;

    /**
     * Determine whether ages of nodes a and b are equivalent to
     * within tolerance TOLERANCE.
     * 
     * @param a
     * @param b
     * @return equivalence
     */
    public static boolean nodesContemp(Node a, Node b) {
        return Math.abs(a.getHeight()-b.getHeight()) < TOLERANCE;
    }

    private class ParticleState extends SystemState {
        public Map<TypedNode, Type> lineageTypes = new HashMap<>();

        public ParticleState() { }

        public ParticleState(SystemState state, TypedNode node) {
            lineageTypes.put(node, node.getType());
            stateMap.putAll(state.stateMap);
        }

        public void assignFrom(ParticleState other) {
            stateMap.clear();
            stateMap.putAll(other.stateMap);

            lineageTypes.clear();
            lineageTypes.putAll(other.lineageTypes);
        }
    }

    Model model;
    TypedTree tree;
    int nParticles;

    @Override
    public void initAndValidate() throws Exception {
        model = modelInput.get();
        tree = treeInput.get();
        nParticles = nParticlesInput.get();
    }

    @Override
    public double calculateLogP() throws Exception {
        logP = 0.0;

        double[] particleWeights = new double[nParticles];
        ParticleState[] particleStates = new ParticleState[nParticles];
        ParticleState[] particleStatesNew = new ParticleState[nParticles];

        List<TypedNode> nodesInvolved = new ArrayList<>();
        
        // Initialize particles
        for (int p=0; p<nParticles; p++) {
            particleStates[p] = new ParticleState(model.getInitialState(),
                (TypedNode)tree.getRoot());
            particleStatesNew[p] = new ParticleState(model.getInitialState(),
                (TypedNode)tree.getRoot());
        }

        // Assemble sorted node list:
        List<Node> nodeList = Lists.newArrayList(tree.getNodesAsArray());
        nodeList.sort((Node o1, Node o2) -> {
            return (int)(o1.getHeight() - o2.getHeight());
        });
        
        double t = 0.0;
        int k = 1;
        for (int nidx=0; nidx<nodeList.size(); nidx++) {
            nodesInvolved.add((TypedNode)nodeList.get(nidx));

            if (nidx<nodeList.size()-1 &&
                    nodesContemp(nodeList.get(nidx+1), nodeList.get(nidx)))
                continue;
            
            // Update particles
            double sumOfWeights = 0.0;
            for (int p=0; p<nParticles; p++) {
                
                double newWeight = updateParticle(particleStates[p], t, k, nodesInvolved);
                
                particleWeights[p] = newWeight;
                sumOfWeights += newWeight;
            }
            
            // Update marginal likelihood estimate
            logP += Math.log(sumOfWeights/nParticles);
            
            if (!(sumOfWeights>0.0))
                return Double.NEGATIVE_INFINITY;
            
            // Sample particle with replacement
            for (int p=0; p<nParticles; p++) {
                double u = Randomizer.nextDouble()*sumOfWeights;
                
                int pChoice;
                for (pChoice = 0; pChoice<nParticles; pChoice++) {
                    u -= particleWeights[pChoice];
                    if (u<0.0)
                        break;
                }
                
                if (pChoice == nParticles)
                    System.err.println("sumOfWeights: " + sumOfWeights);
                
                particleStatesNew[p].assignFrom(particleStates[pChoice]);
            }
            
            // Switch particleStates and particleStatesNew
            ParticleState[] temp = particleStates;
            particleStates = particleStatesNew;
            particleStatesNew = temp;
            
            // Update lineage counter
            for (TypedNode node : nodesInvolved) {
                if (node.isLeaf()) {
                    k -= 1;
                } else {
                    k += 1;
                }
            }
           
            // Update start interval time
            t = model.getNodeTime(nodesInvolved.get(0));

            nodesInvolved.clear();
        } 

        return logP;
    }

    /**
     * Propagate particle over interval.
     * 
     * @param particleState State at the start of the interval.
     * @param startTime Time at the start of the interval.
     * @param lineages Number of ancestral lineages extant at the
     *                 start of the interval.
     * @param nodesInvolved List of nodes involved in the reaction
     *                      terminating the interval.
     * @return 
     */
    private double updateParticle(ParticleState particleState,
        double startTime, int lineages, List<TypedNode> nodesInvolved) {
        double conditionalP = 1.0;

        double t = startTime;
        double endTime = model.getNodeTime(nodesInvolved.get(0));

        while (true) {

            // Calculate reaction propensities
            model.calculatePropensities(particleState);
            
            // Increment time
            double dt;
            if (model.getTotalPropensity()>0.0)
                dt = Randomizer.nextExponential(model.getTotalPropensity());
            else
                dt = Double.POSITIVE_INFINITY;

            // Stop if t>endTime
            if (t+dt>endTime) {
                t = endTime;
                break;
            }

        }

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
