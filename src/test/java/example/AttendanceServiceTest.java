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

import static example.Type.ENTER_OFFICE;
import static example.Type.LEAVE_OFFICE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceTest {

    @Mock
    private AttendanceDao attendanceDao;
    private AttendanceService attendanceService;

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
        assertEquals(expectedAttendance, attendanceService.timeInTheOffice(1L, LocalDate.now()));
    }
}
