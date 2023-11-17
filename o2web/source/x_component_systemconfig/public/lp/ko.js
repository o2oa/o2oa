o2.xApplication.systemconfig.LP = {
    "title": "시스템 설정",
    "searchKey": "설정 항목 검색",
    "default": "기본",
    "permissionDenied": "현재 사용자의 권한이 충분하지 않습니다. 시스템 설정에 액세스하려면 관리자 계정을 사용해야 합니다.",

    "yes": "예",
    "no": "아니요",
    "uploadInfo": "파일을 여기로 끌어오거나 업로드를 클릭하세요",

    "baseConfig": "기본 설정",
    "systemInfo": "시스템 정보",
    "uiConfig": "인터페이스 설정",
    "componentDeploy": "컴포넌트 배포",
    "resourceDeploy": "자원 배포",
    "serviceDeploy": "서비스 배포",

    "securityConfig": "보안 설정",
    "passwordConfig": "암호 설정",
    "loginConfig": "로그인 설정",
    "ssoConfig": "싱글 사인온",
    "ternaryManagement": "삼원 관리",

    "serverConfig": "서버 설정",
    "serversConfig": "서버 구성",
    "centerServer": "중앙 서버",
    "appServer": "애플리케이션 서버",
    "webServer": "웹 서버",
    "databaseServer": "데이터베이스",
    "storageServer": "스토리지 구성",
    "cacheConfig": "캐시 설정",
    "clusterConfig": "클러스터 설정",
    "orgConfig": "조직 구성",
    "processConfig": "프로세스 구성",
    "cloudConfig": "클라우드 서비스",
    "dumpConfig": "백업 설정",
    "worktimeConfig": "근무 시간 설정",

    "messageConfig": "메시지 설정",
    "msgTypeConfig": "메시지 유형 설정",
    "pushConfig": "메시지 푸시",
    "mailConfig": "이메일 설정",
    "smsConfig": "SMS 설정",
    "mqConfig": "메시지 큐 설정",

    "queryIndexConfig": "인덱스 설정",

    "mobileConfig": "모바일 설정",
    "connectConfig": "연결 설정",
    "appConfig": "앱 설정",
    "moduleConfig": "모듈 설정",
    "iconConfig": "아이콘 설정",
    "ddConfig": "딩딩 통합",
    "wechatConfig": "위챗 통합",
    "welinkConfig": "WeLink 통합",
    "appTools": "앱 도구",
    "integrationConfig": "앱 통합",

    "select": "선택",

    "_systemInfo": {
        "title": "시스템 기본 정보 구성",
		"systemName": "시스템 이름",
		"systemNameInfo": "시스템 이름은 로그인 페이지와 브라우저 제목 표시줄에 표시됩니다.",
		"systemSubTitle": "시스템 부제목",
		"systemSubTitleInfo": "시스템 부제목은 로그인 페이지 하단에 표시됩니다.",
		"systemVersion": "현재 시스템 버전",
		"systemVersionInfo": "현재 시스템 버전",
		"baseInfo": "기본 정보",
		"systemStatus": "시스템 상태",
		"moduleStatus": "모듈 운영 상태",
		"language": "언어 환경",
		"languageInfo": "서버 언어 환경 설정",
		"languageValues": {
			"zh-CN": "중국어 간체",
			"en": "영어",
			"es": "스페인어"
		},
		"running": "운영 중",
		"stop": "사용 중지됨",
		"enable": "활성화됨",
		"server": "서버",
		"node": "노드",
		"serverInfo": "서버 정보",
		"webServer": "웹 서버",
		"appServer": "애플리케이션 서버",
		"centerServer": "중앙 서버",
		"dataServer": "데이터베이스 서비스",
		"storageServer": "파일 저장 서비스",
		"dataNode": "데이터베이스",
		"databaseUrl": "데이터베이스 연결",
		"byModule": "모듈별",
		"byServer": "서버별",
		"storageNode": "파일 저장",

        "serverData": {
			// "exposeJest": "인터페이스 문서(exposeJest)",
			"httpProtocol": "http 프로토콜(httpProtocol)",
			"host": "호스트(host)",
			"port": "포트(port)",
			"proxyHost": "프록시 호스트(proxyHost)",
			"proxyPort": "프록시 포트(proxyPort)",
			"requestLogEnable": "http 로그 활성화",
			"requestLogBodyEnable": "로그 본문 내용 기록",
			"requestLogRetainDays": "로그 보존 일수",
			"sslEnable": "SSL 활성화(sslEnable)",
			// "statEnable": "Druid 활성화",

			"cacheSize": "캐시 크기(cacheSize)",
			"includes": "포함된 클래스(includes)",
			"excludes": "제외된 클래스(excludes)",
			"jmxEnable": "JMX 활성화(jmxEnable)",
			"lockTimeout": "테이블 락 타임아웃(lockTimeout)",
			"logLevel": "로그 레벨(logLevel)",
			"maxIdle": "최대 아이들 연결 수(maxIdle)",
			"maxTotal": "최대 연결 수(maxTotal)",
			"slowSqlMillis": "느린 SQL 임계값(slowSqlMillis)",
			"statFilter": "Druid 문장 병합 활성화(statFilter)",
			"tcpPort": "TCP 포트(tcpPort)",
			"webPort": "WEB 포트(webPort)"
		},
        "storageData": {
			"port": "ftp 포트(port)",
			"sslEnable": "SSL 활성화(sslEnable)",
			"name": "이름(name)",
			"passivePorts": "패시브 모드 포트(passivePorts)",
			"prefix": "경로 접두사(prefix)",
			"deepPath": "깊은 경로 사용(deepPath)"
		},
		"storageAccounts": {
			"protocol": "프로토콜",
			"username": "모듈",
			"weight": "가중치",
			"name": "이름",
			"prefix": "경로 접두사",
			"deepPath": "깊은 경로 사용",
			"host": "호스트",
			"port": "포트"
		},
		"moduleData": {
			"node": "서비스 노드",
			"contextPath": "컨텍스트",
			"port": "서비스 포트",
			"sslEnable": "SSL 활성화",
			"proxyHost": "프록시 호스트",
			"proxyPort": "프록시 포트",
			"reportDate": "마지막 보고 시간",
			"moduleName": "모듈 이름",
			"className": "클래스"
		}
    },
    "operation": {
        "edit": "편집",
        "ok": "확인",
        "cancel": "취소",
        "enable": "활성화",
        "disable": "비활성화"
    },
    "_component": {
        "open": "열기",
        "edit": "편집",
        "uninstall": "제거",

        "deploy": "컴포넌트 배포",

        "removeComponentTitle": "컴포넌트 제거 확인",
        "removeComponent": "{name} 컴포넌트를 제거하시겠습니까?",
        "removeComponentOk": "컴포넌트가 제거되었습니다",

        "deploySuccess": "컴포넌트 배포 성공",

        "selectIcon": "아이콘 선택",
        "clearIcon": "아이콘 지우기",

        "name": "컴포넌트 이름",
        "title": "컴포넌트 제목",
        "path": "컴포넌트 경로",
        "urlPathInfo": "경로를 웹 페이지 URL로 추가하려면 '@url:'을 사용할 수 있습니다. 예: '@url:http://www.bing.com'",
        "visible": "가시 여부",
        "allowList": "접근 허용 목록",
        "denyList": "접근 거부 목록",
        "icon": "컴포넌트 아이콘",

        "upload": "리소스 업로드",
        "uploadWarn": "컴포넌트 zip 파일을 업로드하면 기존 컴포넌트가 덮어씌워집니다. 신중하게 작업하세요!",

        "componentDataError": "컴포넌트 이름, 컴포넌트 경로 및 컴포넌트 제목을 비워 둘 수 없습니다"
    },
    "_resource": {
        "webResource": "웹 리소스 배포",
        "webResourceInfo": "여기에서 웹 리소스를 배포할 수 있으며 정적 리소스 파일 또는 zip 파일을 업로드하여 시스템의 웹 서버에 배포하고 HTTP 프로토콜을 통해 액세스할 수 있습니다.",
        "serviceResource": "사용자 정의 서비스 배포",
        "serviceResourceInfo": "여기에서 개발한 사용자 정의 프로젝트를 배포할 수 있으며 컴파일된 jar 파일 또는 war 파일을 업로드할 수 있습니다. 배포 후 서버를 다시 시작해야 합니다.",

        "componentResource": "컴포넌트 배포",
        "componentResourceInfo": "여기에서 사용자 정의 개발한 O2OA 컴포넌트 또는 공식 원본 컴포넌트를 배포할 수 있습니다. O2OA 컴포넌트는 'x_component_{컴포넌트 이름}'이라는 폴더 또는 zip 파일입니다. 자세한 정보는 <a href='https://www.o2oa.net/develop.html' target='_blank'>O2OA 공식 커뮤니티</a>를 참조하십시오.",

        "upload": "리소스 업로드",
        "webUploadWarn": "배포할 정적 리소스 파일을 업로드하십시오. zip 파일은 자동으로 압축이 해제됩니다.",
        "serviceUploadWarn": "배포할 jar 파일 또는 war 파일을 업로드하십시오.",

        "overwrite": "배포 방식",
        "overwriteFalse": "삭제 후 업로드: 동일한 이름의 파일 및 폴더를 삭제한 후 업로드합니다.",
        "overwriteTrue": "덮어쓰기: 동일한 이름의 파일 및 폴더를 직접 덮어씁니다.",

        "deployPath": "배포 경로",
        "deployPathInfo": "zip 파일을 배포하는 경우 경로를 비워 둘 수 있으며 단일 파일을 배포하는 경우 반드시 배포 경로를 지정해야 합니다. 예: /myWebResource/subPath",

        "noDeployFile": "먼저 배포할 리소스 파일을 선택하십시오.",
        "deploySuccess": "리소스 배포 성공",

        "notWebResource": "<span style='color: red'>현재 서버에서 프론트 엔드 웹 리소스 배포가 허용되지 않습니다. 이 기능을 활성화하려면 서버 설정 - 서버 작업에서 설정할 수 있습니다.</span>",
        "notServiceResource": "<span style='color: red'>현재 서버에서 프론트 엔드 사용자 정의 서비스 배포가 허용되지 않습니다. 이 기능을 활성화하려면 서버 설정 - 서버 작업에서 설정할 수 있습니다.</span>"
    },
    "_uiConfig": {
        "baseConfig": "기본 설정",
        "menuConfig": "메인 메뉴 설정",
        "lnkConfig": "사이드바 설정",
        "userConfig": "사용자 인터페이스 설정",

        "openStatus": "시스템 진입",
        "openStatusInfo": "O2OA 시스템에 처음으로 진입할 때마다 기본적으로 마지막으로 종료한 시스템을 열게 됩니다. 이 동작을 변경할 수 있습니다.",
        "openStatusCurrent": "열려진 응용 프로그램과 현재 응용 프로그램을 마지막 종료 시스템 상태로 위치시킵니다(기본값)",
        "openStatusApp": "마지막으로 종료한 시스템의 응용 프로그램을 열고 홈페이지를 현재 응용 프로그램으로 설정합니다",
        "openStatusIndex": "홈페이지 응용 프로그램만 엽니다",

        "skin": "시스템 스킨",
        "skinConfig": "시스템 스킨을 사용자 정의로 변경할 수 있는지 여부",
        "skinConfigInfo": "사용자가 시스템 스킨을 사용자 정의로 변경할 수 있는지 여부",
        "skinDefault": "시스템 기본 스킨",
        "skinDefaultInfo": "시스템의 기본 스킨 색상을 설정합니다",
        "scaleConfig": "축척 허용",
        "scaleConfigInfo": "사용자가 시스템에서 표시되는 축척 비율을 사용자 정의로 설정할 수 있는지 여부",

        "defaultMenuInfo": "기본 메뉴 설정으로 저장하면 사용자 정의 메뉴 설정을 수행하지 않은 사용자에게 해당 설정이 적용됩니다.",
        "forceMenuInfo": "강제 메뉴 설정으로 저장하면 모든 사용자에게 해당 설정이 강제로 적용되며 사용자 정의 설정은 무시됩니다.",
        "userMenuInfo": "모든 사용자의 개인화된 메뉴 설정이 삭제되어 기본 방식으로 메뉴가 표시됩니다.",

        "clearDefaultMenuDataTitle": "기본 메뉴 설정 지우기",
        "clearDefaultMenuData": "기본 메뉴 설정을 지우시겠습니까?",
        "clearDefaultMenuDataSuccess": "기본 메뉴 설정이 지워졌습니다",
        "clearForceMenuDataTitle": "강제 메뉴 설정 지우기",
        "clearForceMenuData": "강제 메뉴 설정을 지우시겠습니까?",
        "clearForceMenuDataSuccess": "강제 메뉴 설정이 지워졌습니다",

        "clearUserMenuData": "사용자 개인화 메뉴 설정 지우기",
        "clearUserMenuDataSuccess": "사용자 개인화 메뉴 설정이 지워졌습니다",
        "clearUserMenuDataConfirm": "모든 사용자의 개인화 메뉴 설정을 지우시겠습니까?",

        "saveDefaultMenuDataSuccess": "기본 메뉴 설정이 성공적으로 저장되었습니다",
        "saveForceMenuDataSuccess": "강제 메뉴 설정이 성공적으로 저장되었습니다",

        "defaultMenu": "기본 메뉴 설정",
        "forceMenu": "강제 메뉴 설정",
        "userMenu": "사용자 개인화 메뉴 설정",

        "saveMenu": "설정 저장",
        "clearMenu": "설정 지우기",
        "loadMenu": "설정 불러오기",
        "clearUserMenu": "설정 지우기",

        "menu": {
            "application": "앱",
            "process": "프로세스",
            "cms": "정보",
            "query": "데이터",

            "defaultMenu": "기본 메뉴 상태로 복원"
        },
        "deleteLink": "자주 사용하는 응용 프로그램 바로 가기 삭제"
    },
    "_passwordConfig": {
        "personPassword": "사용자 암호 설정",
		"adminPassword": "관리자 암호",
		"saveSuccess": "구성 저장 성공",
		"passwordScript": "암호 스크립트",

		"newPersonPassword": "신규 사용자의 초기 암호",
		"newPersonPasswordInfo": "새로운 사용자를 생성할 때 다음 설정에 따라 사용자의 초기 암호가 생성되며 사용자는 시스템에 로그인한 후 암호를 직접 변경할 수 있습니다.",
		"initialPassword": "사용자 초기 암호",
		"initialPasswordText": "초기 암호 입력",
		"initialPasswordTypeOptions": {
			"mobile": "휴대폰 번호 뒷 6자리",
			"unique": "고유 코드 뒷 6자리",
			"employee": "직원 직번",
			"pinyin": "직원 이름의 로마자 표기법",
			"text": "고정된 비밀번호",
			"script": "스크립트를 통해 사용자 지정 초기 암호 생성"
		},
		"initialPasswordType": {
			"mobileScript": "return person.getMobile().slice(-6)",
			"uniqueScript": "return person.getUnique().slice(-6)",
			"employeeScript": "return person.getEmployee()",
			"pinyinScript": "return person.getPinyin()",
			"textInfo": "아래 입력란에 입력한 암호는 새로 생성된 사용자의 초기 암호로 사용됩니다.",
			"scriptInfo": "아래 편집기에 스크립트를 입력하여 새로 생성된 사용자의 초기 암호로 사용될 문자열 값을 반환합니다. person 개체를 사용하여 관련 사용자 정보를 가져올 수 있습니다. 예를 들어, 사용자 이름의 로마자 표기법을 초기 암호로 사용하려면 다음 스크립트를 사용할 수 있습니다: return person.getPinyin()"
		},

        "passwordPeriod": "암호 만료 날짜",
		"passwordPeriodInfo": "암호를 변경하지 않고 설정된 날짜 이상으로 시간이 지난 사용자는 로그인 후 암호를 변경해야하며 그렇지 않으면 시스템에 액세스할 수 없습니다. 0으로 설정하면 암호가 만료되지 않음을 의미합니다.",

		"passwordRegex": "암호 복잡성",
		"passwordRegexInfo": "사용자 암호의 복잡성 요구 사항 설정",

		"passwordRegexMin": "최소 길이",
		"passwordRegexMax": "최대 길이",
		"passwordRegexLength": "암호 길이",
		"passwordRule": "암호 규칙",
		"passwordRuleValue": {
			"useLowercase": "소문자를 포함해야 함",
			"useNumber": "숫자를 포함해야 함",
			"useUppercase": "대문자를 포함해야 함",
			"useSpecial": "특수 문자를 포함해야 함 (#?!@$%^&*-)"
		},
		"passwordRuleRegex": {
			"useLowercase": "(?=.*[a-z])",
			"useNumber": "(?=.*\\d)",
			"useUppercase": "(?=.*[A-Z])",
			"useSpecial": "(?=.*?[#?!@$%^&*-])"
		},
		"savePasswordRule": "암호 규칙 설정 저장",
		"passwordLengthText": "{n} 자, {text}",

		"passwordRsa": "암호 암호화 전송",
		"passwordRsaInfo": "시스템은 기본적으로 평문 전송을 사용합니다. 이 옵션을 활성화하여 암호의 암호화 전송을 활성화 할 수 있습니다. (변경 후 서버를 다시 시작해야 함)",


        "adminPasswordInfo": "여기에서 슈퍼 관리자 xadmin의 암호를 변경할 수 있습니다. (변경 후 서버를 다시 시작해야 함)",
		"modifyAdminPassword": "관리자 암호 수정",

		"oldPassword": "기존 암호",
		"newPassword": "새 암호",
		"confirmPassword": "암호 확인",

		"ternaryPassword": "삼원 관리자 암호",
		"ternaryPasswordInfo": "삼원 관리를 활성화한 경우 시스템 관리자 (systemManager), 보안 관리자 (securityManager) 및 보안 감사자 (auditManager)의 암호를 여기에서 수정할 수 있습니다.",
		"modifySystemManagerPassword": "시스템 관리자 암호 수정",
		"modifySecurityManagerPassword": "보안 관리자 암호 수정",
		"modifyAuditManagerPassword": "보안 감사자 암호 수정",

		"passwordDisaccord": "새 암호와 확인 암호가 일치하지 않습니다.",
		"passwordEmpty": "기존 암호, 새 암호 및 확인 암호를 입력하세요.",

		"tokenEncryptType": "암호 암호화 방식",
		"tokenEncryptTypeInfo": "O2OA는 다음과 같은 몇 가지 암호 및 토큰 암호화 방식을 지원하며 필요에 따라 선택할 수 있습니다. 자세한 내용은 다음을 참조하십시오 : <a href='https://www.o2oa.net/search.html?q=%E5%9B%BD%E5%AF%86' target='_blank'>국가 암호화</a>",
		"tokenEncryptTypeLabel": "암호화 방식",
		"encryptTypeOptions": {
			"default": "기본",
			"sm4": "국가 상용 암호 알고리즘"
		},
		"tokenEncryptTypeInfo3": "<div style='color: red'>주의 : '암호화 방식 변경 확인'을 클릭한 후 이 설정이 즉시 적용됩니다. <ul style='line-height: 30px'><li>이로 인해 : 1. 모든 사용자의 로그인 상태가 만료됩니다. 2. 암호화 방식이 변경되었으므로 기존 사용자는 시스템에 로그인할 수 없게 됩니다.</li>" +
			"<li>시스템을 정상적으로 사용하려면 다음 단계를 따르십시오.：<br> xadmin 계정을 사용하여 시스템에 다시 로그인하고 모든 사용자 암호를 재설정합니다.</li></ul></div>",
		"tokenEncryptTypeButton": "암호 암호화 방식 변경 확인",
		"changeTokenEncryptTypeInfo": "암호 암호화 방식을 변경하시겠습니까?"
    },
    "_loginConfig": {
        "baseConfig": "기본 구성",
		"moreConfig": "추가 구성",
		"ldapConfig": "LDAP 인증 구성",
		"captchaLogin": "이미지 인증 코드 로그인 활성화",
		"codeLogin": "문자 메시지 인증 코드 로그인 활성화",
		"bindLogin": "QR 코드 스캔 로그인 활성화",
		"faceLogin": "얼굴 인식 로그인 활성화",
		"captchaLoginInfo": "활성화하면 로그인할 때 이미지 인증 코드를 올바르게 입력해야 합니다.",
		"codeLoginInfo": "활성화하면 문자 메시지 인증 코드를 사용하여 로그인할 수 있습니다.",
		"bindLoginInfo": "활성화하면 QR 코드를 스캔하여 로그인할 수 있습니다.",
		"faceLoginInfo": "활성화하면 얼굴 인식 로그인을 허용하며 사용자는 개인 설정에서 얼굴 특징을 설정할 수 있습니다. 활성화하려면 face라는 이름의 SSO 구성을 만들어야 하며 키는 xplatform입니다 (이 기능은 실험적이며 https를 활성화해야 함)",

		"loginError": "로그인 오류 처리",
		"loginErrorInfo": "사용자가 연속으로 여러 번 잘못된 암호를 입력하면 계정이 잠깁니다. 여기에서 연속 로그인 오류 횟수 제한과 계정 잠금 기간을 설정할 수 있습니다.",

		"loginErrorCount": "로그인 오류 횟수 제한",
		"lockTime": "잠금 기간 (분)",

		"tokenExpired": "로그인 유효 기간",
		"tokenExpiredInfo": "사용자가 시스템에 로그인한 후 일정 시간 동안 서버와 상호 작용하지 않으면 시스템에서 해당 로그인을 로그아웃합니다. 여기에서 로그인 유효 기간을 분 단위로 설정할 수 있습니다.",

		"tokenName": "토큰 이름",
		"tokenNameInfo": "시스템의 기본 토큰 이름은 x-token이지만 동일한 도메인 내에서 쿠키 충돌을 방지하기 위해 여기에서 토큰 이름을 변경할 수 있습니다. 이는 동일한 도메인에 여러 O2OA를 배포하는 경우에 특히 유용합니다. (서버를 다시 시작해야 함)",

		"tokenCookieHttpOnly": "쿠키 HttpOnly 활성화",
		"tokenCookieHttpOnlyInfo": "토큰 쿠키를 저장할 때 httponly를 활성화할지 여부",

		"tokenCookieSecure": "쿠키 Secure 활성화",
		"tokenCookieSecureInfo": "토큰 쿠키를 저장할 때 secure를 활성화하여 이 쿠키가 https 프로토콜에서만 전송되도록합니다.",

		"enableSafeLogout": "안전한 로그아웃 활성화",
		"enableSafeLogoutInfo": "안전한 로그아웃을 활성화하면 어떤 단말에서 로그아웃 작업을 수행하면 모든 단말에서 로그인 상태가 동시에 로그아웃됩니다.",

		"register": "자가 등록 활성화",
		"registerInfo": "여기에서 시스템 사용자로의 자가 등록을 허용할지 여부 및 자가 등록 방법을 구성할 수 있습니다.",
		"registerValues": {
			"disable": "허용 안 함",
			"captcha": "인증 코드로 등록",
			"code": "문자 메시지로 등록"
		},

        "loginPage": "포털 페이지 사용하여 로그인",
		"loginPageInfo": "시스템은 사용자 지정 포털 페이지를 로그인 페이지로 사용하는 것을 지원하며, 로그인 페이지 앱 템플릿을 앱 스토어에서 무료로 이용할 수 있습니다.",
		"loginPagePortal": "포털 로그인",

		"selectPortal": "포털 선택",

		"indexPage": "포털 페이지를 시스템 홈페이지로 사용",
		"indexPageInfo": "사용자 정의 포털 페이지를 시스템 홈페이지로 사용하여 로그인 후 이 페이지가 열립니다.",
		"indexPagePortal": "홈페이지 포털",

		"ldapAuthEnable": "LDAP 인증 사용",
		"ldapAuthEnableInfo": "이 옵션을 활성화하면 사용자 로그인 인증에 LDAP 인증을 사용하며 이제 더 이상 시스템 비밀번호를 사용하지 않습니다. 아래 LDAP 매개변수를 올바르게 구성하십시오.",
		"ldapAuthUrl": "LDAP 주소",
		"ldapAuthUrlInfo": "LDAP 서비스 주소, ldap://도메인 또는 IP:포트",
		"baseDn": "LDAP 쿼리 루트(BaseDN)",
		"baseDnInfo": "LDAP 쿼리의 루트 이름, 예: dc=zone, DC=COM",
		"userDn": "인증 사용자 바인딩 속성",
		"userDnInfo": "인증 사용자 바인딩 속성: uid, 휴대폰 번호, 직원 코드 또는 이메일(해당 속성을 사용하여 baseDn 아래에서 고유하고 o2에서 관련 사용자를 찾을 수 있어야 함, 예: uid 또는 mail 등",

		"superPermission": "슈퍼 관리자 패스워드 사용",
		"superPermissionInfo": "이 항목을 활성화하면 슈퍼 관리자(xadmin) 패스워드로 다른 사용자 계정에 로그인할 수 있으므로 관리자는 일반 사용자로 로그인하여 데이터 유지 관리 및 문제 해결을 수행할 수 있습니다.",

		"bindDnUser": "관리 사용자 바인딩",
		"bindDnUserInfo": "관리 권한을 가진 사용자를 바인딩하려면 관리자(관리 권한이 있어야 함)를 지정하십시오. 예: cn=root",
		"bindDnPwd": "관리자 사용자 패스워드",
		"bindDnPwdInfo": "관리자 사용자의 패스워드",
		"ldapEnabledError": "LDAP 매개변수를 모두 구성한 후 LDAP 인증을 활성화하십시오."

    },
    "_ssoConfig": {
        "ssoConfig": "인증 키 설정",
		"ssoConfigInfo": "다른 시스템에 대한 단일 로그인(SSO) 및 서비스 호출을 위한 인증을 여러 개 생성할 수 있습니다.",
		"ssoConfigInfo2": "각 인증에는 인증 이름과 키를 제공해야하며, 이 키는 액세스 티켓을 생성하는 데 사용되는 암호화 및 해독 공개 키입니다.",
		"addSSOConfig": "인증 설정 추가",
		"editSSOConfig": "인증 설정 편집",
		"isEnable": "사용 여부",
		"ssoConfigName": "인증 이름",
		"ssoConfigKey": "키",

		"ssoConfigKeyInfo": "키 길이는 8의 배수여야 합니다.",
		"ssoKeyLengthError": "키 길이를 8의 배수로 유지하십시오.",

		"removeSSOConfigTitle": "인증 설정 삭제 확인",
		"removeSSOConfig": "인증 설정을 삭제하시겠습니까: “{name}”",

		"ssoDataError": "인증 이름 및 인증 키를 입력해야 합니다.",
		"ssoSameNameError": "인증 이름 “{name}”은(는) 이미 존재합니다. 다른 이름을 사용하십시오.",

        "useSSOConfig": "인증 키 사용 방법",
		"useSSOConfigInfo": "인증 키는 다음 두 가지 상황에서 사용해야 합니다:",
		"useSSOConfigInfo1": "1. 외부 시스템에서 O2OA와 단일 로그인을 구현해야 하는 경우;",
		"useSSOConfigInfo2": "2. 외부 시스템에서 O2OA 플랫폼의 인터페이스 서비스를 호출해야 하는 경우;",
		"useSSOConfigInfo3": "인증 이름과 키를 외부 시스템에 알려야 하며, 외부 시스템은 3DES 알고리즘을 사용하여 키로 <span style='color: blue'>\"person#timestamp\"</span> 텍스트를 암호화하여 O2OA에 대한 임시 티켓(token)을 획득합니다.<br/>" +
			"<span style='color: blue'>person</span>: 특정 사용자의 사용자 이름, 고유 코드 또는 직원 번호를 나타냅니다. (외부 시스템과 O2OA의 사용자 관련 필드에 따라 사용자가 결정됩니다.)<br/>" +
			"<span style='color: blue'>timestamp</span>: 1970년 1월 1일 0시 0분부터 현재 시간까지의 밀리초 수를 나타냅니다. (토큰의 유효성을 보장하기 위해 유효 시간은 1분입니다.)<br/><br>" +
			"토큰을 생성한 후, 외부 시스템은 다음 주소를 통해 O2OA와의 단일 인증을 구현할 수 있습니다:<br/>" +
			"http://servername/x_desktop/sso.html?client={<span style='color: blue'>client</span>}&xtoken={<span style='color: blue'>token</span>}&redirect={<span style='color: blue'>redirect</span>}<br/>" +
			"<span style='color: blue'>client</span>: 사용 중인 인증 이름을 나타냅니다;<br/>" +
			"<span style='color: blue'>token</span>: 생성된 임시 티켓 토큰을 나타냅니다;<br/>" +
			"<span style='color: blue'>redirect</span>: 인증이 성공한 후로 이동할 주소를 나타냅니다;<br/>",
		"useSSOConfigInfo4": "인증 구성에 대한 자세한 정보는 <a target='_blank' href='https://www.o2oa.net/search.html?q=%E9%89%B4%E6%9D%83'>여기를 클릭하여 확인하십시오</a>.",

        "ssoTokenTools": "관련 도구",
		"ssoTokenCode": "암호화 예제 코드 보기",
		"ssoTokenCheck": "토큰 유효성 검사",

		"oauthConfig": "OAuth 설정",
		"oauthClientConfig": "OAuth 클라이언트 설정",
		"oauthServerConfig": "OAuth 서버 설정",

		"oauthClientConfigInfo": "O2OA 플랫폼을 OAuth2 인증 서버로 사용하는 경우, 여기에서 여러 OAuth 클라이언트를 구성하여 다른 시스템에 대한 로그인 및 인증을 구현할 수 있습니다.",
		"oauthServerConfigInfo": "이미 OAuth2 인증 서버가 있는 경우 여기에서 여러 OAuth 서버를 구성하여 이 시스템에 대한 로그인 및 인증을 구현할 수 있습니다.",

		"addOauthClientConfig": "OAuth 클라이언트 설정 추가",
		"addOauthServerConfig": "OAuth 서버 설정 추가",
		"editOauthClientConfig": "OAuth 클라이언트 편집",
		"editOauthServerConfig": "OAuth 서버 편집",

		"removeOauthConfigTitle": "OAuth 설정 삭제 확인",
		"removeOauthConfig": "OAuth 설정을 삭제하시겠습니까?: “{name}”",

		"oauthClientDataError": "클라이언트 ID(ClientId)와 클라이언트 비밀키(ClientSecret)는 비워 둘 수 없습니다.",
		"oauthClientSameNameError": "클라이언트 ID(ClientId) “{name}”이(가) 이미 존재합니다. 다른 클라이언트 ID를 사용하십시오.",

        "oauth_clientId": "클라이언트 ID",
		"oauth_clientSecret": "클라이언트 비밀키",
		"oauth_mapping": "리턴 매핑",
		"oauth_name": "이름",
		"oauth_displayName": "표시 이름",
		"oauth_icon": "아이콘 URL",
		"oauth_authAddress": "키 요청 주소",
		"oauth_authParameter": "키 요청 매개 변수",
		"oauth_authMethod": "키 요청 방법",

		"oauth_tokenAddress": "토큰 요청 주소",
		"oauth_tokenParameter": "토큰 요청 매개 변수",
		"oauth_tokenMethod": "토큰 요청 방법",
		"oauth_tokenType": "토큰 형식",

		"oauth_infoAddress": "정보 요청 주소",
		"oauth_infoParameter": "정보 요청 매개 변수",
		"oauth_infoMethod": "정보 요청 방법",
		"oauth_infoType": "정보 형식",

		"oauth_infoCredentialField": "개인 정보 필드",
		"oauth_bindingField": "사용자 바인딩 필드",

		"oauth_infoScriptText": "정보 처리 스크립트",

		"infoScriptTextInfo": "정보 형식이 JSON 또는 FORM이 아닐 때 스크립트를 사용하여 정보를 JSON 객체로 서식화하여 시스템이 올바르게 처리할 수 있습니다. 아래 스크립트 편집기에 스크립트를 작성하고 JSON 객체를 반환하십시오. <span style='color: blue'>this.text</span>를 사용하여 응답 정보의 원시 텍스트를 가져올 수 있습니다."

    },
    "_ternaryManagement": {
        "enable": "세 유저 관리 활성화",
		"enableInfo": "시스템은 시스템 관리자, 보안 관리자 및 보안 감사원이 시스템 보안 관리를 위해 책임을 나누고 권한을 나누는 방식을 지원하며, 세 유저 관리를 활성화하면 xadmin 사용자 및 권한이 해제되며 시스템 감사 로그 기록도 활성화됩니다 (서버를 다시 시작해야 함)<br>" +
			"세 개의 역할은 각각 다음과 같습니다 : " +
			"<ul><li>시스템 관리자 (시스템 내장 사용자 : systemManager) : 시스템 사용자, 조직 관리 및 시스템 운영 및 유지 보수 작업을 담당합니다. </li>" +
			"<li>보안 관리자 (시스템 내장 사용자 : securityManager) : 권한 설정을 담당하며 시스템 감사 로그, 사용자 및 시스템 관리자의 작업을 검토하고 분석합니다. </li>" +
			"<li>보안 감사원 (시스템 내장 사용자 : auditManager) : 시스템 관리자 및 보안 관리자의 작업을 감사하고 추적합니다. </li></ul>" +
			"애플리케이션은 매일 1시에 전날의 작업 로그를 분석하여 세 관리자가 감사 및 조회할 수 있도록 합니다.<br>" +
			"세 유저 관리 기능을 완전히 활용하려면 애플리케이션 시장에서 '세 유저 관리' 앱을 설치해야 합니다." +
			"세 유저 관리에 대한 자세한 내용은 다음 문서 및 비디오를 참조하세요 : <a href='https://www.o2oa.net/search.html?q=%E4%B8%89%E5%91%98%E7%AE%A1%E7%90%86' target='_blank'>세 유저 관리</a>",
		"logRetainDays": "로그 보유 일 수",
		"logRetainDaysInfo": "로그를 최대로 보유하는 일 수를 설정합니다.",

		"logBodyEnable": "본문 내용 기록",
		"logBodyEnableInfo": "본문 내용을 기록하면 더 자세한 로그 정보를 얻을 수 있지만 디스크 공간 사용량과 서버 부하가 크게 증가할 수 있습니다."
    },
    "_databaseServer": {
        "databaseSource": "데이터 소스 설정",
		"entity": "엔터티 클래스 설정",
		"tools": "백업 도구",
		"infoInner": "내장 데이터베이스를 사용 중입니다. O2OA의 내장 데이터베이스는 내장형 메모리 데이터베이스로 개발 환경, 기능 데모 환경에 적합하지만 공식 환경으로 사용하기에는 적합하지 않습니다. " +
			"공식 환경에서 사용하는 경우 더 높은 성능과 안정성을 가진 상업용 데이터베이스를 사용하는 것이 좋습니다.",
		"infoExternal": "외부 데이터베이스를 사용 중입니다. O2OA의 내장 데이터베이스가 비활성화되었습니다.",

		"info": "<span style='color: red'>데이터베이스 설정을 수정하면 대부분의 경우 기존 데이터에 영향을 미칩니다. 이 설정을 신중하게 수정하십시오!</span>",
		"info2": "데이터베이스 설정을 수정하기 전에 O2OA의 백업 기능 (ctl -dd)을 사용하여 시스템 데이터를 백업하는 것이 좋습니다. 데이터베이스 구성을 수정한 후 서버를 다시 시작하고 백업된 데이터를 데이터베이스에 복원해야 합니다 (ctl -rd). 모든 데이터베이스 관련 설정 변경에는 서버를 다시 시작해야 합니다.",

		"innerDataSources": "내장 데이터베이스",
		"externalDataSources": "외부 데이터베이스",
		"innerDataSourcesInfo": "O2OA의 내장 데이터베이스는 내장형 메모리 데이터베이스로 개발 환경과 기능 데모 환경에 적합합니다.",
		"externalDataSourcesInfo": "O2OA는 외부 데이터베이스 확장을 지원하며, 프로덕션 환경에서는 데이터 보안과 성능을 보장하기 위해 상업용 데이터베이스를 사용하는 것이 좋습니다.",

		"addDatabaseConfig": "데이터베이스 설정 추가",

		"databaseUrl": "데이터베이스 연결",
		"enable": "사용 여부",
		"username": "사용자 이름",
		"password": "비밀번호",

        "tcpPort": "연결 포트",
		"tcpPortInfo": "데이터베이스 JDBC 연결 포트입니다. 사용자 이름은 sa이며 초기 데이터베이스 암호는 xadmin입니다. 데이터베이스는 /o2server/local/repository/data/X.mv.db에 생성됩니다. 데이터베이스 파일이 생성되면 해당 데이터베이스의 암호가 생성됩니다.",
		"webPort": "WEB 포트",
		"webPortInfo": "H2는 웹 기반 클라이언트를 제공하며, 이 포트는 웹 클라이언트의 액세스 포트입니다. 사용자 이름은 sa이며 초기 xadmin 데이터베이스 암호입니다.",
		"jmxEnable": "JMX 활성화",
		"jmxEnableInfo": "활성화하면 로컬 JMX 클라이언트를 통해 액세스할 수 있으며 원격 JMX 클라이언트는 지원하지 않습니다.",
		"cacheSize": "캐시 크기",
		"cacheSizeInfo": "H2 데이터베이스의 캐시 크기입니다. 캐시로 사용할 메모리 크기를 M 단위로 설정합니다. 기본값은 512M입니다.",
		"logLevel": "로그 레벨",
		"maxTotal": "최대 연결 수",
		"maxIdle": "최대 유휴 연결 수",
		"statEnable": "통계 활성화",
		"statFilter": "통계 필터",
		"slowSqlMillis": "느린 SQL 밀리 초",
		"slowSqlMillisInfo": "느린 SQL을 별도로 기록할 때 사용되는 기준 시간(기본값: 2000 밀리 초)",
		"lockTimeout": "락 타임아웃(밀리 초)",

        "inputDatabaseUrl": "데이터베이스 연결 정보를 입력하세요",

		"entityConfig": "엔터티 클래스 저장소 할당",
		"entityConfigInfo": "다중 데이터베이스를 활성화한 경우 이곳에서 시스템 내 엔터티 클래스 저장소를 할당하여 성능을 향상시킬 수 있습니다. <span style='color: red'>모든 엔터티 클래스에 해당하는 저장소 데이터베이스를 할당했는지 확인해야 합니다.</span>",

		"oneDatabase": "시스템 내 엔터티 클래스에 저장소 데이터베이스를 할당하려면 두 개 이상의 데이터베이스를 활성화해야 합니다. 현재 하나의 데이터베이스만 활성화되어 있습니다.",
		"oneDatabaseInfo": "시스템 내 엔터티 클래스에 저장소 데이터베이스를 할당하려면 두 개 이상의 데이터베이스를 활성화해야 합니다.",

		"includeEntity": "허용된 엔터티 클래스",
		"includeEntityInfo": "이 데이터베이스에 저장을 허용하는 엔터티 클래스 목록입니다. 모두 허용하려면 비워 두십시오. 여러 개의 엔터티 클래스를 입력할 경우 쉼표 또는 줄바꿈으로 구분합니다.",
		"excludeEntity": "제외된 엔터티 클래스",
		"excludeEntityInfo": "이 데이터베이스에 저장을 제한하는 엔터티 클래스 목록입니다. 아무 것도 제한하지 않으려면 비워 두십시오. 여러 개의 엔터티 클래스를 입력할 경우 쉼표 또는 줄바꿈으로 구분합니다.",

		"editDatabase": "데이터베이스 구성 편집",


        "saveDatabaseConfig": "모든 데이터베이스 구성 저장",
		"saveDatabaseConfigInfo": "이 페이지의 구성은 즉시 저장되지 않습니다. 수정한 구성을 저장하려면이 버튼을 클릭해야 합니다.",
		"saveDatabaseConfirm": "데이터베이스 구성을 저장하려고 합니다.<br><span style='color:red'>기존 데이터(비즈니스 및 디자인 데이터 포함)에 영향을 줄 수 있습니다.</span><br><br>데이터베이스 구성을 저장하시겠습니까?",

		"reloadDatabaseConfig": "모든 데이터베이스 구성 다시로드",
		"reloadDatabaseConfigInfo": "이 페이지에서 저장하지 않은 수정 사항을 폐기하려면이 버튼을 클릭할 수 있습니다.",
		"reloadDatabaseConfirm": "데이터베이스 구성을 다시로드하려고 합니다. 저장하지 않은 수정 사항이 손실될 수 있습니다. 데이터베이스 구성을 다시로드하시겠습니까?",

		"saveEntityConfig": "엔터티 클래스 구성 저장",
		"saveEntityConfirm": "엔터티 클래스 구성을 저장하려고 합니다.<br><span style='color:red'>기존 데이터(비즈니스 및 디자인 데이터 포함)에 영향을 줄 수 있습니다.</span><br><br>엔터티 클래스 구성을 저장하시겠습니까?",
		"reloadEntityConfig": "엔터티 클래스 구성 다시로드",
		"reloadEntityConfirm": "엔터티 클래스 구성을 다시로드하려고 합니다. 저장하지 않은 수정 사항이 손실될 수 있습니다. 엔터티 클래스 구성을 다시로드하시겠습니까?",

        "entityList": "선택 목록",
		"selectedEntityList": "선택된 목록",
		"findClass": "클래스 이름 찾기",

		"removeDatabaseConfigTitle": "데이터베이스 구성 삭제 확인",
		"removeDatabaseConfig": "<span style='color: red'>주의: 데이터베이스 구성을 삭제합니다. \"{name}\" 데이터베이스를 삭제하기 전에 시스템 데이터를 백업하세요.</span><br><br>이 작업을 진행하시겠습니까?",

		"saveDatabaseConfigSuccess": "데이터베이스 구성이 성공적으로 저장되었습니다. 서버를 다시 시작하십시오.",
		"saveEntityConfigSuccess": "엔터티 클래스 구성이 성공적으로 저장되었습니다. 서버를 다시 시작하십시오.",

		"dumpRestoreTools": "데이터베이스 백업 복원 도구",
		"toolsInfo": "O2OA는 데이터 백업 및 복원 도구를 제공합니다. <span style='color: red'>데이터베이스 구성을 수정하면 대부분의 경우 시스템 기존 데이터에 영향을 미칠 수 있습니다.</span> " +
			"따라서 데이터베이스 구성을 수정하기 전에 O2OA의 백업 기능을 사용하여 시스템 데이터를 백업하고, 데이터베이스 구성을 수정한 후에 서버를 다시 시작하고 백업된 데이터를 데이터베이스에 복원하는 것이 좋습니다.<br>" +
			"<span class='mainColor_color'>데이터 백업 또는 복원 작업 중에는 이 페이지를 떠나지 마십시오. 다른 브라우저 창에서 다른 작업을 수행할 수 있습니다.</span>",

        "dumpTools": "데이터 백업",
		"dumpToolsInfo": "이 버튼을 클릭하여 데이터를 백업합니다. <span style='color: red'>시스템이 데이터를 빈번하게 읽고 쓰는 동안 백업하지 마십시오.</span>",
		"dumpWaitLog": "데이터 백업 미진행",
		"dumpErrorLog": "데이터 백업 중 오류 발생",

		"dumpBegin": "백업 시작 확인",
		"dumpBeginInfo": "데이터 백업은 서버 성능에 영향을 줄 수 있으므로 데이터 백업을 시작하시겠습니까?",

		"dumpCheckButton": "백업 상태 확인",
		"dumpCheck": "백업 상태 확인 중...",
		"dumpStop": "데이터 백업 미진행",
		"dumpRunning": "데이터 백업 진행 중...",
		"dumpEnd": "데이터 백업이 완료되었습니다.",

        "restoreTools": "데이터 복원",
		"restoreToolsInfo": "이 버튼을 클릭하여 데이터를 복원합니다. <span style='color: red'>시스템이 데이터를 빈번하게 읽고 쓰는 동안 복원하지 마십시오.</span>",
		"restoreToolsInfo2": "시스템에 데이터 테이블이 포함되어 있는 경우, 데이터 복원을 완료한 후 데이터 센터로 이동하여 모든 데이터 테이블을 컴파일한 다음 데이터 복원을 다시 실행하고 서버를 다시 시작하십시오.",
		"restoreWaitLog": "데이터 복원 미진행",
		"restoreErrorLog": "데이터 복원 중 오류 발생",

		"restoreBegin": "복원 시작 확인",
		"restoreBeginInfo": "데이터 복원은 서버 성능에 영향을 줄 수 있으므로 데이터 복원을 시작하시겠습니까?",

		"restoreCheckButton": "복원 상태 확인",
		"restoreCheck": "복원 상태 확인 중...",
		"restoreStop": "데이터 복원 미진행",
		"restoreRunning": "데이터 복원 진행 중...",
		"restoreEnd": "데이터 복원이 완료되었습니다."


    },
    "_cloudConfig": {
        "info": "O2 클라우드 서비스는 앱 마켓, 모바일 오피스 위치, SMS 서비스, 문서 변환 등 다양한 부가 서비스를 제공합니다. O2 클라우드 서버에 로그인하면 이를 사용할 수 있습니다.",
		"recheck": "연결 다시 확인",

		"notValidatedInfo": "O2 클라우드에 로그인하면 앱 마켓에 액세스하고 모바일 오피스 앱을 연결하며 SMS 서비스, 문서 변환 등 다양한 기능을 사용할 수 있습니다!",
		"disconnectInfo": "귀하의 서버는 O2 클라우드에 연결할 수 없습니다. 서버 네트워크 환경을 확인하십시오.",
		"validatedInfo": "<span style='color: #ff0000'>안녕하세요:</span> {name}, O2 클라우드에 로그인되었습니다. 모바일 오피스를 포함한 모든 O2 플랫폼 기능을 사용할 수 있습니다!",

		"connected": "이제 O2 클라우드에 연결할 수 있습니다!",
		"disconnect": "귀하의 서버는 O2 클라우드에 연결할 수 없습니다!",
		"notValidated": "아직 O2 클라우드에 로그인하지 않았습니다!",
		"validated": "이미 O2 클라우드에 로그인되었습니다!",

		"loginInfo": "이미 O2 클라우드 계정이 있으신 경우 여기를 클릭하여 로그인하십시오:",
		"loginButtonText": "O2 클라우드 로그인",
		"registerInfo": "O2 클라우드 계정이 없으신 경우 여기를 클릭하여 등록하십시오:",
		"registerButtonText": "O2 클라우드 등록",
		"forgotPasswordInfo": "O2 클라우드 계정 비밀번호를 잊으셨다면 여기를 클릭하여 재설정하십시오:",
		"forgotPasswordButtonText": "O2 클라우드 비밀번호 재설정",

        "collectUsername": "O2 클라우드 계정",
		"collectPassword": "O2 클라우드 비밀번호",
		"collectMobile": "휴대전화 번호",
		"collectMail": "이메일 주소",
		"collectCode": "인증 코드",
		"collectConfirm": "비밀번호 확인",
		"getCode": "인증 코드 가져오기",
		"regetCode": "다시 가져오기",

		"inputCollectUsername": "O2 클라우드 계정을 입력하세요",
		"inputCollectPassword": "O2 클라우드 비밀번호를 입력하세요",
		"inputCollectMobile": "휴대전화 번호를 입력하세요",
		"inputCollectMail": "이메일 주소를 입력하세요",
		"inputCollectCode": "SMS 인증 코드를 입력하세요",
		"inputCollectConfirm": "비밀번호 확인을 입력하세요",
		"collectUsernameExist": "O2 클라우드 계정 이름이 이미 존재합니다",
		"collectUsernameNotExist": "O2 클라우드 계정 이름이 존재하지 않습니다",
		"passwordDisagree": "비밀번호 확인이 일치하지 않습니다",
		"mobileError": "잘못된 휴대전화 번호 입력",
		"mailError": "잘못된 이메일 주소 입력",

        "registerCollect": "O2 클라우드 계정 등록",
		"forgotPassword": "비밀번호를 잊으셨나요",
		"loginError": "O2 클라우드 로그인 실패, 계정 이름과 비밀번호를 확인하세요",
		"registerError": "O2 클라우드 계정 등록 오류, 기술 지원팀에 문의하세요",
		"deleteError": "O2 클라우드 계정 삭제 오류, 기술 지원팀에 문의하세요",
		"resetPasswordError": "O2 클라우드 계정 비밀번호 재설정 오류, 기술 지원팀에 문의하세요",

		"deleteCollectUnit": "O2 클라우드 계정 삭제",
		"deleteCollectUnitInfo": "O2 클라우드 계정 삭제 중: {name}, 휴대전화 번호를 입력하고 확인을 위해 인증 코드를 가져오세요",

		"resetPasswordCollect": "O2 클라우드 계정 비밀번호 재설정",

		"modifyCollect": "계정 수정",
		"logoutCollect": "연결 해제",
		"modifyCollectPassword": "비밀번호 수정",
		"deleteCollect": "계정 삭제",
		"reloginCollect": "다시 로그인"
    },
    "_serversConfig": {
        "serverInfo": "서버 정보",
		"baseConfig": "기본 설정",
		"environmentConfig": "환경 변수 설정",
		"sameConfig": "동일한 서버 구성 사용",
		"sameConfigInfo": "O2OA에는 중앙 서비스, 응용 프로그램 서비스 및 웹 서비스 세 가지 논리 서버가 있으며 기본적으로 동일한 포트 및 동일한 구성을 사용합니다. 그러나 이러한 서비스에 대한 서로 다른 포트, 호스트 등을 구성할 수도 있습니다.",

		"serverConfig": "서버 구성",
		"serverConfigInfo": "여기에서 서버 관련 매개변수를 구성합니다 (서버를 다시 시작해야 함)",

		"serverPort": "서비스 포트",
		"serverPortInfo": "서버의 수신 대기 포트",

		"serverProxyHost": "액세스 호스트 이름",
		"serverProxyPort": "액세스 호스트 포트",
		"sslEnable": "SSL 사용 여부",
		"httpProtocol": "웹 액세스 프로토콜",
		"sslKeyStorePassword": "SSL 키스토어 비밀번호",
		"sslKeyManagerPassword": "SSL 키 관리자 비밀번호",
		"sslInfo": "<span>SSL을 사용하려면 이미 증명서 파일을 O2OA 서버의 구성 디렉토리로 복사하고 'keystore'로 이름을 변경해야 합니다. 클러스터 환경에서는 각 서버에 증명서 파일을 배치해야 합니다 (서버를 다시 시작해야 함).</span>",

		"saveServerConfig": "서버 구성 저장",
		"saveServerConfigSuccess": "서버 구성 저장 성공",
		"saveServerConfigPortError": "중앙 서버, 응용 프로그램 서버 및 웹 서버의 포트는 모두 동일하거나 모두 다르게 설정되어야 합니다",

		"saveServerSSLConfig": "SSL 구성 저장",
		"saveServerSSLConfigSuccess": "SSL 구성 저장 성공",

        "sslConfig": "SSL 사용 여부",

		"serverTaskConfig": "서버 작업",

		"proxyCenterEnable": "프록시 중앙 서비스",
		"proxyApplicationEnable": "프록시 응용 서비스",
		"proxyTimeOut": "프록시 시간 초과 (초)",

		"includes": "활성화된 응용 프로그램 모듈",
		"includesInfo": "여기에서 서버가 실행을 허용하는 응용 프로그램 모듈을 선택할 수 있으며, 이곳에서만 구성된 응용 프로그램 모듈만 시작됩니다. 이렇게하면 클러스터 환경에서 서버 성능을 더 유연하게 할당할 수 있습니다. 그러나이 구성을 수정할 때 주의하십시오. 잘못된 구성으로 서비스가 비정상적으로 종료될 수 있습니다 (서버를 다시 시작해야 함)",
		"includesInfo2": "<b style='color: #666666'>활성화할 내장 애플리케이션 선택:</b> 모듈을 선택하지 않으면 모든 모듈이 활성화됩니다.",
		"includesInfo3": "<b style='color: #666666'>활성화할 사용자 정의 애플리케이션:</b> 아래 입력란에 사용자 정의 애플리케이션 이름을 입력하고 반각 쉼표로 구분합니다.",

		"saveIncludes": "활성화된 응용 프로그램 모듈 구성 저장",
		"saveExcludes": "비활성화된 응용 프로그램 모듈 구성 저장",

        "excludes": "비활성화된 응용 프로그램 모듈",
		"excludesInfo": "여기에서 서버에서 실행하지 않을 응용 프로그램 모듈을 선택할 수 있으며 이곳에서 구성된 응용 프로그램 모듈은 시작되지 않습니다. 이로 인해 클러스터 환경에서 서버 성능을 더 유연하게 할당할 수 있습니다. 그러나이 구성을 수정할 때 주의하십시오. 잘못된 구성으로 인해 서비스가 비정상적으로 종료될 수 있습니다 (서버를 다시 시작해야 함)",
		"excludesInfo2": "<b style='color: #666666'>비활성화할 내장 애플리케이션 선택:</b> 모듈을 선택하지 않으면 모든 모듈이 비활성화됩니다.",
		"excludesInfo3": "<b style='color: #666666'>비활성화할 사용자 정의 애플리케이션:</b> 아래 입력란에 사용자 정의 애플리케이션 이름을 입력하고 반각 쉼표로 구분합니다.",

		"includesAll": "모든 모듈 활성화",
		"includesSelect": "활성화할 모듈 선택",
		"includesModules": "활성화된 모듈",
		"selectModules": "선택 가능한 모듈",

		"excludesNone": "어떤 모듈도 비활성화하지 않음",
		"excludesSelect": "비활성화할 모듈 선택",

		"saveServerIncludesSuccess": "활성화된 응용 프로그램 모듈 저장 성공",
		"saveServerExcludesSuccess": "비활성화된 응용 프로그램 모듈 저장 성공",

        "requestLogEnable": "HTTP 로그 사용",
		"requestLogBodyEnable": "본문 내용 기록",
		"requestLogRetainDays": "로그 보관 기간",
		"requestLogInfo": "이곳에서 서버 HTTP 로그 관련 내용을 구성합니다 (서버를 다시 시작해야 함)：" +
			"<ul><li>HTTP 로그 사용 후, 로그 파일은 서버 logs 디렉터리에 저장됩니다. (세 인원 관리를 사용하는 경우 HTTP 로그는 항상 사용됩니다)</li>" +
			"<li>본문 내용을 기록하면 더 자세한 로그 정보를 얻을 수 있지만 디스크 공간 사용량과 서버 부하가 크게 증가할 수 있습니다.</li>" +
			"<li>로그를 보관할 수 있는 최대 일 수를 설정하면 이 일 수를 초과하는 로그 파일이 삭제됩니다.</li></ul>",
		
		"webSocketEnable": "WebSocket 사용 여부",
		"webSocketEnableInfo": "WebSocket은 서버에서 웹 사용자에게 메시지 알림 및 채팅 등을 제공하는 데 사용됩니다. WebSocket을 사용하는 경우 nginx, WAF 등 네트워크 시스템을 올바르게 구성하여 WebSocket 프로토콜 통신을 허용해야 합니다. (서버를 다시 시작해야 함)",

		"deployWarEnable": "사용자 정의 애플리케이션 웹 업로드 허용 여부",
		"deployWarEnableInfo": "이 구성은 사용자 정의 애플리케이션 (war)을 웹에서 업로드하여 배포하는 것을 허용할지 여부를 제어합니다. (서버를 다시 시작해야 함)",

		"deployResourceEnable": "웹 리소스 웹 업로드 허용 여부",
		"deployResourceEnableInfo": "이 구성은 웹 구성 요소 및 정적 리소스를 웹에서 업로드하여 배포할지 여부를 제어합니다. (서버를 다시 시작해야 함)",

        "statEnable": "Druid 통계 활성화",
		"statExclusions": "통계 제외 경로",
		"statEnableInfo": "데이터베이스 연결, SQL 실행, HTTP 요청 등과 관련된 Druid 통계 정보를 활성화하시겠습니까? 통계 결과 페이지는 다음 URL을 통해 액세스할 수 있습니다：<a href='{url}' target='_blank'>Druid 모니터</a>。",
		
		"exposeJest": "Restful API 문서 페이지 출력 여부",
		"exposeJestInfo": "Restful API 문서를 출력하시겠습니까? API 문서는 다음 URL을 통해 액세스할 수 있습니다：<a href='{url}' target='_blank'>Restful API</a>。",
		
		"scriptingBlockedClasses": "서버 스크립트에서 사용이 금지된 Java 클래스",
		"scriptingBlockedClassesInfo": "서버 스크립트에서 사용이 허용되지 않는 Java 클래스를 설정합니다. 쉼표로 구분하여 입력합니다.",
		
		"httpWhiteList": "외부 HTTP 인터페이스 서비스 주소 화이트리스트",
		"httpWhiteListInfo": "외부 HTTP 인터페이스 서비스 주소 화이트리스트입니다. *는 제한 없음을 의미하며 쉼표로 구분하여 입력합니다.",
		
		"refererHeadCheckRegular": "Referer 헤더 검증",
		"refererHeadCheckRegularInfo": "서버가 요청의 Referer 헤더를 검증하는 규칙을 설정합니다. 정규식을 설정하여 정규식에 맞는 Referer 값을 가진 요청만 허용됩니다. CSRF 공격을 효과적으로 방지하기 위한 합리적인 설정을 수행합니다. 예를 들어 (.+?)o2oa.net(.+?)를 설정하면 Referer에 'o2oa.net'을 포함한 요청만 허용됩니다.",

        "contentSecurityPolicy": "Content-Security-Policy 응답 헤더",
		"contentSecurityPolicyInfo": "HTTP 응답 헤더 Content-Security-Policy는 사이트 관리자가 특정 페이지에서 어떤 리소스를로드 할 수 있는지를 제어할 수 있게 해줍니다. 설정된 정책은 주로 서버의 출처와 스크립트 엔드 포인트를 지정하는 데 사용됩니다. 이것은 크로스 사이트 스크립트 공격(Cross-Site Script)을 방지하는 데 도움이 됩니다.",
		"contentSecurityPolicyInfo2": "Content-Security-Policy 응답 헤더에 대한 자세한 정보는 다음을 참조하십시오：<a target='_blank' href='https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Security-Policy'>https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Security-Policy</a>",
		
		"accessControlAllowOrigin": "크로스 도메인 출처 허용",
		"accessControlAllowOriginInfo": "크로스 오리진 리소스 공유 허용, HTTP 반환 Access-Control-Allow-Origin 헤더 식별자를 설정하여 CORS 공격 방어에 사용할 수 있습니다. 예: https://www.o2oa.net",
		
		"personUnitOrderByAsc": "사용자 및 조직 오름차순 정렬",
		"personUnitOrderByAscInfo": "사용자 및 조직 데이터를 검색할 때 오름차순으로 정렬할지 여부를 설정합니다. 기본값은 true이며, 그렇지 않으면 내림차순으로 정렬됩니다.",
		
		"attachmentConfig": "첨부 파일 업로드 구성",
		"attachmentConfigInfo": "시스템에서 허용하는 첨부 파일의 크기와 유형을 구성할 수 있습니다.",

        "fileSize": "첨부 파일 크기 제한",
		"fileSizeInfo": "M 단위, 최대 2048M",
		"fileTypeIncludes": "허용된 첨부 파일 유형",
		"fileTypeIncludesInfo": "허용된 첨부 파일 유형 설정, 확장자 설정, 쉼표로 구분",
		"fileTypeExcludes": "업로드 금지된 첨부 파일 유형",
		"fileTypeExcludesInfo": "업로드 금지된 첨부 파일 유형 설정, 확장자 설정, 쉼표로 구분",
		"dumpData": "데이터 자동 백업",
		"dumpDataInfo": "O2OA는 정기적으로 데이터를 자동으로 백업할 수 있습니다. 여기에서 구성하세요.",
		"dumpEnable": "사용 여부",
		"dumpCron": "정기식",
		"dumpSize": "최대 백업 수",
		"dumpPath": "백업 경로",
		"saveDump": "자동 백업 구성 저장",
		"saveDumpSuccess": "자동 백업 구성 저장 성공",
		"restoreData": "데이터 자동 복원",
		"restoreDataInfo": "O2OA는 정기적으로 데이터를 자동으로 복원할 수 있습니다. 여기에서 구성하세요.",
		"restoreEnable": "사용 여부",
		"restoreCron": "정기식",
		"restorePath": "복원 경로",
		"saveRestore": "자동 복원 구성 저장",
		"saveRestoreSuccess": "자동 복원 구성 저장 성공",
		"reloadServerConfig": "서버 구성 다시 로드"
    },
    "_worktimeConfig": {
		"amWorktime": "오전 근무 시간",
		"pmWorktime": "오후 근무 시간",
		"holidays": "휴일",
		"workdays": "근무일",
		"weekends": "주말",

		"amWorktimeInfo": "근무일의 오전 근무 시간 범위를 여기에서 설정하세요.",
		"pmWorktimeInfo": "근무일의 오후 근무 시간 범위를 여기에서 설정하세요.",
		"holidaysInfo": "휴일을 설정하여 근무일로 처리되는 기존 날짜를 여기에 추가하세요.",
		"workdaysInfo": "근무일로 처리되는 기존 비근무일 날짜를 여기에 추가하세요.",
		"weekendsInfo": "주말을 설정하려면 아래에서 주말 비근무일로 설정할 요일을 선택하세요.",

		"timeRangeTo": "부터",
		"startTime": "시작 시간",
		"endTime": "종료 시간",

		"weekData": {
			"월요일": 2,
			"화요일": 3,
			"수요일": 4,
			"목요일": 5,
			"금요일": 6,
			"토요일": 7,
			"일요일": 1
		}
	},
    "_cacheConfig": {
		"type": "캐시 유형",
		"typeInfo": "O2OA 시스템은 guava와 redis 두 가지 캐시를 지원하며 기본적으로 guava를 사용합니다.",

		"guava_maximumSize": "최대 캐시 크기",
		"guava_maximumSizeInfo": "캐시의 최대 크기, 객체 수입니다. 기본값: 3000",
		"guava_expireMinutes": "만료 시간",
		"guava_expireMinutesInfo": "만료 시간, 분 단위입니다. 기본값: 30",

		"redis": "redis 서비스 구성",
		"redisInfo": "여기에서 redis 서비스를 구성하세요.",
		"redis_host": "서버 주소",
		"redis_port": "서버 포트",
		"redis_user": "인증 사용자",
		"redis_password": "인증 암호",
		"redis_connectionTimeout": "연결 대기 시간 초과",
		"redis_socketTimeout": "응답 대기 시간 초과",
		"redis_sslEnable": "SSL 활성화",
		"redis_index": "데이터베이스 번호",

		"saveRedis": "redis 구성 저장",
		"saveRedisSuccess": "redis 구성 저장 성공",
	},
    "_processConfig": {
        "baseConfig": "기본 구성",
		"timerConfig": "타이머 구성",

		"maintenanceIdentity": "프로세스 유지 보수 인증",
		"selectMaintenanceIdentity": "프로세스 유지 보수 인증 선택",
		"maintenanceIdentityInfo": "프로세스 작업 중에 예기치 않은 오류가 발생하고 해당 처리자를 찾을 수 없는 경우, 시스템은 작업을 먼저 작성자 인증에 할당하려고 시도하며 작성자 인증도 얻을 수 없는 경우 여기에 설정된 인증에 할당됩니다.",

		"formVersionCount": "양식 이력 버전 보존 수",
		"formVersionCountInfo": "양식을 저장할 때 시스템은 특수한 경우에 이전 설계를 검색하기 위해 이전 버전을 사본으로 유지할 수 있습니다. 여기에는 양식 이력 버전의 최대 보존 수가 설정되며 이 수를 초과하면 가장 이전의 이력 버전이 삭제됩니다.",

		"processVersionCount": "프로세스 이력 버전 보존 수",
		"processVersionCountInfo": "프로세스를 저장할 때 시스템은 특수한 경우에 이전 설계를 검색하기 위해 이전 버전을 사본으로 유지할 수 있습니다. 여기에는 프로세스 이력 버전의 최대 보존 수가 설정되며 이 수를 초과하면 가장 이전의 이력 버전이 삭제됩니다.",

		"scriptVersionCount": "스크립트 이력 버전 보존 수",
		"scriptVersionCountInfo": "스크립트를 저장할 때 시스템은 특수한 경우에 이전 설계를 검색하기 위해 이전 버전을 사본으로 유지할 수 있습니다. 여기에는 스크립트 이력 버전의 최대 보존 수가 설정되며 이 수를 초과하면 가장 이전의 이력 버전이 삭제됩니다.",

		"docToWordType": "문서 편집기 구성 요소를 WORD로 변환하는 방법",
		"docToWordTypeInfo": "문서 편집기 구성 요소가 'Service'로 WORD로 변환하도록 설정된 경우, 백엔드 서비스가 WORD로 변환합니다. " +
			"O2OA 시스템은 로컬 서비스 변환 또는 클라우드 서비스 변환을 지원하며 클라우드 서비스 변환을 사용하면 WORD 형식과 더 호환성이 높아집니다. 그러나 먼저 O2 클라우드에 연결해야 합니다. '클라우드 서비스 구성'에서 O2 클라우드에 연결하십시오.",
		"docWordTypeSelect": {
			"local": "로컬 서비스",
			"cloud": "클라우드 서비스"
		},

        "press": "작업 알림 설정",
		"pressInfo": "프로세스 설정에서 수동 활동 노드에 알림을 시작할 수 있도록 설정할 수 있으며, 작업을 처리한 사람은 현재 작업을 처리 중인 대상에 대한 처리 알림을 시작할 수 있습니다. 이 동작에 대한 시간 범위 내의 제한 횟수를 여기에서 설정할 수 있습니다.",
		"pressInfo1": "에서",
		"pressInfo2": "분 내에 최대",
		"pressInfo3": "번 알림 시작",

		"executorCount": "전환 실행자 수",
		"executorCountInfo": "프로세스 전환을 처리하는 실행자 수입니다. 기본값은 32이며, 일반적으로 수정을 권장하지 않습니다.",

		"executorQueueBusyThreshold": "실행자 대기열 바쁜 임계값",
		"executorQueueBusyThresholdInfo": "프로세스 전환 실행자 대기열의 바쁜 임계값입니다. 기본값은 5이며, 일반적으로 수정을 권장하지 않습니다.",

		"timerInfo": "O2OA 프로세스 플랫폼은 프로세스 작업을 처리하기 위해 일부 타이머가 필요합니다. 여기에서 이러한 타이머를 구성할 수 있습니다. (타이머에 대한 모든 수정은 서버를 다시 시작해야 적용됩니다.)",

		"enable": "사용 여부",
		"cron": "스케줄 표현식",
		"urge": "촉구 타이머",
		"urgeInfo": "활동에 시간 초과 시간이 설정된 경우이 타이머는 지정된 시간에 도달 할 것으로 예상되는 대기 작업을 확인하고 해당 대기 작업의 처리자에게 촉구 메시지를 보냅니다.",

        "expire": "만료 타이머",
		"expireInfo": "활동에 시간 초과 시간이 설정된 경우, 이 타이머는 대기 작업이 지정된 시간을 초과했는지 여부를 확인하고 이러한 대기 작업을 만료로 표시합니다.",

		"touchDelay": "타임 아웃 활동 트리거 타이머",
		"touchDelayInfo": "이 타이머는 프로세스 내의 타임 아웃 활동을 트리거하는 데 사용됩니다.",

		"deleteDraft": "드래프트 삭제 타이머",
		"deleteDraftInfo": "프로세스에서 드래프트 모드로 프로세스 인스턴스를 생성할 수 있으며, 이 모드에서는 공식적으로 프로세스를 시작하기 전에 저장되지 않습니다. 이 타이머는 오랫동안 흐르지 않은 드래프트 파일을 삭제할 수 있습니다.",

		"thresholdMinutes": "시간 임계값 (분)",
		"thresholdMinutesInfo": "임계값을 설정하십시오. 분 단위로 측정되며,이 시간을 초과하면 삭제 가능한 드래프트로 간주됩니다. 기본값은 10 일입니다.",

		"passExpired": "자동 전송 타이머",
		"passExpiredInfo": "프로세스 활동에서 시간 초과 처리를 활성화하는 경우,이 타이머는 이미 시간 초과 된 대기 작업을 전송하려고 시도합니다.",

		"touchDetained": "유예된 대기 작업 확인 타이머",
		"touchDetainedInfo": "이 타이머는 오랫동안 대기 중인 작업을 찾아 작업을 이동시키려고 시도합니다. 이는 인력 변경 등으로 인해 작업이 지연되는 경우를 자동으로 처리할 수 있습니다.",
		"thresholdMinutesInfo_touchDetained": "이 타이머는 지연 시간이이 임계 값을 초과하는 작업을 처리하며 기본값은 1440 분 (1 일)입니다.",

		"updateTable": "데이터베이스 동기화 타이머",
		"updateTableInfo": "프로세스에서 프로세스 데이터를 데이터베이스에 매핑하는 것을 설정한 경우,이 타이머는 매핑 데이터 큐를 처리하는 데 사용됩니다.",

		"archiveHadoop": "Hadoop으로 아카이빙",
		"archiveHadoopInfo": "O2OA는 완료된 작업 데이터를 Hadoop으로 아카이빙하는 기능을 지원합니다. 여기에서 Hadoop 관련 구성을 설정할 수 있습니다.",
		"fsDefaultFS": "Hadoop 주소",
		"username": "Hadoop 사용자 이름",
		"path": "경로 접두사",
		"saveHadoop": "Hadoop 설정 저장",
		"saveHadooping": "저장 중...",
		"saveHadoopSuccess": "저장 성공",

		"merge": "아카이빙 타이머",
		"mergeInfo": ""
    },
    "_queryConfig": {
        "queryIndexConfig": "인덱스 구성",
		"workConfig": "진행 중 문서",
		"workCompletedConfig": "완료된 문서",
		"documentConfig": "콘텐츠 관리 문서",
		"indexTools": "인덱스 도구",

		"work": "진행 중",
		"workCompleted": "완료",
		"document": "콘텐츠 관리",

		"touchWorkIndex": "진행 중 문서 전체 색인 실행",
		"touchWorkIndexInfo": "인덱스를 처음으로 활성화하거나 이전 버전에서 업그레이드하는 경우, 시스템이 유휴 상태일 때 진행 중 문서의 전체 색인을 즉시 실행할 수 있습니다.",
		"touchWorkIndexAction": "진행 중 문서의 전체 색인 즉시 실행",

        "touchWorkCompletedIndex": "완료된 문서의 전체 색인 실행",
		"touchWorkCompletedIndexInfo": "인덱스를 처음으로 활성화하거나 이전 버전에서 업그레이드하는 경우, 시스템이 유휴 상태일 때 완료된 문서의 전체 색인을 즉시 실행할 수 있습니다.",
		"touchWorkCompletedIndexAction": "완료된 문서의 전체 색인 즉시 실행",

		"touchDocumentIndex": "콘텐츠 관리 문서의 전체 색인 실행",
		"touchDocumentIndexInfo": "인덱스를 처음으로 활성화하거나 이전 버전에서 업그레이드하는 경우, 시스템이 유휴 상태일 때 콘텐츠 관리 문서의 전체 색인을 즉시 실행할 수 있습니다.",
		"touchDocumentIndexAction": "콘텐츠 관리 문서의 전체 색인 즉시 실행",

		"optimizeIndex": "인덱스 최적화 실행",
		"optimizeIndexInfo": "인덱스 최적화는 인덱스 저장 공간을 압축하고 검색 성능을 향상시키기 위해 인덱스 구조를 최적화합니다. 인덱스 최적화를 실행하는 데 시간이 오래 걸릴 수 있으며, 시스템이 유휴 상태일 때 즉시 트리거할 수 있습니다.",
		"optimizeIndexAction": "인덱스 최적화 즉시 실행",

        "indexActionConfirmTitle": "{type} 전체 색인 실행 확인",
		"indexActionConfirm": "전체 색인은 많은 서버 리소스를 사용하며 서버 응답이 느려질 수 있습니다. 시스템이 유휴 상태일 때 실행하는 것이 좋습니다.<br><br>{type} 문서의 전체 색인을 실행하시겠습니까?",
		"indexActionSuccess": "{type} 전체 색인 작업이 대기열에 추가되었습니다. 시스템이 즉시 실행됩니다!",

		"optimizeIndexConfirmTitle": "인덱스 최적화 실행 확인",
		"optimizeIndexConfirm": "인덱스 최적화 실행은 많은 서버 리소스를 사용하며 서버 응답이 느려질 수 있습니다. 시스템이 유휴 상태일 때 실행하는 것이 좋습니다.<br><br>인덱스 최적화를 실행하시겠습니까?",
		"optimizeIndexSuccess": "인덱스 최적화 작업이 대기열에 추가되었습니다. 시스템이 즉시 실행됩니다!",

		"restartServerInfo": "<span style='color: red'>인덱스 구성 변경은 서버를 다시 시작한 후에 적용됩니다!</span>",

		"enable": "인덱스 서비스 사용 여부",

        "modeConfig": "색인 저장 위치",
		"modeConfigInfo": "색인 저장 위치를 선택합니다. 기본값은 '로컬 파일 시스템'입니다.",
		"indexMode": "색인 저장 위치",
		"modeOptions": {
			"localDirectory": "로컬 파일 시스템",
			"hdfsDirectory": "하둡 파일 시스템",
			"sharedDirectory": "공유 파일 시스템"
		},
		"hdfsDirectoryDefaultFS": "하둡 파일 시스템 주소",
		"hdfsDirectoryPath": "하둡 파일 시스템 디렉토리",
		"sharedDirectoryPath": "공유 파일 시스템 디렉토리",

		"optimizeIndexEnable": "색인 최적화",
		"optimizeIndexEnableInfo": "색인 최적화를 활성화하면 색인 저장 공간을 압축하고 색인 구조를 최적화하여 검색 성능을 향상시킵니다.",
		"optimizeIndexCron": "색인 최적화 일정 구성",
		"isEnable": "사용 여부",
		"cron": "일정 표현",

        "dataStringThreshold": "비즈니스 데이터 최대 텍스트 길이 임계값",
		"dataStringThresholdInfo": "비즈니스 데이터 최대 텍스트 길이 임계값. 이 임계값을 초과하는 데이터는 색인에 기록되지 않습니다.",

		"summaryLength": "요약 길이",

		"attachmentMaxSize": "첨부 파일 색인 임계값",
		"attachmentMaxSizeInfo": "첨부 파일 색인 임계값(메가바이트). 이 값보다 큰 첨부 파일은 색인되지 않습니다.",

		"cleanupThresholdDays": "검색 콘텐츠 정리 임계값",
		"cleanupThresholdDaysInfo": "검색 콘텐츠 정리 임계값(일). 이 기간 동안 업데이트되지 않은 색인은 삭제됩니다.",

		"searchMaxPageSize": "검색 페이지당 최대 수",
		"searchMaxPageSizeInfo": "검색 결과가 페이지당 표시되는 최대 항목 수",

		"moreLikeThisMaxSize": "관련 추천 최대 반환 수",
		"moreLikeThisMaxSizeInfo": "관련 추천 검색의 최대 반환 수",

		"workIndexAttachment": "흐름 문서 첨부 파일 색인 여부",
		"workIndexAttachmentInfo": "흐름 문서의 첨부 파일을 색인화할지 여부. (첨부 파일 색인화에는 강력한 서버 성능과 더 많은 메모리가 필요할 수 있음)",

        "lowFreqWorkEnable": "전체 색인 사용 여부",
		"lowFreqWorkEnableInfo": "전체 색인은 문서 흐름에서 모든 색인을 업데이트하여 권한과 데이터의 정확성을 보장합니다.",
		"lowFreqWorkCron": "전체 색인 예약 표현식",
		"lowFreqWorkCronInfo": "전체 색인은 서버 자원을 많이 사용하므로 전체 색인을 사용하는 경우 시스템이 비어 있는 시간대에만 실행하는 것이 좋습니다. 또한, 흐름 중 데이터, 완료된 데이터 및 콘텐츠 관리 데이터의 전체 색인은 다른 시간대에서 실행하도록 분리하는 것이 좋습니다.",
		"lowFreqWorkMaxCount": "한 번에 실행하는 전체 색인 최대 문서 수",
		"lowFreqWorkMaxCountInfo": "한 번의 색인 처리에 실행되는 최대 문서 수를 설정하십시오. 이 수에 도달하면 색인이 중지되며 다음 색인 실행 시 이전 문서 처리 이후에서 계속됩니다. 최대 수량 및 처리 시간 중 어느 하나가 충족되면 색인이 중지됩니다.",
		"lowFreqWorkMaxMinutes": "한 번에 실행하는 전체 색인 처리 최대 시간(분)",
		"lowFreqWorkMaxMinutesInfo": "한 번의 색인 처리에 실행되는 최대 시간을 설정하십시오. 이 시간에 도달하면 색인이 중지되며 다음 색인 실행 시 이전 문서 처리 이후에서 계속됩니다. 최대 수량 및 처리 시간 중 어느 하나가 충족되면 색인이 중지됩니다.",
		
		"highFreqWorkEnable": "증분 색인 사용 여부",
		"highFreqWorkEnableInfo": "증분 색인을 사용하는 경우 문서 데이터 또는 상태가 변경될 때 신호가 발생하고, 증분 색인 예약 기에 신호를 받아와 문서 색인을 업데이트합니다.",
		"highFreqWorkCron": "증분 색인 예약 기",
		"highFreqWorkCronInfo": "증분 색인을 주기적으로 실행하는 예약 표현식",
		"highFreqWorkMaxCount": "한 번에 실행하는 증분 색인 최대 처리량",
		"highFreqWorkMaxMinutes": "한 번에 실행하는 증분 색인 처리 최대 시간(분)",


        "workCompletedIndexAttachment": "완료된 문서의 첨부 파일 색인 사용 여부",
		"workCompletedIndexAttachmentInfo": "완료된 문서의 첨부 파일을 색인에 포함할지 여부입니다. (첨부 파일을 색인에 포함하려면 서버 성능과 더 많은 메모리가 필요할 수 있습니다.)",
		
		"lowFreqWorkCompletedEnable": "전체 색인 사용 여부",
		"lowFreqWorkCompletedEnableInfo": "전체 색인은 완료된 문서의 모든 색인을 업데이트하여 권한과 데이터의 정확성을 보장합니다.",
		"lowFreqWorkCompletedCron": "전체 색인 예약 표현식",
		"lowFreqWorkCompletedCronInfo": "전체 색인은 서버 자원을 많이 사용하므로 전체 색인을 사용하는 경우 시스템이 비어 있는 시간대에만 실행하는 것이 좋습니다. 또한, 흐름 중 데이터, 완료된 데이터 및 콘텐츠 관리 데이터의 전체 색인은 다른 시간대에서 실행하도록 분리하는 것이 좋습니다.",
		"lowFreqWorkCompletedMaxCount": "한 번에 실행하는 전체 색인 최대 문서 수",
		"lowFreqWorkCompletedMaxCountInfo": "한 번의 색인 처리에 실행되는 최대 문서 수를 설정하십시오. 이 수에 도달하면 색인이 중지되며 다음 색인 실행 시 이전 문서 처리 이후에서 계속됩니다. 최대 수량 및 처리 시간 중 어느 하나가 충족되면 색인이 중지됩니다.",
		"lowFreqWorkCompletedMaxMinutes": "한 번에 실행하는 전체 색인 처리 최대 시간(분)",
		"lowFreqWorkCompletedMaxMinutesInfo": "한 번의 색인 처리에 실행되는 최대 시간을 설정하십시오. 이 시간에 도달하면 색인이 중지되며 다음 색인 실행 시 이전 문서 처리 이후에서 계속됩니다. 최대 수량 및 처리 시간 중 어느 하나가 충족되면 색인이 중지됩니다.",


        "highFreqWorkCompletedEnable": "증분 색인 사용 여부",
		"highFreqWorkCompletedEnableInfo": "증분 색인을 사용하면 문서 데이터나 상태 변경 시 신호를 발생시켜 지정된 시간에 증분 색인 예약 실행기가 실행되어 증분 신호를 가져와 문서 색인을 업데이트합니다.",
		"highFreqWorkCompletedCron": "증분 색인 예약 실행기",
		"highFreqWorkCompletedCronInfo": "증분 색인은 정기적으로 실행될 예약 실행식입니다.",
		"highFreqWorkCompletedMaxCount": "증분 색인 단일 처리 최대 수량",
		"highFreqWorkCompletedMaxMinutes": "증분 색인 단일 처리 최대 시간(분)",

		"documentIndexAttachment": "콘텐츠 관리 문서의 첨부 파일을 색인 사용 여부",
		"documentIndexAttachmentInfo": "콘텐츠 관리 문서의 첨부 파일을 색인에 포함할지 여부입니다. (첨부 파일을 색인에 포함하려면 서버 성능과 더 많은 메모리가 필요할 수 있습니다.)",

        "lowFreqDocumentEnable": "콘텐츠 관리의 전체 색인 사용 여부",
		"lowFreqDocumentEnableInfo": "전체 색인은 '정보' 유형의 콘텐츠 관리 문서의 색인을 업데이트하며 권한과 데이터의 정확성을 보장합니다.",
		"lowFreqDocumentCron": "전체 색인 예약 실행기",
		"lowFreqDocumentCronInfo": "전체 색인은 서버 리소스를 상당히 사용하므로 전체 색인을 사용하는 경우 시스템이 비활성 상태일 때만 실행하도록 설정하는 것이 좋습니다. 중요한 점은 전체 색인, 흐름 완료 데이터 및 콘텐츠 관리 데이터에 대한 작업을 서로 다른 시간대에 실행하도록 노력해야 한다는 것입니다.",
		"lowFreqDocumentMaxCount": "전체 색인 실행 최대 수량",
		"lowFreqDocumentMaxCountInfo": "한 번의 색인 처리에 실행할 문서의 최대 수량을 설정합니다. 이 수량에 도달하면 색인이 중지되며 다음 색인 실행 시 이전 작업에서 이어서 처리됩니다. 최대 수량과 처리 기간 중 하나라도 충족되면 색인이 중지됩니다.",
		"lowFreqDocumentMaxMinutes": "전체 색인 실행 처리 기간(분)",
		"lowFreqDocumentMaxMinutesInfo": "한 번의 색인 처리에 실행할 수 있는 최대 시간을 설정합니다. 이 시간에 도달하면 색인이 중지되며 다음 색인 실행 시 이전 작업에서 이어서 처리됩니다. 최대 수량과 처리 기간 중 하나라도 충족되면 색인이 중지됩니다.",

		"highFreqDocumentEnable": "증분 색인 사용 여부",
		"highFreqDocumentEnableInfo": "증분 색인을 사용하면 문서 데이터나 상태 변경 시 신호를 발생시켜 지정된 시간에 증분 색인 예약 실행기가 실행되어 증분 신호를 가져와 문서 색인을 업데이트합니다.",
		"highFreqDocumentCron": "증분 색인 예약 실행기",
		"highFreqDocumentCronInfo": "증분 색인은 정기적으로 실행될 예약 실행식입니다.",
		"highFreqDocumentMaxCount": "증분 색인 단일 처리 최대 수량",
		"highFreqDocumentMaxMinutes": "증분 색인 단일 처리 최대 시간(분)"

    },
    "_appConfig": {
        "connectConfig": "연결 구성",
		"moduleConfig": "모듈 구성",
		"iconConfig": "아이콘 구성",

		"cloudConnect": "클라우드 서비스 연결 확인",
		"connectedInfo": "<span style='color:#5fbf78'>[O2 클라우드 서비스에 연결됨]</span>",
		"notConnectedInfo": "<span style='color:red'>[O2 클라우드 서비스에 연결되지 않음]</span> 클라우드 서비스 구성 페이지에서 등록 및 로그인하십시오.",

		"httpProtocol": "웹 접근 프로토콜",
		"httpProtocolInfo": "모바일에서 센터 서비스에 액세스하는 데 HTTP 프로토콜 또는 HTTPS 프로토콜을 사용할지 선택하십시오.",

		"centerServer": "센터 서버",
		"centerServerInfo": "외부 서비스에 대한 센터 서버의 IP 주소 또는 도메인 및 포트입니다.",

		"webServer": "웹 서버",
		"webServerInfo": "외부 서비스에 대한 웹 서버의 IP 주소 또는 도메인 및 포트입니다. 도메인 또는 IP 주소가 비어 있거나 '127.0.0.1'인 경우 센터 서버 주소를 사용합니다.",

		"applicationServer": "응용 프로그램 서버",
		"applicationServerInfo": "외부 서비스에 대한 응용 프로그램 서버의 IP 주소 또는 도메인 및 포트입니다. 도메인 또는 IP 주소가 비어 있거나 '127.0.0.1'인 경우 센터 서버 주소를 사용합니다.",

		"editServer": "서버 주소 편집",
		"host": "도메인 또는 IP 주소",
		"port": "포트",

        "connectTest": "모바일 연결 테스트",
		"connectTestInfo": "QR 코드를 스캔하여 외부 네트워크에서 서버에 연결할 수 있는지 확인하십시오.",
		"getQrcode": "연결 테스트 QR 코드 생성",

		"mobileIndex": "모바일 홈페이지 구성",
		"mobileIndexInfo": "모바일 앱의 홈페이지를 기본 앱 스타일로 또는 특정 포털 페이지로 구성할 수 있습니다.",

		"simpleMode": "모바일 간단 모드",
		"simpleModeInfo": "모바일에서 간단 모드를 사용하면 홈페이지와 설정 페이지만 표시됩니다.",

		"appIndexPage": "모바일 앱 홈페이지 구성",
		"appIndexPageInfo": "모바일 앱의 여러 주요 페이지를 표시할지 여부를 구성할 수 있습니다.",
		"appIndexPageHome": "홈",
		"appIndexPageIM": "메시지",
		"appIndexPageContact": "연락처",
		"appIndexPageApp": "앱",
		"appIndexPageSettings": "설정",

		"appIndexCenteredTitle": "모바일 앱 홈페이지 가운데 정렬 여부",
		"appIndexCenteredInfo": "모바일 앱의 홈페이지를 가운데 정렬하면 페이지 수가 구성할 수 없게 됩니다.",

        "appIndexCmsFilterTitle": "홈페이지 정보 센터",
		"appIndexCmsFilterCategoryInfo": "정보 센터 목록 카테고리 쿼리 조건입니다. 비어 있으면 모두 쿼리됩니다.",
		"appIndexTaskFilterTitle": "홈페이지 업무 센터",
		"appIndexTaskFilterProcessInfo": "업무 센터 목록 프로세스 쿼리 조건입니다. 비어 있으면 모두 쿼리됩니다.",
		"appIndexTaskFilterProcessSelectorTitle": "프로세스 선택",
		"appIndexCmsFilterCategroySelectorTitle": "카테고리 선택",

		"systemMessageSwitch": "시스템 알림 표시",
		"systemMessageSwitchInfo": "모바일 앱 메시지 목록에서 시스템 알림을 표시할지 여부",
		"systemMessageCanClickInfo": "모바일 앱 시스템 알림을 클릭하여 열 수 있는지 여부",

		"contactPermissionView": "모바일 앱 주소록 권한 뷰",
		"contactPermissionViewInfo": "앱 마켓에 '주소록' 앱을 설치해야 합니다. 해당 앱에는 주소록 권한 구성 뷰가 포함되어 있습니다.",

		"appExitAlert": "앱 종료 경고",
		"appExitAlertInfo": "앱을 종료할 때 팝업 창에 표시할 메시지입니다. 비어 있으면 팝업이 표시되지 않습니다.",

		"nativeAppList": "앱 목록",
		"nativeAppListInfo": "모바일 앱에서 사용할 앱을 설정하고 비활성화할 앱을 지정할 수 있습니다.",

        "imageNames": {
			"application_top": {"text": "애플리케이션 상단 페이지 이미지", "action": "ApplicationTop"},
			"index_bottom_menu_logo_blur": {"text": "홈페이지 하단 메뉴 아이콘 (선택 안 됨)", "action": "MenuLogoBlur"},
			"index_bottom_menu_logo_focus": {"text": "홈페이지 하단 메뉴 아이콘 (선택됨)", "action": "MenuLogoFocus"},
			"launch_logo": {"text": "시작 로고 이미지", "action": "LaunchLogo"},
			"login_avatar": {"text": "로그인 화면 기본 프로필 이미지", "action": "LoginAvatar"},
			"process_default": {"text": "프로세스 기본 아이콘", "action": "ProcessDefault"},
			"setup_about_logo": {"text": "정보 페이지 아이콘", "action": "SetupAboutLogo"}
		},
		"imageSzie": "크기",
		"changeImage": "이미지 변경",
		"defaultImage": "기본 이미지",
		"defaultImageTitle": "기본 이미지 확인",
		"defaultImageInfo": "{name}을(를) 기본 이미지로 바꾸시겠습니까?",
    },
    "_integrationConfig": {
        "title": "모바일 앱 통합",
		"dingding": "딩딩 통합",
		"mPweixin": "위챗 공중 호 통합",
		"qiyeweixin": "기업 위챗 통합",
		"weLink": "화웨이 WeLink 통합",
		"zhengwuDingding": "저우정 딩딩 통합",


        "enable": "디딩 통합 활성화 여부",
		"corpId": "디딩 CorpId",
		"agentId": "디딩 AgentId",
		"appKey": "애플리케이션 고유 식별자",
		"appSecret": "애플리케이션 비밀 키",
		"syncCron": "동기화 검사 콜백 시그널 예약",
		"forceSyncCron": "강제 동기화 예약",
		"oapiAddress": "디딩 API 서버 주소",
		"token": "콜백 토큰",
		"encodingAesKey": "콜백 인코딩 Aes 키",
		"workUrl": "디딩 메시지 작업을 여는 URL",
		"messageRedirectPortal": "처리 완료 후 포털로 리디렉션",
		"messageEnable": "메시지 푸시 활성화 여부",
		"scanLoginEnable": "디딩 QR 코드 로그인 활성화 여부",
		"scanLoginAppId": "디딩 QR 코드 로그인 AppId",
		"scanLoginAppSecret": "디딩 QR 코드 로그인 앱 비밀",
		"attendanceSyncEnable": "출근 정보 활성화 여부",

        "enableInfo": "O2OA 플랫폼은 네이티브 안드로이드 및 iOS 모바일 앱을 제공하며, 마이크로 앱 방식으로 Alibaba DingTalk에 통합할 수 있습니다. 이를 통해 DingTalk의 기업 주소록을 로컬 조직 인력 구조로 동기화하고 업무 처리와 같은 알림을 DingTalk으로 직접 푸시하여 메시지를 알립니다. (서버 재시작 필요)",
		"enableInfo2": "<span class='mainColor_color'>O2OA가 DingTalk에 성공적으로 연동되면 O2OA는 DingTalk에서 모든 인원과 조직을 자동으로 동기화합니다. O2OA의 모든 인원 및 조직은 기업 DingTalk에서 생성한 조직 구조를 따릅니다 (로컬로 생성된 인원 및 조직은 삭제되지 않으며 인원 및 조직의 중복이 발생할 수 있습니다).</span>",
		"enableInfo3": "O2OA 및 DingTalk 통합에 대한 자세한 내용은 다음을 참조하십시오：<a href='https://www.o2oa.net/search.html?q=%E9%92%89%E9%92%89' target='_blank'>DingTalk</a>",

		"syncCronInfo": "콜백 신호가 동기화 검사를 트리거하고 기본적으로 10분마다 실행되며 동안 DingTalk 콜백 신호가 수신되면 동기화 작업이 인원 동기화를 수행합니다. (DingTalk에서 콜백 구성 설정 필요)",
		"forceSyncCronInfo": "강제 동기화 일정 설정, 기본적으로 매일 8시와 12시에 인원 및 조직을 강제로 동기화합니다.",
		"oapiAddressInfo": "DingTalk API 서버 주소, 일반적으로 수정할 필요가 없습니다.",
		"workUrlInfo": "DingTalk 메시지를 여는 작업 URL 주소, 예 : https://sample.o2oa.net/x_desktop/",
		"messageRedirectPortalInfo": "DingTalk 메시지 처리가 완료된 후 특정 포털 페이지로 이동할 수 있습니다.",

		"saveDingding": "DingTalk 구성 저장",
		"saveDingdingSuccess": "DingTalk 구성 저장 성공",

        "mpweixinText": {
			"enable": "활성화 여부",
			"enablePublish": "메뉴 게시 활성화",
			"appid": "WeChat Appid",
			"appSecret": "WeChat AppSecret",
			"token": "WeChat Token",
			"encodingAesKey": "WeChat encodingAesKey",
			"portalId": "처리 완료 후 특정 포털로 이동",
			"workUrl": "WeChat 공식 계정 메시지 작업 열기 URL",
			"scriptId": "서비스 스크립트 실행",
			"messageEnable": "템플릿 메시지 활성화",
			"tempMessageId": "공식 계정 템플릿 메시지 ID",
			"fieldList": "템플릿 필드 설정",
			"tempName": "템플릿 필드",
			"name": "비즈니스 필드",

			"workUrlInfo": "WeChat 공식 계정 메시지 작업 열기 URL 주소, 예 : https://sample.o2oa.net/x_desktop/",
			"enableInfo": "O2OA는 WeChat 공식 계정 통합을 지원하며 사용자는 WeChat 공식 계정을 팔로우하여 작업을 처리할 수 있습니다. 또한 업무 처리 알림을 지원합니다. (서버 재시작 필요)",
			"enableInfo2": "WeChat 공식 계정과 관련된 더 많은 O2OA 내용은 여기를 참조하십시오：<a href='https://www.o2oa.net/search.html?q=%E5%BE%AE%E4%BF%A1%E5%85%AC%E4%BC%97%E5%8F%B7' target='_blank'>WeChat 공식 계정</a>",
			"enablePublishInfo": "메뉴 게시 활성화 후 O2OA에서 구성한 메뉴 기능을 WeChat 공식 계정에 게시할 수 있습니다. WeChat 공식 계정 메뉴를 구성하려면 APP 도구 - WeChat 공식 계정 메뉴 설정에서 설정할 수 있습니다.",
			"portalIdInfo": "메시지 처리가 완료된 후 특정 포털 페이지로 이동할 수 있습니다.",
			"scriptIdInfo": "WeChat에서 텍스트 메시지를 수신하면 여기에서 실행할 인터페이스를 플랫폼 서비스 관리에서 지정할 수 있습니다.",
			"fieldListInfo": "이것은 템플릿 내용의 비즈니스 필드에 해당하는 것입니다. 현재 O2OA는 다음과 같은 비즈니스 필드를 제공합니다: 【creatorPerson: 생성자, activityName: 현재 노드, processName: 프로세스 이름, startTime: 시작 시간, title: 제목】",

			"saveMpweixin": "WeChat 공식 계정 구성 저장",
			"saveMpweixinSuccess": "WeChat 공식 계정 구성 저장 성공"
		},
        "qywenxinText": {
            "enable": "사용 여부",
			"corpId": "기업 WeChat CorpId",
			"agentId": "기업 WeChat AgentId",
			"corpSecret": "기업 WeChat CorpSecret",
			"syncCron": "동기화 검사 콜백 신호 시간 설정",
			"forceSyncCron": "강제 동기화 시간 설정",
			"apiAddress": "API 서비스 주소",
			"qrConnectAddress": "QR 코드 로그인 서비스 주소",
			"oauth2Address": "oAuth2 서비스 주소",
			"syncSecret": "주소록 동기화 비밀 키",
			"token": "콜백 Token",
			"encodingAesKey": "콜백 EncodingAesKey",
			"workUrl": "작업을 여는 메시지의 URL",
			"messageRedirectPortal": "작업 완료 후 포털로 리디렉션",
			"messageEnable": "메시지 푸시 사용 여부",
			"scanLoginEnable": "QR 코드 로그인 사용 여부",
			"attendanceSyncEnable": "근태 정보 사용 여부",
			"attendanceSyncAgentId": "근태 카드 응용 프로그램 ID",
			"attendanceSyncSecret": "근태 카드 응용 프로그램 Secret",
			"bindEnable": "사용자 바인딩 사용 여부",
			"bindEnableInfo": "기본적으로 사용하지 않습니다. 이것은 개인화된 사용자 바인딩에 사용되며 사용자 및 조직 동기화와 상충됩니다!",

            "getUserPrivateInfoMessageTitle": "기업 WeChat 개인 개인 정보 요청 메시지 전송",
			"getUserPrivateInfoMessageDesc": "기업 WeChat의 새로운 버전 동기화 API는 사용자의 개인 정보 (예 : 전화 번호, 이메일 등)를 제한하여 가져올 수 없게 했습니다. 현재 동기화 프로그램은 사용자 이름과 사용자 ID만 가져올 수 있습니다. 아래의 메시지 전송 기능은 사용자에게 개인 정보를 가져올 수 있는 권한을 부여하는 메시지를 보내는 것입니다. 사용자가이 메시지를 클릭하면이 프로그램은 필요한 사용자 정보를 읽을 수 있습니다!",
			"getUserPrivateInfoMessageConsumerList": "메시지 수신자",
			"getUserPrivateInfoMessageFormTitle": "메시지 제목",
			"getUserPrivateInfoMessageFormContent": "메시지 내용",
			"getUserPrivateInfoMessageFormTitleDefault": "개인 정보 허가 획득",
			"getUserPrivateInfoMessageFormContentDefault": "앱은 개인 정보를 가져야 합니다. 승인을 클릭하십시오!",
			"getUserPrivateInfoMessageConsumerEmpty": "먼저 메시지 수신자를 선택하십시오!",
			"getUserPrivateInfoMessageFormTitleEmpty": "메시지 제목을 비워 둘 수 없습니다!",
			"getUserPrivateInfoMessageFormContentEmpty": "메시지 내용을 비워 둘 수 없습니다!",
			"getUserPrivateInfoMessageConfirmTitle": "알림",
			"getUserPrivateInfoMessageConfirmText": "모든 선택한 사용자 및 조직 아래의 사용자에게 기업 WeChat 메시지로 개인 정보를 가져오는 메시지를 보내시겠습니까?",
			"getUserPrivateInfoMessageSendBtn": "메시지 전송",
			"getUserPrivateInfoMessageSendSuccess": "메시지 전송이 성공했습니다. 기업 WeChat에서 나중에 확인하십시오!",


            "syncCronInfo": "콜백 신호가 동기화 검사를 트리거하며 기본적으로 10분마다 실행됩니다. 그 동안 기업 WeChat 콜백 신호가 수신되면 동기화 작업이 트리거됩니다. (기업 WeChat에서 콜백 설정 필요)",
			"forceSyncCronInfo": "강제 동기화 시간 설정은 기본적으로 매일 8시와 12시에 인원 및 조직을 강제로 동기화합니다.",
			"apiAddressInfo": "기업 WeChat API 서버 주소로, 일반적으로 변경할 필요가 없습니다.",
			"workUrlInfo": "기업 WeChat 메시지를 열 때의 작업 URL 주소입니다. 예: https://sample.o2oa.net/x_desktop/",
			"messageRedirectPortalInfo": "기업 WeChat 메시지 처리가 완료된 후 특정 포털 페이지로 리디렉션할 수 있습니다.",
			"enableInfo": "O2OA는 사용자 정의 애플리케이션을 통합하여 기업 WeChat에 연결할 수 있습니다. 기업 WeChat의 기업 주소록을 로컬 조직 인력 구조로 동기화하고 업무 진행과 같은 알림을 직접 기업 WeChat으로 푸시하여 메시지 알림을 받을 수 있습니다.",
			"enableInfo2": "더 많은 O2OA 및 기업 WeChat 콘텐츠에 대한 자세한 내용은 여기를 참조하십시오：<a href='https://www.o2oa.net/search.html?q=%E4%BC%81%E4%B8%9A%E5%BE%AE%E4%BF%A1' target='_blank'>기업 WeChat</a>",
			"saveText": "기업 WeChat 구성 저장",
			"saveSuccess": "기업 WeChat 구성 저장 성공"
        },
        "welinkText": {

            "enable": "###사용 여부",
			"clientId": "애플리케이션의 클라이언트 ID",
			"clientSecret": "애플리케이션의 클라이언트 시크릿###",
			"syncCron": "동기화 검사 콜백 신호 주기",
			"forceSyncCron": "강제 동기화 주기",
			"oapiAddress": "API 서비스 주소",
			"messageEnable": "메시지 푸시 사용 여부",
			"workUrl": "작업 URL을 엽니다",
			"messageRedirectPortal": "처리 완료 후 포털로 리디렉션",

			"enableInfo": "O2OA는 WeLink의 기업 내 경량 애플리케이션으로 통합하는 방식을 지원하며 WeLink의 연락처를 로컬 조직 인력 구조로 동기화하고 작업 등의 알림을 직접 WeLink으로 푸시하여 메시지 알림을 받을 수 있습니다. (서버 재시작 필요)",
			"enableInfo2": "O2OA와 WeLink의 자세한 내용은 여기를 참조하십시오：<a href='https://www.o2oa.net/search.html?q=welink' target='_blank'>WeLink</a>",

			"syncCronInfo": "콜백 신호가 동기화 검사를 트리거하며 기본적으로 10분마다 실행됩니다. 그 동안 WeLink 콜백 신호가 수신되면 동기화 작업이 트리거됩니다. (WeLink에서 콜백 설정 필요)",
			"forceSyncCronInfo": "강제 동기화 주기 설정은 기본적으로 매일 8시와 12시에 인원 및 조직을 강제로 동기화합니다.",

			"workUrlInfo": "WeLink 메시지를 열 때의 작업 URL 주소입니다. 예: https://sample.o2oa.net/x_desktop/",
			"messageRedirectPortalInfo": "WeLink 메시지 처리가 완료된 후 특정 포털 페이지로 리디렉션할 수 있습니다.",

			"saveText": "WeLink 구성 저장",
			"saveSuccess": "WeLink 구성 저장 성공"
        }
    },
    "_storageServer": {
        "innerStorage": "내장 저장 서비스",
		"externalStorage": "확장 저장 서비스",

		"info": "<span style='color: red'>저장 구성을 수정하는 것은 대부분의 경우 시스템 기존 파일 저장에 영향을 미칠 수 있으므로 주의하여 구성을 수정하십시오!</span>",
		"info2": "저장 구성을 수정하기 전에 O2OA 백업 기능(ctl -dd)을 사용하여 시스템 데이터를 백업하는 것이 좋습니다. 저장 구성 수정 후 서버를 다시 시작하고 백업 데이터를 복원(ctl -rd)해야 합니다. 모든 데이터베이스 관련 구성 수정은 서버를 다시 시작해야 합니다",

		"saveStorageConfig": "모든 저장 구성 저장",
		"saveStorageConfigInfo": "이 페이지에서의 구성은 즉시 저장되지 않으며 수정한 구성을 저장하려면이 버튼을 클릭해야 합니다.",
		"saveStorageConfirm": "저장 구성을 저장하려고 합니다.<br><span style='color:red'>이것은 시스템 기존 파일 저장에 영향을 미칠 수 있습니다.</span><br><br>저장 구성을 저장하시겠습니까?",

		"reloadStorageConfig": "모든 저장 구성 복원",
		"reloadStorageConfigInfo": "이 페이지에서 저장하지 않은 수정 사항을 폐기하려면이 버튼을 클릭할 수 있습니다.",
		"reloadStorageConfirm": "이 작업은 저장 구성을 다시 로드하며 저장되지 않은 수정 사항은 손실됩니다. 저장 구성을 복원하시겠습니까?",

		"storageType": "저장 서비스 유형",
		"storageTypeInfo": "O2OA 시스템에는 파일 저장을 위한 내장 서비스가 제공되며 필요에 따라 외부 확장 저장 노드를 사용할 수 있습니다.",
		"storageTypeData": [
			{"value": 'inner', "label": "내장", "text": "내장 저장 서비스"},
			{"value": 'external', "label": "외부", "text": "확장 저장 서비스"}
		],

		"innerInnerInfo": "<span class='mainColor_color'>내장 파일 저장 서비스를 사용 중입니다</span>, <span style='color:red'>각 저장 노드에 다른 이름을 설정해야 합니다</span>",
		"innerExternalInfo": "<span class='mainColor_color'>확장 파일 저장 서비스를 활성화했습니다</span>, 그러나 여전히 내장 파일 저장 서비스의 구성을 수정할 수 있습니다. <span style='color:red'>각 저장 노드에 다른 이름을 설정해야 합니다</span>",

		"innerStorageConfig": "내장 저장 서비스 구성",

        "enable": "사용 여부",
		"port": "포트",
		"name": "이름",
		"prefix": "프리픽스 경로",
		"deepPath": "깊은 경로 사용",
		"saveStorage": "저장 저장 구성",
		"saveStorageSuccess": "저장 구성 저장 성공",

		"externalInnerInfo": "<span class='mainColor_color'>내장 파일 저장 서비스를 사용 중입니다.</span> 그러나 확장 파일 저장 서비스의 구성을 수정할 수 있습니다.",
		"externalExternalInfo": "<span class='mainColor_color'>확장 파일 저장 서비스를 활성화했습니다.</span>",

		"enableExternal": "확장 파일 저장 활성화",
		"disableExternal": "확장 파일 저장 비활성화",
		"enableExternalInfo": "확장 파일 저장을 활성화하려면 확장 파일 저장 구성이 완료되었는지 확인하십시오. 그렇지 않으면 서버 작동에 이상이 발생할 수 있습니다. 확장 저장 서비스를 활성화하거나 비활성화하면 시스템의 기존 파일 저장에 영향을 미칩니다. 시스템 데이터를 먼저 백업하는 것이 좋습니다.",

		"enableExternalTitle": "확장 파일 저장 활성화 확인",
		"enableExternalConfirm": "확장 파일 저장을 활성화하고 내장 파일 저장 서비스를 비활성화하려고 합니다.<br><span style='color:red'>기존에 저장된 시스템 파일에 영향을 미칠 수 있습니다.</span><br><br>확장 파일 저장을 활성화하시겠습니까?",
		"disableExternalTitle": "확장 파일 저장 비활성화 확인",
		"disableExternalConfirm": "확장 파일 저장을 비활성화하고 내장 파일 저장 서비스를 활성화하려고 합니다.<br><span style='color:red'>기존에 저장된 시스템 파일에 영향을 미칠 수 있습니다.</span><br><br>확장 파일 저장을 비활성화하시겠습니까?",

		"externalStorageNode": "확장 저장 노드 구성",
		"addStorageNode": "저장 노드 추가",
		"editStorageNode": "저장 노드 편집",
		"inputStorageNodeKey": "저장 노드 식별자를 입력하세요",
		"inputStorageNodeName": "저장 노드 이름을 입력하세요",

        "external": {
			"protocol": "프로토콜",
			"username": "사용자 이름",
			"password": "암호",
			"host": "호스트",
			"port": "포트",
			"name": "이름",
			"key": "노드 식별자",
			"protocolData": {
				"webdav": "WebDAV",
				"sftp": "SFTP",
				"ftps": "FTPS",
				"ftp": "FTP",
				"file": "파일",
				"hdfs": "HDFS",
				"cifs": "CIFS",
				"ali": "알리클라우드 스토리지",
				"s3": "아마존 클라우드 스토리지",
				"min": "MinIO 스토리지"
			},
			"protocolDataInfo": {
				"ali": "애플리케이션 마켓에 Alibaba Cloud OSS 통합 플러그인을 설치하지 않았다면 먼저 설치하십시오.",
				"min": "애플리케이션 마켓에 MinIO 클라우드 스토리지 통합 플러그인을 설치하지 않았다면 먼저 설치하십시오."
			}
		},
        "removeNodeConfigTitle": "스토리지 노드 삭제 확인",
		"removeNodeConfig": "스토리지 노드 '{name}'를 삭제하려고 합니다. 이 작업은 시스템에 이미 저장된 파일에 영향을 줄 수 있습니다.<br>'{name}' 스토리지 노드를 삭제하시겠습니까?",

		"assignNode": "스토리지 노드 할당",
		"assignNodeInfo": "O2OA에는 다양한 유형의 파일이 있으며 이러한 파일에 스토리지 노드를 할당할 수 있습니다. 한 유형의 파일은 여러 노드에 할당할 수 있습니다.",
		"files": {
			"file": "파일 (file)",
			"processPlatform": "프로세스 플랫폼 파일 (processPlatform)",
			"mind": "마인드 맵 파일 (mind)",
			"meeting": "회의 관리 파일 (meeting)",
			"calendar": "일정 관리 파일 (calendar)",
			"cms": "콘텐츠 관리 파일 (cms)",
			"bbs": "게시판 파일 (bbs)",
			"teamwork": "작업 관리 파일 (strategyDeploy)",
			"structure": "애플리케이션 관리 (structure)",
			"im": "메시지 파일 (im)",
			"general": "기타 일반 파일 (general)",
			"custom": "사용자 지정 애플리케이션 파일 (custom)"
		},

		"store": "스토리지 노드",

		"noStoreNode": "할당된 스토리지 노드 없음",
		"addStore": "스토리지 노드 추가",
		"saveStore": "저장"

    },
    "_appTools": {
        "onlineBuild": "앱 온라인 빌드",
		"mpweixinMenu": "공식 계정 메뉴 구성",

		"onlineBuildInfo": " <ul style='padding: 0'><li>현재 모바일 앱 온라인 빌드 기능은 Android 플랫폼만 지원됩니다.</li>" +
			"<li>온라인 빌드를 위해 먼저 [클라우드 서비스 설정]에서 등록하고 로그인해야 합니다.</li>" +
			"<li>정보를 제출한 후 현재 빌드 상태가 표시되며, 빌드 프로세스에는 시간이 걸릴 수 있으므로 현재 페이지를 떠나서 빌드가 완료될 때까지 기다릴 수 있습니다. 그런 다음 APK 파일을 이 페이지에서 다운로드할 수 있습니다.</li></ul>",

		"onlineBuildInfo1": "<span class='mainColor_color'>앱 온라인 빌드</span>는 앱 스토어에서 더 나은 옵션을 제공하고 있습니다. 앱 스토어에서 확인하여 얻을 수 있습니다.",

        "appPack": {
            "formSubmitBtnTitle": "제출 및 빌드 시작",
			"formReinputBtnTitle": "양식 다시 작성 및 빌드",
			"formRePackBtnTitle": "기존 데이터 사용하여 빌드",
			"formDownloadApkBtnTitle": "APK 파일 다운로드",
			"formDownloadPublishBtnTitle": "로컬에 발행한 파일 다운로드",
			"refreshStatusBtnTitle": "상태 새로고침",
			"formUploadLogoBtnTitle": "이미지 업로드",

            "messageO2cloudNotEnable": "O2 클라우드가 활성화되지 않았거나 연결할 수 없습니다!",
			"messageO2cloudNotLogin": "먼저 O2 클라우드에 로그인하십시오!",
			"messageO2cloudLoginFail": "앱 패키징 서버 로그인 실패!",
			"statusOrderInline": "대기 중...",
			"statusPacking": "패키징 중...",
			"statusPackEnd": "패키징 완료",
			"statusPackError": "패키징 오류",
			"publishStatusNone": "미게시",
			"publishStatusDoing": "게시 중...",
			"publishStatusCompleted": "게시 완료, 앱을 설치하려면 화면에 표시된 QR 코드를 스캔하세요!",
			"publishStatusFail": "게시 실패, 다시 시도하거나 관리자에게 문의하세요!",
			"messageSubmitNotAtStatus": "현재 패키징 중이므로 나중에 다시 시도하십시오!",
			"messageAppnameNotEmpty": "앱 이름을 입력하세요!",
			"messageAppnameLenMax6": "앱 이름은 6자를 초과할 수 없습니다!",
			"messageAppLogoNotEmpty": "로고 이미지를 다시 업로드하세요!",
			"messageAppLogoNeedPng": "로고 이미지는 반드시 PNG 형식이어야 합니다!",
			"messagePortocolNotEmpty": "HTTP 프로토콜을 입력하세요!",
			"messageHostNotEmpty": "중앙 서버 도메인을 입력하세요!",
			"messageHostFormatError": "중앙 서버 도메인 또는 IP 주소를 입력하세요. 예: www.o2oa.net. 'http'와 같은 접두사는 입력하지 마세요!",
			"messagePortNotEmpty": "중앙 서버 포트 번호를 입력하세요!",
			"messageContext_not_empty": "중앙 서버 컨텍스트는 비워둘 수 없습니다!",
			"messagePortocolMustBeHttpHttps": "HTTP 프로토콜은 'http' 또는 'https' 만 허용됩니다!",
			"messageAlertTitle": "제출 확인",
			"messageAlertSubmit": "정말로 제출하시겠습니까? 현재 양식 정보는 모바일 앱으로 패키징됩니다?",

            "statusLabel": "현재 상태",
			"publishStatusLabel": "발표 상태",
			"formAppName": "앱 이름",
			"formAppNameTip": "앱 데스크톱 표시 이름, 6자를 초과하지 않아야 함",
			"formLogo": "로고 이미지",
			"formLogoTip": "앱 데스크톱에 표시되는 로고 이미지, 반드시 png 형식이어야 함",
			"formProtocol": "HTTP 프로토콜",
			"formProtocolTip": "http / https",
			"formHost": "도메인",
			"formHostTip": "중앙 서버 도메인 또는 IP 주소, 예: www.o2oa.net",
			"formPort": "포트 번호",
			"formPortTip": "중앙 서버 포트 번호, 예: 20030",
			"formContext": "컨텍스트",
			"formContextTip": "중앙 서버 컨텍스트, 예: /x_program_center",
			"formUrlMapping": "프록시 url 매핑",
			"formUrlMappingTip": "서버 외부에서 프록시 주소를 사용하는 경우 사용, 예: { \"demo.o2oa.net:20020\": \"demo.o2oa.net/dev/app\" }",
			"formAppVersionName": "앱 버전 이름",
			"formAppVersionNameTip": "앱 버전 이름, 예: v1.0.0. 이 필드는 기본적으로 작성하지 않아도 됨!",
			"formAppBuildNo": "앱 버전 번호",
			"formAppBuildNoTip": "앱 버전 번호, 양의 정수여야 함, 예: 100. 이 필드는 기본적으로 작성하지 않아도 됨!",
			"formEnableOuterPackage": "외부 패키지 이름 사용 여부",
			"formEnableOuterPackageTip": "외부 패키지 이름을 활성화하면 공식 출시 APP과 충돌 및 덮어씌우기를 방지할 수 있습니다"
        },

        "mpMenu": {
            "mpweixinInfo": "⚠️ WeChat 공개 번호 메뉴 기능을 사용하려면 먼저 관련 구성 파일 [mpweixin.json]을 활성화하고 WeChat 공개 번호 관리 백엔드에서 개발 모듈에서 서버 구성을 활성화해야 합니다!",
			"mpweixin": "공개 번호",
			"publishMpweixin": "WeChat 공개 번호에 게시",
			"publishToWxmp": "주의! 현재 작업은 모든 저장된 메뉴 데이터를 WeChat 공개 번호에 덮어쓸 수 있습니다. 계속하시겠습니까?",
			"publishSuccess": "게시 성공, 24시간 후에 모바일에서 동기화됩니다!",
			"subscribeMpweixin": "구독 회신",
			"subscribeMpweixin_desc": "WeChat 공개 번호에 새로운 사용자가 구독할 때 자동으로 전송되는 메시지 내용",
			"subscribeContentErrorEmpty": "회신 메시지 내용을 비워 둘 수 없습니다!",
			"subscribeMpweixin_save": "저장",
			"deleteMenuBtnTitle": "메뉴 삭제",
			"defaultNewName": "새 메뉴 추가",
			"formNameLabel": "메뉴 이름",
			"formOrderLabel": "메뉴 정렬 번호",
			"formRadioLabel": "메뉴 내용",
			"formRadioTypeMsg": "메시지 보내기",
			"formRadioTypeUrl": "웹페이지로 이동",
			"formRadioTypeMiniprogram": "미니프로그램으로 이동",

            "formTypeMsgTips": "이 메뉴를 클릭하면 사용자에게 다음 텍스트를 보냅니다. 인증되지 않은 구독 번호는 텍스트 메시지를 지원하지 않습니다.",
			"formTypeMsgLabel": "텍스트 메시지",
			"formTypeMsgErrorEmpty": "텍스트 메시지 내용을 비워 둘 수 없습니다!",
			"formSubscribeContentErrorEmpty": "회신 메시지 내용을 비워 둘 수 없습니다!",
			"formTypeUrlTips": "이 메뉴를 클릭하면 다음 링크로 이동합니다.",
			"formTypeUrlLabel": "페이지 주소",
			"formTypeUrlErrorEmpty": "페이지 주소를 비워 둘 수 없습니다!",
			"formTypeMiniprogramTips": "이 메뉴를 클릭하면 다음 소규모 프로그램으로 이동합니다.",
			"formTypeMiniprogramAppidLabel": "소규모 프로그램 ID",
			"formTypeMiniprogramAppidPlaceholder": "소규모 프로그램 ID, WeChat 소규모 프로그램 관리 백엔드에서 확인하세요.",
			"formTypeMiniprogramAppidErrorEmpty": "소규모 프로그램 ID를 비워 둘 수 없습니다!",
			"formTypeMiniprogramPathLabel": "소규모 프로그램 경로",
			"formTypeMiniprogramPathPlaceholder": "소규모 프로그램 경로, WeChat 소규모 프로그램 관리 백엔드에서 확인하세요.",
			"formTypeMiniprogramPathErrorEmpty": "소규모 프로그램 경로를 비워 둘 수 없습니다!",
			"formTypeMiniprogramUrlLabel": "예비 웹페이지",
			"formTypeMiniprogramUrlPlaceholder": "예비 웹페이지, 이전 버전의 WeChat에서는 이 예비 웹페이지를 엽니다.",
			"formTypeMiniprogramUrlErrorEmpty": "예비 웹페이지를 비워 둘 수 없습니다!",
			"formNameTips4": "중국어, 영어, 숫자만 지원하며 4자를 초과하지 않습니다.",
			"formNameTips6": "중국어, 영어, 숫자만 지원하며 6자를 초과하지 않습니다.",
			"formOrderTips": "숫자만 지원하며 6자를 초과하지 않습니다. 정렬은 문자열로 정렬됩니다.",
			"msgFirstMaxLen": "1급 메뉴는 최대 3개까지 만들 수 있습니다!",
			"menuMsgSubMaxLen": "2급 메뉴는 최대 5개까지 만들 수 있습니다!",
			"menuMsgParentNotSave": "상위 메뉴 데이터가 저장되지 않았습니다. 먼저 데이터를 저장하십시오!",
			"menuDeleteAlertMsg": "이 데이터를 삭제하시겠습니까? 하위 메뉴도 함께 삭제됩니다.",
			"menuDeleteSuccess": "데이터 삭제 성공!",
			"menuSaveSuccess": "데이터 저장 성공!",
			"formNameErrorEmpty": "메뉴 이름을 비워 둘 수 없습니다!",
			"formNameErrorMaxLen4": "메뉴 이름은 4자를 초과할 수 없습니다!",
			"formNameErrorMaxLen6": "메뉴 이름은 6자를 초과할 수 없습니다!",
			"formNameError": "글자 수가 제한을 초과합니다",
			"formOrderErrorEmpty": "메뉴 정렬 번호를 비워 둘 수 없습니다!",
			"formOrderErrorNotNumber": "메뉴 정렬 번호는 숫자만 입력할 수 있습니다!",
			"formOrderErrorMaxLen": "메뉴 정렬 번호는 6자를 초과할 수 없습니다!"
        }
    },
    "_pushConfig": {
        "pushType": "메시지 푸시 서비스",
		"pushTypeInfo": "O2OA는 JPush 및 Huawei Push 서비스를 지원합니다. 필요에 따라 푸시 서비스를 선택할 수 있습니다.",
		"pushTypeData": [
			{"value": "jpush", "label": "jpush", "text": "JPush 푸시 서비스"},
			{"value": "none", "label": "none", "text": "메시지 푸시 비활성화"}
		],

		"appKey": "JPush 앱 키",
		"masterSecret": "JPush 마스터 비밀",
		"appKeyInfo": "JPush 앱의 앱 키",
		"masterSecretInfo": "JPush 앱의 마스터 비밀",

		"appId": "Huawei 푸시 앱 ID",
		"appSecret": "Huawei 푸시 앱 비밀",
		"appIdInfo": "Huawei 푸시 앱의 앱 ID",
		"appSecretInfo": "Huawei 푸시 앱의 앱 비밀"
    },
    "_messageConfig": {
        "messageConsumers": "채널 구성",
		"messageType": "유형 구성",
		"messageLoader": "로더",
		"messageFilter": "필터",

		"consumerTypes": {
			"ws": "WebSocket",
			"pmsinner": "푸시 메시지",
			"calendar": "일정",
			"dingding": "딩딩",
			"welink": "WeLink",
			"qiyeweixin": "기업 위챗",
			"mpweixin": "위챗 공중계정",
			"kafka": "Kafka",
			"activemq": "ActiveMQ",
			"restful": "Restful",
			"mail": "이메일",
			"jdbc": "JDBC",
			"table": "데이터 테이블",
			"hadoop": "하둡",
			"andfx": "모바일 업무 메시지"
		},
        "consumerInfoTitle": "메시지 채널 설정",
		"consumerInfo": "O2OA 시스템은 다양한 메시지 채널을 제공하며, 여기에서 어떤 방법으로 메시지를 보내야 하는지 설정할 수 있습니다.",
		"consumerInfo2": "메시지 구성에 대한 자세한 내용은 다음을 참조하십시오：<a href='https://www.o2oa.net/search.html?q=%E6%B6%88%E6%81%AF%E9%85%8D%E7%BD%AE' target='_blank'>메시지</a>",

		"addConsumer": "메시지 채널 추가",
		"consumerLabel": {
			"key": "채널 이름",
			"type": "유형",
			"filter": "필터",
			"loader": "로더",
			"startTlsEnable": "전송 보안 강화"
		},
		"none": "없음",
		"editConsumer": "메시지 채널 편집",

		"inputKey": "메시지 채널 이름을 입력하세요",
		"hasKey": "메시지 채널 이름이 이미 존재합니다. 다른 이름을 사용하세요.",

        "consumerData": {
            "kafka": ['bootstrapServers', 'topic', 'securityProtocol', 'saslMechanism', 'saslMechanism', 'username', 'password'],
            "activemq": ['url', 'queueName', 'username', 'password'],
            "restful": ['url', 'method', 'internal'],
            "mail": ['host', 'port', 'sslEnable', 'auth', 'startTlsEnable', 'from', 'password'],
            "jdbc": ['driverClass', 'url', 'catalog', 'schema', 'table', 'username', 'password'],
            "table": ['table'],
            "hadoop": ['fsDefaultFS', 'path', 'username']
        },

        "messageTypeTitle": "메시지 유형 설정",
		"messageTypeInfo": "O2OA 시스템에 내장된 다양한 이벤트는 메시지를 보낼 수 있으며, 이러한 이벤트가 어떤 채널을 통해 메시지를 보내야 하는지 설정할 수 있습니다. 사용자 정의 메시지 유형을 추가할 수도 있습니다.",

		"noConsumer": "이 유형의 메시지에 대한 발송 채널이 선택되지 않았습니다.",
		"selectConsumer": "채널 선택",
		"addTmpConsumer": "채널 추가",

		"addMessageType": "메시지 유형 추가",
		"newMessageData": {
			"key": "메시지 식별자",
			"description": "설명"
		},
		"inputMessageKey": "메시지 식별자를 입력하세요",
		"hasMessageKey": "메시지 식별자가 이미 존재합니다. 다른 식별자를 사용하세요.",

		"deleteTypeTitle": "메시지 유형 삭제 확인",
		"deleteTypeInfo": "메시지 유형 “{name}”을(를) 삭제하시겠습니까?",

        "filterConfigTitle": "메시지 필터 구성",
		"filterConfigInfo": "메시지 채널에서 필터를 사용할 수 있으며, 필터는 메시지를 보내기 전에 호출되는 서버 측 스크립트입니다. 필터가 true를 반환하면 메시지를 보낼 수 있고, false를 반환하면 해당 메시지는 보내지지 않습니다.",
		"addFilter": "메시지 필터 추가",
		"filterKey": "필터 이름",
		"inputFilterKey": "필터 이름을 입력하세요",
		"hasFilterKey": "필터 이름이 이미 존재합니다. 다른 이름을 사용하세요.",
		"deleteFilterTitle": "필터 삭제 확인",
		"deleteFilterInfo": "필터 “{name}”을(를) 삭제하시겠습니까?",

		"loaderConfigTitle": "메시지 로더 구성",
		"loaderConfigInfo": "메시지 채널에서 로더를 사용할 수 있으며, 로더는 메시지를 보내기 전에 메시지 내용을 수정하는 서버 측 스크립트입니다. 메시지를 보내기 전에 JSON 형식의 데이터를 반환해야 합니다. 이 데이터는 전송할 메시지 내용으로 사용됩니다.",
		"addLoader": "메시지 로더 추가",
		"loaderKey": "로더 이름",

        "inputLoaderKey": "로더 이름을 입력하세요",
		"hasLoaderKey": "로더 이름이 이미 존재합니다. 다른 이름을 사용하세요.",

		"deleteLoaderTitle": "로더 삭제 확인",
		"deleteLoaderInfo": "로더 “{name}”을(를) 삭제하시겠습니까?",

		"deleteConsumerTitle": "메시지 채널 삭제 확인",
		"deleteConsumerInfo": "메시지 채널 “{name}”을(를) 삭제하시겠습니까?",

		"loaderComment": "/*\nmessage 객체는 메시지 본문이며 스크립트 실행 컨텍스트에 자동으로 주입됩니다. 여기에는 네 가지 필드가 있습니다.\nmessage.title: 제목\nmessage.person: 수신 대상\nmessage.type: 메시지 유형, 예: task_create\nmessage.body: 메시지 본문, 예: task_create 유형의 메시지에는 JSON 형식의 task(작업) 데이터가 저장됩니다.\nreturn 반환된 message 객체\n*/\nreturn message;",
		"filterComment": "/*\nmessage 객체는 메시지 본문이며 스크립트 실행 컨텍스트에 자동으로 주입됩니다. 여기에는 네 가지 필드가 있습니다.\nmessage.title: 제목\nmessage.person: 수신 대상\nmessage.type: 메시지 유형, 예: task_create\nmessage.body: 메시지 본문, 예: task_create 유형의 메시지에는 JSON 형식의 task(작업) 데이터가 저장됩니다.\nreturn 반환된 boolean, true는 전송 필요, false는 전송하지 않음을 나타냅니다.\n*/\nreturn true;"
    }
}
