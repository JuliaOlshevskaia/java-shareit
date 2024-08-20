package ru.practicum.shareitserver.requests.entity;

import javax.persistence.*;
import lombok.*;
import ru.practicum.shareitserver.item.entity.ItemEntity;
import ru.practicum.shareitserver.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.*;

@Generated
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "public", name = "requests")
public class RequestsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", nullable = false)
    private UserEntity requestor;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @OneToMany(mappedBy = "requests", fetch = FetchType.EAGER, cascade = {CascadeType.MERGE})
    private Set<ItemEntity> items = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass() || ((RequestsEntity) o).id == null || this.id == null) return false;
        RequestsEntity that = (RequestsEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, requestor, created);
    }
}
