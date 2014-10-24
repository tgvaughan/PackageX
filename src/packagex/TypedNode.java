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

import beast.evolution.tree.Node;

/**
 *
 * @author Tim Vaughan (tgvaughan@gmail.com)
 */
public class TypedNode extends Node {

    private Type type;

    /**
     * @return Type associated with this node.
     */
    public Type getType() {
        return type;
    }

    /**
     * Set type associated with this node to newType.
     * 
     * @param newType 
     */
    public void setType(Type newType) {
        startEditing();
        type = newType;
    }

    /**
     * @return (deep) copy of node
     */
    @Override
    public Node copy() {
        final TypedNode node = new TypedNode();
        node.setHeight(height);
        node.setNr(labelNr);
        node.metaDataString = metaDataString;
        node.setParent(null);
        node.setID(getID());

        node.setType(type);

        for (final Node child : getChildren()) {
            node.addChild(child.copy());
        }
        return node;
    } // copy
    
}
