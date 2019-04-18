package tisse.dao;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import tisse.model.DeptOld;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class DeptOldDao {


    @Inject
    private EntityManager em;
    @Inject
    private Logger logger;


    public List<DeptOld> list() {

        String sql = "\twith\n" +
                "\tparams as(\n" +
                "\tselect *\n" +
                "\tfrom param_type pt\n" +
                "\twhere\n" +
                "\t\tpt.obj_type=4\n" +
                "\t\tand pt.id_name in('PT_NAME','PT_FIRMLINK','PT_DEPARTNUM','PT_TIMEZONE','PT_DESCRIPTION','PT_1CGUID')\n" +
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
                "\torder by o.id,p.id\n";

        List<Object[]> resultList = em.createNativeQuery(sql).getResultList();

        Map<BigInteger, List<Object[]>> collect = resultList.stream().collect(Collectors.groupingBy(o -> (BigInteger) o[0]));

        List<DeptOld> depts = new ArrayList<>();
        collect.forEach((key, strings) -> {
            Map<String, String> map = strings.stream().collect(Collectors.toMap(o -> (String) o[1], o -> ObjectUtils.defaultIfNull((String) o[2], "-")));
            depts.add(convert(key, map));
        });
        return depts;
    }

    public DeptOld find(BigInteger id) {
        String sql = "\twith\n" +
                "\tparams as(\n" +
                "\tselect *\n" +
                "\tfrom param_type pt\n" +
                "\twhere\n" +
                "\t\tpt.obj_type=4\n" +
                "\t\tand pt.id_name in('PT_NAME','PT_FIRMLINK','PT_DEPARTNUM','PT_TIMEZONE','PT_DESCRIPTION','PT_1CGUID')\n" +
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
                "where o.id = :id " +
                "\torder by p.id\n";

        List<Object[]> resultList = em.createNativeQuery(sql)
                .setParameter("id", id)
                .getResultList();

        if (CollectionUtils.isNotEmpty(resultList)) {
            Map<String, String> map = resultList.stream().collect(Collectors.toMap(o -> (String) o[1], o -> ObjectUtils.defaultIfNull((String) o[2], "-")));
            return (convert(id, map));
        }
        return null;
    }

    private DeptOld convert(BigInteger key, Map<String, String> map) {
        DeptOld dept = new DeptOld();
        dept.setId(key.intValue());
        dept.setDepartNum(map.get("PT_DEPARTNUM"));
        dept.setName(map.get("PT_NAME"));
        dept.setFirmLink(map.get("PT_FIRMLINK"));
        dept.setTimeZone(map.get("PT_TIMEZONE"));
        dept.setDescription(map.get("PT_DESCRIPTION"));
        dept.setGuid1c(map.get("PT_1CGUID"));
        return dept;
    }


}
