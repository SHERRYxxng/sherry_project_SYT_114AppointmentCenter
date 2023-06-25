package sherry.hosp.mapper;

import sherry.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

// 实体类HospitalSet ， 对应HospitalSetService + HospitalSetServiceImpl 对应HospitalSetController
// HospitalSetMapper
@Repository
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {
    public Integer calCount();
}
