# spring-with-distributed-lock
레이어드 아키텍처에서 분산락 적용시 트러블 슈팅

>분산락을 테스트하기 위한 코드기 때문에 아키텍처나 테스트 방식은 편한방식으로 시도하였음

## 내부 private 메서드 호출시 분산락 어노테이션 미적용 이슈
    * 전체 로직에 분산락을 적용하는게 아닌 특정 메서드에만 분산락을 적용해야하는 경우 어노테이션을 달아도 분산락이 적용안되는 이슈가 있다.
    * 이유는 스프링은 프록시 방식의 AOP를 사용하기 때문이다. 프록시를 거치지 않고 대상을 직접호출하게 되면 AOP도 적용되지 않고 어드바이스도 호출되지 않는다.
### 해결방안
1. 서비스 자기 자신을 주입을 통해서 주입받아서 객체를 통해 메서드 호출
    * 생성자 주입시 순환참조 문제 발생 -> 수정자 주입을 통해 해결가능.
    * 좋은 방법 처럼 보이진 않는다. 생성자 주입을 사용하다가 해당 클래스에서만 생성자 주입을 사용하는건 좋은 방법은 아닌거 같다. 
2. 지연 조회
   * ObjectProvider를 통한 지연 조회를 통한 해결
   * 억지로 끼워 맞춰서 하는 방식이란 느낌이 강하게 든다.
3. 구조 변경
   * 근본적인 해결방법으로 보인다. 하나의 클래스에서 여러가지 책임을 가지고 있어서 발생하는 이슈기 때문에 구조 변경을 통해 책임을 분리하자.
  ```
  스프링 핵심 원리 - 고급편에서 배웠던 방식을 실무에서 직접 만나서 강의를 다시 보게 되는 계기가 되었다
  섹션 14 스프링 AOP - 실무 주의사항의 프록시와 내부 호출 - 문제 편에서 배웠던 이슈이다.
  ```

락이 걸렸는지 간단하게 확인하는방법은 DistributedLockAop에 브레이크포인트를 찍어도 브레이크포인트가 걸리지 않는다!!

### 락획득
![img.png](/image/img.png)
