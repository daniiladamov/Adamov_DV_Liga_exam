package com.example.liga_exam.repository;

import com.example.liga_exam.entity.Box;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BoxRepo extends JpaRepository<Box, Long>, JpaSpecificationExecutor<Box> {

    @Query(value = """
            select distinct b.* from boxes b
            left join orders o  on b.id =o.box_id         
            where( (o."date" != :date or o."date" is null)
            or not (
            o.start_time <= make_time(:h, :m,0) and  make_time(:h, :m,0)<o.end_time
            or
            (o.start_time < make_time(:h, :m,0) +make_interval(mins=>cast((b.ratio *:duration)+1  as int))\s
            and make_time(:h, :m,0) +make_interval(mins=>cast((b.ratio *:duration)+1 as int)) <=o.end_time)
            )
            )
            and
            ( (make_time(:h, :m,0) between b."open" and b."close") and
            make_time(:h, :m,0) +make_interval(mins=>cast((b.ratio *:duration)+1 as int)) between b."open" and b."close"
            )
              """
            , nativeQuery = true)
    List<Box> getFreeBoxes(@Param("date") LocalDate date,
                           @Param("h") int hour,
                           @Param("m") int minute,
                           @Param("duration") int duration);

}
