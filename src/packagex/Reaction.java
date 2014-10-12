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
import java.util.List;
import java.util.Map;

/**
 * A reaction involving individuals of different types.
 *
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class Reaction extends BEASTObject {

    public Input<RealParameter> rateInput = new Input<>("rate",
        "Constant reaction rate", Validate.REQUIRED);

    public Input<List<Type>> reactantsInput = new Input<>("reactant",
        "Reactant type", new ArrayList<>());

    public Input<List<Type>> productsInput = new Input<>("product",
        "Product type", new ArrayList<>());

    public Input<IntegerParameter> p2rMapInput =
        new Input<>("p2rMap", "Product to reactant map.");

    private Multiset<Type> reactants, products;
    private Multimap<Type, Multiset<Type>> offspringMap;
    private Map<Type, Integer> deltas;

    @Override
    public void initAndValidate() {

        reactants = HashMultiset.create();
        for (Type type : reactantsInput.get()) {
            reactants.add(type);
        }

        products = HashMultiset.create();
        for (Type type : productsInput.get()) {
            products.add(type);
        }

        Multimap<Integer, Type> idxMap = HashMultimap.create();
        for (int i=0; i<productsInput.get().size(); i++) {
            idxMap.put(p2rMapInput.get().getValue(i), productsInput.get().get(i));
        }

        offspringMap = HashMultimap.create();
        for (int idx : idxMap.keySet()) {
            Type reactant = reactantsInput.get().get(idx);
            Multiset<Type> offspringSet = HashMultiset.create();
            offspringSet.addAll(idxMap.get(idx));
            offspringMap.put(reactant, offspringSet);
        }

        // Calculate deltas
        deltas = new HashMap<>();
        for (Type type : reactants.elementSet())
            deltas.put(type, reactants.count(type));

        for (Type type : products.elementSet()) {
            int delta = 0;
            if (deltas.containsKey(type))
                delta = deltas.get(type);

            delta -= products.count(type);

            if (delta==0) {
                if (deltas.containsKey(type))
                    deltas.remove(type);
            } else
                deltas.put(type, delta);
        }
    }

    /**
     * Calculate and retrieve reaction propensity for given state.
     * 
     * @param state
     * @return reaction propensity
     */
    public double getPropensity(SystemState state) {
        double prop = rateInput.get().getValue();

        for (Type reactant : reactants.elementSet()) {
            int m = reactants.count(reactant);
            long N = state.get(reactant);
            for (long n = N; n>N-m && n>=0; n--) {
                prop *= n;
            }

            // Early bail-out if propensity has a zero element
            if (prop==0)
                return 0;
        }

        return prop;
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
