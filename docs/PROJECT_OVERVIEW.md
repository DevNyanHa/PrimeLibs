# PrimeLibs 프로젝트 개요

## 1. 프로젝트 목적

PrimeLibs는 Paper 기반 마인크래프트 서버 개발을 위한 멀티모듈 라이브러리/플러그인 스캐폴드입니다.

현재 구조는 책임을 크게 세 영역으로 분리합니다.

- `core/`: 공통 런타임 로직과 버전 해석
- `platform/`: 플랫폼별 추상화와 플러그인 진입점
- `nms/`: 버전별 내부 구현 어댑터

이 구조의 목표는 다음과 같습니다.

- 공통 로직은 `core`에 유지
- 플랫폼 연결 코드는 `platform`에 유지
- 서버 버전에 따라 달라지는 내부 코드는 `nms`에 격리

이렇게 분리해두면 새로운 플랫폼이나 새로운 Minecraft/Paper 버전을 추가할 때 기존 공통 코드와 섞이지 않아 확장이 쉬워집니다.

## 2. 현재 모듈 구조

```text
PrimeLibs/
├─ build.gradle.kts
├─ settings.gradle.kts
├─ gradle.properties
├─ core/
│  ├─ build.gradle.kts
│  └─ src/main/kotlin/com/github/prime/
│     ├─ PrimeLibsBootstrap.kt
│     └─ nms/
│        ├─ MinecraftVersion.kt
│        ├─ NmsAdapter.kt
│        ├─ NmsAdapterFactory.kt
│        ├─ NmsRegistry.kt
│        ├─ NmsResolution.kt
│        └─ SupportedVersionRange.kt
├─ platform/
│  ├─ api/
│  │  ├─ build.gradle.kts
│  │  └─ src/main/kotlin/com/github/prime/
│  │     ├─ Platform.kt
│  │     └─ PlatformProvider.kt
│  ├─ bukkit/
│  │  ├─ build.gradle.kts
│  │  └─ src/main/
│  │     ├─ kotlin/com/github/prime/bukkit/PrimeLibraryPlugin.kt
│  │     └─ resources/
│  │        ├─ plugin.yml
│  │        └─ META-INF/services/com.github.prime.nms.NmsAdapterFactory
│  └─ paper/
│     ├─ build.gradle.kts
│     └─ src/main/kotlin/com/github/prime/PaperPlatform.kt
└─ nms/
   ├─ paper-1_21_11/
   │  ├─ build.gradle.kts
   │  └─ src/main/
   │     ├─ kotlin/com/github/prime/nms/paper/v12111/Paper12111NmsAdapter.kt
   │     └─ resources/META-INF/services/com.github.prime.nms.NmsAdapterFactory
   └─ paper-26_1_2/
      ├─ build.gradle.kts
      └─ src/main/
         ├─ kotlin/com/github/prime/nms/paper/v2612/Paper2612NmsAdapter.kt
         └─ resources/META-INF/services/com.github.prime.nms.NmsAdapterFactory
```

## 3. 루트 Gradle 파일 설명

### `settings.gradle.kts`

이 파일은 현재 빌드에 포함되는 모듈을 정의합니다.

- `core`
- `platform:api`
- `platform:bukkit`
- `platform:paper`
- `nms:paper-1_21_11`
- `nms:paper-26_1_2`

추가로 플러그인 저장소와 Foojay toolchain resolver도 설정해서, 필요한 JDK를 Gradle이 자동으로 준비할 수 있게 합니다.

### `build.gradle.kts`

이 파일은 프로젝트 전체에 공통으로 적용되는 Gradle 설정입니다.

주요 역할:

- 모든 하위 모듈에 공통 Kotlin/JVM 규칙 적용
- Java toolchain 설정
- 바이트코드 타깃 버전 설정
- 공용 저장소 등록
- Spotless 포매터 등록

중요 설정:

- toolchain JDK: `25`
- 생성 바이트코드 타깃: `21`
- 포매터: Spotless + ktlint

### `gradle.properties`

이 파일은 프로젝트 전역에서 사용하는 공통 값을 저장합니다.

- `group`
- `version`
- `javaToolchainVersion`
- `javaTargetVersion`
- `paperApiVersion`
- `paperRecommendedApiVersion`
- `paperRecommendedMcVersion`

버전이나 빌드 속성을 여기로 모아두면 각 `build.gradle.kts` 파일이 간결해지고, 설정 변경도 쉬워집니다.

## 4. Core 모듈

경로:

- `core/build.gradle.kts`
- `core/src/main/kotlin/com/github/prime/PrimeLibsBootstrap.kt`
- `core/src/main/kotlin/com/github/prime/nms/`

### 역할

`core` 모듈은 현재 런타임 구조의 중심입니다.

여기에는 다음이 들어 있습니다.

- 프로젝트 부트스트랩 로직
- 버전 파싱
- NMS 어댑터 조회
- 런타임 상태 객체

### `PrimeLibsBootstrap.kt`

이 파일에는 크게 두 가지가 들어 있습니다.

- `PrimeLibsRuntime`
- `PrimeLibsBootstrap`

#### `PrimeLibsRuntime`

`PrimeLibsRuntime`은 부트스트랩 완료 후 생성되는 런타임 상태 객체입니다.

저장하는 값:

- 현재 `Platform`
- 해석된 `NmsResolution`

또한 `startupSummary()`를 통해 시작 로그에 사용할 문자열을 만들어줍니다.

#### `PrimeLibsBootstrap`

`PrimeLibsBootstrap.bootstrap(...)`은 실제 시작 진입점입니다.

실행 순서:

1. 현재 플랫폼을 `PlatformProvider`에 등록
2. `NmsRegistry` 생성
3. 서버 버전 문자열을 `MinecraftVersion`으로 파싱
4. 해당 버전에 맞는 NMS 어댑터 조회
5. `PrimeLibsRuntime` 반환

현재 서버 버전에 맞는 어댑터가 하나도 없으면 즉시 예외를 발생시키고 시작을 중단합니다.

### `nms/` 디렉터리

이 디렉터리에는 NMS 관련 공통 타입이 나뉘어 들어 있습니다.

- `NmsAdapter.kt`
- `NmsAdapterFactory.kt`
- `MinecraftVersion.kt`
- `SupportedVersionRange.kt`
- `NmsResolution.kt`
- `NmsRegistry.kt`

#### `NmsAdapter`

하나의 구체적인 버전 어댑터를 표현하는 인터페이스입니다.

현재 포함된 요소:

- `id`
- `displayName`
- `describeCapabilities()`

이 인터페이스는 나중에 실제 버전별 기능 구현이 들어갈 자리입니다.

#### `NmsAdapterFactory`

어댑터를 직접 고정 생성하지 않고 팩토리로 분리해두었습니다.
각 팩토리는 다음을 선언합니다.

- 자신의 `id`
- 지원하는 버전 범위
- 실제 어댑터 생성 방법

#### `MinecraftVersion`

이 클래스는 다음과 같은 버전 문자열을 파싱합니다.

- `1.21.11`
- `26.1.2`
- 숫자 외 추가 접미사가 붙은 문자열

문자열에서 숫자만 추출해서 서로 비교 가능한 버전 객체로 변환합니다.

#### `SupportedVersionRange`

팩토리가 어떤 Minecraft 버전을 지원하는지 표현하는 클래스입니다.

보조 함수:

- `between(...)`
- `featureLine(...)`

#### `NmsResolution`

`NmsResolution`은 실제 버전 해석 결과를 담는 객체입니다.

포함 값:

- 요청된 서버 버전
- 선택된 팩토리 ID
- 최종 생성된 어댑터

#### `NmsRegistry`

`NmsRegistry`는 Java `ServiceLoader`를 사용해 클래스패스에 등록된 모든 `NmsAdapterFactory`를 찾습니다.

이 부분이 현재 버전별 어댑터 구조의 핵심입니다.

- 각 버전 모듈은 자기 팩토리를 등록
- `core`는 특정 버전 모듈 이름을 직접 하드코딩하지 않음
- 실제 서버 버전에 따라 런타임에서 동적으로 어댑터를 선택

## 5. Platform 모듈

`platform` 디렉터리에는 서버/플랫폼 계층과 관련된 코드가 모여 있습니다.

### `platform/api`

파일:

- `platform/api/src/main/kotlin/com/github/prime/Platform.kt`
- `platform/api/src/main/kotlin/com/github/prime/PlatformProvider.kt`

#### `Platform.kt`

`core`에서 사용할 공통 추상화를 정의합니다.

포함 요소:

- `PlatformType`
- `PlatformScheduler`
- `Platform`

목적:

- `core`가 Bukkit/Paper 구체 타입에 직접 의존하지 않도록 분리
- 플랫폼별 모듈이 이 인터페이스를 구현하도록 설계

#### `PlatformProvider.kt`

`PlatformProvider`는 현재 활성화된 플랫폼 객체를 저장하는 간단한 전역 홀더입니다.

현재 책임:

- 부트스트랩 이후 현재 플랫폼 인스턴스 저장

이 구조는 이후 공통 코드에서 스케줄러나 플랫폼 정보를 매번 인자로 넘기지 않고 접근할 수 있게 해줍니다.

### `platform/paper`

파일:

- `platform/paper/src/main/kotlin/com/github/prime/PaperPlatform.kt`

역할:

- 추상 `Platform` API를 실제 Paper/Bukkit 스케줄러 호출로 연결

`PaperPlatform`은 `JavaPlugin` 인스턴스를 감싸고 다음을 구현합니다.

- `sync { ... }` -> `runTask`
- `async { ... }` -> `runTaskAsynchronously`

현재 플러그인 부트스트랩에서 사용하는 플랫폼 구현체가 바로 이 클래스입니다.

### `platform/bukkit`

파일:

- `platform/bukkit/build.gradle.kts`
- `platform/bukkit/src/main/kotlin/com/github/prime/bukkit/PrimeLibraryPlugin.kt`
- `platform/bukkit/src/main/resources/plugin.yml`

역할:

- 실제 플러그인 패키징 모듈
- 플러그인 엔트리포인트
- 최종 shaded JAR 생성

#### `PrimeLibraryPlugin.kt`

이 클래스는 `JavaPlugin` 엔트리포인트입니다.

플러그인이 활성화되면 다음 순서로 실행됩니다.

1. `PaperPlatform` 생성
2. `server.minecraftVersion`에서 서버 버전 읽기
3. `PrimeLibsBootstrap.bootstrap(...)` 호출
4. 반환된 런타임 저장
5. 시작 요약과 어댑터 capability 로그 출력

즉, Paper/Bukkit 시작 흐름이 공통 `core` 런타임과 연결되는 지점입니다.

#### `plugin.yml`

이 파일은 서버에 플러그인을 등록하는 메타데이터입니다.

- 플러그인 이름
- 버전
- 메인 클래스
- API 버전

버전 값은 `processResources` 단계에서 실제 프로젝트 버전으로 치환됩니다.

#### `platform/bukkit/build.gradle.kts`

이 모듈이 최종 배포용 플러그인 JAR을 만듭니다.

주요 역할:

- `core` 의존
- 패키징할 모든 NMS 모듈 의존
- `platform:paper` 의존
- Shadow를 이용해 shaded JAR 생성
- `runServer` 태스크 제공

이 모듈에 현재 NMS 모듈들이 모두 들어 있기 때문에, 최종 플러그인은 런타임에서 버전에 맞는 어댑터를 선택할 수 있습니다.

## 6. NMS 모듈

`nms` 디렉터리에는 버전별 구현이 들어갑니다.

Minecraft/Paper 내부 차이를 여기로 격리하는 것이 목적입니다.

### `nms/paper-1_21_11`

파일:

- `nms/paper-1_21_11/build.gradle.kts`
- `nms/paper-1_21_11/src/main/kotlin/com/github/prime/nms/paper/v12111/Paper12111NmsAdapter.kt`
- `nms/paper-1_21_11/src/main/resources/META-INF/services/com.github.prime.nms.NmsAdapterFactory`

이 모듈이 제공하는 것:

- `Paper12111NmsAdapter`
- `Paper12111NmsAdapterFactory`

지원 범위:

- `1.21.0` ~ `1.21.11`

용도:

- 구형 Paper 1.21.x 지원 슬롯

### `nms/paper-26_1_2`

파일:

- `nms/paper-26_1_2/build.gradle.kts`
- `nms/paper-26_1_2/src/main/kotlin/com/github/prime/nms/paper/v2612/Paper2612NmsAdapter.kt`
- `nms/paper-26_1_2/src/main/resources/META-INF/services/com.github.prime.nms.NmsAdapterFactory`

이 모듈이 제공하는 것:

- `Paper2612NmsAdapter`
- `Paper2612NmsAdapterFactory`

지원 범위:

- `26.1.x`

용도:

- 현재 추천하는 최신 Paper 라인 지원 슬롯

### 왜 `ServiceLoader`를 쓰는가

각 NMS 모듈은 아래 파일을 통해 팩토리를 등록합니다.

- `META-INF/services/com.github.prime.nms.NmsAdapterFactory`

이 방식의 장점:

- 새 버전 모듈 추가
- 해당 팩토리 등록
- 플랫폼 패키징 모듈에 의존성 추가
- 서버 버전에 맞으면 자동 선택

즉, `core`에 거대한 `when`이나 버전별 분기문을 만들지 않아도 됩니다.

## 7. 빌드 및 패키징 흐름

### 전체 빌드

프로젝트 루트에서:

```bash
./gradlew build
```

모든 모듈을 컴파일하고 각 모듈의 JAR을 생성합니다.

### 포맷 적용

```bash
./gradlew ktlintFormat
```

포맷 대상:

- `.kt`
- `.gradle.kts`
- `.gitignore`
- `.editorconfig`

### 최종 플러그인 JAR 생성

```bash
./gradlew :platform:bukkit:shadowJar
```

실제 배포할 플러그인 JAR을 생성합니다.

### 로컬 Paper 서버 실행

```bash
./gradlew :platform:bukkit:runServer
```

설정된 추천 Paper 버전을 기준으로 로컬 테스트 서버를 띄웁니다.

## 8. 런타임 실행 흐름

런타임 동작 순서는 다음과 같습니다.

1. Paper가 `plugin.yml`을 읽고 플러그인 로드
2. `PrimeLibraryPlugin.onEnable()` 실행
3. `PaperPlatform` 생성
4. `server.minecraftVersion`으로 서버 버전 확인
5. `PrimeLibsBootstrap.bootstrap(...)` 호출
6. `NmsRegistry`가 모든 `NmsAdapterFactory` 구현체 로드
7. 버전 범위에 맞는 어댑터 선택
8. `PrimeLibsRuntime` 생성
9. 시작 요약 로그 출력

즉, 실제 서버 버전이 어떤 NMS 어댑터가 활성화될지 결정합니다.

## 9. 버전 전략

현재 프로젝트는 분리된 버전 전략을 사용합니다.

### 공통 컴파일 기준선

- `paperApiVersion=1.21.11-R0.1-SNAPSHOT`

공통 모듈이 안정적인 기준선 위에서 컴파일되도록 유지하면서 JVM 21 바이트코드 타깃도 유지하기 위한 선택입니다.

### 추천 런타임 라인

- `paperRecommendedApiVersion=26.1.2.build.64-stable`
- `paperRecommendedMcVersion=26.1.2`

이 값은 현재 추천 개발/실행 라인이 26.1.x임을 의미합니다.

### Java 설정

- toolchain: Java 25
- bytecode target: Java 21

이유:

- 최신 Paper 라인은 더 높은 JDK를 요구할 수 있음
- 개발용 JDK와 실제 산출 바이트코드 타깃은 분리해서 관리할 필요가 있음

## 10. 프로젝트 확장 방법

### 새로운 NMS 버전 추가

1. `nms/` 아래에 새 모듈 생성
2. `settings.gradle.kts`에 모듈 등록
3. 새 `NmsAdapter` 구현
4. 새 `NmsAdapterFactory` 구현
5. `META-INF/services/com.github.prime.nms.NmsAdapterFactory`에 등록
6. `platform:bukkit`에 의존성 추가

### 새로운 플랫폼 추가

1. `platform/` 아래에 새 모듈 생성
2. `Platform` 구현체 작성
3. 필요하면 새로운 플랫폼용 엔트리포인트 작성
4. 공통 로직은 계속 `core`에 유지

## 11. 현재 상태와 한계

현재 프로젝트는 완성형 라이브러리라기보다 스캐폴드에 가깝습니다.

이미 갖춘 것:

- 모듈 분리
- 버전 파싱
- 어댑터 레지스트리
- 플랫폼 추상화
- 플러그인 부트스트랩
- 포매터 연동

아직 단순한 부분:

- 실제 NMS 기능은 아직 플레이스홀더 수준
- 어댑터는 현재 capability 문자열만 제공
- 부트스트랩 외 서비스 계층은 아직 없음
- 커맨드/리스너/실제 기능 구현은 아직 없음

이 구조를 기반으로 이후 다음 같은 기능을 확장할 수 있습니다.

- 패킷 헬퍼
- reflection/NMS 래퍼
- 버전별 엔티티/아이템 처리
- 스케줄러 유틸리티
- 플랫폼별 기능 분기

## 12. 추천 읽기 순서

처음 프로젝트를 파악할 때는 아래 순서로 읽는 것을 추천합니다.

1. `settings.gradle.kts`
2. `build.gradle.kts`
3. `platform/bukkit/src/main/kotlin/com/github/prime/bukkit/PrimeLibraryPlugin.kt`
4. `core/src/main/kotlin/com/github/prime/PrimeLibsBootstrap.kt`
5. `core/src/main/kotlin/com/github/prime/nms/`
6. `platform/api/src/main/kotlin/com/github/prime/Platform.kt`
7. `platform/paper/src/main/kotlin/com/github/prime/PaperPlatform.kt`
8. `nms/paper-1_21_11/...`
9. `nms/paper-26_1_2/...`

이 순서는 실제 실행 흐름과 가장 비슷하게, 빌드 설정 -> 플러그인 시작 -> 부트스트랩 -> 버전 선택 구조로 이어집니다.
