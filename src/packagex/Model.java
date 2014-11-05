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
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void initAndValidate() throws Exception { }
    
}
