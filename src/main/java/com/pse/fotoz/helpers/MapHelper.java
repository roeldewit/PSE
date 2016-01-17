/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pse.fotoz.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

/**
 *
 * @author Robert
 */
public class MapHelper {
    
    /**
     * Merges two maps together.
     * 
     * Returns a map m such that m contains all keys of both input maps a, b 
     * with keys/values as follows:
     * 
     * k -> a.v iff !b.v
     * k -> b.v iff !a.v
     * k -> a.v combine b.v otherwise
     * 
     * This facilitates folding (reduction) over maps.
     * 
     * Courtesy of Map::merge being pants on head retarded.
     * 
     * @param <K> The type of keys stored in both maps.
     * @param <V> The type of values stored in both maps.
     * @param combiner An operator combining two values in the maps.
     * @return A map containing all keys of both maps, having combined the
     * values using the combiner, or the singular value in case only one map
     * contained the key.
     */
    public static <K, V> BinaryOperator<Map<K, V>> merge(
            BinaryOperator<V> combiner) {
        return (m1, m2) -> {
                Map result = new HashMap<>(m1);
                
                m2.entrySet().forEach(e -> {
                    if (m1.containsKey(e.getKey())) {
                        result.put(e.getKey(), combiner.apply(e.getValue(), 
                                m1.get(e.getKey())));
                    } else {
                        result.put(e.getKey(), e.getValue());
                    }
                });                
                
                return result;
        };
    }
}
