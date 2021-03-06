package br.com.cincopatas.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.cincopatas.dto.InstituicaoDTO;
import br.com.cincopatas.dto.PessoaDTO;
import br.com.cincopatas.model.Pessoa;
import br.com.cincopatas.openapi.PessoaOpenAPI;
import br.com.cincopatas.request.PessoaRequest;
import br.com.cincopatas.security.permissoes.PatinhasSecurity;
import br.com.cincopatas.service.PessoaService;

@CrossOrigin
@RestController
@RequestMapping("/pessoa")
public class PessoaController implements PessoaOpenAPI {

	@Autowired
	private PessoaService service;
	@Autowired
	private PatinhasSecurity patinhasSecurity;

	@GetMapping
	public List<PessoaDTO> listar() {
		return service.listar();
	}

	@PostMapping
	public ResponseEntity<PessoaDTO> salvar(@RequestBody PessoaRequest request) {
		PessoaDTO pessoa = service.salvar(request);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(pessoa.getId()).toUri();
		return ResponseEntity.created(uri).body(pessoa);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PessoaDTO> buscar(@PathVariable Long id) {
		PessoaDTO pessoa = service.buscar(id);

		if (pessoa != null) {

			return ResponseEntity.ok().body(pessoa);
		}
		return ResponseEntity.notFound().build();
	}
	
	@GetMapping(value = "/codigo")
	public PessoaDTO listarComCodigo() {
		Long tipo = patinhasSecurity.getTipo();
		Long codigo = patinhasSecurity.getCodigo();	
		return service.buscarComCodigo(codigo);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> atualizar(@RequestBody PessoaRequest pessoaRequest, @PathVariable Long id) {

		PessoaDTO pessoaAtual = service.buscar(id);

		if (pessoaAtual != null) {
			BeanUtils.copyProperties(pessoaRequest, pessoaAtual, "id");
			service.atualizar(pessoaRequest);
			return ResponseEntity.ok(pessoaAtual);
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Pessoa> excluir(@PathVariable Long id) {

		try {
			service.excluir(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/instituicao")
	public List<PessoaDTO> listarAnimaisPorInstituicao() {
//		Long tipo = patinhasSecurity.getTipo();
		Long codigo = patinhasSecurity.getCodigo();	
		return service.listarPorInstituicao(codigo);
	}
}
