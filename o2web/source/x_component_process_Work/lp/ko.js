MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xApplication.process.Work.LP = {
	"title": "작업",
    "selectRoute": "결정 선택",
    "inputOpinion": "의견 입력",
    "selectPerson" : "인원 선택",
    "cancel": "취소",
    "ok": "확인",
    "close": "닫기",
    "saveWrite": "저장",
    "inputText": "여기에 프로세스 의견 작성",

    "mustSelectRoute": "먼저 결정을 선택하세요",
    "mustSelectRouteGroup" : "먼저 결정 그룹을 선택하세요",
    "opinionRequired" : "의견을 작성하세요",

    "searchKey": "키워드 입력",

    "task": "대기함",
    "done": "완료함",
    "draft": "드래프트",
    "myfile": "내 파일",
    "reset": "담당자 재설정",
    "reroute": "재조정",
    "addSplit": "분기 추가",
    "rollback": "프로세스 회수",
    "goBack": "되돌아가기",

    "phone": "핸드폰",
    "mail": "메일",
    "save": "저장",
    "process": "프로세스 계속",
    "handwriting": "손글씨",
    "audioRecord": "음성 녹음",

    "noAppendTaskIdentityConfig" : "전송인을 구성하지 않았습니다. 관리자에게 문의하세요.",
    "selectAppendTaskIdentityNotice" : "전송인을 선택하세요",
    "routeValidFailure" : "경로 유효성 검사 실패",
    "loadedOrgCountUnexpected" : "인원 선택 화면이 완료되지 않았습니다. 잠시 기다려주세요...",

    "taskCompletedPerson": "처리자",
    "readPerson": "읽음",
    "systemFlow": "시스템 자동 처리",

    "openWorkError": "이 문서를 볼 수 있는 권한이 없거나 이 문서가 삭제되었습니다.",

    "rollbackConfirmTitle": "프로세스 회수 확인",
    "rollbackConfirmContent": "프로세스를 “{log}” 상태로 회수하시겠습니까? (프로세스 회수 시 이 상태 이후의 모든 정보가 삭제됩니다)",

    "recoverFileConfirmTitle": "내용 복구 확인",
    "recoverFileConfirmContent": "“{att}” 버전으로 내용을 복구하시겠습니까? (복구 후 저장된 임시 파일이 삭제되며 다시 복구할 수 없습니다)",

    "notRecoverFileConfirmTitle": "내용 복구 취소 확인",
    "notRecoverFileConfirmContent": "내용 복구를 취소하시겠습니까? (취소 후 저장된 임시 파일이 삭제되며 다시 복구할 수 없습니다)",

    "closePageCountDownText" : "{second}초 후에 페이지가 닫힙니다!",
    "closePage" : "페이지 닫기",

    "selectRouteGroup" : "결정 그룹 선택",
    "defaultDecisionOpinionName" : "기타",
    "routeGroupOrderList" : ["동의","동의하지 않음","기타"],

    "selectWork": "열고자 하는 파일이 여러 개의 분기로 생성되었습니다. 확인할 파일을 선택하세요:",
    "currentActivity": "현재 활동: ",
    "currentUsers": "현재 처리자: ",
    "completedWork": "파일이 완료되었습니다",

    "managerProcessNotice" : "참고: 빠른 처리 기능은 다음 경우에 적용됩니다. 그렇지 않으면 오류가 발생할 수 있습니다:<br\>1. 양식에 필수 항목이 입력되었습니다.<br\>2. 제출 시 사용자 선택이 필요하지 않습니다.<br\>3. 사용자 ID를 기반으로 계산된 내용이 없습니다.<br\>관리자 권한으로 대기자 로그인 후 양식을 제출하려면 아래 링크를 클릭하세요.",
    "managerLogin" : "모의 로그인 및 파일 열기",
    "managerLoginConfirmTitle" : "모의 로그인",
    "managerLoginConfirmContent" : "{user} 사용자로 로그인하고 파일을 열려면 확인하세요. 확인을 클릭하면 현재 사용자로 돌아가려면 로그아웃한 다음 다시 로그인해야 합니다.",
    "managerLoginSuccess" : "{user}로 성공적으로 전환되었습니다.",

    "selectIdentity": "이 대기 작업을 처리할 신분 선택",
    "selectIdentityInfo": "현재 작업에 여러 가지 다른 신분의 대기 작업이 있습니다. 이 작업을 처리할 신분을 선택하세요",

    "org": "조직",
    "duty": "직무"
};
MWF.xApplication.process.Work["lp."+o2.language] = MWF.xApplication.process.Work.LP;
