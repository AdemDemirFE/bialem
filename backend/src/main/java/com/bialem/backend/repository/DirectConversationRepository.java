package com.bialem.backend.repository;

import com.bialem.backend.domain.DirectConversation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DirectConversationRepository extends JpaRepository<DirectConversation, Long> {
    Optional<DirectConversation> findByParticipantOne_IdAndParticipantTwo_Id(Long participantOneId, Long participantTwoId);

    @Query("select c from DirectConversation c join fetch c.participantOne join fetch c.participantTwo where c.participantOne.id = :profileId or c.participantTwo.id = :profileId order by c.updatedAt desc")
    List<DirectConversation> findAllForProfile(@Param("profileId") Long profileId);

    @Query("select c from DirectConversation c join fetch c.participantOne join fetch c.participantTwo where c.id = :id and (c.participantOne.id = :profileId or c.participantTwo.id = :profileId)")
    Optional<DirectConversation> findAccessible(@Param("id") Long id, @Param("profileId") Long profileId);
}
