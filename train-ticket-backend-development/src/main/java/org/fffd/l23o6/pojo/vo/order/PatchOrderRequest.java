package org.fffd.l23o6.pojo.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.fffd.l23o6.pojo.enum_.PaymentType;

@Data
@Schema(description = "支付订单")
public class PatchOrderRequest {

    @Schema(description = "支付方式", required = true)
    @NotNull
    private PaymentType type;

    @Schema(description = "是否使用积分抵扣", required = true)
    @NotNull
    private Boolean points;
}
