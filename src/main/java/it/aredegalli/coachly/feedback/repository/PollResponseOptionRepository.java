package it.aredegalli.coachly.feedback.repository;

import it.aredegalli.coachly.feedback.model.PollResponseOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PollResponseOptionRepository extends JpaRepository<PollResponseOption, UUID> {

    @Query("select pro.pollOptionId, count(pro) from PollResponseOption pro join PollResponse pr on pr.id = pro.pollResponseId where pr.pollId = :pollId group by pro.pollOptionId")
    List<Object[]> countByOptionForPoll(@Param("pollId") UUID pollId);
}


