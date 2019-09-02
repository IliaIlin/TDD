package example;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class AttendanceService {
    private final AttendanceDao attendanceDao;

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
                .minus(Duration.ofMinutes(30));
    }

    private Optional<Record> getRecordByType(List<Record> records, Type type) {
        return records.stream()
                .filter(record -> record.getType() == type)
                .findFirst();
    }
}
