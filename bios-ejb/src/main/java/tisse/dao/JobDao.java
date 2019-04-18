package tisse.dao;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import tisse.model.Job;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class JobDao {

    @Inject
    private EntityManager em;
    @Inject
    private Logger logger;

    public List<Job> list() {
        String sql = "with params as(\n" +
                "    select *\n" +
                "    from param_type pt\n" +
                "    where\n" +
                "      pt.obj_type=6\n" +
                "      and pt.id_name in('PT_NAME','PT_1CGUID')\n" +
                "    order by 1\n" +
                ")\n" +
                "select\n" +
                "  o.id,\n" +
                "  p.id_name,\n" +
                "  --p.param_name,\n" +
                "  op.value\n" +
                "from object o\n" +
                "  join params p on p.obj_type=o.type\n" +
                "  left join object_params op on op.obj_id=o.id and op.param_id=p.id\n" +
                "order by o.id,p.id;\n";

        List<Object[]> resultList = em.createNativeQuery(sql).getResultList();

        Map<BigInteger, List<Object[]>> collect = resultList.stream().collect(Collectors.groupingBy(o -> (BigInteger) o[0]));

        List<Job> jobs = new ArrayList<>();
        collect.forEach((key, strings) -> {
            Map<String, String> map = strings.stream().collect(Collectors.toMap(o -> (String) o[1], o -> ObjectUtils.defaultIfNull((String) o[2], "-")));
            jobs.add(convert(key, map));
        });

        return jobs;
    }

    private Job convert(BigInteger key, Map<String, String> map) {
        Job job = new Job();
        job.setId(key.intValue());
        job.setName(map.get("PT_NAME"));
        job.setGuid1c(map.get("PT_1CGUID"));
        return job;
    }

}
