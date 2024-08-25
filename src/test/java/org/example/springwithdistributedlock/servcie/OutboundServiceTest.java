package org.example.springwithdistributedlock.servcie;

import lombok.extern.slf4j.Slf4j;
import org.example.springwithdistributedlock.inventory.domain.InventoryEntity;
import org.example.springwithdistributedlock.inventory.infrastructure.InventoryRepository;
import org.example.springwithdistributedlock.outbound.service.OutboundService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SqlGroup({
        @Sql(value = "/sql/distributed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-distributed.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@Slf4j
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class OutboundServiceTest {

    @Autowired
    private OutboundService outboundService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    void 한건만_출고시_재고차감과_재고이동이_정상적으로_작동한다() {
        //given
        //when
        InventoryEntity inventory = outboundService.notDistributedOutboundChange(1L);

        //then
        assertThat(inventory.getGoodsId()).isEqualTo("G1");
        assertThat(inventory.getLocation()).isEqualTo("O1");
        assertThat(inventory.getLocationType()).isEqualTo("OUT");
        assertThat(inventory.getQuantity()).isEqualTo(1L);
    }

    @Test
    void 열명의_사용자가_같은품목을_순차적으로_출고시_재고차감과_재고이동이_정상적으로_작동한다() {
        int count = 10;

        for (int i = 0; i < count; i++) {
            InventoryEntity inventory = outboundService.notDistributedOutboundChange(1L);
            log.info("잔여재고 => {}", inventory.getQuantity());
        }

        InventoryEntity inventory = inventoryRepository.findById(2L)
                .orElseThrow(RuntimeException::new);

        assertThat(inventory.getQuantity()).isEqualTo(10L);

        log.info("종료후 잔여 재고 => {}", inventory.getQuantity());
    }

    @Test
    void 분산락_적용이_안됐을때_열명의_사용자가_동시에_같은품목을_출고시_재고차감과_재고이동이_정상적으로_작동하지_않는다() throws InterruptedException {
        int threadNumber = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);

        CountDownLatch latch = new CountDownLatch(threadNumber);

        for (int i = 0; i < threadNumber; i++) {
            executorService.submit(() -> {
                try {
                    InventoryEntity inventory = outboundService.notDistributedOutboundChange(1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        InventoryEntity inventory = inventoryRepository.findById(2L)
                .orElseThrow(RuntimeException::new);

        assertThat(inventory.getQuantity()).isNotEqualTo(10L);

        log.info("종료후 잔여 재고 => {}", inventory.getQuantity());
    }

    @Test
    void 분산락_적용이_됐을때_열명의_사용자가_동시에_같은품목을_출고시_재고차감과_재고이동이_정상적으로_작동한다() throws InterruptedException {
        int threadNumber = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadNumber);

        CountDownLatch latch = new CountDownLatch(threadNumber);

        for (int i = 0; i < threadNumber; i++) {
            executorService.submit(() -> {
                try {
                    InventoryEntity inventory = outboundService.distributedOutboundChange(1L);
                    log.info("잔여 재고 => {}", inventory.getQuantity());
                } catch (Exception e){
                    log.error("예외 => ", e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        InventoryEntity inventory = inventoryRepository.findById(2L)
                .orElseThrow(RuntimeException::new);

        assertThat(inventory.getQuantity()).isEqualTo(10L);

        log.info("종료후 잔여 재고 => {}", inventory.getQuantity());
    }

}
