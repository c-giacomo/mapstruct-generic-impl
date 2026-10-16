package com.generic.mapper.model;

public class MethodItem {
    private final String name;
    private final String parameterType;   // solo metodi a 1 parametro
    private final String returnType;

    public MethodItem(String name, String parameterType, String returnType) {
        this.name = name;
        this.parameterType = parameterType;
        this.returnType = returnType;
    }

    public String getName() { return name; }
    public String getParameterType() { return parameterType; }
    public String getReturnType() { return returnType; }
}
