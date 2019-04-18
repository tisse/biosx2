package tisse.dao;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import tisse.model.Firm;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class FirmDao {

    private static final String PT_FIRMLINK = "PT_FIRMLINK";
    private static final String PT_NAME = "PT_NAME";
    private static final String PT_DEPARTNUM = "PT_DEPARTNUM";

    @Inject
    private EntityManager em;
    @Inject
    private Logger logger;

    public List<Firm> list() {

        String[] params = {PT_NAME,
                PT_DEPARTNUM,
                PT_FIRMLINK,
                "PT_DESCRIPTION"};

        String sql = "with params as(\n" +
                "select *\n" +
                "from param_type pt\n" +
                "where\n" +
                "pt.obj_type=3\n" +
                "and pt.id_name in('PT_NAME','PT_FIRMLINK','PT_DEPARTNUM','PT_DESCRIPTION')\n" +
                "order by 1\n" +
                ")\n" +
                "select\n" +
                "o.id,\n" +
                "p.id_name,\n" +
                "--p.param_name,\n" +
                "op.value\n" +
                "from object o\n" +
                "join params p on p.obj_type=o.type\n" +
                "left join object_params op on op.obj_id=o.id and op.param_id=p.id\n" +
                "order by o.id, p.id;";

        List<Object[]> resultList = em.createNativeQuery(sql).getResultList();

        Map<BigInteger, List<Object[]>> collect = resultList.stream().collect(Collectors.groupingBy(o -> (BigInteger) o[0]));

        List<Firm> firms = new ArrayList<>();
        collect.forEach((key, strings) -> {
            Map<String, String> map = strings.stream().collect(Collectors.toMap(o -> (String) o[1], o -> ObjectUtils.defaultIfNull((String) o[2], "-")));
            firms.add(convert(key, map));
        });
        return firms;
    }

    private Firm convert(BigInteger key, Map<String, String> map) {
        Firm firm = new Firm();
        firm.setId(key.intValue());
        firm.setBiosmartId(key.toString());
        firm.setDepartmentId(map.get(PT_DEPARTNUM));
        firm.setName(map.get(PT_NAME));
        return firm;
    }

}
