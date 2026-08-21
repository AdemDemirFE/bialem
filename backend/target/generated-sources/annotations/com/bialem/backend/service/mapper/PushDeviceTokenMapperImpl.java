package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.PushDeviceToken;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.PushDeviceTokenDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T00:11:57+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class PushDeviceTokenMapperImpl implements PushDeviceTokenMapper {

    @Override
    public PushDeviceToken toEntity(PushDeviceTokenDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PushDeviceToken pushDeviceToken = new PushDeviceToken();

        pushDeviceToken.setId( dto.getId() );
        pushDeviceToken.setToken( dto.getToken() );
        pushDeviceToken.setPlatform( dto.getPlatform() );
        pushDeviceToken.setCreatedAt( dto.getCreatedAt() );
        pushDeviceToken.setUpdatedAt( dto.getUpdatedAt() );
        pushDeviceToken.user( userDTOToUser( dto.getUser() ) );

        return pushDeviceToken;
    }

    @Override
    public List<PushDeviceToken> toEntity(List<PushDeviceTokenDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<PushDeviceToken> list = new ArrayList<PushDeviceToken>( dtoList.size() );
        for ( PushDeviceTokenDTO pushDeviceTokenDTO : dtoList ) {
            list.add( toEntity( pushDeviceTokenDTO ) );
        }

        return list;
    }

    @Override
    public List<PushDeviceTokenDTO> toDto(List<PushDeviceToken> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<PushDeviceTokenDTO> list = new ArrayList<PushDeviceTokenDTO>( entityList.size() );
        for ( PushDeviceToken pushDeviceToken : entityList ) {
            list.add( toDto( pushDeviceToken ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(PushDeviceToken entity, PushDeviceTokenDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getToken() != null ) {
            entity.setToken( dto.getToken() );
        }
        if ( dto.getPlatform() != null ) {
            entity.setPlatform( dto.getPlatform() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
        if ( dto.getUser() != null ) {
            if ( entity.getUser() == null ) {
                entity.user( new User() );
            }
            userDTOToUser1( dto.getUser(), entity.getUser() );
        }
    }

    @Override
    public PushDeviceTokenDTO toDto(PushDeviceToken s) {
        if ( s == null ) {
            return null;
        }

        PushDeviceTokenDTO pushDeviceTokenDTO = new PushDeviceTokenDTO();

        pushDeviceTokenDTO.setUser( toDtoUserId( s.getUser() ) );
        pushDeviceTokenDTO.setId( s.getId() );
        pushDeviceTokenDTO.setToken( s.getToken() );
        pushDeviceTokenDTO.setPlatform( s.getPlatform() );
        pushDeviceTokenDTO.setCreatedAt( s.getCreatedAt() );
        pushDeviceTokenDTO.setUpdatedAt( s.getUpdatedAt() );

        return pushDeviceTokenDTO;
    }

    @Override
    public UserDTO toDtoUserId(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId( user.getId() );

        return userDTO;
    }

    protected User userDTOToUser(UserDTO userDTO) {
        if ( userDTO == null ) {
            return null;
        }

        User user = new User();

        user.setId( userDTO.getId() );
        user.setLogin( userDTO.getLogin() );

        return user;
    }

    protected void userDTOToUser1(UserDTO userDTO, User mappingTarget) {
        if ( userDTO == null ) {
            return;
        }

        if ( userDTO.getId() != null ) {
            mappingTarget.setId( userDTO.getId() );
        }
        if ( userDTO.getLogin() != null ) {
            mappingTarget.setLogin( userDTO.getLogin() );
        }
    }
}
