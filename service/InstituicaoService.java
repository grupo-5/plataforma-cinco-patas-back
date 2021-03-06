package br.com.cincopatas.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import br.com.cincopatas.dto.AnimalDTO;
import br.com.cincopatas.dto.InstituicaoDTO;
import br.com.cincopatas.exception.OngNaoEncontradaException;
import br.com.cincopatas.mapper.InstituicaoMapper;
import br.com.cincopatas.model.Animal;
import br.com.cincopatas.model.Instituicao;
import br.com.cincopatas.repository.CidadeRepository;
import br.com.cincopatas.repository.EstadoRepository;
import br.com.cincopatas.repository.InstituicaoRepository;
import br.com.cincopatas.request.InstituicaoRequest;

@Service
public class InstituicaoService {

	@Autowired
	private InstituicaoRepository instituicaoRepository;
	@Autowired
	private CidadeRepository cidadeRepository;
	@Autowired
	private EstadoRepository estadoRepository;

	@Autowired
	private InstituicaoMapper instituicaoMapper;

	public List<InstituicaoDTO> listar() {
		List<Instituicao> instituicao = instituicaoRepository.findAll();
		return instituicao.stream().map(ani -> instituicaoMapper.modelToDTO(ani)).collect(Collectors.toList());
	}
	
	public Optional<Instituicao> buscar(Long id) {
		return this.instituicaoRepository.findById(id);
	}
	

	@Transactional
	public InstituicaoDTO salvar(InstituicaoRequest instituicaoRequest) {

		Instituicao instituicao = instituicaoMapper.dtoRequestToModel(instituicaoRequest);

		if (instituicaoRequest.getEndereco().getCidade().getEstado().getId() == null) {
			estadoRepository.save(instituicaoRequest.getEndereco().getCidade().getEstado());
			cidadeRepository.save(instituicaoRequest.getEndereco().getCidade());
		}

		return instituicaoMapper.modelToDTO(instituicaoRepository.save(instituicao));
	}

	@Transactional
	public void remover(Long id) {
		try {
			instituicaoRepository.deleteById(id);
			instituicaoRepository.flush();

		} catch (EmptyResultDataAccessException e) {
			throw new OngNaoEncontradaException(id);
		}
	}
	
	@Transactional
	public void atualizar(Instituicao instituicao) {
				
		instituicaoMapper.modelToDTO(instituicaoRepository.save(instituicao));
	}

	public List<InstituicaoDTO> buscarInstituicoesCidade(Long id) {
		List<Instituicao> instituicao = instituicaoRepository.buscarInstituicoesCidade(id);
		return instituicao.stream().map(inst -> instituicaoMapper.modelToDTO(inst)).collect(Collectors.toList());
	}

	public List<InstituicaoDTO> buscarPorEstado(Long id) {
		List<Instituicao> instituicao = instituicaoRepository.buscarPorEstado(id);
		
		return instituicao.stream()
				.map(inst -> instituicaoMapper.modelToDTO(inst))
				.collect(Collectors.toList());
	}
}
