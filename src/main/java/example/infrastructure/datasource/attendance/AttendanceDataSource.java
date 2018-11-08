package example.infrastructure.datasource.attendance;

import example.domain.model.attendance.AttendanceOfDay;
import example.domain.model.attendance.AttendanceOfMonth;
import example.domain.model.attendance.AttendanceRepository;
import example.domain.model.worker.WorkerIdentifier;
import example.domain.type.date.Date;
import example.domain.type.date.YearMonth;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

@Repository
public class AttendanceDataSource implements AttendanceRepository {
    AttendanceMapper mapper;

    @Override
    public void registerWorkTime(WorkerIdentifier userId, AttendanceOfDay work) {
        Long identifier = mapper.newWorkTimeIdentifier();
        mapper.registerWorkTime(identifier, userId, work);
        mapper.deleteWorkTimeMapper(userId, work.date());
        mapper.registerWorkTimeMapper(identifier, userId, work.date());
    }

    @Override
    public AttendanceOfDay findBy(WorkerIdentifier userId, Date workDay) {
        AttendanceOfDay ret = mapper.findBy(userId, workDay);
        return (ret == null) ? new AttendanceOfDay(workDay) : ret;
    }

    @Override
    public AttendanceOfMonth findMonthly(WorkerIdentifier userId, YearMonth month) {
        return new AttendanceOfMonth(month, month.days().stream()
                .map(day -> findBy(userId, day)).collect(Collectors.toList()));
    }

    AttendanceDataSource(AttendanceMapper mapper) {
        this.mapper = mapper;
    }
}
