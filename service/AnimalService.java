package br.com.cincopatas.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.com.cincopatas.dto.AnimalDTO;
import br.com.cincopatas.dto.PessoaDTO;
import br.com.cincopatas.exception.AnimalNaoEncontradodException;
import br.com.cincopatas.filtro.AnimalFiltro;
import br.com.cincopatas.mapper.AnimalMapper;
import br.com.cincopatas.model.Animal;
import br.com.cincopatas.model.Pessoa;
import br.com.cincopatas.repository.AnimalRepository;
import br.com.cincopatas.repository.CidadeRepository;
import br.com.cincopatas.repository.EstadoRepository;
import br.com.cincopatas.request.AnimalRequest;

@Service
public class AnimalService {

	@Autowired
	private AnimalRepository animalRepository;
	@Autowired
	private CidadeRepository cidadeRepository;
	@Autowired
	private EstadoRepository estadoRepository;
	@Autowired
	private AnimalMapper animalMapper;

	public List<AnimalDTO> listar(AnimalFiltro filtro) {
		
		List<Animal> animais = animalRepository.findAll(filtro.getCidade(), 
				filtro.getEstado(), filtro.getPorte(),
				filtro.getEspecie());		
		
		return animais.stream()
					  .map(ani -> animalMapper.modelToDTO(ani))
					  .collect(Collectors.toList());
	}

	public AnimalDTO buscar(Long id) {
		Optional<Animal> animal = animalRepository.findById(id);
	
		if(animal.isPresent()) {
			return animalMapper.modelToDTO(animal.get());
		}
		return null;	
	}
	
	@Transactional
	public AnimalDTO salvar(AnimalRequest animalRequest) {

		Animal animal = animalMapper.dtoRequestToModel(animalRequest);

		if (animalRequest.getEndereco().getCidade().getEstado().getId() == null) {
			estadoRepository.save(animalRequest.getEndereco().getCidade().getEstado());
			cidadeRepository.save(animalRequest.getEndereco().getCidade());
		}

		animalRequest.getPersonalidades().stream().forEach(personalidade -> personalidade.setAnimal(animal));
		animalRequest.getCuidadosVet().stream().forEach(cuidados -> cuidados.setAnimal(animal));
		return animalMapper.modelToDTO(animalRepository.save(animal));
	}

	
	@Transactional
	public void remover(Long id) {
		try {
			animalRepository.deleteById(id);
			animalRepository.flush();
		
		} catch (EmptyResultDataAccessException e) {
			throw new AnimalNaoEncontradodException(id);
		};			
	}

	@Transactional
	public void atualizar(AnimalRequest animalRequest) {
		Animal animal = animalMapper.dtoRequestToModel(animalRequest);
		
		if (animalRequest.getEndereco().getCidade().getEstado().getId() == null) {
			estadoRepository.save(animalRequest.getEndereco().getCidade().getEstado());
			cidadeRepository.save(animalRequest.getEndereco().getCidade());
		}

		animalRequest.getPersonalidades().stream().forEach(personalidade -> personalidade.setAnimal(animal));
		animalRequest.getCuidadosVet().stream().forEach(cuidados -> cuidados.setAnimal(animal));	
		animalMapper.modelToDTO(animalRepository.save(animal));
	}

	public List<AnimalDTO> listarPorInstituicao(Long codigo) {

		List<Animal> animais = animalRepository.buscarPorInstituicao(codigo);		
		
		return animais.stream()
					  .map(ani -> animalMapper.modelToDTO(ani))
					  .collect(Collectors.toList());
	}
}