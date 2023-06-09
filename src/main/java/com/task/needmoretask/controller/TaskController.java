package com.task.needmoretask.controller;

import com.task.needmoretask.core.auth.session.MyUserDetails;
import com.task.needmoretask.dto.ResponseDTO;
import com.task.needmoretask.dto.task.TaskRequest;
import com.task.needmoretask.dto.task.TaskResponse;
import com.task.needmoretask.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    // Task 생성
    @PostMapping("/task")
    public ResponseEntity<?> createTask(@RequestBody @Valid TaskRequest taskRequest, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        taskService.createTask(taskRequest, myUserDetails.getUser());
        return ResponseEntity.ok(new ResponseDTO<>());
    }

    // Task 수정
    @PutMapping("/task/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody @Valid TaskRequest taskRequest, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails){
        TaskResponse.Test task = taskService.updateTask(id, taskRequest, myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(task));
    }

    // Task 삭제
    @DeleteMapping("/task/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){
        TaskResponse.Delete delete = taskService.deleteTask(id,myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(delete));
    }

    // Task 상세보기
    @GetMapping("/task/{id}")
    public ResponseEntity<?> getDetailTask(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails){
        TaskResponse.Detail detail = taskService.getDetailTask(id);
        return ResponseEntity.ok().body(new ResponseDTO<>(detail));
    }

    // [DashBoard] 가장 최근 생성된 task 7개 return
    @GetMapping("/tasks/latest")
    public ResponseEntity<?> getLatestTasks(){
        List<TaskResponse.LatestTaskOutDTO> responseList;
        responseList = taskService.getLatestTasks();

        return ResponseEntity.ok().body(new ResponseDTO<>(responseList));
    }

    // [DashBoard] 최근 2주간의 task, done 갯수 return
    @GetMapping("/performance")
    public ResponseEntity<?> getPerfomances(){
        List<TaskResponse.PerformanceOutDTO> responceList;
        responceList = taskService.getPerfomance();

        return ResponseEntity.ok().body(new ResponseDTO<>(responceList));
    }

    // [DashBoard] 최근 1주일간의 통계 데이터
    @GetMapping("/progress")
    public ResponseEntity<?> getProgresses(){
        TaskResponse.ProgressOutDTO responce;
        responce = taskService.getProgress();

        return ResponseEntity.ok().body(new ResponseDTO<>(responce));
    }

    // [Kanban] 내가 속한 task 조회
    @GetMapping("/kanbans")
    public ResponseEntity<?> getKanbans(@AuthenticationPrincipal MyUserDetails myUserDetails){
        Long id = myUserDetails.getUser().getId();

        List<TaskResponse.KanbanOutDTO> kanbanOutDTOList;
        kanbanOutDTOList = taskService.getKanban(id);

        return ResponseEntity.ok().body(new ResponseDTO<>(kanbanOutDTOList));
    }

    // [Calendar] 조회
    @GetMapping("/calendars")
    public ResponseEntity<?> getCalendars(
            @RequestParam("year") int year,
            @RequestParam("month") int month){

        List<TaskResponse.CalendarOutDTO> calendarOutDTOList;
        calendarOutDTOList = taskService.getCalendar(year, month);

        return ResponseEntity.ok().body(new ResponseDTO<>(calendarOutDTOList));
    }

    // Daily Overview
    @GetMapping("/tasks")
    public ResponseEntity<?> getDailyTasks(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam("page") int page
            ){
        Pageable pageable = PageRequest.of(page, 10);
        TaskResponse.DailyTasksOutDTO dailyTasksOutDTO;
        dailyTasksOutDTO = taskService.getDailyTasks(date, pageable);

        return ResponseEntity.ok().body(new ResponseDTO<>(dailyTasksOutDTO));
    }

    // Admin Overview
    @GetMapping("/tasks/period")
    public ResponseEntity<?> getPickedTasks(
            @RequestParam("startat") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endat") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam("page") int page
    ){
        Pageable pageable = PageRequest.of(page, 10);
        TaskResponse.DailyTasksOutDTO adminDailyTasksOutDTOList;
        adminDailyTasksOutDTOList = taskService.getPickedTasks(startDate, endDate, pageable);

        return ResponseEntity.ok().body(new ResponseDTO<>(adminDailyTasksOutDTOList));
    }
}
