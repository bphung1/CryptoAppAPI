package crypto.dto;

import java.math.BigDecimal;

public class Crypto {
    private String asset_id_base;
    private BigDecimal rate;

    public String getAsset_id_base() {
        return asset_id_base;
    }

    public void setAsset_id_base(String asset_id_base) {
        this.asset_id_base = asset_id_base;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

}
