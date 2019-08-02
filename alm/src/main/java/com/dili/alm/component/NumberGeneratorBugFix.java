package com.dili.alm.component;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dili.alm.constant.AlmConstants.ApproveType;
import com.dili.alm.dao.ApproveMapper;
import com.dili.alm.dao.HardwareResourceApplyMapper;
import com.dili.alm.dao.ProjectApplyMapper;
import com.dili.alm.dao.ProjectChangeMapper;
import com.dili.alm.dao.ProjectCompleteMapper;
import com.dili.alm.dao.ProjectMapper;
import com.dili.alm.dao.ProjectOnlineApplyMapper;
import com.dili.alm.dao.TravelCostApplyMapper;
import com.dili.alm.dao.WorkOrderMapper;
import com.dili.alm.domain.ApplyType;
import com.dili.alm.domain.Approve;
import com.dili.alm.domain.HardwareResourceApply;
import com.dili.alm.domain.Project;
import com.dili.alm.domain.ProjectApply;
import com.dili.alm.domain.ProjectChange;
import com.dili.alm.domain.ProjectComplete;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.TravelCostApply;
import com.dili.alm.domain.WorkOrder;
import com.dili.alm.provider.WorkOrderTypeProvider;
import com.dili.ss.dto.DTOUtils;

import tk.mybatis.mapper.entity.Example;

@Component
public class NumberGeneratorBugFix implements InitializingBean {

	@Autowired
	private ProjectApplyMapper projectApplyMapper;
	@Autowired
	private ProjectMapper projectMapper;
	@Autowired
	private ApproveMapper approveMapper;
	@Autowired
	private ProjectChangeMapper projectChangeMapper;
	@Autowired
	private ProjectCompleteMapper projectCompleteMapper;
	@Autowired
	private ProjectOnlineApplyMapper projectOnlineApplyMapper;
	@Autowired
	private HardwareResourceApplyMapper hraMapper;
	@Autowired
	private TravelCostApplyMapper tcaMapper;
	@Autowired
	private WorkOrderMapper workOrderMapper;
	private static final Logger LOGGER = LoggerFactory.getLogger(NumberGeneratorBugFix.class);
	@Value("${com.diligrp.alm.serial-number-bug-fix-switch:false}")
	private Boolean fixSwitch;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!this.fixSwitch) {
			return;
		}
		LOGGER.info("=======================开始修复各个模块序列号..........");
		this.fixHardwareNumber();
		this.fixProjectNumber();
		this.fixProjectOnlineNumber();
		this.fixTravelCostNumber();
		this.fixWorkOrderNumber();
		LOGGER.info("=======================修复完成..........");
	}

	private void fixProjectNumber() {
		ProjectApply record = DTOUtils.newDTO(ProjectApply.class);
		record.setOrder("id");
		record.setSort("desc");
		List<ProjectApply> projectApplyList = this.projectApplyMapper.select(record);
		Iterator<ProjectApply> it = projectApplyList.iterator();
		while (it.hasNext()) {
			ProjectApply pa = it.next();
			String[] strs = pa.getNumber().split("-");
			int number = Integer.valueOf(strs[2]);
			DecimalFormat df = new DecimalFormat("0000");
			boolean needUpdate = false;
			do {
				Example example = new Example(ProjectApply.class);
				example.createCriteria().andLike("number", "%-" + df.format(number));
				List<ProjectApply> tmpList = this.projectApplyMapper.selectByExample(example);
				if (tmpList.stream().filter(pa1 -> !pa1.getId().equals(pa.getId())).findFirst().orElse(null) != null || number == 0) {
					number++;
					needUpdate = true;
					continue;
				} else {
					break;
				}
			} while (true);
			if (needUpdate) {
				String projectNumber = strs[0] + "-" + strs[1] + "-" + df.format(number);
				pa.setNumber(projectNumber);
				this.projectApplyMapper.updateByPrimaryKey(pa);
				Project pq = DTOUtils.newDTO(Project.class);
				pq.setApplyId(pa.getId());
				Approve aq = DTOUtils.newDTO(Approve.class);
				aq.setProjectApplyId(pa.getId());
				aq.setType(ApproveType.APPLY.getCode());
				Approve a = this.approveMapper.selectOne(aq);
				if (a != null) {
					a.setNumber(projectNumber);
					this.approveMapper.updateByPrimaryKey(a);
				}
				Project p = this.projectMapper.selectOne(pq);
				if (p != null) {
					p.setSerialNumber(projectNumber);
					this.projectMapper.updateByPrimaryKey(p);
					ProjectChange pcq = DTOUtils.newDTO(ProjectChange.class);
					pcq.setProjectId(p.getId());
					List<ProjectChange> pcList = this.projectChangeMapper.select(pcq);
					pcList.forEach(pc -> {
						pc.setNumber(projectNumber);
						this.projectChangeMapper.updateByPrimaryKey(pc);
						Approve aq1 = DTOUtils.newDTO(Approve.class);
						aq1.setProjectApplyId(pc.getId());
						aq1.setType(ApproveType.CHANGE.getCode());
						Approve a1 = this.approveMapper.selectOne(aq1);
						if (a1 != null) {
							a1.setNumber(projectNumber);
							this.approveMapper.updateByPrimaryKey(a1);
						}
					});
					ProjectComplete pcq1 = DTOUtils.newDTO(ProjectComplete.class);
					pcq1.setProjectId(p.getId());
					List<ProjectComplete> pc1List = this.projectCompleteMapper.select(pcq1);
					pc1List.forEach(pc -> {
						pc.setNumber(projectNumber);
						this.projectCompleteMapper.updateByPrimaryKey(pc);
						Approve aq1 = DTOUtils.newDTO(Approve.class);
						aq1.setProjectApplyId(pc.getId());
						aq1.setType(ApproveType.COMPLETE.getCode());
						Approve a1 = this.approveMapper.selectOne(aq1);
						if (a1 != null) {
							a1.setNumber(projectNumber);
							this.approveMapper.updateByPrimaryKey(a1);
						}
					});
					ProjectOnlineApply poaq = DTOUtils.newDTO(ProjectOnlineApply.class);
					poaq.setProjectId(p.getId());
					List<ProjectOnlineApply> poaList = this.projectOnlineApplyMapper.select(poaq);
					poaList.forEach(poa -> {
						poa.setProjectSerialNumber(projectNumber);
						this.projectOnlineApplyMapper.updateByPrimaryKey(poa);
					});
					HardwareResourceApply hraq = DTOUtils.newDTO(HardwareResourceApply.class);
					hraq.setProjectId(p.getId());
					List<HardwareResourceApply> hraList = this.hraMapper.select(hraq);
					hraList.forEach(hra -> {
						hra.setProjectSerialNumber(projectNumber);
						this.hraMapper.updateByPrimaryKey(hra);
					});
				}
			}
		}
	}

	private void fixHardwareNumber() {
		HardwareResourceApply record = DTOUtils.newDTO(HardwareResourceApply.class);
		record.setOrder("id");
		record.setSort("desc");
		List<HardwareResourceApply> hraList = this.hraMapper.select(record);
		hraList.forEach(hra -> {
			int number = Integer.valueOf(hra.getSerialNumber());
			DecimalFormat df = new DecimalFormat("00000");
			boolean needUpdate = false;
			do {
				HardwareResourceApply hraq = DTOUtils.newDTO(HardwareResourceApply.class);
				hraq.setSerialNumber(df.format(number));
				List<HardwareResourceApply> tmpList = this.hraMapper.select(hraq);
				if (tmpList.stream().filter(h -> !h.getId().equals(hra.getId())).findFirst().orElse(null) != null || number == 0) {
					number++;
					needUpdate = true;
					continue;
				} else {
					break;
				}
			} while (true);
			if (needUpdate) {
				hra.setSerialNumber(df.format(number));
				this.hraMapper.updateByPrimaryKey(hra);
			}
		});

	}

	private void fixProjectOnlineNumber() {
		ProjectOnlineApply record = DTOUtils.newDTO(ProjectOnlineApply.class);
		record.setOrder("id");
		record.setSort("desc");
		List<ProjectOnlineApply> poaList = this.projectOnlineApplyMapper.select(record);
		poaList.forEach(poa -> {
			int number = Integer.valueOf(poa.getSerialNumber());
			DecimalFormat df = new DecimalFormat("0000");
			boolean needUpdate = false;
			do {
				ProjectOnlineApply poaq = DTOUtils.newDTO(ProjectOnlineApply.class);
				poaq.setSerialNumber(df.format(number));
				List<ProjectOnlineApply> tmpList = this.projectOnlineApplyMapper.select(poaq);
				if (tmpList.stream().filter(a -> !a.getId().equals(poa.getId())).findFirst().orElse(null) != null || number == 0) {
					number++;
					needUpdate = true;
					continue;
				} else {
					break;
				}
			} while (true);
			if (needUpdate) {
				poa.setSerialNumber(df.format(number));
				this.projectOnlineApplyMapper.updateByPrimaryKey(poa);
			}
		});
	}

	private void fixTravelCostNumber() {
		TravelCostApply record = DTOUtils.newDTO(TravelCostApply.class);
		record.setOrder("id");
		record.setSort("desc");
		List<TravelCostApply> tcaList = this.tcaMapper.select(record);
		tcaList.forEach(tca -> {
			int number = Integer.valueOf(tca.getSerialNumber());
			DecimalFormat df = new DecimalFormat("00000");
			boolean needUpdate = false;
			do {
				TravelCostApply poaq = DTOUtils.newDTO(TravelCostApply.class);
				poaq.setSerialNumber(df.format(number));
				List<TravelCostApply> tmpList = this.tcaMapper.select(poaq);
				if (tmpList.stream().filter(a -> a.getId().equals(tca.getId())).findFirst().orElse(null) != null || number == 0) {
					number++;
					needUpdate = true;
					continue;
				} else {
					break;
				}
			} while (true);
			if (needUpdate) {
				tca.setSerialNumber(df.format(number));
				this.tcaMapper.updateByPrimaryKey(tca);
			}
		});
	}

	private void fixWorkOrderNumber() {
		WorkOrder record = DTOUtils.newDTO(WorkOrder.class);
		record.setOrder("id");
		record.setSort("desc");
		List<WorkOrder> woList = this.workOrderMapper.select(record);
		woList.forEach(wo -> {
			if (wo.getWorkOrderType().equals(3)) {
				System.out.println("aaa");
			}
			String prefix = wo.getSerialNumber().substring(0, 8);
			int number = Integer.valueOf(wo.getSerialNumber().substring(7));
			DecimalFormat df = new DecimalFormat("0000");
			boolean needUpdate = false;
			do {
				WorkOrder poaq = DTOUtils.newDTO(WorkOrder.class);
				poaq.setSerialNumber(prefix + df.format(number));
				List<WorkOrder> tmpList = this.workOrderMapper.select(poaq);
				if (tmpList.stream().filter(w -> !w.getId().equals(wo.getId())).findFirst().orElse(null) != null || number == 0) {
					number++;
					needUpdate = true;
					continue;
				} else {
					break;
				}
			} while (true);
			if (needUpdate) {
				wo.setSerialNumber(prefix + df.format(number));
				this.workOrderMapper.updateByPrimaryKey(wo);
			}
		});
	}

}