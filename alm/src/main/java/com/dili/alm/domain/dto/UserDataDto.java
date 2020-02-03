package com.dili.alm.domain.dto;

import com.dili.ss.dto.IBaseDomain;

/**
 * <B>Description</B>
 * <B>Copyright</B>
 * 本软件源代码版权归农丰时代所有,未经许可不得任意复制与传播.<br />
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @createTime 2018/5/31 18:23
 */
public interface UserDataDto extends IBaseDomain {

    /**
     * 数据ID
     * @return
     */
    String getTreeId();
    void setTreeId(String treeId);

    /**
     * 父级ID
     * @return
     */
    String getParentId();
    void setParentId(String parentId);

    /**
     * 节点名称
     * @return
     */
    String getName();
    void setName(String name);

    /**
     * 节点是否选中
     * @return
     */
    Boolean getChecked();
    void setChecked(Boolean checked);
}
