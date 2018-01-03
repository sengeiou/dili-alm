package com.dili.alm.provider;

import com.dili.alm.cache.AlmCache;
import com.dili.alm.domain.Department;
import com.dili.alm.domain.User;
import com.dili.alm.rpc.DepartmentRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValuePairImpl;
import com.dili.ss.metadata.ValueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DepProvider implements ValueProvider{


    @Autowired
    private DepartmentRpc departmentRpc;


    @PostConstruct
    public void init(){
        //应用启动时初始化userMap
        if(AlmCache.DEP_MAP.isEmpty()){
            BaseOutput<List<Department>> output = departmentRpc.list(new Department());
            if(output.isSuccess()){
                output.getData().forEach(dep ->{
                    AlmCache.DEP_MAP.put(dep.getId(), dep);
                });
            }
        }
    }
    @Override
    public List<ValuePair<?>> getLookupList(Object o, Map map, FieldMeta fieldMeta) {
        init();
        ArrayList buffer = new ArrayList<ValuePair<?>>();
        AlmCache.DEP_MAP.forEach((k, v)->{
            buffer.add(new ValuePairImpl(v.getName(), k));
        });
        return buffer;
    }

    @Override
    public String getDisplayText(Object o, Map map, FieldMeta fieldMeta) {
        if(o == null) return null;
        init();
        Department dep = AlmCache.DEP_MAP.get(o);
        return dep == null ? null : dep.getName();
    }
}
