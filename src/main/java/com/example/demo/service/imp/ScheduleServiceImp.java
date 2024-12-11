/**
 * Implementation service for managing schedules.
 * This service provides methods to perform CRUD operations on schedules and suggested schedules in the database.
 */
package com.example.demo.service.imp;

import com.example.demo.exception.*;
import com.example.demo.model.agriculture.*;
import com.example.demo.persistence.ScheduleRepository;
import com.example.demo.persistence.SuggestedScheduleRepository;
import com.example.demo.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImp implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private SuggestedScheduleRepository suggestedScheduleRepository;

    /**
     * Retrieves all schedules from the database.
     * If the table is empty, an exception is thrown.
     *
     * @return A list of schedules found in the database.
     * @throws EmptyTableException When the schedules table has no records.
     */
    @Override
    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        if (schedules.isEmpty()) throw new EmptyTableException(Schedule.class);
        return schedules;
    }

    /**
     * Retrieves all suggested schedules from the database.
     * If the table is empty, an exception is thrown.
     *
     * @return A list of suggested schedules found in the database.
     * @throws EmptyTableException When the suggested schedules table has no records.
     */
    @Override
    public List<SuggestedSchedule> getAllSuggestedSchedule() {
        List<SuggestedSchedule> suggestedSchedules = suggestedScheduleRepository.findAll();
        if (suggestedSchedules.isEmpty()) throw new EmptyTableException(SuggestedSchedule.class);
        return suggestedSchedules;
    }

    /**
     * Retrieves a suggested schedule by its ID.
     *
     * @param id The ID of the suggested schedule to retrieve.
     * @return The suggested schedule found.
     * @throws EmptyRecordException When no suggested schedule is found with the provided ID.
     */
    @Override
    public SuggestedSchedule getSuggestedScheduleById(Long id) {
        Optional<SuggestedSchedule> schedule = suggestedScheduleRepository.findById(id);
        if (schedule.isEmpty()) throw new EmptyRecordException(SuggestedSchedule.class);
        return schedule.get();
    }

    /**
     * Retrieves a schedule by its ID.
     *
     * @param id The ID of the schedule to retrieve.
     * @return The schedule found.
     * @throws EmptyRecordException When no schedule is found with the provided ID.
     */
    @Override
    public Schedule getScheduleById(Long id) {
        Optional<Schedule> schedule = scheduleRepository.findById(id);
        if (schedule.isEmpty()) throw new EmptyRecordException(Schedule.class);
        return schedule.get();
    }

    /**
     * Retrieves all schedules filtered by crop, status, and date.
     * If no schedules match the criteria, an exception is thrown.
     *
     * @param crop The crop to filter schedules by.
     * @param status The status to filter schedules by.
     * @param date The date to filter schedules by.
     * @return A list of schedules matching the criteria.
     * @throws EmptyFilterException When no schedules match the provided filters.
     */
    @Override
    public List<Schedule> getAllScheduleByCropAndStatusAndDate(Crop crop,
                                                               ScheduleStatus status, LocalDate date) {
        List<Schedule> schedules = scheduleRepository.findByCropAndStatusAndDate(crop, status, date);
        if (schedules.isEmpty()) throw new EmptyFilterException(Schedule.class, Crop.class, ScheduleStatus.class, LocalDate.class);
        return schedules;
    }

    /**
     * Filters all suggested schedules by their status and crop.
     * If no schedules match the criteria, an exception is thrown.
     *
     * @param status The status to filter suggested schedules by.
     * @param crop The crop to filter suggested schedules by.
     * @return A list of suggested schedules matching the criteria.
     * @throws EmptyFilterException When no suggested schedules match the provided filters.
     */
    @Override
    public List<SuggestedSchedule> getAllSuggestedScheduleByStatusAndCrop(SuggestedScheduleStatus status, Crop crop) {
        List<SuggestedSchedule> schedules = suggestedScheduleRepository.findByStatusAndCrop(status, crop);
        if (schedules.isEmpty()) throw new EmptyFilterException(SuggestedSchedule.class, SuggestedScheduleStatus.class, Crop.class);
        return schedules;
    }

    /**
     * Filters all suggested schedules by their status.
     * If no schedules match the criteria, an exception is thrown.
     *
     * @param status The status to filter suggested schedules by.
     * @return A list of suggested schedules matching the criteria.
     * @throws EmptyFilterException When no suggested schedules match the provided filter.
     */
    @Override
    public List<SuggestedSchedule> getAllSuggestedScheduleByStatus(SuggestedScheduleStatus status) {
        List<SuggestedSchedule> schedules = suggestedScheduleRepository.findByStatus(status);
        if (schedules.isEmpty()) throw new EmptyFilterException(SuggestedSchedule.class, SuggestedScheduleStatus.class);
        return schedules;
    }

    /**
     * Creates or updates a suggested schedule in the database.
     * If the suggested schedule is not saved successfully, an exception is thrown.
     *
     * @param suggestedSchedule The suggested schedule to be saved.
     * @return The saved suggested schedule.
     * @throws SaveRecordFailException When the suggested schedule could not be saved.
     */
    @Override
    public SuggestedSchedule saveSuggestedSchedule(SuggestedSchedule suggestedSchedule) {
        suggestedSchedule.setUpdatedAt(LocalDateTime.now());
        SuggestedSchedule savedSuggestedSchedule = suggestedScheduleRepository.save(suggestedSchedule);
        if (savedSuggestedSchedule.getId() == null) throw new SaveRecordFailException(SuggestedSchedule.class);
        return savedSuggestedSchedule;
    }

    /**
     * Saves a schedule to the database, creating a new record or updating an existing one.
     * This method first validates the provided schedule to ensure it meets the necessary criteria,
     * including that the start time is in the future and that there are no conflicts with existing schedules.
     * If the validation fails, an appropriate exception is thrown.
     * After validation, the schedule is saved to the database. If the save operation is unsuccessful
     * a {@link SaveRecordFailException} is thrown.
     *
     * @param schedule The schedule object to be saved.
     * @return The saved schedule object.
     * @throws FutureScheduleException If the schedule's start time is in the past.
     * @throws ScheduleConflictException If the schedule conflicts with existing schedules for the same crop.
     * @throws SaveRecordFailException If the schedule could not be saved to the database.
     */
    @Override
    public Schedule saveSchedule(Schedule schedule) {
        validateSchedule(schedule);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        if (savedSchedule.getId() == null) {
            throw new SaveRecordFailException(Schedule.class);
        }
        return savedSchedule;
    }

    /**
     * Validates the given schedule to ensure it meets the necessary criteria.
     *
     * @param schedule the schedule to validate
     * @throws FutureScheduleException if the schedule's start time is in the past
     * @throws ScheduleConflictException if the schedule conflicts with existing schedules
     */
    private void validateSchedule(Schedule schedule) {
        LocalDateTime startDateTime = schedule.getDate().atTime(schedule.getStartTime());
        // Check if the schedule's start time is in the past
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new FutureScheduleException();
        }
        List<Schedule> schedules = getPendingSchedulesForToday(schedule.getCrop());
        // Check for conflicts with existing schedules
        for (Schedule existingSchedule : schedules) {
            if (schedule.getStartTime().isBefore(existingSchedule.getEndTime()) &&
                    schedule.getEndTime().isAfter(existingSchedule.getStartTime())) {
                throw new ScheduleConflictException();
            }
        }
    }

    /**
     * Retrieves all pending schedules for the given crop for today.
     *
     * @param crop the crop for which to fetch schedules
     * @return a list of pending schedules for today
     */
    private List<Schedule> getPendingSchedulesForToday(Crop crop) {
        return getAllScheduleByCropAndStatusAndDate(crop, ScheduleStatus.PENDING, LocalDate.now());
    }
}