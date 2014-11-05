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

/**
 * Objects representing a reaction occurring at a specific time.
 *
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class SystemEvent {

    private Reaction reaction;
    private double time;
    private int multiplicity;

    public SystemEvent() {
    }

    public SystemEvent(Reaction reaction, double time, int multiplicity) {
        this.reaction = reaction;
        this.time = time;
        this.multiplicity = multiplicity;
    }

    public SystemEvent(Reaction reaction, double time) {
        this(reaction, time, 1);
    }
    

    /**
     * @return reaction corresponding to this event.
     */
    public Reaction getReaction() {
        return reaction;
    }

    /**
     * @return time at which this event occurred.
     */
    public double getTime() {
        return time;
    }

    /**
     * @return multiplicity of reaction.
     */
    public int getMultiplicity() {
        return multiplicity;
    }
}
