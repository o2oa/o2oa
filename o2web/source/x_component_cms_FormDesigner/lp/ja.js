MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.FormDesigner.LP = Object.merge( {}, MWF.xApplication.process.FormDesigner.LP, {
	"selectApplication": "アプリケーションを選択",
	"formType": {
		"empty": "空白のフォーム",
		"publishEdit": "公開編集フォーム",
		"publishRead": "公開閲覧フォーム",
		"publishEditGreen": "公開編集フォーム（緑色）",
		"publishReadGreen": "公開閲覧フォーム（緑色）",
		"dataInput": "データ入力フォーム"
	},
	"validation": {
		"publish": "公開時"
	},
	"modules": {
		"reader": "読者",
		"commend": "いいね",
		"author": "著者",
		"log": "閲覧履歴",
		"comment": "コメント",
		"logCommend": "いいね履歴",
		"group_cms": "コンテンツ管理"
	},
    "formStyle":{
        "noneStyle": "空のスタイル",
		"defaultStyle": "伝統的なスタイル",
		"redSimple": "赤いシンプル",
		"blueSimple": "青いシンプル",
		"greenFlat": "緑のフラット",
		"defaultMobileStyle": "モバイルスタイル",
		"banner": "バナー",
		"title": "タイトル",
		"sectionTitle": "セクションタイトル",
		"section": "セクション"
    },
    "propertyTemplate": {
        "setPopular": "人気設定",
		"commentPerPage": "ページごとのコメント数",
		"tiao": "本",
		"allowModifyComment": "投稿後の編集を許可",
		"allowComment": "コメントを許可",
		"editor": "エディター",
		"editorTitle": "CKEditor設定スクリプト",
		"editorConfigNote": "エディターの初期化に使用されるCKEditor設定オブジェクトを返します",
		"editorConfigLinkNote": "詳細なプロパティについては、以下をご覧ください",
		"table": "テーブル",
		"text": "テキスト",
		"format": "フォーマット",
		"validationSave": "保存の検証",
		"validationPublish": "公開の検証",
		"notice": "通知",
		"noticeInfo": "注意：メッセージ送信の全般スイッチはカテゴリ構成で設定されています。",
		"noticeRange": "範囲",
		"noticeByReader": "読者の範囲に従って",
		"noticeByCustom": "カスタム",
		"notifyCreatePerson": "作成者へ通知",
		"blankToAllNotify": "読者（公開範囲）が空の場合、読者範囲に通知",
		"blankNotToAllNotify": "読者（公開範囲）が空の場合、通知しない",
		"specificValue": "指定",
		"formField": "フォームフィールド"
    },
    "actionBar": {
        "close": "閉じる",
		"closeTitle": "ドキュメントを閉じる",
		"edit": "編集",
		"editTitle": "ドキュメントを編集",
		"save": "保存",
		"saveTitle": "ドキュメントを保存",
		"publish": "公開",
		"publishTitle": "ドキュメントを公開",
		"publishDelayed": "タイマー公開",
		"publishDelayedTitle": "ドキュメントをタイマー公開",
		"saveDraft": "下書き保存",
		"saveDraftTitle": "下書き保存",
		"popular": "人気設定",
		"popularTitle": "人気設定",
		"delete": "削除",
		"deleteTitle": "ドキュメントを削除",
		"print": "印刷",
		"printTitle": "ドキュメントを印刷",
		"setTop": "トップに設定",
		"setTopTitle": "ドキュメントをトップに設定",
		"cancelTop": "トップ解除",
		"cancelTopTitle": "ドキュメントのトップ解除",
		"downloadAll": "一括ダウンロード",
		"downloadAllTitle": "一括ダウンロード"
    }
});
