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

import java.util.HashMap;
import java.util.Map;

/**
 * The state of a system described by the model.
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class SystemState {

    Map<Type, Long> stateMap = new HashMap<>();

    public SystemState() {
    }

    /**
     * Set the number of individuals of given type in the state to the
     * given size.
     * 
     * @param type
     * @param size
     * @return the state object, allowing method chaining.
     */
    SystemState put(Type type, long size) {
        if (size>0) {
            stateMap.put(type, size);
        } else {
            if (stateMap.containsKey(type))
                stateMap.remove(type);
        }

        return this;
    }

    /**
     * Retrieve the number of individuals of the given type in the state.
     * 
     * @param type
     * @return number of individuals with given type
     */
    long get(Type type) {
        if (stateMap.containsKey(type))
            return stateMap.get(type);
        else
            return 0;
    }

    /**
     * @return a new copy of this state.
     */
    SystemState copy() {
        SystemState stateCopy = new SystemState();
        stateCopy.stateMap.putAll(stateMap);

        return stateCopy;
    }
}
