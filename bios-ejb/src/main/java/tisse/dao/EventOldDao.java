package tisse.dao;

import org.slf4j.Logger;
import tisse.model.Event;
import tisse.model.EventOld;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Stateless
public class EventOldDao {

    @Inject
    private EntityManager em;
    @Inject
    private Logger logger;

    public List<EventOld> list(Integer sub) {
        String sql = "\tselect\n" +
                "\t\t\"date\",\n" +
                "\t\tdate_local,\n" +
                "\t\tsubject_id,\n" +
                "\t\tobject_id,\n" +
                "\t\tcard_id,\n" +
                "\t\tevent,\n" +
                "\t\tfirm,\n" +
                "\t\tcurtime\n" +
                "\tfrom\n" +
                "\t\tlog_indexed l\n" +
                "\twhere "+
// l.date_local >= :df  and l.date_local < :d and
 "l.event in(151,152,202,203) "+
// and l.subject_id= :sub \n" +
                "\torder by l.date_local desc limit 10;";

        Calendar df = Calendar.getInstance();
        df.clear();
        df.add(Calendar.DAY_OF_YEAR, -1);

        Calendar dt = Calendar.getInstance();
        dt.clear();

        List<Object[]> resultList = em.createNativeQuery(sql)
//                .setParameter("sub", sub)
//                .setParameter("df", df)
//                .setParameter("dt", dt)
                .getResultList();

        List<EventOld> events = new ArrayList<>();
        for (Object[] objects : resultList) {
            EventOld event = new EventOld();
            int i = 0;
            event.setDate(getCalendar(objects[i++]));
            event.setDateLocal(getCalendar(objects[i++]));
            event.setSubjectId((BigInteger) objects[i++]);
            event.setObjectId((BigInteger) objects[i++]);
            event.setCardId((BigInteger) objects[i++]);
            event.setEvent((Integer) objects[i++]);
            event.setDept((BigInteger) objects[i++]);
            event.setCurTime(getCalendarFromTimestamp(objects[i]));
            events.add(event);
        }
        return events;
    }

    private Calendar getCalendar(Object object) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(((Integer) object).longValue());
        return instance;
    }

    private Calendar getCalendarFromTimestamp(Object object) {
        Timestamp timestamp = (Timestamp) object;
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis((timestamp).getTime());
        return instance;
    }

}
