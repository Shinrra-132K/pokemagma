package co.edu.unbosque.pokemagma.service;

import java.util.List;

/**
 * Define las operaciones CRUD basicas para los servicios
 * 
 * @param <T> tipo de DTO usado por el servicio
 */
public interface CRUDOperation<T> {
	
	/**
	 * Crea una entidad
	 * 
	 * @param data DTO con los datos que se crearan
	 * @return codigo de resultado definido por el servicio
	 */
	public int create(T data);
	
	/**
	 * Obtiene todas las entidades
	 * 
	 * @return lista de DTO
	 */
	public List<T> getAll();
	
	/**
	 * Actualiza una entidad por su ID
	 * 
	 * @param id identificador de la entidad
	 * @param newData DTO con los nuevos datos
	 * @return codigo de resultado definido por el servicio
	 */
	public int updateById(Long id, T newData);
	
	/**
	 * Elimina un registro mediante su identificador
	 * 
	 * @param id identificador del registro
	 * @return codigo de resultado de la operacion
	 */
	public int deleteById(Long id);
	
	/**
	 * Cuenta las entidades persistidas
	 * 
	 * @return cantidad de entidades
	 */
	public Long count();
	
	/**
	 * Comprueba si existe una entidad con el ID indicado
	 * 
	 * @param id identificador de la entidad
	 * @return true si existe
	 */
	public boolean exist(Long id);
	

}
