package example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static example.Type.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {

    @Mock
    private AttendanceDao attendanceDao;
    private AttendanceService attendanceService;

    private final long EMPLOYEE_ID = 1L;
    private final LocalDate DATE_TO_CHECK = LocalDate.now();

    @BeforeEach
    public void init() {
        attendanceService = new AttendanceService(attendanceDao);
    }

    @Test
    public void pressEnter_and_pressLeave_withoutLunch() {
        when(attendanceDao.getRecords(anyLong(), any()))
                .thenReturn(
                        List.of(new Record(ENTER_OFFICE, LocalTime.of(10, 30)),
                                new Record(LEAVE_OFFICE, LocalTime.of(18, 30))));
        Duration expectedAttendance = Duration.ofHours(7).plus(Duration.ofMinutes(30));
        assertEquals(expectedAttendance, attendanceService.timeInTheOffice(EMPLOYEE_ID, DATE_TO_CHECK));
    }

    @Test
    public void pressEnterOnly() {
        when(attendanceDao.getRecords(anyLong(), any()))
                .thenReturn(
                        List.of(new Record(ENTER_OFFICE, LocalTime.of(10, 30))));
        assertEquals(Duration.ZERO, attendanceService.timeInTheOffice(EMPLOYEE_ID, DATE_TO_CHECK));
    }

    @Test
    public void pressLeaveOnly() {
        when(attendanceDao.getRecords(anyLong(), any()))
                .thenReturn(
                        List.of(new Record(LEAVE_OFFICE, LocalTime.of(18, 0))));
        assertEquals(Duration.ZERO, attendanceService.timeInTheOffice(EMPLOYEE_ID, DATE_TO_CHECK
        ));
    }

    @Test
    public void pressEnter_and_pressLeave_withLunch() {
        when(attendanceDao.getRecords(anyLong(), any()))
                .thenReturn(
                        List.of(new Record(ENTER_OFFICE, LocalTime.of(10, 30)),
                                new Record(ENTER_LUNCH, LocalTime.of(13, 30)),
                                new Record(LEAVE_LUNCH, LocalTime.of(14, 30)),
                                new Record(LEAVE_OFFICE, LocalTime.of(18, 30))));
        assertEquals(Duration.ofHours(7), attendanceService.timeInTheOffice(EMPLOYEE_ID, DATE_TO_CHECK));
    }

    @Test
    public void pressLeaveEarlierThanEnter() {
        when(attendanceDao.getRecords(anyLong(), any()))
                .thenReturn(
                        List.of(new Record(LEAVE_OFFICE, LocalTime.of(10, 30)),
                                new Record(ENTER_OFFICE, LocalTime.of(11, 30))));
        assertEquals(Duration.ZERO, attendanceService.timeInTheOffice(EMPLOYEE_ID, DATE_TO_CHECK));
    }

    @Test
    public void pressLeaveLunchEarlierThanEnterLunch() {
        when(attendanceDao.getRecords(anyLong(), any()))
                .thenReturn(
                        List.of(new Record(ENTER_OFFICE, LocalTime.of(8, 0)),
                                new Record(LEAVE_LUNCH, LocalTime.of(11, 30)),
                                new Record(ENTER_LUNCH, LocalTime.of(12, 30)),
                                new Record(LEAVE_OFFICE, LocalTime.of(18, 30))));
        assertEquals(Duration.ofHours(10), attendanceService.timeInTheOffice(EMPLOYEE_ID, DATE_TO_CHECK));
    }

    @Test
    public void pressEnter_and_pressLeaveTwoTimes() {
        when(attendanceDao.getRecords(anyLong(), any()))
                .thenReturn(
                        List.of(new Record(ENTER_OFFICE, LocalTime.of(8, 0)),
                                new Record(LEAVE_OFFICE, LocalTime.of(16, 0)),
                                new Record(LEAVE_OFFICE, LocalTime.of(17, 30))));
        assertEquals(Duration.ofHours(9), attendanceService.timeInTheOffice(EMPLOYEE_ID, DATE_TO_CHECK));
    }

    @Test
    public void pressEnterTwoTimes_and_pressLeaveOneTime() {
        when(attendanceDao.getRecords(anyLong(), any()))
                .thenReturn(
                        List.of(new Record(ENTER_OFFICE, LocalTime.of(8, 0)),
                                new Record(ENTER_OFFICE, LocalTime.of(9, 0)),
                                new Record(LEAVE_OFFICE, LocalTime.of(16, 0))));
        Duration expectedDuration = Duration.ofHours(7).plus(Duration.ofMinutes(30));
        assertEquals(expectedDuration, attendanceService.timeInTheOffice(EMPLOYEE_ID, DATE_TO_CHECK));
    }

    @Test
    public void durationBetweenRecordsIsLessThanLunchTime() {
        when(attendanceDao.getRecords(anyLong(), any()))
                .thenReturn(
                        List.of(new Record(ENTER_OFFICE, LocalTime.of(8, 0)),
                                new Record(LEAVE_OFFICE, LocalTime.of(8, 25))));
        assertEquals(Duration.ofMinutes(25), attendanceService.timeInTheOffice(EMPLOYEE_ID, DATE_TO_CHECK));
    }
}
