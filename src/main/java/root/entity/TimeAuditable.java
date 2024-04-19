package root.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * @ EntityListeners(AuditingEntityListener.class): tìm @CreatedDate và generate ngày tạo
 * Thêm @EnableJpaAuditing ở class Application
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimeAuditable {
    @Column(updatable = false)
    @CreatedDate // auto gen new date
    private Date createdAt;

    @LastModifiedDate
    private Date updateAt;
}
