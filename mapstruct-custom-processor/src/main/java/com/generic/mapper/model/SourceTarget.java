package com.generic.mapper.model;

@SuppressWarnings("rawtypes")
public class SourceTarget {

	public final Class source;
	public final Class target;

	public SourceTarget(Class source, Class target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof SourceTarget st && source == st.source && target == st.target;
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(source, target);
	}
}
