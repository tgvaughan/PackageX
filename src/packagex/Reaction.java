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
import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.List;

/**
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

    private Multiset<Type> reactants;

    @Override
    public void initAndValidate() {
    }
    
}
