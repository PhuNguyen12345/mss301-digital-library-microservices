package fu.edu.mss301.digilib.member.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
@Table("member_profiles")
@Data
@NoArgsConstructor@AllArgsConstructor
@Builder
public class MemberProfile implements Persistable<String> {

    @Id
    private String id; // Matches Keycloak sub field
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String memberType;
    private String memberCode;
    private int borrowingLimit;
    private int loanPeriodDays;
    private BigDecimal outstandingBalance;
    private String avatarKey;
    private Instant createdAt;
    private Instant updatedAt;

    @Transient
    @Builder.Default
    private boolean isNewRecord = false;

    @Override
    public boolean isNew() {
        return this.isNewRecord || this.createdAt == null;
    }
}