package sherry.hosp.service.Impl;

import sherry.hosp.mapper.HospitalSetMapper;
import sherry.hosp.service.HospitalSetService;
import sherry.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
}
