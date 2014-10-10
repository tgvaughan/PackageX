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
import beast.core.State;
import java.util.List;
import java.util.Random;

/**
 * Computes the probability density of the given tree under the chosen model.
 * 
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class TreeDensity extends Distribution {

    @Override
    public double calculateLogP() throws Exception {
        logP = 0.0;

        return logP;
    }

    @Override
    public List<String> getArguments() {
        return null;
    }

    @Override
    public List<String> getConditions() {
        return null;
    }

    @Override
    public void sample(State state, Random random) {
        // TODO: Implement this - shouldn't be too hard!
    }
    
}
