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
import beast.core.parameter.IntegerParameter;
import beast.core.parameter.RealParameter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A reaction involving individuals of different types.
 *
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class Reaction extends BEASTObject {

    public Input<List<Type>> reactantsInput = new Input<>("reactant",
        "Reactant type", new ArrayList<>());

    public Input<List<Type>> productsInput = new Input<>("product",
        "Product type", new ArrayList<>());

    public Input<List<Integer>> p2rMapInput =
        new Input<>("p2rMap", "Product to reactant map.", new ArrayList<>());

    public Input<RealParameter> rateInput = new Input<>("rate",
        "Constant reaction rate");

    public class Nodule {
        public Type type;

        public Type getType() {
            return type;
        }
    }

    public class ReactantNodule extends Nodule {
        public Set<ProductNodule> children = new HashSet<>();

        public ReactantNodule(Type type) {
            this.type = type;
        }

        public void addChild(ProductNodule child) {
            children.add(child);
            child.setParent(this);
        }

        public Set<ProductNodule> getChildren() {
            return children;
        }
    }

    public class ProductNodule extends Nodule {
        public ReactantNodule parent = null;

        public ProductNodule(Type type) {
            this.type = type;
        }

        public void setParent(ReactantNodule parent) {
            this.parent = parent;
        }

        public ReactantNodule getParent() {
            return parent;
        }
    }

    protected List<ReactantNodule> reactantNodules = new ArrayList<>();
    protected List<ProductNodule> productNodules = new ArrayList<>();

    protected Multiset<Type> reactants = HashMultiset.create();
    protected Multiset<Type> products = HashMultiset.create();
    protected Map<Type, Integer> deltas = new HashMap<>();

    public enum ReactionKind { COALESCENCE, SAMPLE, OTHER };
    protected ReactionKind reactionKind = ReactionKind.OTHER;
    protected ReactantNodule reactionParentNodule;

    @Override
    public void initAndValidate() {

        for (Type type : reactantsInput.get()) {
            reactants.add(type);
            ReactantNodule reactNodule = new ReactantNodule(type);
            reactantNodules.add(reactNodule);
        }

        for (int idx=0; idx<productsInput.get().size(); idx++) {
            Type type = productsInput.get().get(idx);
            products.add(type);
            ProductNodule prodNodule = new ProductNodule(type);

            int mapIdx = p2rMapInput.get().get(idx);
            if (mapIdx>=0) {
                reactantNodules.get(mapIdx).addChild(prodNodule);
            }
            productNodules.add(prodNodule);
        }

        // Calculate deltas

        for (Type type : reactants.elementSet()) {
            deltas.put(type, -reactants.count(type));
        }

        for (Type type : products.elementSet()) {
            if (deltas.containsKey(type))
                deltas.put(type, deltas.get(type) + products.count(type));
            else
                deltas.put(type, products.count(type));

            if (deltas.get(type) == 0)
                deltas.remove(type);
        }

        // Classify reaction as sample-generating or coalescent-generating.

        for (ReactantNodule reactNodule : reactantNodules) {
            if (reactNodule.getChildren().size()>2) {
                throw new IllegalArgumentException("Models may only create binary trees!");
            }

            if (reactNodule.getChildren().size()==2)
                reactionKind = ReactionKind.COALESCENCE;

            for (ProductNodule prodNodule : reactNodule.getChildren()) {
                if (prodNodule.getType() == Type.SAMPLED) {
                    reactionKind = ReactionKind.SAMPLE;
                }
            }

            if (reactionKind != ReactionKind.OTHER) {
                reactionParentNodule = reactNodule;
                break;
            }
        }
    }

    /**
     * @return Kind of this reaction in terms of effect on tree.
     */
    public ReactionKind getReactionKind() {
        return reactionKind;
    }

    /**
     * @return Reactant nodule for reactions capable of modifying tree.
     */
    public ReactantNodule getReactionParentNodule() {
        return reactionParentNodule;
    }

    /**
     * Determine the number of reactant permutations available in the given
     * state. Used to calculate propensities of Reactions and the number of
     * times MultiReactions fire.
     *
     * Note that this number is computed and returned as a double, to avoid
     * integer overflows when extremely large populations are involved.
     *
     * @param state
     * @return permutation count.
     */
    protected double getReactantPermutations(SystemState state) {
        double perms = 1;

        for (Type reactant : reactants.elementSet()) {
            int m = reactants.count(reactant);
            long N = state.get(reactant);
            for (long n = N; n>N-m && n>=0; n--) {
                perms *= n;
            }

            if (perms==0)
                return 0;
        } 

        return perms;
    }

    /**
     * Calculate and retrieve reaction propensity for given state.
     * 
     * @param state
     * @return reaction propensity
     */
    public double getPropensity(SystemState state) {
        if (rateInput.get() != null)
            return getReactantPermutations(state)*rateInput.get().getValue();
        else
            return 0.0;
    }

    /**
     * @return true if this reaction possesses a rate.
     */
    public boolean hasRate() {
        return rateInput.get() != null;
    }

    /**
     * Increment the given state by applying this reaction.
     * 
     * @param state state to increment
     */
    public void incrementState(SystemState state) {
        for (Type type : deltas.keySet())
            state.put(type, state.get(type)+deltas.get(type));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (reactants.isEmpty())
            sb.append("0");
        else {
            boolean isFirst = true;
            for (Type reactant : reactants)  {
                if (isFirst)
                    isFirst = false;
                else
                    sb.append(" + ");

                sb.append(reactant.getID());
            }
        }

        sb.append(" -> ");

        if (products.isEmpty())
            sb.append("0");
        else {
            boolean isFirst = true;
            for (Type product : products)  {
                if (isFirst)
                    isFirst = false;
                else
                    sb.append(" + ");

                sb.append(product.getID());
            }
        }

        return sb.toString();
    }
}
