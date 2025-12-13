package com.energygames.lojadegames.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.energygames.lojadegames.dto.mapper.UsuarioMapper;
import com.energygames.lojadegames.dto.request.LoginRequestDTO;
import com.energygames.lojadegames.dto.request.SenhaResetDTO;
import com.energygames.lojadegames.dto.request.SenhaUpdateDTO;
import com.energygames.lojadegames.dto.request.UsuarioRequestDTO;
import com.energygames.lojadegames.dto.response.AuthResponseDTO;
import com.energygames.lojadegames.dto.response.UsuarioResponseDTO;
import com.energygames.lojadegames.exception.BusinessException;
import com.energygames.lojadegames.exception.DuplicateResourceException;
import com.energygames.lojadegames.exception.ResourceNotFoundException;
import com.energygames.lojadegames.exception.UnauthorizedException;
import com.energygames.lojadegames.model.Usuario;
import com.energygames.lojadegames.repository.UsuarioRepository;
import com.energygames.lojadegames.security.JwtService;
import com.energygames.lojadegames.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);

	private final UsuarioRepository usuarioRepository;
	private final UsuarioMapper usuarioMapper;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;

	public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper,
			JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.usuarioMapper = usuarioMapper;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UsuarioResponseDTO> buscarTodos() {
		log.info("Buscando todos os usuários");
		return usuarioRepository.findAll().stream()
				.map(usuarioMapper::toResponseDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public UsuarioResponseDTO buscarPorId(Long id) {
		log.info("Buscando usuário com ID: {}", id);
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
		return usuarioMapper.toResponseDTO(usuario);
	}

	@Override
	@Transactional
	public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
		log.info("Cadastrando novo usuário: {}", dto.getUsuario());

		if (usuarioRepository.findByUsuario(dto.getUsuario()).isPresent()) {
			throw new DuplicateResourceException("Email já cadastrado: " + dto.getUsuario());
		}

		Usuario usuario = usuarioMapper.toEntity(dto);
		usuario.setSenha(criptografarSenha(usuario.getSenha()));
		
		// Adicionar role padrão
		usuario.getRoles().add(com.energygames.lojadegames.enums.RoleEnum.ROLE_USER);

		Usuario usuarioSalvo = usuarioRepository.save(usuario);

		log.info("Usuário cadastrado com sucesso. ID: {}", usuarioSalvo.getId());
		return usuarioMapper.toResponseDTO(usuarioSalvo);
	}

	@Override
	@Transactional
	public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
		log.info("Atualizando usuário ID: {}", id);

		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

		// Verificar se email já existe para outro usuário
		usuarioRepository.findByUsuario(dto.getUsuario()).ifPresent(usuarioExistente -> {
			if (!usuarioExistente.getId().equals(id)) {
				throw new DuplicateResourceException("Email já cadastrado: " + dto.getUsuario());
			}
		});

		usuarioMapper.updateEntity(dto, usuario);
		usuario.setSenha(criptografarSenha(usuario.getSenha()));

		Usuario usuarioAtualizado = usuarioRepository.save(usuario);

		log.info("Usuário atualizado com sucesso. ID: {}", id);
		return usuarioMapper.toResponseDTO(usuarioAtualizado);
	}

	@Override
	@Transactional
	public AuthResponseDTO autenticar(LoginRequestDTO dto) {
		log.info("Autenticando usuário: {}", dto.getUsuario());

		try {
			var credenciais = new UsernamePasswordAuthenticationToken(dto.getUsuario(), dto.getSenha());
			Authentication authentication = authenticationManager.authenticate(credenciais);

			if (authentication.isAuthenticated()) {
				Usuario usuario = usuarioRepository.findByUsuario(dto.getUsuario())
						.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + dto.getUsuario()));

				String token = gerarToken(dto.getUsuario());

				log.info("Usuário autenticado com sucesso: {}", dto.getUsuario());
				return usuarioMapper.toAuthResponseDTO(usuario, token);
			}
		} catch (Exception e) {
			log.warn("Falha na autenticação para usuário: {}", dto.getUsuario());
			throw new UnauthorizedException("Credenciais inválidas");
		}

		throw new UnauthorizedException("Credenciais inválidas");
	}

	@Override
	@Transactional
	public void alterarSenha(Long id, SenhaUpdateDTO dto) {
		Usuario usuario = usuarioRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

		if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
			throw new BusinessException("Senha atual incorreta");
		}

		usuario.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
		usuarioRepository.save(usuario);
		log.info("Senha alterada com sucesso para usuário ID: {}", id);
	}

	@Override
	public void recuperarSenha(SenhaResetDTO dto) {
		log.info("Solicitação de recuperação de senha para email: {}", dto.getEmail());
		// Simulação de envio de email
		log.info("Email de recuperação enviado para: {}", dto.getEmail());
	}

	private String criptografarSenha(String senha) {
		return passwordEncoder.encode(senha);
	}

	private String gerarToken(String usuario) {
		return "Bearer " + jwtService.generateToken(usuario);
	}
}
