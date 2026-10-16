package com.generic.mapper.model;

import java.util.List;

/**
 * @author gchiavolotti
 * 
 */
public class Item {
    private final String mapperType;   // FQN dell'interfaccia mapper
    private final String sourceType;
    private final String targetType;
    private final List<MethodItem> methods;

    public Item(String mapperType, String sourceType, String targetType, List<MethodItem> methods) {
        this.mapperType = mapperType;
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.methods = methods;
    }

    public String getMapperType() { return mapperType; }
    public String getSourceType() { return sourceType; }
    public String getTargetType() { return targetType; }
    public List<MethodItem> getMethods() { return methods; }
}