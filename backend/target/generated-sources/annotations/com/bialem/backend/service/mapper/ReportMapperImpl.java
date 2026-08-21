package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Report;
import com.bialem.backend.domain.User;
import com.bialem.backend.service.dto.ProfileDTO;
import com.bialem.backend.service.dto.ReportDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T22:10:22+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class ReportMapperImpl implements ReportMapper {

    @Override
    public Report toEntity(ReportDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Report report = new Report();

        report.setId( dto.getId() );
        report.setTargetType( dto.getTargetType() );
        report.setTargetId( dto.getTargetId() );
        report.setReason( dto.getReason() );
        report.setDetails( dto.getDetails() );
        report.setStatus( dto.getStatus() );
        report.setResolvedAt( dto.getResolvedAt() );
        report.setCreatedAt( dto.getCreatedAt() );
        report.setUpdatedAt( dto.getUpdatedAt() );
        report.reporter( profileDTOToProfile( dto.getReporter() ) );
        report.resolvedBy( profileDTOToProfile( dto.getResolvedBy() ) );

        return report;
    }

    @Override
    public List<Report> toEntity(List<ReportDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Report> list = new ArrayList<Report>( dtoList.size() );
        for ( ReportDTO reportDTO : dtoList ) {
            list.add( toEntity( reportDTO ) );
        }

        return list;
    }

    @Override
    public List<ReportDTO> toDto(List<Report> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<ReportDTO> list = new ArrayList<ReportDTO>( entityList.size() );
        for ( Report report : entityList ) {
            list.add( toDto( report ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(Report entity, ReportDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getTargetType() != null ) {
            entity.setTargetType( dto.getTargetType() );
        }
        if ( dto.getTargetId() != null ) {
            entity.setTargetId( dto.getTargetId() );
        }
        if ( dto.getReason() != null ) {
            entity.setReason( dto.getReason() );
        }
        if ( dto.getDetails() != null ) {
            entity.setDetails( dto.getDetails() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getResolvedAt() != null ) {
            entity.setResolvedAt( dto.getResolvedAt() );
        }
        if ( dto.getCreatedAt() != null ) {
            entity.setCreatedAt( dto.getCreatedAt() );
        }
        if ( dto.getUpdatedAt() != null ) {
            entity.setUpdatedAt( dto.getUpdatedAt() );
        }
        if ( dto.getReporter() != null ) {
            if ( entity.getReporter() == null ) {
                entity.reporter( new Profile() );
            }
            profileDTOToProfile1( dto.getReporter(), entity.getReporter() );
        }
        if ( dto.getResolvedBy() != null ) {
            if ( entity.getResolvedBy() == null ) {
                entity.resolvedBy( new Profile() );
            }
            profileDTOToProfile1( dto.getResolvedBy(), entity.getResolvedBy() );
        }
    }

    @Override
    public ReportDTO toDto(Report s) {
        if ( s == null ) {
            return null;
        }

        ReportDTO reportDTO = new ReportDTO();

        reportDTO.setReporter( toDtoProfileId( s.getReporter() ) );
        reportDTO.setResolvedBy( toDtoProfileId( s.getResolvedBy() ) );
        reportDTO.setId( s.getId() );
        reportDTO.setTargetType( s.getTargetType() );
        reportDTO.setTargetId( s.getTargetId() );
        reportDTO.setReason( s.getReason() );
        reportDTO.setDetails( s.getDetails() );
        reportDTO.setStatus( s.getStatus() );
        reportDTO.setResolvedAt( s.getResolvedAt() );
        reportDTO.setCreatedAt( s.getCreatedAt() );
        reportDTO.setUpdatedAt( s.getUpdatedAt() );

        return reportDTO;
    }

    @Override
    public ProfileDTO toDtoProfileId(Profile profile) {
        if ( profile == null ) {
            return null;
        }

        ProfileDTO profileDTO = new ProfileDTO();

        profileDTO.setId( profile.getId() );

        return profileDTO;
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

    protected Profile profileDTOToProfile(ProfileDTO profileDTO) {
        if ( profileDTO == null ) {
            return null;
        }

        Profile profile = new Profile();

        profile.setId( profileDTO.getId() );
        profile.setDisplayName( profileDTO.getDisplayName() );
        profile.setUsername( profileDTO.getUsername() );
        profile.setAvatarUrl( profileDTO.getAvatarUrl() );
        profile.setBio( profileDTO.getBio() );
        profile.setCity( profileDTO.getCity() );
        profile.setStatus( profileDTO.getStatus() );
        profile.setIsVerified( profileDTO.getIsVerified() );
        profile.setCreatedAt( profileDTO.getCreatedAt() );
        profile.setUpdatedAt( profileDTO.getUpdatedAt() );
        profile.user( userDTOToUser( profileDTO.getUser() ) );

        return profile;
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

    protected void profileDTOToProfile1(ProfileDTO profileDTO, Profile mappingTarget) {
        if ( profileDTO == null ) {
            return;
        }

        if ( profileDTO.getId() != null ) {
            mappingTarget.setId( profileDTO.getId() );
        }
        if ( profileDTO.getDisplayName() != null ) {
            mappingTarget.setDisplayName( profileDTO.getDisplayName() );
        }
        if ( profileDTO.getUsername() != null ) {
            mappingTarget.setUsername( profileDTO.getUsername() );
        }
        if ( profileDTO.getAvatarUrl() != null ) {
            mappingTarget.setAvatarUrl( profileDTO.getAvatarUrl() );
        }
        if ( profileDTO.getBio() != null ) {
            mappingTarget.setBio( profileDTO.getBio() );
        }
        if ( profileDTO.getCity() != null ) {
            mappingTarget.setCity( profileDTO.getCity() );
        }
        if ( profileDTO.getStatus() != null ) {
            mappingTarget.setStatus( profileDTO.getStatus() );
        }
        if ( profileDTO.getIsVerified() != null ) {
            mappingTarget.setIsVerified( profileDTO.getIsVerified() );
        }
        if ( profileDTO.getCreatedAt() != null ) {
            mappingTarget.setCreatedAt( profileDTO.getCreatedAt() );
        }
        if ( profileDTO.getUpdatedAt() != null ) {
            mappingTarget.setUpdatedAt( profileDTO.getUpdatedAt() );
        }
        if ( profileDTO.getUser() != null ) {
            if ( mappingTarget.getUser() == null ) {
                mappingTarget.user( new User() );
            }
            userDTOToUser1( profileDTO.getUser(), mappingTarget.getUser() );
        }
    }
}
