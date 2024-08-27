package org.example.springwithdistributedlock.servcie;

import lombok.extern.slf4j.Slf4j;
import org.example.springwithdistributedlock.common.exception.ForceException;
import org.example.springwithdistributedlock.inventory.domain.OutboundInventoryMappingEntity;
import org.example.springwithdistributedlock.outbound.infrastructure.OutboundInventoryMappingRepository;
import org.example.springwithdistributedlock.outbound.service.ManyOutboundService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@SqlGroup({
        @Sql(value = "/sql/many-distributed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-distributed.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@Slf4j
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class ManyOutboundServiceTest {

    @Autowired
    private ManyOutboundService manyOutboundService;

    @Autowired
    private OutboundInventoryMappingRepository outboundInventoryMappingRepository;

    @Test
    void 엔티티를_새로운_트랜잭션에_전달후_예외발생시_성공한_매핑테이블의_process는_false이다() {
        //given
        //when
        assertThatThrownBy(() -> {
            manyOutboundService.distributedOutboundChangeWhenManyInventory(1L);
        }).isInstanceOf(ForceException.class);

        OutboundInventoryMappingEntity entity = outboundInventoryMappingRepository.findById(1L)
                .orElseThrow();
        //then
        assertThat(entity.getProcess()).isFalse();
    }

    @Test
    void 모델을_새로운_트랜잭션에_전달후_예외발생시_성공한_매핑테이블의_process는_true이다() {
        //given
        //when
        assertThatThrownBy(() -> {
            manyOutboundService.distributedOutboundChangeWhenManyInventoryWithModel(1L);
        }).isInstanceOf(ForceException.class);

        OutboundInventoryMappingEntity entity = outboundInventoryMappingRepository.findById(1L)
                .orElseThrow();
        //then
        assertThat(entity.getProcess()).isTrue();
    }

}
