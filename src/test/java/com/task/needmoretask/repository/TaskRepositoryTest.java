package com.task.needmoretask.repository;


import com.task.needmoretask.model.profile.Profile;
import com.task.needmoretask.model.profile.ProfileRepository;
import com.task.needmoretask.model.task.Task;
import com.task.needmoretask.model.task.TaskJPQLRepository;
import com.task.needmoretask.model.task.TaskRepository;
import com.task.needmoretask.model.user.User;
import com.task.needmoretask.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@Import({TaskJPQLRepository.class,BCryptPasswordEncoder.class})
@DataJpaTest
public class TaskRepositoryTest {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskJPQLRepository taskJPQLRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager em;

    long userId1,userId2,taskId;
    @BeforeEach
    void setUp(
            @Autowired ProfileRepository profileRepository
    ) {
        Profile profile = profileRepository.save(new Profile(null, "img.jpg"));
        User user1 = User.builder()
                .email("user1@email.com")
                .password("1234")
                .phone("010-0000-0000")
                .fullname("user1")
                .department(User.Department.HR)
                .joinCompanyYear(2023)
                .profile(profile)
                .role(User.Role.USER)
                .build();
        User user2 = User.builder()
                .email("user2@email.com")
                .password("1234")
                .phone("010-0000-0000")
                .fullname("user2")
                .department(User.Department.DEVELOPMENT)
                .joinCompanyYear(2024)
                .profile(profile)
                .role(User.Role.USER)
                .build();
        List<User> users = List.of(user1,user2);
        userRepository.saveAll(users);
        userId1 = user1.getId();
        userId2 = user2.getId();

        // given
        long userId = 1;
        User userPS = userRepository.findById(userId).orElse(null);
        LocalDate startAt = LocalDate.of(2023, 5, 3);
        LocalDate endAt = LocalDate.of(2023, 6, 3);

        for (int i = 0; i < 8; i++) {
            Task task1 = Task.builder()
                    .user(userPS)
                    .title("title"+i)
                    .description("desc"+i)
                    .startAt(startAt)
                    .endAt(endAt)
                    .progress(Task.Progress.DONE)
                    .priority(Task.Priority.LOW)
                    .build();

            taskRepository.save(task1);
        }

//        em.flush();
//        em.clear();
    }

    @Test
    @DisplayName("최신 생성 Task 7개")
    @DirtiesContext
    public void findLatestTasks_test(){
        List<Task> tasksPS = taskJPQLRepository.findLatestTasks();

        assertThat(tasksPS.size()).isEqualTo(7);
    }

    @Test
    @DisplayName("지정 날짜에 존재하는 Task")
    @DirtiesContext
    public void findTasksByDate(){
        List<Task> tasksPS = taskJPQLRepository.findTasksByDate(
                ZonedDateTime.now());

        assertThat(tasksPS.size()).isEqualTo(8);
    }

    @Test
    @DisplayName("지정 날짜에 존재하는 Done 인 Task 수")
    @DirtiesContext
    public void findDoneCountByDate(){
        int cnt = taskJPQLRepository.findDoneCountByDate(
                ZonedDateTime.now());

        assertThat(cnt).isEqualTo(8);
    }

    @Test
    @DisplayName("지정 날짜에 존재하는 지정 progress인 Task 수")
    @DirtiesContext
    public void findCountByProgressTime(){
        int cnt = taskJPQLRepository.findCountByProgressTime(
                Task.Progress.DONE,
                ZonedDateTime.now()
        );

        assertThat(cnt).isEqualTo(8);
    }

    @Test
    @DisplayName("[Kanban] 내가 Owner로 있는 task")
    @DirtiesContext
    public void findTasksByUserId(){
        Long userId = 1L;
        List<Task> tasksPS = taskJPQLRepository.findTasksByUserId(userId);

        assertThat(tasksPS.size()).isEqualTo(8);
    }

    @Test
    @DisplayName("[Calendar] Calendar조회")
    @DirtiesContext
    public void findTaskByStartEndDate(){
        LocalDate date = LocalDate.of(2023, 5, 2);
        List<Task> tasksPS = taskJPQLRepository.findTaskByStartEndDate(date);

        assertThat(tasksPS.size()).isEqualTo(8);
    }

    @Test
    @DisplayName("Daily OverView")
    @DirtiesContext
    public void findTasksByDaliyDate(){
        LocalDate date = LocalDate.of(2023, 5, 5);
        List<Task> tasksPS = taskJPQLRepository.findTasksByDaliyDate(date);

        assertThat(tasksPS.size()).isEqualTo(8);
    }

    @Test
    @DisplayName("admin OverView")
    @DirtiesContext
    public void findTasksByBetweenDate(){
        LocalDate startDate = LocalDate.of(2023, 3, 5);
        LocalDate endDate = LocalDate.of(2023, 5, 5);
        List<Task> tasksPS = taskJPQLRepository.findTasksByBetweenDate(startDate, endDate);

        assertThat(tasksPS.size()).isEqualTo(8);
    }

    @Test
    @DisplayName("Daily OverView")
    @DirtiesContext
    public void findTasksByDDate(){
        LocalDate date = LocalDate.of(2023, 5, 5);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> tasksPS = taskRepository.findByDate(date, pageable);

        assertThat(tasksPS.getTotalElements()).isEqualTo(8);
    }



}
