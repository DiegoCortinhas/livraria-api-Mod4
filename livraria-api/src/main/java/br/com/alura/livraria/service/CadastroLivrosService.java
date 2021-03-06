package br.com.alura.livraria.service;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.alura.livraria.dto.AtualizacaoLivrosFormDto;
import br.com.alura.livraria.dto.CadastroLivrosDto;
import br.com.alura.livraria.dto.CadastroLivrosFormDto;
import br.com.alura.livraria.modelo.CadastroAutores;
import br.com.alura.livraria.modelo.CadastroLivros;
import br.com.alura.livraria.repository.CadastroAutoresRepository;
import br.com.alura.livraria.repository.CadastroLivrosRepository;

@Service
public class CadastroLivrosService {
	
	@Autowired
	private CadastroLivrosRepository cadastroLivrosRepository;
	
	@Autowired
	private CadastroAutoresRepository cadastroAutoresRepository;
	
	private ModelMapper modelMapper = new ModelMapper();

	public Page<CadastroLivrosDto> listar(Pageable paginacao) {
		Page<CadastroLivros> cadastroLivros = cadastroLivrosRepository.findAll(paginacao);
		return cadastroLivros.map(c -> modelMapper.map(c, CadastroLivrosDto.class));
	}
	
	@Transactional
	public CadastroLivrosDto cadastrar(@Valid CadastroLivrosFormDto dto) {
		//throw new NullPointerException("testando Erro NullPointer");
		
		Long idAutor = dto.getCadastroAutorId();
		
		try {
			CadastroAutores autor = cadastroAutoresRepository.getById(idAutor);
			CadastroLivros cadastro = modelMapper.map(dto, CadastroLivros.class);
			cadastro.setId(null);
			cadastro.setCadastroAutor(autor);
			cadastroLivrosRepository.save(cadastro);
			return modelMapper.map(cadastro, CadastroLivrosDto.class);
			
		} catch (EntityNotFoundException e) {
			throw new IllegalArgumentException("autor inexistente");
		}

		
		 
	}
	
	@Transactional
	public CadastroLivrosDto atualizar(AtualizacaoLivrosFormDto dto) {
		CadastroLivros livro = cadastroLivrosRepository.getById(dto.getId());
		livro.atualizarInformacoes(dto.getTitulo(),dto.getDataLancamento(),dto.getNumeroPaginas());
		
		return modelMapper.map(livro, CadastroLivrosDto.class);
	}
	
	@Transactional
	public void remover(Long id) {
		cadastroLivrosRepository.deleteById(id);
	}

	public CadastroLivrosDto detalhar(Long id) {
		CadastroLivros livro = cadastroLivrosRepository
				.findById(id)
				.orElseThrow(() -> new EntityNotFoundException());
		return modelMapper.map(livro, CadastroLivrosDto.class);
	}
	
}
