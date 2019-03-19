# TODO List Project - Rest API

## TODO List Project
#### 기능
* 사용자는 텍스트로 된 할일을 추가할 수 있다.
  * 할일 추가 시 다른 할일들을 참조 걸 수 있다.
  * 참조는 다른 할일의 id를 명시하는 형태로 구현한다. (예시 참고)
* 사용자는 할일을 수정할 수 있다.
* 사용자는 할일 목록을 조회할 수 있다.
  * 조회시 작성일, 최종수정일, 내용이 조회 가능하다.
  * 할일 목록은 페이징 기능이 있다.
* 사용자는 할일을 완료처리 할 수 있다.``
  * 완료처리 시 참조가 걸린 완료되지 않은 할일이 있다면 완료처리할 수 없다. (예시 참고)

#### 목록조회 예시 및 설명
| id | 할일 | 작성일시 | 최종수정일시 | 완료처리 |
|----|-------------|---------------------|----------|---------------------|
| 1 | 집안일 | 2018-04-01 10:00:00 | 2018-04-01 13:00:00 |  |
| 2 | 빨래 @1 | 2018-04-01 11:00:00 | 2018-04-01 11:00:00 |  |
| 3 | 청소 @1 | 2018-04-01 12:00:00 | 2018-04-01 13:00:00 |  |
| 4 | 방청소 @1 @3 | 2018-04-01 12:00:00 | 2018-04-01 13:00:00 |  | 
                        | 1 | 2 | 3 | 4 | 5 |
* 할일 2, 3번은 1번에 참조가 걸린 상태이다.
* 할일 4번은 할일 1, 3번에 참조가 걸린 상태이다.
* 할일 1번은 할일 2번, 3번, 4번이 모두 완료되어야 완료처리가 가능하다.
* 할일 3번은 할일 4번이 완료되어야 완료처리가 가능하다.


## 프로젝트 구성
#### Dependencies
- spring-boot-starter-data-jpa
- spring-boot-starter-web
- spring-boot-starter-hateoas

#### DB
- h2db (mem) 

#### 구동 방법
- web-todo-list 웹 프로젝트와 중복되지 않게 port 설정해야 한다.
    - application.properties (server.port=8081)
- maven build 및 application 구동
    - $ mvn clean package
    - $ cd target
    - $ jar -jar rest-todo-list-0.0.1-SNAPSHOT.jar

## 주요 사항들.
#### Main Entity : Todo
```
    id, content, status, reference, referenced, createDate, updateDate, completeDate
```

- Todo Entity 내 주요사항
    - 할일은 여러 다른 할일에 참조걸수 있기 때문에 참조 할일 ID(reference_id)는 manyToMany 관계. 
    - A 할일에서 B 할일을 참조하면, B 할일에는 A 할일에 참조가 걸렸다는 것을 알 수 있어야 한다.
        - reference : A 할일이 참조하고 있는 다른 할일 정보
        - referenced : A 할일에 참조 걸고 있는 다른 할일 정보
    - 목록 조회 예시와 같이 참조가 걸린 정보를 보여줄 때에는 referenced 정보를 이용한다.
        - [4] 방청소 @1 @3      
```
    @ManyToMany
    @OrderBy("id ASC")
    @JoinColumn(name = "reference_id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Todo> reference = new HashSet<>();

    @OrderBy("id ASC")
    @ManyToMany(mappedBy = "reference")
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Todo> referenced = new HashSet<>();
```

#### hateoas 적용
- rest api에서 link 관계를 보여준다.
```
    {
        "_embedded": {
            "todoList": [
                {
                    "id": 1,
                    "content": "집안일",
                    "completeDate": null,
                    "reference": [].
                    "referenced": []
                    ~~
                }
            ]
        },
        "_links": {
            "first": {
                "href": "http://127.0.0.1:8081/api/todo?page=0&size=5&sort=id,asc"
            },
            "self": {
                "href": "http://127.0.0.1:8081/api/todo?page=0&size=5&sort=id,asc"
            },
            "next": {
                "href": "http://127.0.0.1:8081/api/todo?page=1&size=5&sort=id,asc"
            },
            "last": {
                "href": "http://127.0.0.1:8081/api/todo?page=3&size=5&sort=id,asc"
            }
        },
        "page": {
            "size": 5,
            "totalElements": 17,
            "totalPages": 4,
            "number": 0
        }
    }
```


