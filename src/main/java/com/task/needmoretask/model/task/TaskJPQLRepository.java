package com.task.needmoretask.model.task;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class TaskJPQLRepository {

    private final EntityManager em;

    // 가장 최근 생성한 task 7개 가져오기
    public List<Task> findLatestTasks(){
       TypedQuery<Task> query =
                em.createQuery("select t " +
                                "from Task t " +
                                "join fetch t.user " +
                                "where t.isDeleted = false " +
                                "order by t.createdAt desc"
                ,Task.class);

       query.setFirstResult(0);
       query.setMaxResults(7);


        List<Task> taskListPS = query.getResultList();

        return taskListPS;
    }
    // date 이전에 존재했던 Task 가져오기
    public List<Task> findTasksByDate(ZonedDateTime date){
        TypedQuery<Task> query =
                em.createQuery("select t " +
                        "from Task t " +
                        "where t.createdAt <= :date"
                                , Task.class)
                        .setParameter("date", date);


//        Long tastCnt =
//                em.createQuery("select COUNT(t) " +
//                        "from Task t " +
//                        "where t.createdAt <= :date " +
//                        "and t.endAt >= :date", Long.class)
//                        .setParameter("date", date)
//                        .getSingleResult();


        return query.getResultList();
    }

    // date 이전에 존재했던 DONE 인 Task 갯수 가져오기
    public int findDoneCountByDate(ZonedDateTime date){
        Long tastCnt =
                em.createQuery("select COUNT(t) " +
                                "from Task t " +
                                "where t.createdAt <= :date " +
                                "and t.progress = :done", Long.class)
                        .setParameter("date", date)
                        .setParameter("done", Task.Progress.DONE)
                        .getSingleResult();

        return  tastCnt.intValue();
    }
}
