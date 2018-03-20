package com.dili.alm.service.impl;

import com.dili.alm.dao.EmailAddressMapper;
import com.dili.alm.domain.EmailAddress;
import com.dili.alm.service.EmailAddressService;
import com.dili.ss.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-03-13 15:29:09.
 */
@Service
public class EmailAddressServiceImpl extends BaseServiceImpl<EmailAddress, Long> implements EmailAddressService {

    public EmailAddressMapper getActualDao() {
        return (EmailAddressMapper)getDao();
    }
}