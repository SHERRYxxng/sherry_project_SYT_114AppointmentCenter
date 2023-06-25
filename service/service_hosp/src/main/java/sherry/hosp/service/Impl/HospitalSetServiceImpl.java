package sherry.hosp.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import sherry.hosp.mapper.HospitalSetMapper;
import sherry.hosp.service.HospitalSetService;
import sherry.model.hosp.HospitalSet;

@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {

}