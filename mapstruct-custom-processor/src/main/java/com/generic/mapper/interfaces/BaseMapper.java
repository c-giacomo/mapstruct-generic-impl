package com.generic.mapper.interfaces;

import org.mapstruct.MappingTarget;

/**
 * @author gchiavolotti
 * @param <E>
 * @param <D>
 */

public interface BaseMapper<E, D> {
	
	/**
	 * mapper from E to D
	 * 
	 * @param E entity
	 * @return D dto
	 * 
	 */
	D serialize(E e);

	/**
	 * mapper from E to D
	 *
	 * @param D dto
	 * @return E entity
	 *
	 */
	E deserialize(D d);
	
	/**
	 * Update the object.
	 * 
	 * @param D
	 * @param E
	 * @return E
	 */
	E update(D from, @MappingTarget E to);
	
}
