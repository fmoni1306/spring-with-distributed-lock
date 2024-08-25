package org.example.springwithdistributedlock.outbound.infrastructure;

import org.example.springwithdistributedlock.outbound.domain.OutboundEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundRepository extends JpaRepository<OutboundEntity, Long> {

}
