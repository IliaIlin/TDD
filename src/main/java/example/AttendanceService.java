package example;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static example.Type.*;

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
        if (enterTime == null || leaveTime == null || leaveTime.isBefore(enterTime)) {
            return Duration.ZERO;
        }
        Duration inOffice = Duration.between(enterTime, leaveTime);
        Duration lunchDuration = timeForLunch(records);
        return inOffice.compareTo(lunchDuration) > 0 ? inOffice.minus(lunchDuration) : inOffice;
    }

    private Duration timeForLunch(List<Record> records) {
        Optional<Record> lunchEnterRecord = getRecordByType(records, ENTER_LUNCH);
        Optional<Record> lunchLeaveRecord = getRecordByType(records, LEAVE_LUNCH);
        if (lunchEnterRecord.isEmpty() || lunchLeaveRecord.isEmpty()) {
            return DEFAULT_LUNCH_DURATION;
        }
        LocalTime lunchEnterTime = lunchEnterRecord.get().getTime();
        LocalTime lunchLeaveTime = lunchLeaveRecord.get().getTime();
        if (lunchEnterTime == null || lunchLeaveTime == null || lunchLeaveTime.isBefore(lunchEnterTime)) {
            return DEFAULT_LUNCH_DURATION;
        }
        return Duration.between(lunchEnterTime, lunchLeaveTime);
    }


    private Optional<Record> getRecordByType(List<Record> records, Type type) {
        Stream<Record> recordStream = records.stream()
                .filter(record -> record.getType() == type);
        if (type == ENTER_OFFICE || type == ENTER_LUNCH) {
            return recordStream.min(Comparator.comparing(Record::getTime));
        }
        return recordStream.max(Comparator.comparing(Record::getTime));
    }
}
