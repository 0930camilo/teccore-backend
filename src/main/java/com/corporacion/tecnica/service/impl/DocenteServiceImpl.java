package com.corporacion.tecnica.service.impl;

import com.corporacion.tecnica.dto.PageResponse;
import com.corporacion.tecnica.dto.docente.DocenteRequest;
import com.corporacion.tecnica.dto.docente.DocenteResponse;
import com.corporacion.tecnica.entity.Docente;
import com.corporacion.tecnica.entity.Institucion;
import com.corporacion.tecnica.entity.Rol;
import com.corporacion.tecnica.entity.RolNombre;
import com.corporacion.tecnica.entity.Sede;
import com.corporacion.tecnica.entity.Usuario;
import com.corporacion.tecnica.exception.BusinessException;
import com.corporacion.tecnica.exception.ResourceNotFoundException;
import com.corporacion.tecnica.mapper.DocenteMapper;
import com.corporacion.tecnica.repository.DocenteRepository;
import com.corporacion.tecnica.repository.RolRepository;
import com.corporacion.tecnica.repository.UsuarioRepository;
import com.corporacion.tecnica.service.DocenteService;
import com.corporacion.tecnica.util.ApiResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocenteServiceImpl implements DocenteService {

    private final DocenteRepository docenteRepository;
    private final DocenteMapper docenteMapper;
    private final InstitutionScopeResolver institutionScopeResolver;
    private final SedeScopeResolver sedeScopeResolver;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public DocenteResponse crear(DocenteRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BusinessException("Debe enviar el email para crear la cuenta del docente");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BusinessException("Debe enviar la contraseña para crear la cuenta del docente");
        }

        usuarioRepository.findByEmail(request.getEmail().trim()).ifPresent(existing -> {
            throw new BusinessException("Ya existe un usuario con ese email");
        });

        Docente docente = docenteMapper.toEntity(request);

        Institucion institucion =
                institutionScopeResolver.getRequiredInstitution(
                        request.getInstitucionId()
                );

        Sede sede = null;
        if (request.getSedeId() != null) {
            sede = sedeScopeResolver.getRequiredSede(request.getSedeId());
            if (!sede.getInstitucion().getId().equals(institucion.getId())) {
                throw new BusinessException("La sede no pertenece a la institución del docente");
            }
        }

        docente.setInstitucion(institucion);
        Docente guardado = docenteRepository.save(docente);

        Rol rol = rolRepository.findByNombre(RolNombre.DOCENTE)
                .orElseThrow(() -> new BusinessException("Rol DOCENTE no configurado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(guardado.getNombres() + " " + guardado.getApellidos());
        usuario.setEmail(request.getEmail().trim());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        usuario.setInstitucion(institucion);
        usuario.setSede(sede);
        usuarioRepository.save(usuario);

        return docenteMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DocenteResponse> listar(
            String q,
            int page,
            int size) {

        Long institucionId =
                institutionScopeResolver.resolveInstitutionId(null);

        String filtro = q == null ? "" : q.trim();

        Page<DocenteResponse> result =
                docenteRepository
                        .buscarPorNombreODocumento(
                                institucionId,
                                filtro,
                                PageRequest.of(page, size)
                        )
                        .map(docenteMapper::toResponse);

        return ApiResponseFactory.page(result);
    }

    @Override
    @Transactional(readOnly = true)
    public DocenteResponse obtener(Long id) {

        Long institucionId =
                institutionScopeResolver.resolveInstitutionId(null);

        Docente docente =
                docenteRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Docente no encontrado"
                                )
                        );

        validarInstitucion(docente, institucionId);

        return docenteMapper.toResponse(docente);
    }

    @Override
    @Transactional
    public DocenteResponse actualizar(
            Long id,
            DocenteRequest request) {

        Long institucionId =
                institutionScopeResolver.resolveInstitutionId(null);

        Docente docente =
                docenteRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Docente no encontrado"
                                )
                        );

        validarInstitucion(docente, institucionId);

        Institucion institucion =
                institutionScopeResolver.getRequiredInstitution(
                        request.getInstitucionId()
                );

        if (!institucion.getId().equals(institucionId)) {
            throw new BusinessException(
                    "El docente no puede asignarse a otra institución"
            );
        }

        Sede sede = null;
        if (request.getSedeId() != null) {
            sede = sedeScopeResolver.getRequiredSede(request.getSedeId());
            if (!sede.getInstitucion().getId().equals(institucion.getId())) {
                throw new BusinessException("La sede no pertenece a la institución del docente");
            }
        }

        docente.setNombres(request.getNombres().trim());
        docente.setApellidos(request.getApellidos().trim());
        docente.setDocumento(request.getDocumento().trim());
        docente.setCorreo(
                request.getCorreo() != null
                        ? request.getCorreo().trim()
                        : null
        );
        docente.setCargaHorariaSemanal(
                request.getCargaHorariaSemanal()
        );

        docente.setInstitucion(institucion);

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail().trim()).orElse(null);
        if (usuario != null) {
            usuario.setSede(sede);
            usuario.setInstitucion(institucion);
            usuarioRepository.save(usuario);
        }

        return docenteMapper.toResponse(
                docenteRepository.save(docente)
        );
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        Long institucionId =
                institutionScopeResolver.resolveInstitutionId(null);

        Docente docente =
                docenteRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Docente no encontrado"
                                )
                        );

        validarInstitucion(docente, institucionId);

        docenteRepository.delete(docente);
    }

    private void validarInstitucion(
            Docente docente,
            Long institucionId) {

        if (docente.getInstitucion() == null
                || docente.getInstitucion().getId() == null
                || !docente.getInstitucion().getId().equals(institucionId)) {

            throw new BusinessException(
                    "El docente no pertenece a la institución autenticada"
            );
        }
    }
}