package com.bialem.backend.service.mapper;

import com.bialem.backend.domain.CityEventSyncLog;
import com.bialem.backend.service.dto.CityEventSyncLogDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-21T15:24:49+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class CityEventSyncLogMapperImpl implements CityEventSyncLogMapper {

    @Override
    public CityEventSyncLog toEntity(CityEventSyncLogDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CityEventSyncLog cityEventSyncLog = new CityEventSyncLog();

        cityEventSyncLog.setId( dto.getId() );
        cityEventSyncLog.setProviderCode( dto.getProviderCode() );
        cityEventSyncLog.setStatus( dto.getStatus() );
        cityEventSyncLog.setImportedCount( dto.getImportedCount() );
        cityEventSyncLog.setErrorMessage( dto.getErrorMessage() );
        cityEventSyncLog.setStartedAt( dto.getStartedAt() );
        cityEventSyncLog.setFinishedAt( dto.getFinishedAt() );

        return cityEventSyncLog;
    }

    @Override
    public CityEventSyncLogDTO toDto(CityEventSyncLog entity) {
        if ( entity == null ) {
            return null;
        }

        CityEventSyncLogDTO cityEventSyncLogDTO = new CityEventSyncLogDTO();

        cityEventSyncLogDTO.setId( entity.getId() );
        cityEventSyncLogDTO.setProviderCode( entity.getProviderCode() );
        cityEventSyncLogDTO.setStatus( entity.getStatus() );
        cityEventSyncLogDTO.setImportedCount( entity.getImportedCount() );
        cityEventSyncLogDTO.setErrorMessage( entity.getErrorMessage() );
        cityEventSyncLogDTO.setStartedAt( entity.getStartedAt() );
        cityEventSyncLogDTO.setFinishedAt( entity.getFinishedAt() );

        return cityEventSyncLogDTO;
    }

    @Override
    public List<CityEventSyncLog> toEntity(List<CityEventSyncLogDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<CityEventSyncLog> list = new ArrayList<CityEventSyncLog>( dtoList.size() );
        for ( CityEventSyncLogDTO cityEventSyncLogDTO : dtoList ) {
            list.add( toEntity( cityEventSyncLogDTO ) );
        }

        return list;
    }

    @Override
    public List<CityEventSyncLogDTO> toDto(List<CityEventSyncLog> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<CityEventSyncLogDTO> list = new ArrayList<CityEventSyncLogDTO>( entityList.size() );
        for ( CityEventSyncLog cityEventSyncLog : entityList ) {
            list.add( toDto( cityEventSyncLog ) );
        }

        return list;
    }

    @Override
    public void partialUpdate(CityEventSyncLog entity, CityEventSyncLogDTO dto) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            entity.setId( dto.getId() );
        }
        if ( dto.getProviderCode() != null ) {
            entity.setProviderCode( dto.getProviderCode() );
        }
        if ( dto.getStatus() != null ) {
            entity.setStatus( dto.getStatus() );
        }
        if ( dto.getImportedCount() != null ) {
            entity.setImportedCount( dto.getImportedCount() );
        }
        if ( dto.getErrorMessage() != null ) {
            entity.setErrorMessage( dto.getErrorMessage() );
        }
        if ( dto.getStartedAt() != null ) {
            entity.setStartedAt( dto.getStartedAt() );
        }
        if ( dto.getFinishedAt() != null ) {
            entity.setFinishedAt( dto.getFinishedAt() );
        }
    }
}
