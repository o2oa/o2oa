MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.FormDesigner.LP = Object.merge( {}, MWF.xApplication.process.FormDesigner.LP, {
	"selectApplication": "애플리케이션 선택",
	"formType": {
		"empty": "빈 양식",
		"publishEdit": "게시 편집 양식",
		"publishRead": "게시 읽기 양식",
		"publishEditGreen": "게시 편집 양식 (녹색)",
		"publishReadGreen": "게시 읽기 양식 (녹색)",
		"dataInput": "데이터 입력 양식"
	},
	"validation": {
		"publish": "게시할 때"
	},
	"modules": {
		"reader": "독자",
		"commend": "좋아요",
		"author": "작성자",
		"log": "읽기 기록",
		"comment": "댓글",
		"logCommend": "좋아요 기록",
		"group_cms": "콘텐츠 관리 모듈"
	},
    "formStyle":{
        "noneStyle": "빈 스타일",
		"defaultStyle": "전통 스타일",
		"redSimple": "빨간색 간결한 스타일",
		"blueSimple": "파란색 간결한 스타일",
		"greenFlat": "녹색 평면 스타일",
		"defaultMobileStyle": "모바일 스타일",
		"banner": "배너",
		"title": "제목",
		"sectionTitle": "섹션 제목",
		"section": "섹션"
    },
    "propertyTemplate": {
        "setPopular": "인기 설정",
		"commentPerPage": "페이지당 댓글 수",
		"tiao": "개",
		"allowModifyComment": "게시 후 수정 허용",
		"allowComment": "댓글 허용",
		"editor": "에디터",
		"editorTitle": "CKEditor 구성 스크립트",
		"editorConfigNote": "에디터 초기화에 사용되는 CKEditor 구성 객체 반환",
		"editorConfigLinkNote": "더 많은 속성 도움말은 참조하세요",
		"table": "테이블",
		"text": "텍스트",
		"format": "형식",
		"validationSave": "저장 유효성 검사",
		"validationPublish": "발행 유효성 검사",
		"notice": "알림",
		"noticeInfo": "참고: 메시지 전송의 총 스위치는 분류 구성에서 설정됩니다.",
		"noticeRange": "범위",
		"noticeByReader": "독자 범위에 따라",
		"noticeByCustom": "사용자 정의",
		"notifyCreatePerson": "작성자 알림",
		"blankToAllNotify": "독자(게시 범위)가 비어 있을 때 알림",
		"blankNotToAllNotify": "독자(게시 범위)가 비어 있을 때 알림 안 함",
		"specificValue": "지정",
		"formField": "폼 필드"
    },
    "actionBar": {
        "close": "닫기",
		"closeTitle": "문서 닫기",
		"edit": "편집",
		"editTitle": "문서 편집",
		"save": "저장",
		"saveTitle": "문서 저장",
		"publish": "게시",
		"publishTitle": "문서 게시",
		"publishDelayed": "지연 게시",
		"publishDelayedTitle": "문서 지연 게시",
		"saveDraft": "드래프트 저장",
		"saveDraftTitle": "드래프트 저장",
		"popular": "인기 설정",
		"popularTitle": "인기 설정",
		"delete": "삭제",
		"deleteTitle": "문서 삭제",
		"print": "인쇄",
		"printTitle": "문서 인쇄",
		"setTop": "상단 고정",
		"setTopTitle": "문서 상단 고정",
		"cancelTop": "상단 고정 해제",
		"cancelTopTitle": "문서 상단 고정 해제",
		"downloadAll": "일괄 다운로드",
		"downloadAllTitle": "일괄 다운로드"
    }
});
