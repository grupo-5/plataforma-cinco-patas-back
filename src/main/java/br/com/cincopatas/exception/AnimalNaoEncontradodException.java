package br.com.cincopatas.exception;

public class AnimalNaoEncontradodException extends EntidadeNaoEncontradaException {
	private static final long serialVersionUID = 1L;

	public AnimalNaoEncontradodException(String mensagem) {
		super(mensagem);		
	}

	public AnimalNaoEncontradodException(Long id) {
		this(String.format("Não existe um cadastro de animal com código %d", id));
	}
}
