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
where
((make_time(:h, :m,0) between b."open" and b."close") and
cast(make_time(:h, :m,0) +interval '1 minute'*cast(ceil(b.ratio *:duration) as int) as time)
between b."open" and b."close")
except
select distinct bx.* from boxes bx join orders o  on bx.id =o.box_id
where
o."date" =:date and o.order_status in ('ACTIVE', 'ACTIVE_ARRIVED') and(
(o.start_time <= make_time(:h, :m,0) and  make_time(:h, :m,0)<o.end_time)
or
(o.start_time < cast(make_time(:h, :m,0) +cast(ceil(bx.ratio *:duration)  as int)*interval '1 minute' as time)
and cast(make_time(:h, :m,0) +cast(ceil(bx.ratio *:duration) as int)*interval '1 minute'as time) <=o.end_time)
or (
o.start_time between make_time(:h, :m,0) and cast(make_time(:h, :m,0) +cast(ceil(bx.ratio *:duration)  
as int)*interval '1 minute' as time)
and
o.end_time between make_time(:h, :m,0) and cast(make_time(:h, :m,0) +cast(ceil(bx.ratio *:duration)  as int)
*interval '1 minute' as time)
)
)
""" , nativeQuery = true)
    List<Box> getFreeBoxes(@Param("date") LocalDate date,
                           @Param("h") int hour,
                           @Param("m") int minute,
                           @Param("duration") int duration);

}
