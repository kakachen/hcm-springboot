package br.com.litecode.domain.repository;

import br.com.litecode.domain.model.PatientData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface PatientDataRepository<T extends PatientData> extends CrudRepository<T, Integer> {
	List<T> findAllByOrderByNameAsc();
}