package tisse.dao;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import tisse.model.Person;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class PersonDao {

    private String[] params = {"PT_NAME", "PT_SURNAME", "PT_PATRONYMIC", "PT_WORKERNUM",
            "PT_GENDER", "PT_BIRTHDAY", "PT_DATE_BEGIN", "PT_DATE_END", "PT_FIRMLINK", "PT_JOBLINK", "PT_1CGUID",
            "PT_MAINWORKER_LINK"};

    @Inject
    private EntityManager em;
    @Inject
    private Logger logger;

    public List<Person> list() {
        String sql = "\twith params as(\n" +
                "\tselect *\n" +
                "\tfrom param_type pt\n" +
                "\twhere\n" +
                "\t\tpt.obj_type=1\n" +
                "\t\tand pt.id_name in('PT_NAME','PT_SURNAME','PT_PATRONYMIC','PT_WORKERNUM',\n" +
                "\t\t\t\t\t\t\t'PT_GENDER','PT_BIRTHDAY','PT_DATE_BEGIN','PT_DATE_END',\n" +
                "\t\t\t\t\t\t\t'PT_FIRMLINK','PT_JOBLINK','PT_1CGUID','PT_MAINWORKER_LINK')\n" +
                "\torder by 1\n" +
                "\t)\n" +
                "\tselect\n" +
                "\t\to.id,\n" +
                "\t\tp.id_name,\n" +
                "\t\t--p.param_name,\n" +
                "\t\top.value\n" +
                "\tfrom object o\n" +
                "\t\tjoin params p on p.obj_type=o.type\n" +
                "\t\tleft join object_params op on op.obj_id=o.id and op.param_id=p.id\n" +
                "\torder by o.id,p.id";

        List<Object[]> resultList = em.createNativeQuery(sql).getResultList();

        Map<BigInteger, List<Object[]>> collect = resultList.stream().collect(Collectors.groupingBy(o -> (BigInteger) o[0]));

        List<Person> people = new ArrayList<>();
        collect.forEach((key, strings) -> {
            Map<String, String> map = strings.stream().collect(Collectors.toMap(o -> (String) o[1], o -> ObjectUtils.defaultIfNull((String) o[2], "-")));
            people.add(convert(key, map));
        });

        return people;
    }

    private Person convert(BigInteger key, Map<String, String> map) {
        Person person = new Person();
        person.setId(key.intValue());
        person.setFirstName(map.get("PT_NAME"));
        person.setLastName(map.get("PT_SURNAME"));
        person.setMidName(map.get("PT_PATRONYMIC"));
        person.setTabNum(map.get("PT_WORKERNUM"));
        person.setGender(map.get("PT_GENDER"));
        person.setBirthDay(map.get("PT_BIRTHDAY"));
        person.setBeginDay(map.get("PT_DATE_BEGIN"));
        person.setEndDay(map.get("PT_DATE_END"));
        person.setFirmId(map.get("PT_FIRMLINK"));
        person.setJobId(map.get("PT_JOBLINK"));
        person.setGuid1c(map.get("PT_1CGUID"));
        return person;
    }

}
