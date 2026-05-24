package com.gzang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzang.app.entity.PaymentMethod;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 支付方式Mapper接口
 *
 * @author G-Zang Team
 */
public interface PaymentMethodMapper extends BaseMapper<PaymentMethod> {

    /**
     * 根据用户ID查询支付方式列表
     *
     * @param userId 用户ID
     * @return 支付方式列表
     */
    @Select("SELECT * FROM t_payment_method WHERE user_id = #{userId} AND is_enabled = 1 ORDER BY sort_order")
    List<PaymentMethod> selectPaymentMethodsByUserId(@Param("userId") Long userId);

    /**
     * 根据代码查询支付方式
     *
     * @param userId 用户ID
     * @param methodCode 支付方式代码
     * @return 支付方式
     */
    @Select("SELECT * FROM t_payment_method WHERE user_id = #{userId} AND method_code = #{methodCode} AND is_enabled = 1 LIMIT 1")
    PaymentMethod selectByCode(@Param("userId") Long userId, @Param("methodCode") String methodCode);

    /**
     * 检查支付方式代码是否存在
     *
     * @param userId 用户ID
     * @param methodCode 支付方式代码
     * @param excludeId 排除的ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM t_payment_method WHERE user_id = #{userId} AND method_code = #{methodCode} AND id != #{excludeId}")
    int countByCode(@Param("userId") Long userId, @Param("methodCode") String methodCode, @Param("excludeId") Long excludeId);
}
