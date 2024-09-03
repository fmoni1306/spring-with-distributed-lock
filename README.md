# spring-with-distributed-lock
분산락 적용시 트러블 슈팅

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

### 구조 변경 후 락획득
![img.png](/image/img.png)

## 영속성 컨텐스트 이슈
어찌보면 당연한건데 require_new를 통한 분산락 적용시 분산락 적용한 메서드 이전에 호출한 엔티티들은 분산락 적용 메서드에서 CUD를 해도 변경 되지 않는다.
왜냐하면 require_new를 통해 새로운 트랜잭션이 생성될떄 해당 메서드에서는 다른 영속성 컨텍스트가 적용 된다.

### 해결방안
1. 비즈니스 로직 변경
   * 변경이 발생할 엔티티들을 분산락이 적용된 메서드에서 조회하고 업데이트를 진행했다. 
   * 레이어드 아키텍처에서는 비즈니스로직을 변경해서 해당 이슈를 해결했다.
   * 다만 반복문을 통해 분산락이 적용된 메서드를 호출한다면, 한번의 조회로 해결될 로직이 반복문의 횟수만큼 추가적으로 발생하는건 성능상의 이슈로 생각됐다.
2. domain 계층의 entity model 추가
   * 해당 클래스의 추가로 비즈니스로직이 jpa의 영향을 받지 않고 원하는 시점에 업데이트를 할 수 있게 함.
