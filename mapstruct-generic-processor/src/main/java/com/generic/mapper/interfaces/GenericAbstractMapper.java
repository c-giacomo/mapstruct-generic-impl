package com.generic.mapper.interfaces;


public interface GenericAbstractMapper {

	<T> T serializza(Object from, Class<T> to);

	<T> T deserializza(Class<T> to, Object from);

	<T> T aggiorna(Object from, T to);

	/** Richiama per nome un metodo a un parametro di un mapper (standard o custom). */
	<T> T invoke(String methodName, Object from, Class<T> to);

	@SuppressWarnings("rawtypes")
	boolean hasRegisteredMapper(Class from, Class to);
}