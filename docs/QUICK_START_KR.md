# PrimeLibs 초보자용 요약

## 1. 이 프로젝트는 뭐하는 건가요?

PrimeLibs는 Paper 서버용 멀티모듈 라이브러리/플러그인 프로젝트입니다.

핵심 목적은 다음 3가지를 분리하는 것입니다.

- `core/`: 공통 로직
- `platform/`: Bukkit/Paper 같은 플랫폼 연결
- `nms/`: 버전별 구현

쉽게 말하면:

- 공통 기능은 `core`
- 서버와 직접 붙는 코드는 `platform`
- 버전마다 달라지는 코드는 `nms`

## 2. 처음엔 어디를 보면 되나요?

처음 읽을 때는 이 순서가 가장 쉽습니다.

1. `platform/bukkit/src/main/kotlin/com/github/prime/bukkit/PrimeLibraryPlugin.kt`
2. `core/src/main/kotlin/com/github/prime/PrimeLibsBootstrap.kt`
3. `core/src/main/kotlin/com/github/prime/nms/`
4. `platform/api/src/main/kotlin/com/github/prime/Platform.kt`
5. `platform/paper/src/main/kotlin/com/github/prime/PaperPlatform.kt`

이 순서대로 보면:

- 플러그인이 어떻게 시작되는지
- 시작 후 어떤 공통 로직으로 들어가는지
- 서버 버전에 따라 어떤 어댑터를 고르는지

를 한 번에 이해할 수 있습니다.

## 3. 폴더별 역할

### `core/`

공통 로직이 들어 있습니다.

중요 파일:

- `PrimeLibsBootstrap.kt`
- `nms/` 디렉터리 전체

여기서 하는 일:

- 플러그인 시작 준비
- 서버 버전 파싱
- 맞는 NMS 어댑터 선택

### `platform/`

플랫폼별 코드가 들어 있습니다.

현재 포함된 모듈:

- `platform/api`
- `platform/paper`
- `platform/bukkit`

각 역할:

- `platform/api`: 공통 플랫폼 인터페이스
- `platform/paper`: Paper 스케줄러 연결
- `platform/bukkit`: 실제 플러그인 엔트리포인트와 최종 JAR 생성

### `nms/`

버전별 구현이 들어 있습니다.

현재 포함된 모듈:

- `paper-1_21_11`
- `paper-26_1_2`

즉, 서버 버전에 따라 다른 어댑터를 사용할 수 있도록 만든 구조입니다.

## 4. 플러그인은 어떻게 시작되나요?

아주 단순하게 보면 흐름은 이렇습니다.

1. Paper가 `plugin.yml`을 읽습니다.
2. `PrimeLibraryPlugin`이 실행됩니다.
3. `PrimeLibsBootstrap.bootstrap(...)`이 호출됩니다.
4. 서버 버전을 읽습니다.
5. 그 버전에 맞는 NMS 어댑터를 찾습니다.
6. 선택된 어댑터 정보를 로그로 출력합니다.

즉, 시작점은 `platform/bukkit`이고, 실제 버전 선택 로직은 `core` 안에 있습니다.

## 5. 빌드는 어떻게 하나요?

프로젝트 루트에서 실행하면 됩니다.

### 전체 빌드

```bash
./gradlew build
```

### 코드 포맷 적용

```bash
./gradlew ktlintFormat
```

### 최종 플러그인 JAR 생성

```bash
./gradlew :platform:bukkit:shadowJar
```

### 로컬 테스트 서버 실행

```bash
./gradlew :platform:bukkit:runServer
```

## 6. 최종 플러그인 파일은 어디에 생기나요?

배포용 파일은 `platform:bukkit` 모듈이 만듭니다.

즉, 보통 최종 결과물은 다음 경로에 생깁니다.

```text
platform/bukkit/build/libs/
```

서버에 넣을 JAR은 여기서 찾으면 됩니다.

## 7. 어떤 파일을 수정해야 하나요?

### 공통 기능을 추가하고 싶을 때

`core/`를 수정하면 됩니다.

예:

- 공통 서비스
- 버전 판별 로직
- 공통 유틸

### Paper/Bukkit 동작을 바꾸고 싶을 때

`platform/`을 수정하면 됩니다.

예:

- 스케줄러 처리
- 플러그인 시작 로직
- plugin.yml 설정

### 특정 버전 전용 처리를 넣고 싶을 때

`nms/`의 해당 버전 모듈을 수정하면 됩니다.

예:

- `1.21.11` 전용 처리 -> `nms/paper-1_21_11`
- `26.1.x` 전용 처리 -> `nms/paper-26_1_2`

## 8. 새 버전을 추가하려면?

예를 들어 새 Paper 버전을 지원하고 싶다면 보통 이렇게 합니다.

1. `nms/` 아래에 새 모듈 생성
2. `settings.gradle.kts`에 새 모듈 추가
3. 새 `NmsAdapter` 구현
4. 새 `NmsAdapterFactory` 구현
5. `META-INF/services/com.github.prime.nms.NmsAdapterFactory`에 등록
6. `platform/bukkit/build.gradle.kts`에 의존성 추가

핵심은:

- 새 버전마다 `core`를 크게 건드리지 않아도 되게 만드는 것

입니다.

## 9. 왜 이렇게 복잡하게 나눠놨나요?

처음 보면 조금 복잡해 보일 수 있지만, 장점이 분명합니다.

- 버전별 코드가 섞이지 않음
- 공통 로직이 더 깔끔해짐
- 나중에 새 버전 추가가 쉬움
- 플랫폼별 확장도 쉬움

작은 프로젝트라면 한 모듈에 몰아도 되지만, 다중 버전을 오래 지원하려면 지금 구조가 더 관리하기 좋습니다.

## 10. 지금 상태에서 알아두면 좋은 점

현재 프로젝트는 완성된 기능 라이브러리라기보다 “기반 구조”에 가깝습니다.

이미 준비된 것:

- 멀티모듈 구조
- 버전별 어댑터 구조
- 플랫폼 추상화
- 빌드/패키징 구조
- 코드 포매터

아직 단순한 것:

- 실제 NMS 기능은 거의 없음
- 어댑터는 지금 설명 문자열 위주
- 실사용 기능은 앞으로 추가해야 함

즉, 지금은 “틀이 잘 잡힌 시작 프로젝트”라고 보면 됩니다.

## 11. 초보자 추천 작업 순서

처음 기여하거나 공부할 때는 이 순서를 추천합니다.

1. `./gradlew build` 한 번 실행
2. `PrimeLibraryPlugin.kt` 읽기
3. `PrimeLibsBootstrap.kt` 읽기
4. `core/src/main/kotlin/com/github/prime/nms/` 아래 파일들 읽기
5. `platform/paper` 읽기
6. `nms` 안 버전 모듈 하나 읽기

그 다음부터는:

- 공통 기능이면 `core`
- 플랫폼 처리면 `platform`
- 버전별 처리면 `nms`

로 생각하면 헷갈림이 많이 줄어듭니다.

## 12. 더 자세한 설명이 필요하면

더 자세한 전체 설명은 아래 문서를 보면 됩니다.

- `docs/PROJECT_OVERVIEW.md`

이 문서는 빠르게 감을 잡기 위한 요약본이고, 전체 구조 설명은 개요 문서가 기준입니다.
