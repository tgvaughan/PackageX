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

/**
 *
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class PopulationSize extends BEASTObject {

    public Input<Type> popTypeInput = new Input<>("popType",
        "Population type.", Validate.REQUIRED);

    public Input<IntegerParameter> popSizeInput = new Input<>("popSize",
        "Population size.", Validate.REQUIRED);

    @Override
    public void initAndValidate() throws Exception {
    }

    public Type getType() {
        return popTypeInput.get();
    }

    public int getSize() {
        return popSizeInput.get().getValue();
    }
    
}
