package example;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static example.Type.ENTER_LUNCH;
import static example.Type.LEAVE_LUNCH;

public class AttendanceService {
    private final AttendanceDao attendanceDao;
    private final Duration DEFAULT_LUNCH_DURATION = Duration.ofMinutes(30);

    public AttendanceService(AttendanceDao attendanceDao) {
        this.attendanceDao = attendanceDao;
    }

    public Duration timeInTheOffice(long employeeId, LocalDate date) {
        List<Record> records = attendanceDao.getRecords(employeeId, date);
        Optional<Record> enterRecord = getRecordByType(records, Type.ENTER_OFFICE);
        Optional<Record> leaveRecord = getRecordByType(records, Type.LEAVE_OFFICE);
        if (enterRecord.isEmpty() || leaveRecord.isEmpty()) {
            return Duration.ZERO;
        }
        LocalTime enterTime = enterRecord.get().getTime();
        LocalTime leaveTime = leaveRecord.get().getTime();
        if (enterTime == null || leaveTime == null) {
            return Duration.ZERO;
        }
        return Duration.between(enterTime, leaveTime)
                .minus(timeForLunch(records));
    }

    private Duration timeForLunch(List<Record> records) {
        Optional<Record> lunchEnterRecord = getRecordByType(records, ENTER_LUNCH);
        Optional<Record> lunchLeaveRecord = getRecordByType(records, LEAVE_LUNCH);
        if (lunchEnterRecord.isEmpty() || lunchLeaveRecord.isEmpty()) {
            return DEFAULT_LUNCH_DURATION;
        }
        LocalTime lunchEnterTime = lunchEnterRecord.get().getTime();
        LocalTime lunchLeaveTime = lunchLeaveRecord.get().getTime();
        if (lunchEnterTime == null || lunchLeaveTime == null) {
            return DEFAULT_LUNCH_DURATION;
        }
        return Duration.between(lunchEnterTime, lunchLeaveTime);
    }


    private Optional<Record> getRecordByType(List<Record> records, Type type) {
        return records.stream()
                .filter(record -> record.getType() == type)
                .findFirst();
    }
}
