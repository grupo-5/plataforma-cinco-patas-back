package br.com.cincopatas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.cincopatas.model.Animal;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long>{

}
