MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xApplication.process.Work.LP = {
	"title": "業務",
    "selectRoute": "決定の選択",
    "inputOpinion": "意見の記入",
    "selectPerson" : "人物の選択",
    "cancel": "キャンセル",
    "ok": "確認",
    "close": "閉じる",
    "saveWrite": "保存",
    "inputText": "ここにプロセス意見を記入してください",

    "mustSelectRoute": "まず決定を選択してください",
    "mustSelectRouteGroup" : "まず決定グループを選択してください",
    "opinionRequired" : "意見を記入してください",

    "searchKey": "キーワードを入力してください",

    "task": "未完了タスク",
    "done": "完了タスク",
    "draft": "下書き",
    "myfile": "私のファイル",
    "reset": "担当者をリセット",
    "reroute": "再配信",
    "addSplit": "分岐を追加",
    "rollback": "プロセスロールバック",
    "goBack": "戻る",

    "phone": "電話",
    "mail": "メール",
    "save": "保存",
    "process": "プロセス継続",
    "handwriting": "手書き",
    "audioRecord": "録音",

    "noAppendTaskIdentityConfig" : "転送人が設定されていません。管理者に連絡してください。",
    "selectAppendTaskIdentityNotice" : "転送人を選択してください",
    "routeValidFailure" : "ルートの検証に失敗しました",
    "loadedOrgCountUnexpected" : "人員選択画面が完了していません。お待ちください...",

    "taskCompletedPerson": "処理者",
    "readPerson": "読み取り者",
    "systemFlow": "システム自動処理",

    "openWorkError": "この文書を表示する権限がありません、またはこの文書は削除されました。",

    "rollbackConfirmTitle": "プロセスロールバックの確認",
    "rollbackConfirmContent": "プロセスを“{log}”の状態にロールバックしますか？（プロセスロールバックはこの状態以降のすべての情報を消去します）",

    "recoverFileConfirmTitle": "コンテンツの復元の確認",
    "recoverFileConfirmContent": "“{att}”バージョンにコンテンツを復元しますか？（復元後、保存された一時ファイルが削除され、再度復元できません）",

    "notRecoverFileConfirmTitle": "コンテンツの復元をキャンセルの確認",
    "notRecoverFileConfirmContent": "コンテンツの復元をキャンセルしますか？（キャンセル後、保存された一時ファイルが削除され、再度復元できません）",

    "closePageCountDownText" : "{second}秒後にページが閉じます！",
    "closePage" : "ページを閉じる",

    "selectRouteGroup" : "決定グループの選択",
    "defaultDecisionOpinionName" : "その他",
    "routeGroupOrderList" : ["同意","不同意","その他"],

    "selectWork": "表示するファイルが複数の分岐で生成されています。表示する分岐を選択してください：",
    "currentActivity": "現在のアクティビティ: ",
    "currentUsers": "現在の処理者: ",
    "completedWork": "ファイルが完了しました",

    "managerProcessNotice" : "注意: クイック処理機能は以下の場合に適用されます。それ以外の場合、エラーが発生する可能性があります:<br\>1、フォームに必須項目が入力されています。<br\>2、提出時にユーザーを選択する必要がありません。<br\>3、ユーザーIDを基にした内容がありません。<br\>あなたは管理者です。待ち人としてログインし、フォームを提出することができます。以下のリンクをクリックして実行してください。",
    "managerLogin" : "模擬ログインとファイルを開く",
    "managerLoginConfirmTitle" : "模擬ログイン",
    "managerLoginConfirmContent" : "{user}の身分でログインし、ファイルを開きますか？確定をクリックした後、元のユーザーに戻るにはログアウトして再度ログインする必要があります。",
    "managerLoginSuccess" : "{user}に切り替えました",

    "selectIdentity": "この待ち作業を処理する身分を選択",
    "selectIdentityInfo": "現在の作業には異なる身分の待ち作業が複数あります。この作業を処理する身分を選択してください",

    "org": "組織",
    "duty": "役職"
};
MWF.xApplication.process.Work["lp."+o2.language] = MWF.xApplication.process.Work.LP;
