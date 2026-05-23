package com.gzang.app.converter;

import com.gzang.app.entity.Budget;
import com.gzang.app.vo.BudgetVO;

/**
 * Budget实体与VO转换器
 *
 * @author G-Zang Team
 */
public class BudgetConverter {

    /**
     * 将Budget实体转换为BudgetVO
     * BudgetVO已包含计算逻辑（剩余金额、使用率、预警状态）
     *
     * @param budget Budget实体
     * @return BudgetVO
     */
    public static BudgetVO toVO(Budget budget) {
        if (budget == null) {
            return null;
        }

        BudgetVO vo = new BudgetVO();
        vo.setId(budget.getId());
        vo.setUserId(budget.getUserId());
        vo.setCompanyId(budget.getCompanyId());
        vo.setBookId(budget.getBookId());
        vo.setCategoryId(budget.getCategoryId());
        vo.setAmount(budget.getAmount());
        vo.setUsedAmount(budget.getUsedAmount());
        vo.setPeriodType(budget.getPeriodType());
        vo.setPeriodStart(budget.getPeriodStart());
        vo.setPeriodEnd(budget.getPeriodEnd());
        vo.setName(budget.getName());
        vo.setWarningThreshold(budget.getWarningThreshold());
        vo.setWarningEnabled(budget.getWarningEnabled());
        vo.setRemark(budget.getRemark());
        vo.setCreateTime(budget.getCreateTime());
        vo.setUpdateTime(budget.getUpdateTime());

        return vo;
    }
}
