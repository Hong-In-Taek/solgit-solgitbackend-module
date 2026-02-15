# solgit-solgitbackend-module

Spring Boot 기반 RabbitMQ 메시지 발행 서비스입니다. HTTP API를 통해 메시지를 RabbitMQ Exchange로 발행하는 백엔드 모듈입니다.

## 개요

이 서비스는 HTTP API를 제공하여 다른 서비스들이 RabbitMQ로 메시지를 발행할 수 있도록 합니다. 표준 메시지 포맷을 사용하며, `routingKey`에 따라 적절한 Exchange와 Routing Key로 메시지를 라우팅합니다.

## 기술 스택

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.0
- **Messaging**: Spring AMQP (RabbitMQ)
- **Build Tool**: Maven

## 기능

### HTTP Publisher

- `POST /api/messages/publish`: 메시지를 RabbitMQ Exchange로 발행
- 표준 메시지 포맷 자동 생성 (messageId, timestamp, version 등)
- `routingKey` 기반 Exchange 및 Routing Key 매핑
- Persistent 메시지 전송 보장

## 처리 가능한 서비스

이 모듈은 HTTP API를 통해 다음 기능을 제공합니다:

### 메시지 발행 서비스

- **표준 메시지 포맷 변환**: 요청받은 payload를 표준 메시지 포맷으로 자동 변환
- **라우팅 키 매핑**: `routingKey`에 따라 적절한 Exchange와 Routing Key로 메시지 발행
- **메시지 메타데이터 관리**: messageId, timestamp, correlationId, source 등 자동 설정
- **Persistent 메시지**: 메시지 지속성 보장

### 지원하는 Routing Key

설정 파일(`application.yml`)에서 다음 routing key들을 매핑할 수 있습니다:

- `MATTERMOST.NOTI`: Mattermost 알림 메시지
- `MATTERMOST.CREATE`: Mattermost 생성 메시지
- `MATTERMOST.UPDATE`: Mattermost 업데이트 메시지

기타 routing key는 기본 Exchange(`solgit.main.exchange`)로 발행됩니다.

## 프로젝트 구조

```
solgit-solgitbackend-module/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── solgit/
│       │           └── backend/
│       │               ├── config/          # RabbitMQ 설정
│       │               ├── controller/      # HTTP 컨트롤러
│       │               ├── dto/             # 요청/응답 DTO
│       │               ├── model/            # 메시지 모델
│       │               └── service/          # 비즈니스 로직
│       └── resources/
│           └── application.yml              # 설정 파일
├── pom.xml
├── Dockerfile
└── README.md
```

## 빌드 및 실행

### 로컬 빌드

```bash
mvn clean package
java -jar target/solgit-solgitbackend-module-1.0.0.jar
```

### Docker 빌드

```bash
docker build -t hit1414/solgit-backend-module:1.0.0 .
```

### Docker 실행

```bash
docker run -d \
  --name solgit-backend \
  -p 8085:8085 \
  -e RABBITMQ_HOST=host.docker.internal \
  -e RABBITMQ_PORT=5672 \
  -e RABBITMQ_USERNAME=admin \
  -e RABBITMQ_PASSWORD=password \
  hit1414/solgit-backend-module:1.0.0
```

## 환경 변수

| 변수명 | 설명 | 기본값 |
|--------|------|--------|
| `RABBITMQ_HOST` | RabbitMQ 호스트 | `localhost` |
| `RABBITMQ_PORT` | RabbitMQ 포트 | `5672` |
| `RABBITMQ_USERNAME` | RabbitMQ 사용자명 | `guest` |
| `RABBITMQ_PASSWORD` | RabbitMQ 비밀번호 | `guest` |
| `SERVER_PORT` | HTTP 서버 포트 | `8085` |

## API 사용 예제

이 모듈은 RabbitMQ에 메시지를 발행하며, 다음 모듈들이 구독하여 처리합니다:
- **solgit-mattermost-module**: Mattermost 관련 메시지 처리
- **solgit-project-module**: GitLab 및 Jenkins 프로젝트 관련 메시지 처리
- **solgit-security-module**: Jenkins 보안 프로젝트 관련 메시지 처리

### 1. Mattermost 모듈 메시지 발행

#### 1.1 MM_USER_CREATE - Mattermost 사용자 생성

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "MATTERMOST.CREATE",
    "messageType": "MM_USER_CREATE",
    "payload": {
      "token": "your-mattermost-bearer-token",
      "email": "user@example.com",
      "username": "newuser",
      "first_name": "John",
      "last_name": "Doe",
      "nickname": "Johnny",
      "password": "secure-password-123",
      "locale": "ko"
    }
  }'
```

#### 1.2 MM_USER_UPDATE - Mattermost 사용자 업데이트

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "MATTERMOST.UPDATE",
    "messageType": "MM_USER_UPDATE",
    "payload": {
      "token": "your-mattermost-bearer-token",
      "user_id": "user-id-123",
      "email": "updated@example.com",
      "username": "updateduser",
      "first_name": "Jane",
      "last_name": "Smith",
      "nickname": "JaneS",
      "locale": "en"
    }
  }'
```

#### 1.3 MM_USER_PROFILE_UPDDATE - Mattermost 사용자 프로필 업데이트

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "MATTERMOST.UPDATE",
    "messageType": "MM_USER_PROFILE_UPDDATE",
    "payload": {
      "token": "your-mattermost-bearer-token",
      "user_id": "user-id-123",
      "first_name": "Updated",
      "last_name": "Name",
      "nickname": "UpdatedNick",
      "position": "Senior Developer",
      "props": {
        "custom_field": "custom_value",
        "department": "Engineering"
      }
    }
  }'
```

#### 1.4 MM_TEAM_CREATE - Mattermost 팀 생성

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "MATTERMOST.CREATE",
    "messageType": "MM_TEAM_CREATE",
    "payload": {
      "token": "your-mattermost-bearer-token",
      "name": "engineering-team",
      "display_name": "Engineering Team",
      "type": "O"
    }
  }'
```

#### 1.5 MM_CHANNEL_CREATE - Mattermost 채널 생성

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "MATTERMOST.CREATE",
    "messageType": "MM_CHANNEL_CREATE",
    "payload": {
      "token": "your-mattermost-bearer-token",
      "team_id": "team-id-123",
      "name": "general",
      "display_name": "General",
      "type": "O",
      "header": "General discussion channel",
      "purpose": "For general team discussions"
    }
  }'
```

#### 1.6 MM_PERSON_NOTI - Mattermost 개인 알림 (DM)

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "MATTERMOST.NOTI",
    "messageType": "MM_PERSON_NOTI",
    "payload": {
      "token": "your-mattermost-bearer-token",
      "from_user_id": "sender-user-id-123",
      "to_user_id": "receiver-user-id-456",
      "message": "안녕하세요! 개인 알림 메시지입니다."
    }
  }'
```

#### 1.7 MM_CHANNEL_NOTI - Mattermost 채널 알림

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "MATTERMOST.NOTI",
    "messageType": "MM_CHANNEL_NOTI",
    "payload": {
      "token": "your-mattermost-bearer-token",
      "channel_id": "channel-id-123",
      "from_user_id": "sender-user-id-123",
      "message": "채널에 알림 메시지를 전송합니다!"
    }
  }'
```

### 2. Project 모듈 메시지 발행

#### 2.1 GL_PROJECT_FORK - GitLab 프로젝트 Fork

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "project.create",
    "messageType": "GL_PROJECT_FORK",
    "payload": {
      "gitType": "GitlabAi",
      "project_id": 123,
      "name": "new-project-name",
      "namespace": "optional-namespace",
      "path": "optional-path"
    }
  }'
```

#### 2.2 GL_PROJECT_ADD_MEMBER - GitLab 프로젝트에 사용자 추가

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "project.update",
    "messageType": "GL_PROJECT_ADD_MEMBER",
    "payload": {
      "gitType": "GitlabOnprem",
      "project_id": 123,
      "user_id": [456, 789, 101],
      "access_level": 30
    }
  }'
```

**참고**: `user_id`는 단일 값 또는 배열 형태로 전달 가능합니다. `access_level`은 10(Guest), 20(Reporter), 30(Developer), 40(Maintainer), 50(Owner) 중 선택 가능합니다.

#### 2.3 JENKINS_PROJECT_COPY - Jenkins 프로젝트 복사

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "project.create",
    "messageType": "JENKINS_PROJECT_COPY",
    "payload": {
      "source_job_name": "/a/b/template",
      "target_folder_path": "/new/era/",
      "new_job_name": "new-era-project"
    }
  }'
```

### 3. Security 모듈 메시지 발행

#### 3.1 JENKINS_PROJECT_COPY - Jenkins 보안 프로젝트 복사

```bash
curl -X POST http://localhost:8085/api/messages/publish \
  -H "Content-Type: application/json" \
  -d '{
    "routingKey": "security.create",
    "messageType": "JENKINS_PROJECT_COPY",
    "payload": {
      "source_job_name": "/security/template",
      "target_folder_path": "/security/projects/",
      "new_job_name": "new-security-project"
    }
  }'
```

### 프로그래밍 언어별 예시

#### JavaScript/TypeScript

```javascript
const axios = require('axios');

async function publishMattermostUserCreate() {
  try {
    const response = await axios.post('http://localhost:8085/api/messages/publish', {
      routingKey: 'MATTERMOST.CREATE',
      messageType: 'MM_USER_CREATE',
      payload: {
        token: 'your-mattermost-bearer-token',
        email: 'user@example.com',
        username: 'newuser',
        first_name: 'John',
        last_name: 'Doe'
      }
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    console.log('Published:', response.data);
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

async function publishGitLabProjectFork() {
  try {
    const response = await axios.post('http://localhost:8085/api/messages/publish', {
      routingKey: 'project.create',
      messageType: 'GL_PROJECT_FORK',
      payload: {
        gitType: 'GitlabAi',
        project_id: 123,
        name: 'new-project-name'
      }
    }, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    console.log('Published:', response.data);
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}

publishMattermostUserCreate();
publishGitLabProjectFork();
```

#### Java

```java
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.HashMap;

public class MessagePublisher {
    private static final String API_URL = "http://localhost:8085/api/messages/publish";
    
    public void publishMattermostUserCreate() {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("token", "your-mattermost-bearer-token");
        payload.put("email", "user@example.com");
        payload.put("username", "newuser");
        payload.put("first_name", "John");
        payload.put("last_name", "Doe");
        
        Map<String, Object> request = new HashMap<>();
        request.put("routingKey", "MATTERMOST.CREATE");
        request.put("messageType", "MM_USER_CREATE");
        request.put("payload", payload);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        MessageResponse response = restTemplate.postForObject(API_URL, entity, MessageResponse.class);
        System.out.println("Published: " + response);
    }
    
    public void publishGitLabProjectFork() {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("gitType", "GitlabAi");
        payload.put("project_id", 123);
        payload.put("name", "new-project-name");
        
        Map<String, Object> request = new HashMap<>();
        request.put("routingKey", "project.create");
        request.put("messageType", "GL_PROJECT_FORK");
        request.put("payload", payload);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
        MessageResponse response = restTemplate.postForObject(API_URL, entity, MessageResponse.class);
        System.out.println("Published: " + response);
    }
}
```

#### Python

```python
import requests

def publish_mattermost_user_create():
    url = "http://localhost:8085/api/messages/publish"
    headers = {"Content-Type": "application/json"}
    data = {
        "routingKey": "MATTERMOST.CREATE",
        "messageType": "MM_USER_CREATE",
        "payload": {
            "token": "your-mattermost-bearer-token",
            "email": "user@example.com",
            "username": "newuser",
            "first_name": "John",
            "last_name": "Doe"
        }
    }
    
    response = requests.post(url, json=data, headers=headers)
    print(f"Status: {response.status_code}")
    print(f"Response: {response.json()}")

def publish_gitlab_project_fork():
    url = "http://localhost:8085/api/messages/publish"
    headers = {"Content-Type": "application/json"}
    data = {
        "routingKey": "project.create",
        "messageType": "GL_PROJECT_FORK",
        "payload": {
            "gitType": "GitlabAi",
            "project_id": 123,
            "name": "new-project-name"
        }
    }
    
    response = requests.post(url, json=data, headers=headers)
    print(f"Status: {response.status_code}")
    print(f"Response: {response.json()}")

publish_mattermost_user_create()
publish_gitlab_project_fork()
```

### 성공 응답

```json
{
  "success": true,
  "messageId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 실패 응답

```json
{
  "success": false,
  "messageId": null,
  "error": "메시지 발행 중 오류가 발생했습니다: ..."
}
```

## 메시지 형식

서비스는 요청받은 payload를 다음 표준 메시지 형식으로 변환합니다:

```json
{
  "header": {
    "messageId": "uuid",
    "messageType": "MM_CHANNEL_NOTI",
    "version": "v1",
    "timestamp": "2024-01-01T00:00:00Z",
    "correlationId": null,
    "source": "solgit-backend-module"
  },
  "body": {
    "payload": {
      "token": "your-mattermost-token",
      "channel_id": "channel-id-123",
      "message": "안녕하세요!"
    }
  }
}
```

## 요청 형식

### MessageRequest

```json
{
  "routingKey": "string (필수)",
  "messageType": "string (필수)",
  "payload": "object (필수)",
  "queueName": "string (선택)"
}
```

- `routingKey`: 메시지를 라우팅할 키 (대소문자 구분 없음)
- `messageType`: 메시지 타입 (예: `MM_CHANNEL_NOTI`, `GL_PROJECT_FORK` 등)
- `payload`: 실제 메시지 데이터 (객체 형태)
- `queueName`: 선택적 필드 (현재 미사용)

## 라이선스

이 프로젝트는 solgit 프로젝트의 일부입니다.
